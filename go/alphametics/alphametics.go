package alphametics

import (
	"errors"
	"fmt"
	"math"
	"regexp"
)

// Solve solves the alphametics puzzle.
func Solve(puzzle string) (map[string]int, error) {
	// 1. Parse the puzzle
	re := regexp.MustCompile(`^([A-Z]+(\s*\+\s*[A-Z]+)*)\s*==\s*([A-Z]+)$`)
	matches := re.FindStringSubmatch(puzzle)
	if matches == nil {
		return nil, errors.New("invalid puzzle format")
	}

	leftSide := matches[1]
	resultWord := matches[3]
	operandWords := regexp.MustCompile(`\s*\+\s*`).Split(leftSide, -1)

	allWords := append(operandWords, resultWord)
	uniqueLetters := map[rune]struct{}{}
	leadingLetters := map[rune]struct{}{}

	for _, word := range allWords {
		if len(word) > 1 {
			leadingLetters[rune(word[0])] = struct{}{}
		} else if len(word) == 1 {
			// Single letter words are also leading letters if they are the only letter
			leadingLetters[rune(word[0])] = struct{}{}
		}
		for _, r := range word {
			uniqueLetters[r] = struct{}{}
		}
	}

	if len(uniqueLetters) > 10 {
		return nil, errors.New("too many unique letters")
	}

	letters := make([]rune, 0, len(uniqueLetters))
	for r := range uniqueLetters {
		letters = append(letters, r)
	}

	assignment := make(map[rune]int)
	usedDigits := make([]bool, 10)

	// 2. Backtracking solver
	var solveRecursive func(int) bool
	solveRecursive = func(letterIndex int) bool {
		if letterIndex == len(letters) {
			// Base case: all letters assigned, check the equation
			return checkEquation(assignment, operandWords, resultWord)
		}

		currentLetter := letters[letterIndex]
		_, isLeading := leadingLetters[currentLetter]

		for digit := 0; digit <= 9; digit++ {
			if usedDigits[digit] {
				continue // Digit already used
			}
			if isLeading && digit == 0 {
				continue // Leading letter cannot be 0
			}

			// Assign
			assignment[currentLetter] = digit
			usedDigits[digit] = true

			// Recurse
			if solveRecursive(letterIndex + 1) {
				return true // Solution found
			}

			// Backtrack
			usedDigits[digit] = false
			delete(assignment, currentLetter)
		}
		return false // No valid digit found for this letter
	}

	if solveRecursive(0) {
		// Convert result map[rune]int to map[string]int
		resultMap := make(map[string]int)
		for r, digit := range assignment {
			resultMap[string(r)] = digit
		}
		return resultMap, nil
	}

	return nil, errors.New("no solution found")
}

// checkEquation checks if the current assignment satisfies the puzzle's equation.
func checkEquation(assignment map[rune]int, operands []string, result string) bool {
	var sum int64
	for _, word := range operands {
		val, err := wordToValue(word, assignment)
		if err != nil {
			// This should not happen if all letters are assigned
			fmt.Println("Error converting operand:", err)
			return false
		}
		sum += val
	}

	resultVal, err := wordToValue(result, assignment)
	if err != nil {
		// This should not happen
		fmt.Println("Error converting result:", err)
		return false
	}

	return sum == resultVal
}

// wordToValue converts a word to its integer value based on the assignment.
func wordToValue(word string, assignment map[rune]int) (int64, error) {
	var value int64
	for i, r := range word {
		digit, ok := assignment[r]
		if !ok {
			return 0, fmt.Errorf("letter %c not assigned", r)
		}
		// Check leading zero again just in case (although handled in main recursion)
		if i == 0 && len(word) > 1 && digit == 0 {
			return 0, errors.New("leading zero detected during value conversion")
		}
		power := len(word) - 1 - i
		value += int64(digit) * int64(math.Pow10(power))
	}
	return value, nil
}
