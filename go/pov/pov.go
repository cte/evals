package pov

type Tree struct {
	value    string
	children []*Tree
}

// New creates and returns a new Tree with the given root value and children.
func New(value string, children ...*Tree) *Tree {
	return &Tree{
		value:    value,
		children: children,
	}
}

// Value returns the value at the root of a tree.
func (tr *Tree) Value() string {
	return tr.value
}

// Children returns a slice containing the children of a tree.
// There is no need to sort the elements in the result slice,
// they can be in any order.
func (tr *Tree) Children() []*Tree {
	return tr.children
}

// String describes a tree in a compact S-expression format.
// This helps to make test outputs more readable.
// Feel free to adapt this method as you see fit.
func (tr *Tree) String() string {
	if tr == nil {
		return "nil"
	}
	result := tr.Value()
	if len(tr.Children()) == 0 {
		return result
	}
	for _, ch := range tr.Children() {
		result += " " + ch.String()
	}
	return "(" + result + ")"
}
func (tr *Tree) PathTo(from, to string) []string {
	newRoot := tr.FromPov(from)
	if newRoot == nil {
		return nil
	}
	path := findPath(newRoot, to)
	if path == nil {
		return nil
	}
	result := make([]string, len(path))
	for i, node := range path {
		result[i] = node.value
	}
	return result
}

func (tr *Tree) FromPov(target string) *Tree {
	newRoot, found := rerootHelper(tr, target)
	if !found {
		return nil
	}
	return newRoot
}

func rerootHelper(node *Tree, target string) (*Tree, bool) {
	if node == nil {
		return nil, false
	}
	if node.value == target {
		// Clone the subtree rooted at target
		return &Tree{
			value:    node.value,
			children: cloneChildren(node.children),
		}, true
	}
	for _, child := range node.children {
		newChild, found := rerootHelper(child, target)
		if found {
			// Remove the child leading to the target
			newSiblings := []*Tree{}
			for _, c := range node.children {
				if c != child {
					newSiblings = append(newSiblings, c)
				}
			}
			// Attach the current node as a child of the rerooted subtree
			newChild.children = append(newChild.children, &Tree{
				value:    node.value,
				children: newSiblings,
			})
			// Return the rerooted subtree (which has the target as root)
			return newChild, true
		}
	}
	return nil, false
}

func cloneChildren(children []*Tree) []*Tree {
	clones := make([]*Tree, len(children))
	for i, child := range children {
		clones[i] = &Tree{
			value:    child.value,
			children: cloneChildren(child.children),
		}
	}
	return clones
}

// findPath returns the path from root to the node with value target
func findPath(root *Tree, target string) []*Tree {
	if root == nil {
		return nil
	}
	if root.value == target {
		return []*Tree{root}
	}
	for _, child := range root.children {
		if path := findPath(child, target); path != nil {
			return append([]*Tree{root}, path...)
		}
	}
	return nil
}

// POV problem-specific functions

// FromPov returns the pov from the node specified in the argument.
