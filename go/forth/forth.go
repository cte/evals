package forth

import (
	"errors"
	"fmt"
	"strconv"
	"strings"
)

// Define errors
var (
	ErrDivisionByZero = errors.New("division by zero")
	ErrStackUnderflow = errors.New("stack underflow")
	ErrUnknownWord    = errors.New("unknown word")
	ErrInvalidWord    = errors.New("invalid word definition")
)

// forthState holds the stack and custom word definitions.
type forthState struct {
	stack       []int
	definitions map[string][]string
	defining    bool
	currentDef  []string
	currentName string
}

// newForthState creates a new Forth state.
func newForthState() *forthState {
	// Initialize built-in definitions (although handled directly in eval for performance)
	defs := make(map[string][]string)
	return &forthState{
		stack:       make([]int, 0),
		definitions: defs,
		defining:    false,
	}
}

// push adds an element to the stack.
func (s *forthState) push(val int) {
	s.stack = append(s.stack, val)
}

// pop removes and returns the top element from the stack.
func (s *forthState) pop() (int, error) {
	if len(s.stack) == 0 {
		return 0, ErrStackUnderflow
	}
	val := s.stack[len(s.stack)-1]
	s.stack = s.stack[:len(s.stack)-1]
	return val, nil
}

// eval evaluates a sequence of words iteratively.
func (s *forthState) eval(initialWords []string) error {
	// Use a slice as a stack for words to process. Start with the initial words.
	// Create a copy to avoid modifying the original slice from the caller (e.g., definitions map)
	wordsToProcess := make([]string, len(initialWords))
	copy(wordsToProcess, initialWords)

	for len(wordsToProcess) > 0 {
		// Pop the next word from the front
		word := wordsToProcess[0]
		wordsToProcess = wordsToProcess[1:] // Consume the word

		lowerWord := strings.ToLower(word)

		if s.defining {
			if lowerWord == ";" {
				if s.currentName == "" { // Name must be set after ':'
					s.defining = false // Reset state
					s.currentName = ""
					s.currentDef = nil
					return ErrInvalidWord
				}
				// Check for redefinition of numbers - not allowed
				if _, err := strconv.Atoi(s.currentName); err == nil {
					s.defining = false // Reset state
					s.currentName = ""
					s.currentDef = nil
					return ErrInvalidWord
				}
				// Store the definition
				s.definitions[s.currentName] = s.currentDef
				s.defining = false
				s.currentName = ""
				s.currentDef = nil
			} else if s.currentName == "" { // This is the word name after ':'
				// Check if the word name is a number - not allowed
				if _, err := strconv.Atoi(lowerWord); err == nil {
					s.defining = false // Reset state
					return ErrInvalidWord
				}
				s.currentName = lowerWord // Store definition name in lower case
			} else { // This is part of the definition body
				s.currentDef = append(s.currentDef, word) // Store original case word in definition
			}
			continue // Move to the next word in the input sequence
		}

		// Try parsing as a number
		if val, err := strconv.Atoi(word); err == nil {
			s.push(val)
			continue
		}

		// Handle built-in words and custom definitions
		switch lowerWord {
		case ":":
			if s.defining {
				return ErrInvalidWord // Cannot nest definitions
			}
			s.defining = true
			s.currentName = "" // Reset, will be set by the next word
			s.currentDef = make([]string, 0)
		case "+":
			if len(s.stack) < 2 { return ErrStackUnderflow }
			b, _ := s.pop()
			a, _ := s.pop()
			s.push(a + b)
		case "-":
			if len(s.stack) < 2 { return ErrStackUnderflow }
			b, _ := s.pop()
			a, _ := s.pop()
			s.push(a - b)
		case "*":
			if len(s.stack) < 2 { return ErrStackUnderflow }
			b, _ := s.pop()
			a, _ := s.pop()
			s.push(a * b)
		case "/":
			if len(s.stack) < 2 { return ErrStackUnderflow }
			b, _ := s.pop()
			a, _ := s.pop()
			if b == 0 { return ErrDivisionByZero }
			s.push(a / b)
		case "dup":
			if len(s.stack) < 1 { return ErrStackUnderflow }
			val := s.stack[len(s.stack)-1]
			s.push(val)
		case "drop":
			if _, err := s.pop(); err != nil { return err }
		case "swap":
			if len(s.stack) < 2 { return ErrStackUnderflow }
			b, _ := s.pop()
			a, _ := s.pop()
			s.push(b)
			s.push(a)
		case "over":
			if len(s.stack) < 2 { return ErrStackUnderflow }
			a := s.stack[len(s.stack)-2]
			s.push(a)
		default:
			// Check if it's a custom defined word
			if def, ok := s.definitions[lowerWord]; ok {
				// Prepend the definition's words to the list of words to process.
				// Make a copy of the definition slice to avoid modification issues if the definition is modified later.
				defCopy := make([]string, len(def))
				copy(defCopy, def)
				wordsToProcess = append(defCopy, wordsToProcess...) // Prepend definition words
			} else {
				return fmt.Errorf("%w: %s", ErrUnknownWord, word)
			}
		}
	}
	return nil
}

// Forth is the main entry point for the Forth evaluator.
// It takes a slice of strings, where each string is a line of Forth code.
func Forth(input []string) ([]int, error) {
	state := newForthState()

	for _, line := range input {
		// Preprocess line: handle definitions spanning multiple lines?
		// The tests seem to imply definitions are per line or handled correctly by sequential processing.
		// Let's assume definitions fit within the `eval` logic for now.
		words := strings.Fields(line)
		if err := state.eval(words); err != nil {
			// If an error occurs during evaluation, return immediately.
			return nil, err
		}
	}

	// After processing all lines, check if we are still in defining mode (missing ';')
	if state.defining {
		return nil, ErrInvalidWord // Indicate an unterminated definition
	}

	// Return the final state of the stack
	return state.stack, nil
}
