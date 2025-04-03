package connect

func ResultOf(lines []string) (string, error) {
	board := parseBoard(lines)

	if hasPath(board, 'X') {
		return "X", nil
	}
	if hasPath(board, 'O') {
		return "O", nil
	}
	return "", nil
}

func parseBoard(lines []string) [][]rune {
	board := make([][]rune, len(lines))
	for i, line := range lines {
		row := []rune{}
		for _, ch := range line {
			if ch != ' ' {
				row = append(row, ch)
			}
		}
		board[i] = row
	}
	return board
}

func hasPath(board [][]rune, player rune) bool {
	rows := len(board)
	visited := make([][]bool, rows)
	for i := range visited {
		visited[i] = make([]bool, len(board[i]))
	}

	var stack [][2]int

	if player == 'X' {
		// Start from left edge
		for r := 0; r < rows; r++ {
			if len(board[r]) > 0 && board[r][0] == player {
				stack = append(stack, [2]int{r, 0})
				visited[r][0] = true
			}
		}
	} else {
		// Start from top edge
		if rows > 0 {
			for c := 0; c < len(board[0]); c++ {
				if board[0][c] == player {
					stack = append(stack, [2]int{0, c})
					visited[0][c] = true
				}
			}
		}
	}

	directions := [][2]int{
		{-1, 0},  // up
		{-1, 1},  // up-right
		{0, -1},  // left
		{0, 1},   // right
		{1, -1},  // down-left
		{1, 0},   // down
	}

	for len(stack) > 0 {
		pos := stack[len(stack)-1]
		stack = stack[:len(stack)-1]
		r, c := pos[0], pos[1]

		if player == 'X' && c == len(board[r])-1 {
			return true
		}
		if player == 'O' && r == rows-1 {
			return true
		}

		for _, d := range directions {
			nr, nc := r+d[0], c+d[1]
			if nr >= 0 && nr < rows &&
				nc >= 0 && nc < len(board[nr]) &&
				!visited[nr][nc] && board[nr][nc] == player {
				visited[nr][nc] = true
				stack = append(stack, [2]int{nr, nc})
			}
		}
	}

	return false
}
