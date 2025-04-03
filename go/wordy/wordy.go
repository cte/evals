package wordy

import "strings"

func Answer(question string) (int, bool) {
	const prefix = "What is "
	const suffix = "?"

	if len(question) < len(prefix)+len(suffix) || question[:len(prefix)] != prefix || question[len(question)-1:] != suffix {
		return 0, false
	}

	// Trim prefix and suffix
	content := question[len(prefix) : len(question)-1]

	// Initial split by spaces
	rawTokens := strings.Fields(content)

	// Merge negative and positive numbers with sign
	tokens := []string{}
	i := 0
	for i < len(rawTokens) {
		token := rawTokens[i]
		if (token == "-" || token == "+") && i+1 < len(rawTokens) {
			// Merge sign with next token if it's a number
			next := rawTokens[i+1]
			if len(next) > 0 && (next[0] >= '0' && next[0] <= '9') {
				tokens = append(tokens, token+next)
				i += 2
				continue
			}
		}
		// Merge "multiplied" + "by"
		if token == "multiplied" && i+1 < len(rawTokens) && rawTokens[i+1] == "by" {
			tokens = append(tokens, "multiplied by")
			i += 2
			continue
		}
		// Merge "divided" + "by"
		if token == "divided" && i+1 < len(rawTokens) && rawTokens[i+1] == "by" {
			tokens = append(tokens, "divided by")
			i += 2
			continue
		}
		tokens = append(tokens, token)
		i++
	}

	if len(tokens) == 0 {
		return 0, false
	}

	// Parse first number
	result, ok := parseInt(tokens[0])
	if !ok {
		return 0, false
	}

	i = 1
	for i < len(tokens) {
		if i+1 >= len(tokens) {
			return 0, false // missing operand
		}
		op := tokens[i]
		nextNumStr := tokens[i+1]
		nextNum, ok := parseInt(nextNumStr)
		if !ok {
			return 0, false
		}

		switch op {
		case "plus":
			result += nextNum
		case "minus":
			result -= nextNum
		case "multiplied by":
			result *= nextNum
		case "divided by":
			if nextNum == 0 {
				return 0, false
			}
			result /= nextNum
		default:
			return 0, false
		}
		i += 2
	}

	return result, true
}

func parseInt(s string) (int, bool) {
	var sign int
	if len(s) > 0 && (s[0] == '-' || s[0] == '+') {
		if s[0] == '-' {
			sign = -1
		} else {
			sign = 1
		}
		s = s[1:]
	} else {
		sign = 1
	}
	if len(s) == 0 {
		return 0, false
	}
	n := 0
	for i := 0; i < len(s); i++ {
		if s[i] < '0' || s[i] > '9' {
			return 0, false
		}
		n = n*10 + int(s[i]-'0')
	}
	return sign * n, true
}
