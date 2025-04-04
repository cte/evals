package pov

import "sort" // Import sort for String() method consistency

// Tree represents a node in a tree graph.
type Tree struct {
	value    string
	children []*Tree
	parent   *Tree // Adding parent pointer to facilitate reparenting
}

// New creates and returns a new Tree with the given root value and children.
func New(value string, children ...*Tree) *Tree {
	tr := &Tree{
		value:    value,
		children: children,
	}
	// Set the parent pointer for each child
	for _, child := range children {
		if child != nil { // Ensure child is not nil before setting parent
			child.parent = tr
		}
	}
	return tr
}

// Value returns the value at the root of a tree.
func (tr *Tree) Value() string {
	if tr == nil {
		return "" // Consistent with potential test expectations for nil tree
	}
	return tr.value
}

// Children returns a slice containing the children of a tree.
func (tr *Tree) Children() []*Tree {
	if tr == nil {
		return nil
	}
	// Return a copy to prevent external modification? Let's stick to direct slice for now.
	// If tests fail due to modification of returned slice, create a copy here.
	childrenCopy := make([]*Tree, len(tr.children))
	copy(childrenCopy, tr.children)
	return childrenCopy // Returning a copy is safer
}

// String describes a tree in a compact S-expression format.
// Added sorting for deterministic output.
func (tr *Tree) String() string {
	if tr == nil {
		return "nil"
	}
	result := tr.Value()
	children := tr.Children() // Use the method to get children
	if len(children) == 0 {
		return result
	}
	// Sort children by value for consistent string representation
	sort.Slice(children, func(i, j int) bool {
		// Handle potential nil children if they can occur
		if children[i] == nil { return true }
		if children[j] == nil { return false }
		return children[i].Value() < children[j].Value()
	})
	result = "(" + result
	for _, ch := range children {
		result += " " + ch.String()
	}
	result += ")"
	return result
}


// --- Helper Functions ---

// findNodeAndPath uses BFS to find a node by value and returns the node
// and the path (list of nodes) from the given 'tr' (assumed root for this search) to that node.
func findNodeAndPath(tr *Tree, value string) (*Tree, []*Tree) {
	if tr == nil {
		return nil, nil
	}

	queue := []*Tree{tr}
	// parentMap stores the parent of each node *in the context of this BFS traversal*
	parentMap := make(map[*Tree]*Tree)
	parentMap[tr] = nil // Mark root's parent as nil for path reconstruction termination

	visited := make(map[*Tree]bool)
	visited[tr] = true

	var targetNode *Tree = nil

	// BFS traversal
	for len(queue) > 0 {
		current := queue[0]
		queue = queue[1:]

		if current.value == value {
			targetNode = current
			break // Found the target node
		}

		// Explore children (use the Children() method which returns a safe copy)
		for _, child := range current.Children() { // Use Children() method
			if child != nil && !visited[child] {
				visited[child] = true
				parentMap[child] = current // Record parent for path reconstruction
				queue = append(queue, child)
			}
		}
	}

	if targetNode == nil {
		return nil, nil // Node not found
	}

	// Reconstruct the path from targetNode back to the root (tr)
	path := []*Tree{}
	curr := targetNode
	for curr != nil {
		path = append(path, curr)
		// Move up using the parentMap created during BFS
		curr = parentMap[curr]
	}

	// Reverse the path to be root -> targetNode
	for i, j := 0, len(path)-1; i < j; i, j = i+1, j-1 {
		path[i], path[j] = path[j], path[i]
	}

	return targetNode, path
}

// findNodeAnywhere uses BFS starting from startNode and explores both children and parent pointers
// to find a node anywhere in the connected component.
func findNodeAnywhere(startNode *Tree, value string) *Tree {
    if startNode == nil {
        return nil
    }
    queue := []*Tree{startNode}
    visited := make(map[*Tree]bool)
    visited[startNode] = true

    for len(queue) > 0 {
        current := queue[0]
        queue = queue[1:]

        if current.value == value {
            return current
        }

        // Explore children (using direct access for internal traversal)
        for _, child := range current.children {
            if child != nil && !visited[child] {
                visited[child] = true
                queue = append(queue, child)
            }
        }
        // Explore parent
        if current.parent != nil && !visited[current.parent] {
            visited[current.parent] = true
            queue = append(queue, current.parent)
        }
    }
    return nil // Node not found in the connected component
}


