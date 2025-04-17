import java.util.List;

public class Satellite {
    public Tree treeFromTraversals(List<Character> preorderInput, List<Character> inorderInput) {
        if (preorderInput.size() != inorderInput.size()) {
            throw new IllegalArgumentException("traversals must have the same length");
        }

        if (preorderInput.size() != preorderInput.stream().distinct().count()) {
            throw new IllegalArgumentException("traversals must contain unique items");
        }

        if (!preorderInput.stream().sorted().toList().equals(inorderInput.stream().sorted().toList())) {
            throw new IllegalArgumentException("traversals must have the same elements");
        }

        return new Tree(build(preorderInput, inorderInput));
    }

    private Node build(List<Character> preorder, List<Character> inorder) {
        if (preorder.isEmpty()) {
            return null;
        }

        char rootVal = preorder.get(0);
        int rootIndex = inorder.indexOf(rootVal);

        List<Character> leftInorder = inorder.subList(0, rootIndex);
        List<Character> rightInorder = inorder.subList(rootIndex + 1, inorder.size());

        List<Character> leftPreorder = preorder.subList(1, 1 + leftInorder.size());
        List<Character> rightPreorder = preorder.subList(1 + leftInorder.size(), preorder.size());

        Node left = build(leftPreorder, leftInorder);
        Node right = build(rightPreorder, rightInorder);

        return new Node(rootVal, left, right);
    }
}
