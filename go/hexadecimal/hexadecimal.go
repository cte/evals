package hexadecimal

import (
	"errors"
	"math"
	"strings"
)

// Define specific error types for clarity, although the test only checks strings.
var errSyntax = errors.New("invalid hexadecimal syntax")
var errRange = errors.New("hexadecimal value out of int64 range")

// ParseHex converts a hexadecimal string to its int64 decimal equivalent.
func ParseHex(s string) (int64, error) {
	s = strings.ToLower(s) // Case-insensitive
	if len(s) == 0 {
		// The test case "" expects syntax error.
		return 0, errSyntax
	}

	var result int64 = 0
	
	// Check length to prevent overflow during calculation if length > 16
    // A 16-digit hex number can represent up to 2^64 - 1.
    // If len(s) > 16, it *might* be out of range, but leading zeros could make it fit.
    // Example: "00000000000000001" is valid.
    // A more robust check happens during calculation.

	// Iterate from left to right
	for i := 0; i < len(s); i++ {
		char := s[i]
		var val int64

		switch {
		case '0' <= char && char <= '9':
			val = int64(char - '0')
		case 'a' <= char && char <= 'f':
			val = int64(char - 'a' + 10)
		default:
			return 0, errSyntax // Invalid character
		}

		// Check for overflow *before* multiplying result by 16
        // If result > MaxInt64 / 16, then result * 16 will overflow.
        // Also, if result == MaxInt64 / 16, then result * 16 + val might overflow if val > MaxInt64 % 16
        if result > math.MaxInt64 / 16 {
             return 0, errRange
        }
        
        // Check the edge case more precisely
        if result == math.MaxInt64 / 16 && val > math.MaxInt64 % 16 {
             return 0, errRange
        }

		result = result*16 + val
	}

	return result, nil
}

// HandleErrors calls ParseHex for each input string and returns the error category.
func HandleErrors(inputs []string) []string {
	results := make([]string, len(inputs))
	for i, input := range inputs {
		_, err := ParseHex(input)
		if err == nil {
			results[i] = "none"
		} else if errors.Is(err, errSyntax) || strings.Contains(strings.ToLower(err.Error()), "syntax") {
            // Check specific error type or string content for compatibility with tests
			results[i] = "syntax"
		} else if errors.Is(err, errRange) || strings.Contains(strings.ToLower(err.Error()), "range") {
            // Check specific error type or string content
			results[i] = "range"
		} else {
            // Should not happen with defined errors, but catch unexpected errors
            results[i] = "unknown" 
        }
	}
	return results
}
