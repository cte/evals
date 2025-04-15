package sublist

import "reflect"

// Relation type is defined in relations.go file.

// contains checks if slice `a` contains slice `b` as a subsequence.
func contains(a, b []int) bool {
	lenA := len(a)
	lenB := len(b)

	if lenB == 0 {
		return true // An empty list is a sublist of any list
	}
	if lenA < lenB {
		return false // Cannot contain a larger list
	}

	for i := 0; i <= lenA-lenB; i++ {
		if reflect.DeepEqual(a[i:i+lenB], b) {
			return true
		}
	}
	return false
}

func Sublist(l1, l2 []int) Relation {
	// Removed len1 and len2 declarations

	isSublist := contains(l2, l1)
	isSuperlist := contains(l1, l2)

	if isSublist && isSuperlist {
		// If l1 contains l2 and l2 contains l1, they must be equal.
		// reflect.DeepEqual in contains handles the equality check.
		return RelationEqual
	}

	if isSublist {
		return RelationSublist
	}

	if isSuperlist {
		return RelationSuperlist
	}

	return RelationUnequal
}
