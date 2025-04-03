package alphametics

import (
	"errors"
	"strings"
)

func Solve(puzzle string) (map[string]int, error) {
	// Remove spaces
	puzzle = strings.ReplaceAll(puzzle, " ", "")

	// Split into LHS and RHS
	parts := strings.Split(puzzle, "==")
	if len(parts) != 2 {
		return nil, errors.New("invalid puzzle format")
	}
	left := parts[0]
	right := parts[1]

	// Split LHS into terms
	terms := strings.Split(left, "+")

	// Collect unique letters and leading letters
	letterSet := make(map[rune]bool)
	leading := make(map[rune]bool)
	words := append(terms, right)
	for _, word := range words {
		runes := []rune(word)
		for _, r := range runes {
			letterSet[r] = true
		}
		leading[runes[0]] = true
	}

	letters := make([]rune, 0, len(letterSet))
	for r := range letterSet {
		letters = append(letters, r)
	}
	if len(letters) > 10 {
		return nil, errors.New("too many unique letters")
	}

	perm := make([]int, len(letters))
	used := make([]bool, 10)

	var solve func(pos int) (map[string]int, bool)
	solve = func(pos int) (map[string]int, bool) {
		if pos == len(letters) {
			assign := make(map[rune]int)
			for i, r := range letters {
				assign[r] = perm[i]
			}
			// Check leading zeros
			for _, word := range words {
				if assign[rune(word[0])] == 0 {
					return nil, false
				}
			}
			// Compute sum of LHS
			sum := 0
			for _, term := range terms {
				val := 0
				for _, r := range term {
					val = val*10 + assign[rune(r)]
				}
				sum += val
			}
			// Compute RHS
			target := 0
			for _, r := range right {
				target = target*10 + assign[rune(r)]
			}
			if sum == target {
				result := make(map[string]int)
				for k, v := range assign {
					result[string(k)] = v
				}
				return result, true
			}
			return nil, false
		}

		for d := 0; d <= 9; d++ {
			if used[d] {
				continue
			}
			// Leading zero check during assignment
			if d == 0 && leading[letters[pos]] {
				continue
			}
			used[d] = true
			perm[pos] = d
			if res, ok := solve(pos + 1); ok {
				return res, true
			}
			used[d] = false
		}
		return nil, false
	}

	if res, ok := solve(0); ok {
		return res, nil
	}
	return nil, errors.New("no solution")
}
