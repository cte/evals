import java.util.*;
import java.util.stream.Collectors;

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

    public String getLabel() {
        return label;
    }

    public List<Tree> getChildren() {
        return children;
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
        // Need a more robust comparison that doesn't rely on child order
        return label.equals(tree.label)
                && new HashSet<>(children).equals(new HashSet<>(tree.children));
    }

    @Override
    public int hashCode() {
        // Hash based on label and a sorted set of children hashes for order independence
        List<Integer> childrenHashes = children.stream()
                                               .map(Tree::hashCode)
                                               .sorted()
                                               .collect(Collectors.toList());
        return Objects.hash(label, childrenHashes);
    }


    @Override
    public String toString() {
        return "Tree{" + label +
                ", " + children +
                "}";
    }

    // Helper method to build an undirected graph representation (adjacency list)
    private Map<String, Set<String>> buildGraph() {
        Map<String, Set<String>> adj = new HashMap<>();
        Queue<Tree> queue = new LinkedList<>();
        Set<String> visitedNodes = new HashSet<>(); // Keep track of visited nodes to avoid cycles in graph building if any

        queue.offer(this);
        visitedNodes.add(this.label);


        while (!queue.isEmpty()) {
            Tree current = queue.poll();
            adj.putIfAbsent(current.label, new HashSet<>()); // Ensure node exists in adj map

            for (Tree child : current.children) {
                 if (!visitedNodes.contains(child.label)) { // Process only if not visited
                    adj.putIfAbsent(child.label, new HashSet<>());
                    adj.get(current.label).add(child.label);
                    adj.get(child.label).add(current.label); // Add edge in both directions
                    visitedNodes.add(child.label);
                    queue.offer(child);
                 } else {
                     // If already visited, still ensure the edge exists if the graph allows cycles (though trees shouldn't have them)
                     // For a strict tree, this case might indicate an issue, but we handle it defensively.
                     adj.putIfAbsent(child.label, new HashSet<>());
                     adj.get(current.label).add(child.label);
                     adj.get(child.label).add(current.label);
                 }
            }
        }
        return adj;
    }

    // Helper method to reconstruct the tree from a given node using BFS
    private Tree reconstructTree(String rootLabel, Map<String, Set<String>> adj) {
        // Check if rootLabel exists in the graph keys
        if (!adj.containsKey(rootLabel)) {
             throw new UnsupportedOperationException("Tree could not be reoriented");
        }

        Map<String, Tree> newNodes = new HashMap<>();
        Queue<String> queue = new LinkedList<>();
        Set<String> visited = new HashSet<>();

        queue.offer(rootLabel);
        visited.add(rootLabel);
        newNodes.put(rootLabel, new Tree(rootLabel));

        while (!queue.isEmpty()) {
            String currentLabel = queue.poll();
            Tree currentNode = newNodes.get(currentLabel);

            // Check if currentLabel exists in adj before accessing its neighbors
            if (adj.containsKey(currentLabel)) {
                // Sort neighbors to ensure deterministic child order for equals/hashCode if needed, though HashSet equality handles it.
                List<String> neighbors = new ArrayList<>(adj.get(currentLabel));
                Collections.sort(neighbors); // Optional: for consistent toString/debugging

                for (String neighborLabel : neighbors) {
                    if (!visited.contains(neighborLabel)) {
                        visited.add(neighborLabel);
                        Tree neighborNode = new Tree(neighborLabel);
                        newNodes.put(neighborLabel, neighborNode);
                        currentNode.children.add(neighborNode); // Add to the children list
                        queue.offer(neighborLabel);
                    }
                }
            }
        }
        // Sort children of the final root node if consistent order is desired
        // Collections.sort(newNodes.get(rootLabel).children, Comparator.comparing(Tree::getLabel)); // Optional
        return newNodes.get(rootLabel);
    }


    public Tree fromPov(String fromNode) {
        Map<String, Set<String>> adj = buildGraph();
        // Check specifically if the target node 'fromNode' exists in the graph
        if (!adj.containsKey(fromNode)) {
             throw new UnsupportedOperationException("Tree could not be reoriented");
        }
        return reconstructTree(fromNode, adj);
    }

    public List<String> pathTo(String fromNode, String toNode) {
        Map<String, Set<String>> adj = buildGraph();

        // Check if both start and end nodes exist in the graph
        if (!adj.containsKey(fromNode) || !adj.containsKey(toNode)) {
            throw new UnsupportedOperationException("No path found");
        }

        Queue<List<String>> queue = new LinkedList<>();
        Set<String> visited = new HashSet<>();

        List<String> initialPath = new ArrayList<>();
        initialPath.add(fromNode);
        queue.offer(initialPath);
        visited.add(fromNode);

        while (!queue.isEmpty()) {
            List<String> currentPath = queue.poll();
            String lastNode = currentPath.get(currentPath.size() - 1);

            if (lastNode.equals(toNode)) {
                return currentPath; // Path found
            }

            // Check if lastNode exists in adj before accessing its neighbors
            if (adj.containsKey(lastNode)) {
                 for (String neighbor : adj.get(lastNode)) {
                    if (!visited.contains(neighbor)) {
                        visited.add(neighbor);
                        List<String> newPath = new ArrayList<>(currentPath);
                        newPath.add(neighbor);
                        queue.offer(newPath);
                    }
                }
            }
        }

        // If the loop finishes without finding the path (shouldn't happen in a connected tree if both nodes exist)
        throw new UnsupportedOperationException("No path found");
    }
}
