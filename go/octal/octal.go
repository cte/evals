package octal

import (
	"errors"
)

func ParseOctal(input string) (int64, error) {
	var result int64 = 0
	for _, ch := range input {
		if ch < '0' || ch > '7' {
			return 0, errors.New("invalid octal digit")
		}
		result = result*8 + int64(ch-'0')
	}
	return result, nil
}
