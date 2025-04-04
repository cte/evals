package say

import (
	"strings"
)

var ones = []string{
	"", "one", "two", "three", "four", "five", "six", "seven", "eight", "nine",
	"ten", "eleven", "twelve", "thirteen", "fourteen", "fifteen", "sixteen", "seventeen", "eighteen", "nineteen",
}

var tens = []string{
	"", "", "twenty", "thirty", "forty", "fifty", "sixty", "seventy", "eighty", "ninety",
}

var scales = []string{
	"", "thousand", "million", "billion",
}

// sayChunk converts a number between 0 and 999 into English words.
func sayChunk(n int64) string {
	if n == 0 {
		return "" // Don't say anything for a zero chunk unless it's the only chunk (handled in Say)
	}

	var parts []string
	hundreds := n / 100
	remainder := n % 100

	if hundreds > 0 {
		parts = append(parts, ones[hundreds], "hundred")
	}

	if remainder > 0 {
		if hundreds > 0 {
			parts = append(parts, "") // Add space placeholder, will be joined later
		}
		if remainder < 20 {
			parts = append(parts, ones[remainder])
		} else {
			ten := remainder / 10
			one := remainder % 10
			if one == 0 {
				parts = append(parts, tens[ten])
			} else {
				parts = append(parts, tens[ten]+"-"+ones[one])
			}
		}
	}

	// Use space as separator, filter empty strings from join
	var resultParts []string
	for _, p := range parts {
		if p != "" {
			resultParts = append(resultParts, p)
		}
	}
	return strings.Join(resultParts, " ")
}

// Say converts a non-negative number less than a trillion into English words.
func Say(n int64) (string, bool) {
	if n < 0 || n >= 1_000_000_000_000 {
		return "", false // Out of range
	}

	if n == 0 {
		return "zero", true
	}

	var parts []string
	scaleIndex := 0
	for n > 0 {
		if n%1000 != 0 {
			chunkStr := sayChunk(n % 1000)
			if scaleIndex > 0 {
				parts = append([]string{chunkStr, scales[scaleIndex]}, parts...)
			} else {
				parts = append([]string{chunkStr}, parts...)
			}
		}
		n /= 1000
		scaleIndex++
	}

	// Join parts with spaces, filtering potential empty strings if logic allowed them
	var finalParts []string
	for _, p := range parts {
		if p != "" {
			finalParts = append(finalParts, p)
		}
	}

	return strings.Join(finalParts, " "), true
}
