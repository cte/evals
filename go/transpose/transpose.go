package transpose

func Transpose(input []string) []string {
	if len(input) == 0 {
		return []string{}
	}

	// Find the maximum length of input lines
	maxLen := 0
	for _, line := range input {
		if len(line) > maxLen {
			maxLen = len(line)
		}
	}

	// Build full transposed matrix with padding
	matrix := make([][]rune, maxLen)
	for i := 0; i < maxLen; i++ {
		var row []rune
		for _, line := range input {
			if i < len(line) {
				row = append(row, rune(line[i]))
			} else {
				row = append(row, ' ')
			}
		}
		matrix[i] = row
	}

	// Trim trailing spaces in each transposed line based on last non-space character
	output := make([]string, maxLen)
	for i, row := range matrix {
		output[i] = string(row)
	}

	return output
}
