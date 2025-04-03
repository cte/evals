package matrix

import (
	"errors"
	"strconv"
	"strings"
)

// Matrix represents a 2D matrix of integers
type Matrix interface {
	Cols() [][]int
	Rows() [][]int
	Set(row, col, val int) bool
}

type matrixImpl struct {
	data [][]int
}

// New parses a string into a Matrix
func New(s string) (Matrix, error) {
	if len(strings.TrimSpace(s)) == 0 {
		return &matrixImpl{[][]int{}}, nil
	}

	lines := strings.Split(s, "\n")
	matrix := make([][]int, len(lines))
	var cols int

	for i, line := range lines {
		line = strings.TrimSpace(line)
		if line == "" {
			return nil, errors.New("empty row detected")
		}
		fields := strings.Fields(line)
		if i == 0 {
			cols = len(fields)
			if cols == 0 {
				return nil, errors.New("no columns in first row")
			}
		} else if len(fields) != cols {
			return nil, errors.New("uneven rows")
		}

		row := make([]int, cols)
		for j, f := range fields {
			val, err := strconv.ParseInt(f, 10, 64)
			if err != nil {
				return nil, err
			}
			if val > int64(int(^uint(0)>>1)) || val < int64(-int(^uint(0)>>1)-1) {
				return nil, errors.New("int64 overflow")
			}
			row[j] = int(val)
		}
		matrix[i] = row
	}

	return &matrixImpl{matrix}, nil
}

// Cols returns a copy of the columns of the matrix
func (m *matrixImpl) Cols() [][]int {
	if len(m.data) == 0 {
		return [][]int{}
	}
	rows := len(m.data)
	cols := len(m.data[0])
	result := make([][]int, cols)
	for c := 0; c < cols; c++ {
		col := make([]int, rows)
		for r := 0; r < rows; r++ {
			col[r] = m.data[r][c]
		}
		result[c] = col
	}
	return result
}

// Rows returns a copy of the rows of the matrix
func (m *matrixImpl) Rows() [][]int {
	result := make([][]int, len(m.data))
	for i, row := range m.data {
		newRow := make([]int, len(row))
		copy(newRow, row)
		result[i] = newRow
	}
	return result
}

// Set sets the value at (row, col) if valid, returns true if successful
func (m *matrixImpl) Set(row, col, val int) bool {
	if row < 0 || row >= len(m.data) {
		return false
	}
	if col < 0 || col >= len(m.data[0]) {
		return false
	}
	m.data[row][col] = val
	return true
}
