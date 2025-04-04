package transpose

import "strings"

func Transpose(input []string) []string {
	if len(input) == 0 {
		return []string{}
	}

	maxLen := 0
	for _, row := range input {
		if len(row) > maxLen {
			maxLen = len(row)
		}
	}

	// If input contains only empty strings, maxLen could be 0
	// but we still need to return an empty slice according to tests.
	// However, the first check handles the case of empty input slice.
	// If input is ["", ""], maxLen is 0, we should return [].
	// If input is ["A"], maxLen is 1.
	// If input is ["", "A"], maxLen is 1.

	outputBuilders := make([]strings.Builder, maxLen)
	numInputRows := len(input)

	for c := 0; c < maxLen; c++ { // Iterate through columns (output rows)
		currentOutputRow := &outputBuilders[c]
		for r := 0; r < numInputRows; r++ { // Iterate through input rows
			runes := []rune(input[r])
			if c < len(runes) {
				// Append the character
				currentOutputRow.WriteRune(runes[c])
			} else {
				// Check if padding is needed by looking ahead
				needsPadding := false
				for k := r + 1; k < numInputRows; k++ {
					if len([]rune(input[k])) > c {
						needsPadding = true
						break
					}
				}
				if needsPadding {
					currentOutputRow.WriteRune(' ')
				}
			}
		}
	}


	output := make([]string, maxLen)
	for i := 0; i < maxLen; i++ {
		output[i] = outputBuilders[i].String()
	}

	return output
}
