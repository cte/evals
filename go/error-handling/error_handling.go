package erratum

import "fmt"

// Use opens a resource using opener, frobs it with input, and closes it.
// It handles TransientErrors during opening by retrying.
// It handles FrobErrors and other panics during Frob, ensuring Defrob is called for FrobErrors
// and the resource is always closed.
func Use(opener ResourceOpener, input string) (err error) {
	var res Resource

	// Attempt to open the resource, retrying on TransientError.
	for {
		res, err = opener()
		if err == nil {
			// Successfully opened
			break
		}
		// Check if the error is a TransientError
		_, isTransient := err.(TransientError)
		if !isTransient {
			// Not a TransientError, return the error immediately.
			return err
		}
		// If it's a TransientError, the loop continues to retry.
	}

	// Ensure the resource is closed when the function returns,
	// regardless of errors or panics.
	// Also handle potential panics from Frob within this deferred function.
	defer func() {
		// Recover from any panic that might have occurred.
		if r := recover(); r != nil {
			// A panic occurred. Check if it's a FrobError.
			if frobErr, ok := r.(FrobError); ok {
				// It's a FrobError. Call Defrob with the tag.
				res.Defrob(frobErr.defrobTag)
				// Set the return error to the FrobError.
				err = frobErr
			} else {
				// It's some other panic value. Convert it to an error.
				// Check if it's already an error type.
				errPanic, ok := r.(error)
				if ok {
					err = errPanic // Use the existing error.
				} else {
					// Wrap the panic value in a new error.
					err = fmt.Errorf("%v", r)
				}
			}
		}
		// Crucially, close the resource *after* handling potential panics and Defrob.
		res.Close()
	}()

	// Call Frob on the opened resource.
	// If Frob returns an error, it will be assigned to the named return variable 'err'.
	// If Frob panics, the deferred function above will handle it.
	res.Frob(input)

	// Return the error (which might have been set by Frob directly,
	// or by the deferred function if a panic occurred).
	return err
}
