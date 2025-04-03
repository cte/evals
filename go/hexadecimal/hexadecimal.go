package hexadecimal

import (
	"errors"
	"math"
	"strings"
)

// ParseHex parses a hexadecimal string into an int64.
// Returns an error containing "syntax" for invalid input,
// or "range" if the value overflows int64.
func ParseHex(s string) (int64, error) {
	if s == "" {
		return 0, errors.New("syntax error: empty string")
	}

	var result int64 = 0
	for _, r := range s {
		var digit int64
		switch {
		case '0' <= r && r <= '9':
			digit = int64(r - '0')
		case 'a' <= r && r <= 'f':
			digit = int64(r - 'a' + 10)
		case 'A' <= r && r <= 'F':
			digit = int64(r - 'A' + 10)
		default:
			return 0, errors.New("syntax error: invalid character")
		}

		if result > (math.MaxInt64-digit)/16 {
			return 0, errors.New("range error: overflow")
		}

		result = result*16 + digit
	}

	return result, nil
}

// HandleErrors calls ParseHex on each input string and returns a slice
// indicating the error case: "none", "syntax", or "range".
func HandleErrors(inputs []string) []string {
	results := make([]string, len(inputs))
	for i, s := range inputs {
		_, err := ParseHex(s)
		if err == nil {
			results[i] = "none"
		} else {
			lowerErr := strings.ToLower(err.Error())
			if strings.Contains(lowerErr, "syntax") {
				results[i] = "syntax"
			} else if strings.Contains(lowerErr, "range") {
				results[i] = "range"
			} else {
				results[i] = "unknown"
			}
		}
	}
	return results
}
