package tree

import (
	"errors"
	"fmt"
	"sort"
)

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

// Build constructs a tree from a slice of records.
func Build(records []Record) (*Node, error) {
	if len(records) == 0 {
		return nil, nil
	}

	// Sort records by ID to make processing easier and allow validation checks.
	sort.Slice(records, func(i, j int) bool {
		return records[i].ID < records[j].ID
	})

	nodes := make([]*Node, len(records)) // Use a slice for direct ID lookup

	for i, r := range records {
		// Validation checks based on sorted order
		if r.ID != i {
			return nil, fmt.Errorf("non-continuous node ID: %d (expected %d)", r.ID, i)
		}
		if r.ID == 0 {
			if r.Parent != 0 {
				return nil, errors.New("root node cannot have a parent")
			}
		} else { // Non-root nodes
			if r.Parent >= r.ID {
				return nil, fmt.Errorf("node %d has invalid parent %d", r.ID, r.Parent)
			}
		}

		// Create the node
		nodes[i] = &Node{ID: r.ID}

		// Link to parent if not the root node
		if r.ID != 0 {
			parent := nodes[r.Parent]
			if parent == nil {
				// This case should theoretically not happen if IDs are continuous and Parent < ID
				return nil, fmt.Errorf("parent node %d not found for node %d", r.Parent, r.ID)
			}
			parent.Children = append(parent.Children, nodes[i])
		}
	}

	// Check if root node exists (nodes[0] should have been created)
	if nodes[0] == nil {
		// This case implies records were present but ID 0 was missing,
		// which should be caught by the non-continuous check.
		return nil, errors.New("no root node found")
	}

	return nodes[0], nil
}
