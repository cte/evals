package sublist

// Relation type is defined in relations.go file.

func Sublist(l1, l2 []int) Relation {
	if slicesEqual(l1, l2) {
		return RelationEqual
	}
	if isSublist(l1, l2) {
		return RelationSublist
	}
	if isSublist(l2, l1) {
		return RelationSuperlist
	}
	return RelationUnequal
}

func slicesEqual(a, b []int) bool {
	if len(a) != len(b) {
		return false
	}
	for i := range a {
		if a[i] != b[i] {
			return false
		}
	}
	return true
}

func isSublist(sub, super []int) bool {
	subLen := len(sub)
	superLen := len(super)

	if subLen == 0 {
		return true
	}
	if subLen > superLen {
		return false
	}

	for i := 0; i <= superLen-subLen; i++ {
		match := true
		for j := 0; j < subLen; j++ {
			if super[i+j] != sub[j] {
				match = false
				break
			}
		}
		if match {
			return true
		}
	}
	return false
}
