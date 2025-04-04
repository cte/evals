import java.util.List;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Satellite {

    private Map<Character, Integer> inorderIndexMap;
    private int preorderIndex; // Use a member variable to track preorder index

    public Tree treeFromTraversals(List<Character> preorderInput, List<Character> inorderInput) {
        // Handle potentially null inputs first
        boolean preorderEmpty = (preorderInput == null || preorderInput.isEmpty());
        boolean inorderEmpty = (inorderInput == null || inorderInput.isEmpty());

        // Specific case for empty tree test
        if (preorderEmpty && inorderEmpty) {
            return new Tree(null);
        }

        // General validation: null, empty (if not both), different lengths
        if (preorderEmpty || inorderEmpty || preorderInput.size() != inorderInput.size()) {
            throw new IllegalArgumentException("traversals must have the same length");
        }

        // Validate consistency and duplicates
        inorderIndexMap = new HashMap<>();
        Set<Character> inorderSet = new HashSet<>();
        for (int i = 0; i < inorderInput.size(); i++) {
            char val = inorderInput.get(i);
            // Check for duplicates in inorder
            if (!inorderSet.add(val)) {
                 throw new IllegalArgumentException("traversals must contain unique items");
            }
            inorderIndexMap.put(val, i);
        }

        // Check for duplicates in preorder and element consistency
        Set<Character> preorderSet = new HashSet<>();
        for (char val : preorderInput) {
             if (!inorderSet.contains(val)) { // Check if preorder element exists in inorder set
                 throw new IllegalArgumentException("traversals must have the same elements");
             }
             if (!preorderSet.add(val)) { // Check for duplicates in preorder
                 throw new IllegalArgumentException("traversals must contain unique items");
             }
        }
         // Final check if sets have same size (redundant if above checks pass, but safe)
        if (preorderSet.size() != inorderSet.size()) {
             throw new IllegalArgumentException("traversals must have the same elements");
        }


        // Reset preorder index and build tree
        preorderIndex = 0;
        Node root = buildTreeHelper(preorderInput, 0, inorderInput.size() - 1);
        return new Tree(root);
    }

    // Simplified helper using member variable for preorder index and only inorder bounds
    private Node buildTreeHelper(List<Character> preorder, int inStart, int inEnd) {
        // Base case
        if (inStart > inEnd) {
            return null;
        }

        // Get current root from preorder and advance index
        char rootValue = preorder.get(preorderIndex++);
        Node root = new Node(rootValue);

        // Find root's position in inorder (guaranteed to exist due to earlier checks)
        int inorderRootIndex = inorderIndexMap.get(rootValue);

        // Recursively build left subtree (inorder elements from inStart to inorderRootIndex - 1)
        root.left = buildTreeHelper(preorder, inStart, inorderRootIndex - 1);

        // Recursively build right subtree (inorder elements from inorderRootIndex + 1 to inEnd)
        root.right = buildTreeHelper(preorder, inorderRootIndex + 1, inEnd);

        return root;
    }
}
