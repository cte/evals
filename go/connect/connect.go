package connect

import "strings"

type point struct{ r, c int }

// ResultOf determines the winner of a Hex game board.
func ResultOf(lines []string) (string, error) {
	board, rows, cols := parseBoardToMap(lines)
	if rows == 0 || cols == 0 {
		return "", nil // Empty board
	}

	// Check for 'O' win (top to bottom)
	visitedO := make(map[point]bool)
	for c := 0; c < cols; c++ {
		startPoint := point{0, c}
		if board[startPoint] == 'O' {
			if dfsMap(startPoint, 'O', board, visitedO, rows, cols, true) {
				return "O", nil
			}
		}
	}

	// Check for 'X' win (left to right)
	visitedX := make(map[point]bool)
	for r := 0; r < rows; r++ {
		startPoint := point{r, 0}
		if board[startPoint] == 'X' {
			if dfsMap(startPoint, 'X', board, visitedX, rows, cols, false) {
				return "X", nil
			}
		}
	}

	return "", nil // No winner
}

// parseBoardToMap converts the input lines into a map representation and returns dimensions.
func parseBoardToMap(lines []string) (map[point]rune, int, int) {
	board := make(map[point]rune)
	rows := len(lines)
	cols := 0
	for r, line := range lines {
		colIndex := 0
		for _, char := range line {
			if char != ' ' { // Ignore spaces
				p := point{r, colIndex}
				board[p] = char
				colIndex++
			}
		}
		if colIndex > cols {
			cols = colIndex // Track the maximum column index found
		}
	}
	// Ensure all points exist in the map, filling gaps with '.' if necessary
	// (Though the problem implies a full parallelogram)
	// Let's assume the input forms a consistent grid after removing spaces.
	return board, rows, cols
}

// dfsMap performs Depth First Search on the map representation.
func dfsMap(p point, player rune, board map[point]rune, visited map[point]bool, rows, cols int, isPlayerO bool) bool {
	// Check boundaries (implicit via map lookup), visited status, and player match
	if cell, exists := board[p]; !exists || visited[p] || cell != player {
		return false
	}
	visited[p] = true

	// Check win condition
	if isPlayerO {
		if p.r == rows-1 {
			return true // Player 'O' reached the bottom row
		}
	} else {
		if p.c == cols-1 {
			return true // Player 'X' reached the rightmost column
		}
	}

	// Explore neighbors recursively
	neighbors := getMapNeighbors(p)
	for _, n := range neighbors {
		// We don't need explicit boundary checks here because dfsMap handles non-existent points
		if dfsMap(n, player, board, visited, rows, cols, isPlayerO) {
			return true // Path found through this neighbor
		}
	}

	return false // No path found from this point
}

// getMapNeighbors returns potential neighbors for a point based on the hex grid structure.
func getMapNeighbors(p point) []point {
	return []point{
		{p.r, p.c - 1},     // Left
		{p.r, p.c + 1},     // Right
		{p.r - 1, p.c},     // Top-Left
		{p.r - 1, p.c + 1}, // Top-Right
		{p.r + 1, p.c},     // Bottom-Right
		{p.r + 1, p.c - 1}, // Bottom-Left
	}
}

// Helper function to print the board map (for debugging)
func printBoardMap(board map[point]rune, rows, cols int) {
	println("Board Map:")
	for r := 0; r < rows; r++ {
		print(strings.Repeat(" ", r)) // Indentation for visualization
		for c := 0; c < cols; c++ {
			p := point{r, c}
			if char, ok := board[p]; ok {
				print(string(char), " ")
			} else {
				print(". ") // Print '.' for empty spots if needed
			}
		}
		println()
	}
}
