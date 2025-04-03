package trinary

import (
	"errors"
)

func ParseTrinary(arg string) (int64, error) {
	var result int64 = 0
	for _, c := range arg {
		if c < '0' || c > '2' {
			return 0, errors.New("invalid trinary digit")
		}
		digit := int64(c - '0')
		if result > (1<<63-1-digit)/3 {
			return 0, errors.New("overflow")
		}
		result = result*3 + digit
	}
	return result, nil
}
