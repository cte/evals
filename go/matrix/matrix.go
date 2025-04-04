package matrix

import (
	"errors"
	"strconv"
	"strings"
)

// Matrix represents a matrix of integers.
type Matrix [][]int

// New creates a new Matrix from a string representation.
func New(s string) (Matrix, error) {
	// Handle completely empty or whitespace-only input first
	if strings.TrimSpace(s) == "" {
		return Matrix{}, nil
	}

	lines := strings.Split(s, "\n")
	var matrixData [][]int
	var expectedCols int = -1
	var processedRowCount int = 0

	for _, line := range lines {
		trimmedLine := strings.TrimSpace(line)

		// If the line is empty after trimming, it's an error according to tests.
		// We already handled the case where the *entire* input 's' is whitespace.
		// Therefore, if we encounter an empty trimmedLine here, it must be part of a non-empty input 's',
		// meaning it's an invalid empty row (leading, middle, or trailing potentially from split).
		if trimmedLine == "" {
			return nil, errors.New("invalid input: empty row encountered")
		}

		fields := strings.Fields(trimmedLine)
		// Note: len(fields) will be > 0 here because trimmedLine is not empty.

		if processedRowCount == 0 { // First non-empty row determines dimensions
			expectedCols = len(fields)
		} else if len(fields) != expectedCols {
			return nil, errors.New("mismatched row lengths")
		}

		row := make([]int, expectedCols)
		for j, field := range fields {
			val, err := strconv.Atoi(field)
			if err != nil {
				// Use a more specific error message
				return nil, errors.New("invalid number format: " + err.Error())
			}
			row[j] = val
		}
		matrixData = append(matrixData, row)
		processedRowCount++
	}

	// If we reach here, matrixData should be valid and non-empty because
	// the empty input case was handled upfront, and errors return early.
	return Matrix(matrixData), nil
}

// Rows returns a copy of the matrix rows.
func (m Matrix) Rows() [][]int {
	if m == nil { // Should not happen if created via New
		return nil
	}
	if len(m) == 0 { // Handle empty matrix created by New("")
		return [][]int{}
	}
	rowsCopy := make([][]int, len(m))
	for i, row := range m {
		if row == nil { // Defensive check, should not happen with New
			return nil // Or indicate error
		}
		rowsCopy[i] = make([]int, len(row))
		copy(rowsCopy[i], row)
	}
	return rowsCopy
}

// Cols returns the matrix columns.
func (m Matrix) Cols() [][]int {
	if m == nil { // Should not happen
		return nil
	}
	if len(m) == 0 { // Handle empty matrix
		return [][]int{}
	}
	// New ensures rows are non-empty and rectangular, so len(m[0]) is safe
	if len(m[0]) == 0 { // Defensive check
	    return [][]int{} // Matrix with 0 columns
	}

	numRows := len(m)
	numCols := len(m[0])
	colsData := make([][]int, numCols)

	for j := 0; j < numCols; j++ {
		colsData[j] = make([]int, numRows)
		for i := 0; i < numRows; i++ {
			// Assuming rectangular matrix guaranteed by New
			colsData[j][i] = m[i][j]
		}
	}
	return colsData
}

// Set updates the value at the given row and column index.
func (m Matrix) Set(row, col, val int) bool {
	// Check for nil matrix or empty matrix first
	if m == nil || len(m) == 0 {
		return false
	}
	// Check row bounds
	if row < 0 || row >= len(m) {
		return false
	}
	// Check col bounds (use len(m[0]) assuming rectangularity from New)
	if len(m[0]) == 0 { // Defensive check for 0-column matrix
	    return false
	}
	if col < 0 || col >= len(m[0]) {
		return false
	}

	m[row][col] = val
	return true
}
