package wordsearch

import "fmt"

func Solve(words []string, puzzle []string) (map[string][2][2]int, error) {
	height := len(puzzle)
	if height == 0 {
		return nil, nil
	}
	width := len(puzzle[0])

	directions := [8][2]int{
		{1, 0},   // right
		{-1, 0},  // left
		{0, 1},   // down
		{0, -1},  // up
		{1, 1},   // down-right
		{-1, -1}, // up-left
		{1, -1},  // up-right
		{-1, 1},  // down-left
	}

	result := make(map[string][2][2]int)
	allFound := true

	for _, word := range words {
		found := false
		wordLen := len(word)

		for y := 0; y < height && !found; y++ {
			for x := 0; x < width && !found; x++ {
				for _, dir := range directions {
					dx, dy := dir[0], dir[1]
					endX := x + (wordLen-1)*dx
					endY := y + (wordLen-1)*dy

					if endX < 0 || endX >= width || endY < 0 || endY >= height {
						continue
					}

					match := true
					for i := 0; i < wordLen; i++ {
						nx := x + i*dx
						ny := y + i*dy
						if puzzle[ny][nx] != word[i] {
							match = false
							break
						}
					}

					if match {
						result[word] = [2][2]int{{x, y}, {endX, endY}}
						found = true
						break
					}
				}
			}
		}

		if !found {
			result[word] = [2][2]int{{-1, -1}, {-1, -1}}
			allFound = false
		}
	}

	if !allFound {
		return result, fmt.Errorf("one or more words not found")
	}
	return result, nil
}
