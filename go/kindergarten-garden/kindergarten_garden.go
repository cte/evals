package kindergarten

import (
	"fmt"
	"sort"
)

type Garden struct {
	studentPlants map[string][]string
}

// The diagram argument starts each row with a '\n'.  This allows Go's
// raw string literals to present diagrams in source code nicely as two
// rows flush left, for example,
//
//     diagram := `
//     VVCCGG
//     VVCCGG`

func NewGarden(diagram string, children []string) (*Garden, error) {
	plantMap := map[rune]string{
		'R': "radishes",
		'C': "clover",
		'G': "grass",
		'V': "violets",
	}

	// Validate children: no duplicates
	childSet := make(map[string]bool)
	for _, c := range children {
		if childSet[c] {
			return nil, fmt.Errorf("duplicate child name: %s", c)
		}
		childSet[c] = true
	}

	// Sort children alphabetically
	sortedChildren := make([]string, len(children))
	copy(sortedChildren, children)
	sort.Strings(sortedChildren)

	// Diagram must start with newline
	if len(diagram) == 0 || diagram[0] != '\n' {
		return nil, fmt.Errorf("diagram must start with newline")
	}

	// Split rows
	rows := []string{}
	for _, line := range splitLines(diagram) {
		if line != "" {
			rows = append(rows, line)
		}
	}
	if len(rows) != 2 {
		return nil, fmt.Errorf("diagram must have two rows")
	}

	row1, row2 := rows[0], rows[1]
	if len(row1) != len(row2) {
		return nil, fmt.Errorf("rows must be the same length")
	}
	if len(row1)%2 != 0 {
		return nil, fmt.Errorf("number of cups must be even")
	}
	if len(row1)/2 != len(children) {
		return nil, fmt.Errorf("number of children does not match diagram")
	}

	studentPlants := make(map[string][]string)
	for i, child := range sortedChildren {
		start := i * 2
		cups := []rune{rune(row1[start]), rune(row1[start+1]), rune(row2[start]), rune(row2[start+1])}
		plants := make([]string, 4)
		for j, cup := range cups {
			name, ok := plantMap[cup]
			if !ok {
				return nil, fmt.Errorf("invalid plant code: %c", cup)
			}
			plants[j] = name
		}
		studentPlants[child] = plants
	}

	return &Garden{studentPlants: studentPlants}, nil
}

// splitLines splits a string by newlines, compatible with \n and \r\n
func splitLines(s string) []string {
	var lines []string
	start := 0
	for i := 0; i < len(s); i++ {
		if s[i] == '\n' {
			lines = append(lines, s[start:i])
			start = i + 1
		}
	}
	if start <= len(s) {
		lines = append(lines, s[start:])
	}
	return lines
}

func (g *Garden) Plants(child string) ([]string, bool) {
	plants, ok := g.studentPlants[child]
	if !ok {
		return nil, false
	}
	return plants, true
}
