// zipper.js

// Helper function for deep cloning tree nodes
const deepClone = (node) => {
  // Base case: handle null or non-object values
  if (node === null || typeof node !== 'object') {
    return node;
  }
  // Recursively clone left and right children
  const clonedNode = {
    value: node.value, // Copy the value
    left: deepClone(node.left),
    right: deepClone(node.right),
  };
  return clonedNode;
};

export class Zipper {
  // focus: the current node { value: V, left: Node | null, right: Node | null }
  // path: array of breadcrumbs, each storing parent info
  // breadcrumb: { direction: 'left' | 'right', parentValue: V, sibling: Node | null }
  constructor(focus, path = []) {
    // Store focus and path. Assume they are provided correctly.
    // Cloning happens in fromTree and modification methods to ensure immutability.
    this.focus = focus;
    this.path = path;
  }

  static fromTree(tree) {
    // Creates a zipper from a tree. The focus is the root node.
    // Returns null if the input tree is null or undefined.
    if (tree === null || typeof tree === 'undefined') {
       return null; // Or handle as per test expectations
    }
    // Deep clone the input tree to prevent modifying the original structure.
    return new Zipper(deepClone(tree), []);
  }

  toTree() {
    // Navigates up to the root and returns the entire tree structure.
    let currentZipper = this;
    // Keep moving up until we reach the root (path is empty).
    while (currentZipper.path.length > 0) {
      const parentZipper = currentZipper.up();
      // Safety check: up() should return a valid zipper if path is not empty.
      if (!parentZipper) {
         throw new Error("Internal error: Failed to navigate up during toTree().");
      }
      currentZipper = parentZipper;
    }
    // The focus of the root zipper is the complete tree.
    // Return a deep clone to ensure the internal state isn't exposed directly.
    return deepClone(currentZipper.focus);
  }

  value() {
    // Returns the value of the node currently in focus.
    // Check if focus exists, although constructor/navigation should ensure it.
    return this.focus ? this.focus.value : undefined;
  }

  left() {
    // Moves focus to the left child, if it exists. Returns null otherwise.
    if (!this.focus || !this.focus.left) {
      return null;
    }
    // Create a breadcrumb representing the step down to the left.
    const newPathEntry = {
      direction: 'left',
      parentValue: this.focus.value,
      sibling: this.focus.right, // The right child is the sibling when moving left.
    };
    // Return a new Zipper focusing on the left child, with the updated path.
    return new Zipper(this.focus.left, [newPathEntry, ...this.path]);
  }

  right() {
    // Moves focus to the right child, if it exists. Returns null otherwise.
    if (!this.focus || !this.focus.right) {
      return null;
    }
    // Create a breadcrumb representing the step down to the right.
    const newPathEntry = {
      direction: 'right',
      parentValue: this.focus.value,
      sibling: this.focus.left, // The left child is the sibling when moving right.
    };
    // Return a new Zipper focusing on the right child, with the updated path.
    return new Zipper(this.focus.right, [newPathEntry, ...this.path]);
  }

  up() {
    // Moves focus to the parent node. Returns null if at the root.
    if (this.path.length === 0) {
      return null; // Cannot move up from the root.
    }

    // Get the parent information from the first breadcrumb.
    const [parentInfo, ...restPath] = this.path;
    const { direction, parentValue, sibling } = parentInfo;

    // Reconstruct the parent node using the current focus and the stored sibling.
    let parentNode;
    if (direction === 'left') {
      // If we came from the left, the current focus is the left child.
      parentNode = { value: parentValue, left: this.focus, right: sibling };
    } else { // direction === 'right'
      // If we came from the right, the current focus is the right child.
      parentNode = { value: parentValue, left: sibling, right: this.focus };
    }

    // Return a new Zipper focusing on the reconstructed parent, with the shorter path.
    return new Zipper(parentNode, restPath);
  }

  setValue(newValue) {
    // Returns a new Zipper with the value of the focus node updated.
    if (!this.focus) return this; // Or throw error? Should not happen.
    // Create a new focus node object, preserving children.
    const newFocus = {
      ...this.focus,
      value: newValue,
    };
    // Return a new Zipper with the updated focus and the same path.
    return new Zipper(newFocus, this.path);
  }

  setLeft(newLeft) {
    // Returns a new Zipper with the left child of the focus node replaced.
    if (!this.focus) return this; // Or throw error?
    // Create a new focus node object, preserving value and right child.
    const newFocus = {
      ...this.focus,
      // Deep clone the new left child to maintain immutability.
      left: deepClone(newLeft),
    };
    // Return a new Zipper with the updated focus and the same path.
    return new Zipper(newFocus, this.path);
  }

  setRight(newRight) {
    // Returns a new Zipper with the right child of the focus node replaced.
     if (!this.focus) return this; // Or throw error?
    // Create a new focus node object, preserving value and left child.
    const newFocus = {
      ...this.focus,
      // Deep clone the new right child to maintain immutability.
      right: deepClone(newRight),
    };
    // Return a new Zipper with the updated focus and the same path.
    return new Zipper(newFocus, this.path);
  }
}
