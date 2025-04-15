package octal

import (
	"errors"
	"math"
)

// ParseOctal converts an octal string representation to its decimal integer equivalent.
// It returns an error if the input string contains invalid characters (not 0-7).
func ParseOctal(input string) (int64, error) {
	var result int64
	length := len(input)

	for i, r := range input {
		digit := int64(r - '0')

		// Check for invalid characters
		if digit < 0 || digit > 7 {
			return 0, errors.New("invalid octal digit")
		}

		power := int64(length - 1 - i)
		result += digit * int64(math.Pow(8, float64(power)))
	}

	return result, nil
}
