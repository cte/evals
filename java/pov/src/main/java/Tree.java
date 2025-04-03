import java.util.*;

class Tree {
    private final String label;
    private final List<Tree> children;

    public Tree(String label) {
        this(label, new ArrayList<>());
    }

    public Tree(String label, List<Tree> children) {
        this.label = label;
        this.children = children;
    }

    public static Tree of(String label) {
        return new Tree(label);
    }

    public static Tree of(String label, List<Tree> children) {
        return new Tree(label, children);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tree tree = (Tree) o;
        return label.equals(tree.label)
                && children.size() == tree.children.size()
                && children.containsAll(tree.children)
                && tree.children.containsAll(children);
    }

    @Override
    public int hashCode() {
        return Objects.hash(label, children);
    }

    @Override
    public String toString() {
        return "Tree{" + label +
                ", " + children +
                "}";
    }

    public Tree fromPov(String fromNode) {
        List<String> path = findPath(this, fromNode);
        if (path == null) {
            throw new UnsupportedOperationException("Tree could not be reoriented");
        }
        return reorient(this, path, 0);
    }

    private Tree reorient(Tree current, List<String> path, int index) {
        if (index == path.size() - 1) {
            return current;
        }

        String childLabel = path.get(index + 1);
        Tree child = null;
        for (Tree c : current.children) {
            if (c.label.equals(childLabel)) {
                child = c;
                break;
            }
        }

        if (child == null) {
            throw new UnsupportedOperationException("Tree could not be reoriented");
        }

        // Remove the child from current's children
        List<Tree> siblings = new ArrayList<>(current.children);
        siblings.remove(child);

        // Recursively reorient subtree rooted at child
        Tree newSubtree = reorient(child, path, index + 1);

        // The new parent node is the current node with its other children (siblings of the child on the path)
        Tree newParent = new Tree(current.label, siblings);

        // The new root's children are:
        // - original children of newSubtree (preserving subtree rooted at fromNode)
        // - plus the newParent (the ancestor chain) as a single child
        List<Tree> newChildren = new ArrayList<>(newSubtree.children);
        newChildren.add(newParent);

        return new Tree(newSubtree.label, newChildren);
    }

    private List<String> findPath(Tree node, String target) {
        if (node.label.equals(target)) {
            List<String> path = new ArrayList<>();
            path.add(node.label);
            return path;
        }
        for (Tree child : node.children) {
            List<String> childPath = findPath(child, target);
            if (childPath != null) {
                childPath.add(0, node.label);
                return childPath;
            }
        }
        return null;
    }

    public List<String> pathTo(String fromNode, String toNode) {
        Tree newRoot;
        try {
            newRoot = fromPov(fromNode);
        } catch (UnsupportedOperationException e) {
            throw new UnsupportedOperationException("No path found");
        }
        List<String> path = findPath(newRoot, toNode);
        if (path == null) {
            throw new UnsupportedOperationException("No path found");
        }
        List<String> result = new ArrayList<>();
        result.add(fromNode);
        result.addAll(path.subList(1, path.size()));
        return result;
    }
}
