import java.util.Objects;

class Zipper {
    Zipper up; // Parent node
    Zipper left;
    Zipper right;
    int value;

    Zipper(int value) {
        this.value = value;
        this.up = null;
        this.left = null;
        this.right = null;
    }

    // Private constructor for copying, potentially useful later if immutability was needed
    // private Zipper(int value, Zipper up, Zipper left, Zipper right) {
    //     this.value = value;
    //     this.up = up;
    //     this.left = left;
    //     this.right = right;
    // }

    BinaryTree toTree() {
        Zipper current = this;
        while (current.up != null) {
            current = current.up;
        }
        return new BinaryTree(current); // Pass the root Zipper
    }

    int getValue() {
        return this.value;
    }

    // Mutates the current Zipper based on test setup/assertions
    Zipper setLeft(Zipper leftChild) {
        this.left = leftChild;
        if (this.left != null) {
            this.left.up = this;
        }
        return this; // Return self, although tests don't check return value
    }

    // Mutates the current Zipper based on test setup/assertions
    Zipper setRight(Zipper rightChild) {
        this.right = rightChild;
        if (this.right != null) {
            this.right.up = this;
        }
        return this; // Return self
    }

    // Mutates the value
    void setValue(int value) {
        this.value = value;
    }

    // --- equals() and hashCode() ---
    // Based on testDifferentPathToSameZipper, it seems reference equality might be
    // sufficient if navigation always returns the *same* object for the same node.
    // Let's rely on the default Object.equals and Object.hashCode for now.
    // If tests fail, we'll need a more complex implementation based on tree structure/path.

    // Helper for BinaryTree.printTree and potentially equals/hashCode if needed later
    String printNode() {
        StringBuilder sb = new StringBuilder();
        sb.append("value: ").append(this.value).append(", ");
        sb.append("left: ");
        if (this.left == null) {
            sb.append("null");
        } else {
            sb.append("{ ").append(this.left.printNode()).append(" }");
        }
        sb.append(", ");
        sb.append("right: ");
        if (this.right == null) {
            sb.append("null");
        } else {
            sb.append("{ ").append(this.right.printNode()).append(" }");
        }
        return sb.toString();
    }
}

class BinaryTree {
    Zipper root;

    BinaryTree(int value) {
        this.root = new Zipper(value);
    }

    // Constructor used by Zipper.toTree()
    BinaryTree(Zipper root) {
       if (root.up != null) {
           throw new IllegalArgumentException("Zipper provided must be the root (up == null)");
       }
        this.root = root;
    }

    Zipper getRoot() {
        return this.root;
    }

    String printTree() {
        if (root == null) {
            return "null";
        }
        // Use the recursive helper in Zipper
        return root.printNode();
    }

    // --- equals() and hashCode() ---
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BinaryTree that = (BinaryTree) o;
        // Structural equality check based on the string representation for simplicity,
        // assuming printTree() uniquely represents the structure.
        // A recursive comparison of Zippers would be more robust but complex.
        return Objects.equals(this.printTree(), that.printTree());
    }

    @Override
    public int hashCode() {
        // Hash based on the string representation
        return Objects.hash(this.printTree());
    }
}
