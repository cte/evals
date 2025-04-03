package forth

import (
	"fmt"
	"strconv"
	"strings"
)

func Forth(input []string) ([]int, error) {
	stack := []int{}
	dict := map[string][]string{}

	for _, line := range input {
		tokens := tokenize(line)
		if len(tokens) == 0 {
			continue
		}
		if tokens[0] == ":" {
			if len(tokens) < 4 || tokens[len(tokens)-1] != ";" {
				return nil, fmt.Errorf("invalid definition")
			}
			word := strings.ToLower(tokens[1])
			if _, err := strconv.Atoi(word); err == nil {
				return nil, fmt.Errorf("illegal operation")
			}
			def := []string{}
			for _, tok := range tokens[2 : len(tokens)-1] {
				def = append(def, tok)
			}
			dict[word] = def
			continue
		}

		var err error
		stack, err = eval(tokens, stack, dict)
		if err != nil {
			return nil, err
		}
	}

	return stack, nil
}

func tokenize(line string) []string {
	parts := strings.Fields(line)
	for i, p := range parts {
		parts[i] = strings.ToLower(p)
	}
	return parts
}

func eval(tokens []string, stack []int, dict map[string][]string) ([]int, error) {
	queue := append([]string{}, tokens...)

	for len(queue) > 0 {
		token := queue[0]
		queue = queue[1:]

		switch token {
		case "+", "-", "*", "/", "dup", "drop", "swap", "over":
			switch token {
			case "+":
				if len(stack) < 2 {
					return nil, fmt.Errorf("only one value on the stack")
				}
				a, b := stack[len(stack)-2], stack[len(stack)-1]
				stack = stack[:len(stack)-2]
				stack = append(stack, a+b)
			case "-":
				if len(stack) < 2 {
					return nil, fmt.Errorf("only one value on the stack")
				}
				a, b := stack[len(stack)-2], stack[len(stack)-1]
				stack = stack[:len(stack)-2]
				stack = append(stack, a-b)
			case "*":
				if len(stack) < 2 {
					return nil, fmt.Errorf("only one value on the stack")
				}
				a, b := stack[len(stack)-2], stack[len(stack)-1]
				stack = stack[:len(stack)-2]
				stack = append(stack, a*b)
			case "/":
				if len(stack) < 2 {
					return nil, fmt.Errorf("only one value on the stack")
				}
				a, b := stack[len(stack)-2], stack[len(stack)-1]
				if b == 0 {
					return nil, fmt.Errorf("divide by zero")
				}
				stack = stack[:len(stack)-2]
				stack = append(stack, a/b)
			case "dup":
				if len(stack) < 1 {
					return nil, fmt.Errorf("empty stack")
				}
				stack = append(stack, stack[len(stack)-1])
			case "drop":
				if len(stack) < 1 {
					return nil, fmt.Errorf("empty stack")
				}
				stack = stack[:len(stack)-1]
			case "swap":
				if len(stack) < 2 {
					if len(stack) == 0 {
						return nil, fmt.Errorf("empty stack")
					}
					return nil, fmt.Errorf("only one value on the stack")
				}
				stack[len(stack)-1], stack[len(stack)-2] = stack[len(stack)-2], stack[len(stack)-1]
			case "over":
				if len(stack) < 2 {
					if len(stack) == 0 {
						return nil, fmt.Errorf("empty stack")
					}
					return nil, fmt.Errorf("only one value on the stack")
				}
				stack = append(stack, stack[len(stack)-2])
			}
		default:
			val, err := strconv.Atoi(token)
			if err == nil {
				stack = append(stack, val)
				continue
			}
			if def, ok := dict[token]; ok {
				queue = append(def, queue...)
				continue
			}
			return nil, fmt.Errorf("undefined operation")
		}
	}

	return stack, nil
}
