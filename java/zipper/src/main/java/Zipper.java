class Zipper {
    int value;
    Zipper up;
    Zipper left;
    Zipper right;

    Zipper(int val) {
        this.value = val;
    }

    BinaryTree toTree() {
        Zipper root = this;
        while (root.up != null) {
            root = root.up;
        }
        return new BinaryTree(cloneTree(root, null));
    }

    private Zipper cloneTree(Zipper node, Zipper parent) {
        if (node == null) return null;
        Zipper copy = new Zipper(node.value);
        copy.up = parent;
        copy.left = cloneTree(node.left, copy);
        copy.right = cloneTree(node.right, copy);
        return copy;
    }

    int getValue() {
        return this.value;
    }

    Zipper setLeft(Zipper leftChild) {
        this.left = leftChild;
        if (leftChild != null) {
            leftChild.up = this;
        }
        return this;
    }

    Zipper setRight(Zipper rightChild) {
        this.right = rightChild;
        if (rightChild != null) {
            rightChild.up = this;
        }
        return this;
    }

    void setValue(int val) {
        this.value = val;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Zipper)) return false;
        Zipper other = (Zipper) obj;
        // Two zippers are equal if their root trees are equal
        BinaryTree thisTree = this.toTree();
        BinaryTree otherTree = other.toTree();
        return thisTree.equals(otherTree);
    }

    @Override
    public int hashCode() {
        return this.toTree().hashCode();
    }
}

class BinaryTree {
    private Zipper root;

    BinaryTree(int value) {
        this.root = new Zipper(value);
    }

    BinaryTree(Zipper root) {
        this.root = root;
    }

    Zipper getRoot() {
        return this.root;
    }

    String printTree() {
        return printTreeHelper(root);
    }

    private String printTreeHelper(Zipper node) {
        if (node == null) {
            return "null";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("value: ").append(node.value).append(", ");
        sb.append("left: ");
        if (node.left != null) {
            sb.append("{ ").append(printTreeHelper(node.left)).append(" }, ");
        } else {
            sb.append("null, ");
        }
        sb.append("right: ");
        if (node.right != null) {
            sb.append("{ ").append(printTreeHelper(node.right)).append(" }");
        } else {
            sb.append("null");
        }
        return sb.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof BinaryTree)) return false;
        BinaryTree other = (BinaryTree) obj;
        return equalsHelper(this.root, other.root);
    }

    private boolean equalsHelper(Zipper n1, Zipper n2) {
        if (n1 == null && n2 == null) return true;
        if (n1 == null || n2 == null) return false;
        if (n1.value != n2.value) return false;
        return equalsHelper(n1.left, n2.left) && equalsHelper(n1.right, n2.right);
    }

    @Override
    public int hashCode() {
        return hashHelper(root);
    }

    private int hashHelper(Zipper node) {
        if (node == null) return 0;
        int result = node.value;
        result = 31 * result + hashHelper(node.left);
        result = 31 * result + hashHelper(node.right);
        return result;
    }
}
