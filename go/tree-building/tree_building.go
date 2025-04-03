package tree

import "fmt"

type Record struct {
	ID     int
	Parent int
	// feel free to add fields as you see fit
}

type Node struct {
	ID       int
	Children []*Node
	// feel free to add fields as you see fit
}

func Build(records []Record) (*Node, error) {
	if len(records) == 0 {
		return nil, nil
	}

	// Sort records by ID
	sorted := make([]Record, len(records))
	copy(sorted, records)
	// Simple insertion sort since input is small; for large input, use sort.Slice
	for i := 1; i < len(sorted); i++ {
		j := i
		for j > 0 && sorted[j-1].ID > sorted[j].ID {
			sorted[j-1], sorted[j] = sorted[j], sorted[j-1]
			j--
		}
	}

	nodes := make(map[int]*Node, len(sorted))

	for i, r := range sorted {
		// Check ID continuity
		if r.ID != i {
			return nil, fmt.Errorf("non-continuous IDs")
		}
		// Root node validation
		if r.ID == 0 {
			if r.Parent != 0 {
				return nil, fmt.Errorf("root node has parent")
			}
		} else {
			if r.ID <= r.Parent {
				return nil, fmt.Errorf("node %d has parent >= itself", r.ID)
			}
		}
		// Create node
		if _, exists := nodes[r.ID]; exists {
			return nil, fmt.Errorf("duplicate node ID %d", r.ID)
		}
		nodes[r.ID] = &Node{ID: r.ID}
	}

	var root *Node
	for _, r := range sorted {
		if r.ID == 0 {
			root = nodes[0]
			continue
		}
		parent, ok := nodes[r.Parent]
		if !ok {
			return nil, fmt.Errorf("parent %d not found for node %d", r.Parent, r.ID)
		}
		parent.Children = append(parent.Children, nodes[r.ID])
	}

	return root, nil
}
