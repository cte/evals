package kindergarten

import (
	"errors"
	"sort"
	"strings"
)

// Plant names corresponding to their codes.
var plantNames = map[rune]string{
	'V': "violets",
	'R': "radishes",
	'C': "clover",
	'G': "grass",
}

// Garden represents the kindergarten garden layout.
type Garden struct {
	children []string
	plants   map[string][]string
}

// NewGarden initializes a Garden based on the diagram and children list.
// The diagram string represents two rows of plants.
// Children are assigned plants alphabetically.
func NewGarden(diagram string, children []string) (*Garden, error) {
	// --- Input Validation ---

	// 1. Diagram format validation
	if !strings.HasPrefix(diagram, "\n") {
		return nil, errors.New("diagram must start with a newline")
	}
	diagram = diagram[1:] // Remove leading newline

	rows := strings.Split(diagram, "\n")
	if len(rows) != 2 {
		return nil, errors.New("diagram must have exactly two rows")
	}
	row1 := rows[0]
	row2 := rows[1]

	if len(row1) != len(row2) {
		return nil, errors.New("diagram rows must have the same length")
	}
	if len(row1)%2 != 0 {
		return nil, errors.New("diagram rows must have an even number of cups")
	}
	if len(row1) == 0 {
		return nil, errors.New("diagram cannot be empty")
	}

	// 2. Validate plant codes
	for _, r := range row1 + row2 {
		if _, ok := plantNames[r]; !ok {
			return nil, errors.New("invalid plant code in diagram")
		}
	}

	// 3. Children list validation
	if len(children) == 0 {
		return nil, errors.New("children list cannot be empty")
	}
	if len(row1)/2 != len(children) {
		return nil, errors.New("number of children does not match diagram size")
	}

	// Check for duplicate children names
	childSet := make(map[string]struct{})
	sortedChildren := make([]string, len(children))
	copy(sortedChildren, children)
	sort.Strings(sortedChildren) // Sort children alphabetically

	for _, child := range sortedChildren {
		if _, exists := childSet[child]; exists {
			return nil, errors.New("duplicate child name found")
		}
		childSet[child] = struct{}{}
	}

	// --- Garden Initialization ---
	g := &Garden{
		children: sortedChildren,
		plants:   make(map[string][]string),
	}

	// Assign plants to children
	for i, child := range g.children {
		cupIndex := i * 2
		childPlants := []string{
			plantNames[rune(row1[cupIndex])],
			plantNames[rune(row1[cupIndex+1])],
			plantNames[rune(row2[cupIndex])],
			plantNames[rune(row2[cupIndex+1])],
		}
		g.plants[child] = childPlants
	}

	return g, nil
}

// Plants returns the list of plants for a specific child.
// It returns the plants and true if the child is found, otherwise nil and false.
func (g *Garden) Plants(child string) ([]string, bool) {
	plants, ok := g.plants[child]
	if !ok {
		return nil, false // Child not found
	}
	// Return a copy to prevent external modification
	plantsCopy := make([]string, len(plants))
	copy(plantsCopy, plants)
	return plantsCopy, true
}