// --- POV problem-specific functions ---

// FromPov returns the pov from the node specified in the argument by modifying the tree structure.
func (tr *Tree) FromPov(from string) *Tree {
	// Find the target node anywhere in the tree structure connected to tr
	targetNode := findNodeAnywhere(tr, from)
	if targetNode == nil {
		return nil // Node not found
	}

	// Reparenting logic: reverse path from targetNode up towards the original root/boundaries.
	var prev *Tree = nil // Represents the new child (previously the parent)
	curr := targetNode

	for curr != nil {
		originalParent := curr.parent // Store original parent before modification

		// 1. Set current node's parent pointer to the node processed in the previous iteration (its new parent)
		curr.parent = prev

		// 2. Modify current node's children list:
		newChildren := []*Tree{}
		// Keep all original children *except* the one that is becoming the new parent (`prev`)
		for _, child := range curr.children {
			if child != prev {
				newChildren = append(newChildren, child)
			}
		}
		// Add the original parent as a child, *if* it exists
		if originalParent != nil {
			// Check if originalParent is already in newChildren (shouldn't be if prev logic is correct)
			isAlreadyChild := false
			for _, c := range newChildren {
				if c == originalParent {
					isAlreadyChild = true
					break
				}
			}
			if !isAlreadyChild {
				newChildren = append(newChildren, originalParent)
			}
		}
		curr.children = newChildren

		// Move up the original path: current becomes previous, original parent becomes current
		prev = curr
		curr = originalParent
	}

	// targetNode is now the root of the reparented tree
	if targetNode != nil {
		targetNode.parent = nil // Ensure the new root's parent is explicitly nil
	}
	return targetNode
}


// PathTo returns the shortest path between two nodes in the tree using LCA. Does not modify the tree.
func (tr *Tree) PathTo(from, to string) []string {
	if from == to {
		// Check if the node exists before returning path of length 1
		node, _ := findNodeAndPath(tr, from) // Use findNodeAndPath which searches downwards from tr
		if node != nil {
			return []string{from}
		} else {
			// If not found downwards, it might still exist elsewhere if tr wasn't the absolute root.
			// However, PathTo is usually called on the root. Let's assume findNodeAndPath is sufficient.
			return nil // Node doesn't exist or isn't reachable downwards from tr
		}
	}

	// Find nodes and paths from the root (tr)
	fromNode, pathFromRootToFrom := findNodeAndPath(tr, from)
	if fromNode == nil {
		return nil // 'from' node not found downwards from tr
	}

	toNode, pathFromRootToTo := findNodeAndPath(tr, to)
	if toNode == nil {
		return nil // 'to' node not found downwards from tr
	}

	// Find the Lowest Common Ancestor (LCA)
	lcaIndex := -1
	maxLength := len(pathFromRootToFrom)
	if len(pathFromRootToTo) < maxLength {
		maxLength = len(pathFromRootToTo)
	}
	for i := 0; i < maxLength; i++ {
		if pathFromRootToFrom[i] == pathFromRootToTo[i] {
			lcaIndex = i
		} else {
			break // Divergence point found
		}
	}

	if lcaIndex == -1 {
		// This implies tr itself wasn't a common ancestor, which is unexpected.
		return nil // Error or unexpected tree structure
	}

	// Construct the path: from -> ... -> LCA -> ... -> to
	pathResult := []string{}

	// Part 1: Path from 'from' up to LCA
	// Iterate backwards on pathFromRootToFrom from 'fromNode' down to the node *at* LCA
	for i := len(pathFromRootToFrom) - 1; i >= lcaIndex; i-- {
		pathResult = append(pathResult, pathFromRootToFrom[i].value)
	}

	// Part 2: Path from LCA (exclusive) down to 'to'
	// Iterate forwards on pathFromRootToTo from the node *after* LCA to 'toNode'
	for i := lcaIndex + 1; i < len(pathFromRootToTo); i++ {
		pathResult = append(pathResult, pathFromRootToTo[i].value)
	}

	return pathResult
}
