package wordsearch

import "errors"

// Solve finds words in a puzzle grid.
// words: A list of words to search for.
// puzzle: A slice of strings representing the grid.
// Returns a map where keys are found words and values are their start/end coordinates {{startX, startY}, {endX, endY}},
// and an error if any word is not found. The coordinates are 0-indexed.
func Solve(words []string, puzzle []string) (map[string][2][2]int, error) {
	if len(puzzle) == 0 {
        // Handle empty puzzle based on test expectations. Return error or empty map?
        // Let's return an error for an empty puzzle if words are expected.
        if len(words) > 0 {
		    return nil, errors.New("puzzle cannot be empty when words are provided")
        }
        // If no words are provided, an empty map is fine.
        return make(map[string][2][2]int), nil
	}
	height := len(puzzle)
    if height == 0 { // Should be caught by len(puzzle) == 0, but good practice
        return make(map[string][2][2]int), nil
    }
	width := len(puzzle[0]) // Assuming a rectangular puzzle
    if width == 0 {
        // Handle empty rows
        if len(words) > 0 {
            return nil, errors.New("puzzle rows cannot be empty when words are provided")
        }
        return make(map[string][2][2]int), nil
    }

	results := make(map[string][2][2]int)


	// Define the 8 directions (dx, dy)
	directions := [][2]int{
		{0, 1},   // Down
		{0, -1},  // Up
		{1, 0},   // Right
		{-1, 0},  // Left
		{1, 1},   // Down-Right
		{1, -1},  // Up-Right
		{-1, 1},  // Down-Left
		{-1, -1}, // Up-Left
	}

	wordsToFind := make(map[string]bool)
	for _, w := range words {
		if len(w) > 0 { // Ignore empty words in the input list
			wordsToFind[w] = true
		}
	}
    
    if len(wordsToFind) == 0 { // If only empty words were provided
        return make(map[string][2][2]int), nil
    }


	for y := 0; y < height; y++ {
		for x := 0; x < width; x++ {
			for _, word := range words {
                // Skip if word already found or is empty
				if _, found := results[word]; found || len(word) == 0 {
					continue
				}

				if puzzle[y][x] == word[0] {
					// Potential start found, check all directions
					for _, dir := range directions {
						dx, dy := dir[0], dir[1]
						endX, endY := x+(len(word)-1)*dx, y+(len(word)-1)*dy

						// Check if the word fits within bounds in this direction
						if endX >= 0 && endX < width && endY >= 0 && endY < height {
							match := true
							for i := 1; i < len(word); i++ {
								checkX, checkY := x+i*dx, y+i*dy
								if puzzle[checkY][checkX] != word[i] {
									match = false
									break
								}
							}

							if match {
								// Word found! Store coordinates (0-indexed)
								// Start: {x, y}, End: {endX, endY}
								results[word] = [2][2]int{{x, y}, {endX, endY}}
                                // Optimization: If all words are found, we can exit early
                                if len(results) == len(wordsToFind) {
                                    return results, nil
                                }
								// Since we found this word starting at (x,y) in this direction,
                                // break the direction loop for this word at this starting cell.
                                // We only need the *first* occurrence as per instructions.
                                // However, we need to continue searching for *other* words.
                                break 
							}
						}
					}
				}
			}
            // Optimization check after checking all words for a given cell
            if len(results) == len(wordsToFind) {
                 return results, nil
            }
		}
	}

    // After checking the entire grid, verify if all requested words were found
    if len(results) != len(wordsToFind) {
         // The tests expect an error if not all words are found.
         return nil, errors.New("not all words found in puzzle")
    }

	return results, nil
}
