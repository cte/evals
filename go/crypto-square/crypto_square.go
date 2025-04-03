package cryptosquare

func Encode(pt string) string {
	// Step 1: Normalize input
	normalized := make([]rune, 0, len(pt))
	for _, r := range pt {
		if (r >= 'A' && r <= 'Z') || (r >= 'a' && r <= 'z') || (r >= '0' && r <= '9') {
			if r >= 'A' && r <= 'Z' {
				r += 'a' - 'A'
			}
			normalized = append(normalized, r)
		}
	}

	n := len(normalized)
	if n == 0 {
		return ""
	}

	// Step 2: Determine rectangle size
	var rRows, cCols int
	for cCols = 1; cCols <= n; cCols++ {
		rRows = (n + cCols - 1) / cCols // ceiling division
		if cCols >= rRows && cCols-rRows <= 1 {
			break
		}
	}

	// Step 3: Pad normalized text to fill rectangle
	size := rRows * cCols
	padded := make([]rune, size)
	copy(padded, normalized)
	for i := n; i < size; i++ {
		padded[i] = ' '
	}

	// Step 4: Read columns to encode
	columns := make([][]rune, cCols)
	for c := 0; c < cCols; c++ {
		col := make([]rune, rRows)
		for r := 0; r < rRows; r++ {
			col[r] = padded[r*cCols+c]
		}
		columns[c] = col
	}

	// Step 5: Build output chunks of length rRows
	result := make([]rune, 0, size+cCols-1)
	for c, col := range columns {
		if c > 0 {
			result = append(result, ' ')
		}
		result = append(result, col...)
	}

	return string(result)
}
