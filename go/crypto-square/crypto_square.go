package cryptosquare

import (
	"math"
	"strings"
	"unicode"
)

// normalize removes non-alphanumeric characters and converts to lowercase.
func normalize(pt string) string {
	var sb strings.Builder
	for _, r := range pt {
		if unicode.IsLetter(r) || unicode.IsDigit(r) {
			sb.WriteRune(unicode.ToLower(r))
		}
	}
	return sb.String()
}

// calculateDims calculates the dimensions (rows r, columns c) for the crypto square.
// It finds the smallest integer c such that c >= r, c-r <= 1, and r*c >= length.
func calculateDims(length int) (r, c int) {
	if length == 0 {
		return 0, 0
	}

	// Start checking from ceil(sqrt(length)) upwards for the column count c.
	c = int(math.Ceil(math.Sqrt(float64(length))))

	for {
		// Calculate rows based on current columns c
		r = int(math.Ceil(float64(length) / float64(c)))

		// Check if the conditions (c >= r and c - r <= 1) are met.
		// Since we iterate c upwards, the first c that satisfies this
		// along with r*c >= length (implicitly handled by ceil(length/c))
		// will be the smallest valid c.
		if r <= c && c-r <= 1 {
			break // Found the correct dimensions
		}

		// If conditions are not met, increment c and recalculate r.
		// This typically happens when r is too large compared to c.
		// Increasing c tends to decrease or stabilize r.
		c++
	}

	return r, c
}


// Encode implements the crypto square encoding.
func Encode(pt string) string {
	normalized := normalize(pt)
	length := len(normalized)

	if length == 0 {
		return ""
	}

	r, c := calculateDims(length)

	// If length is 0, calculateDims returns 0,0. Handle this case again just to be safe.
	if r == 0 || c == 0 {
		return ""
	}

	// Build the encoded message column by column
	var resultChunks []string
	for col := 0; col < c; col++ {
		var chunk strings.Builder
		for row := 0; row < r; row++ {
			idx := row*c + col
			if idx < length {
				chunk.WriteByte(normalized[idx])
			} else {
				// Pad with space if the index is beyond the normalized text length
				chunk.WriteRune(' ')
			}
		}
		resultChunks = append(resultChunks, chunk.String())
	}

	return strings.Join(resultChunks, " ")
}
