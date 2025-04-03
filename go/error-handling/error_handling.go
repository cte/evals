package erratum

func Use(opener ResourceOpener, input string) (err error) {
	var res Resource
	for {
		var openErr error
		res, openErr = opener()
		if openErr != nil {
			if _, ok := openErr.(TransientError); ok {
				continue
			}
			return openErr
		}
		break
	}

	defer func() {
		if r := recover(); r != nil {
			switch e := r.(type) {
			case FrobError:
				res.Defrob(e.defrobTag)
				err = e
			case error:
				err = e
			default:
				panic(r)
			}
		}
		res.Close()
	}()

	res.Frob(input)
	return nil
}
