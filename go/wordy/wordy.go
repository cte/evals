package wordy

import (
	"strconv"
	"strings"
)

func Answer(question string) (int, bool) {
	// Check prefix and suffix
	if !strings.HasPrefix(question, "What is") || !strings.HasSuffix(question, "?") {
		return 0, false
	}

	// Remove prefix and suffix, trim whitespace
	trimmedQuestion := strings.TrimSpace(strings.TrimSuffix(strings.TrimPrefix(question, "What is"), "?"))

	// Handle case with just a number
	if num, err := strconv.Atoi(trimmedQuestion); err == nil {
		return num, true
	}

	// Replace "multiplied by" and "divided by" for easier splitting
	trimmedQuestion = strings.ReplaceAll(trimmedQuestion, "multiplied by", "multiplied")
	trimmedQuestion = strings.ReplaceAll(trimmedQuestion, "divided by", "divided")

	parts := strings.Fields(trimmedQuestion)

	if len(parts) == 0 {
		return 0, false // Empty question after trimming
	}

	// First part must be a number
	result, err := strconv.Atoi(parts[0])
	if err != nil {
		return 0, false // Invalid start
	}

	// Process remaining parts in pairs (operator, number)
	i := 1
	for i < len(parts) {
		// Expecting an operator
		if i+1 >= len(parts) {
			return 0, false // Operator without a number following
		}
		operator := parts[i]
		operandStr := parts[i+1]

		operand, err := strconv.Atoi(operandStr)
		if err != nil {
			// Check if the "operand" was actually another operator (invalid syntax)
			switch operandStr {
			case "plus", "minus", "multiplied", "divided":
				return 0, false
			default:
				// Could be an unknown word or invalid number
				return 0, false
			}
		}

		switch operator {
		case "plus":
			result += operand
		case "minus":
			result -= operand
		case "multiplied":
			result *= operand
		case "divided":
			if operand == 0 {
				return 0, false // Division by zero is undefined/error
			}
			result /= operand
		default:
			return 0, false // Unsupported operation or invalid syntax
		}
		i += 2 // Move to the next operator
	}

	return result, true
}
