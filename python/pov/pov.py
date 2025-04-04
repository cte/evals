from json import dumps
from copy import deepcopy

class Tree:
    def __init__(self, label, children=None):
        self.label = label
        # Ensure children is always a list, even if None is passed
        self.children = children if children is not None else []

    def __dict__(self):
        # Sort children by label for consistent dictionary representation and comparison
        # Use a lambda function for sorting based on the 'label' attribute
        return {self.label: [c.__dict__() for c in sorted(self.children, key=lambda node: node.label)]}

    def __str__(self, indent=None):
        # Use the custom __dict__ method for JSON serialization
        return dumps(self.__dict__(), indent=indent)

    def __lt__(self, other):
        # Comparison based on label
        return self.label < other.label

    def __eq__(self, other):
        # Check if the other object is an instance of Tree before comparing dictionaries
        if not isinstance(other, Tree):
            return NotImplemented # Indicate comparison is not supported
        # Use the custom __dict__ method for equality comparison
        return self.__dict__() == other.__dict__()

    def __getstate__(self):
        # Return the state needed to reconstruct the object
        # Ensure children are part of the state
        return {'label': self.label, 'children': self.children}

    def __setstate__(self, state):
        # Restore the object's state from the dictionary
        self.label = state['label']
        self.children = state['children']

    def _find_node_with_path(self, target_label, current_path_nodes=None):
        """
        Helper method to find a node by its label using DFS.
        Returns the node object and the path (list of node objects) from the root to that node.
        Returns (None, []) if the node is not found.
        """
        if current_path_nodes is None:
            current_path_nodes = []

        # Create a new list for the path at this level to avoid modifying parent's path
        path_here = current_path_nodes + [self]

        if self.label == target_label:
            return self, path_here # Node found

        # Recursively search in children
        for child in self.children:
            # Pass a copy of path_here to avoid modification by sibling branches
            found_node, found_path = child._find_node_with_path(target_label, path_here)
            if found_node:
                return found_node, found_path # Propagate the found result upwards

        # Node not found in this subtree
        return None, []

    def from_pov(self, from_node_label):
        """
        Re-roots the tree from the perspective of the node with the given label.
        Returns a new Tree instance representing the re-rooted tree.
        Raises ValueError if the specified node is not found.
        """
        original_root = self
        node_to_reroot, path_nodes = original_root._find_node_with_path(from_node_label)

        if not node_to_reroot:
            raise ValueError("Tree could not be reoriented")

        # If the target node is the original root, return a deep copy of the original tree
        if node_to_reroot == original_root:
            return deepcopy(original_root)

        # --- Rerooting Logic (Path Modification Approach) ---
        # Create a deep copy of the entire tree first to work on.
        # This ensures all nodes and their relationships are copied initially.
        copied_root = deepcopy(original_root)

        # Find the path again, but this time within the *copied* tree.
        # We need the actual node objects from the copy.
        node_to_reroot_copy, path_nodes_copy = copied_root._find_node_with_path(from_node_label)

        if not node_to_reroot_copy:
             # This should not happen if the original find worked, but safety check.
             raise ValueError("Error finding node in copied tree.")

        # Iterate up the path in the *copied* tree, modifying relationships
        # Start from the node *above* the target node (its parent) up to the root.
        for i in range(len(path_nodes_copy) - 1, 0, -1):
            parent_copy = path_nodes_copy[i-1]
            child_copy = path_nodes_copy[i] # This is the node that needs to become the parent

            # Remove child_copy from parent_copy's children list
            # We need to compare objects, not just labels, in case of duplicate labels.
            parent_copy.children = [c for c in parent_copy.children if c is not child_copy]

            # Add parent_copy as a child to child_copy
            child_copy.children.append(parent_copy)

        # The copy of the original target node is the new root of the reoriented tree.
        return node_to_reroot_copy


    def path_to(self, from_node_label, to_node_label):
        """
        Finds the shortest path between two nodes identified by their labels.
        Returns a list of labels representing the path.
        Raises ValueError if either node is not found.
        """
        original_root = self
        _, path1_nodes = original_root._find_node_with_path(from_node_label)
        # Check if the source node was found
        if not path1_nodes:
            # Test expects "Tree could not be reoriented" for missing source node
            raise ValueError("Tree could not be reoriented")

        _, path2_nodes = original_root._find_node_with_path(to_node_label)
        # Check if the destination node was found
        if not path2_nodes:
            # Test expects "No path found" for missing destination node
            raise ValueError("No path found")

        # Find the index of the Lowest Common Ancestor (LCA) node in the paths
        lca_index = 0
        min_len = min(len(path1_nodes), len(path2_nodes))
        # Increment lca_index as long as nodes at that index match in both paths
        while lca_index < min_len and path1_nodes[lca_index].label == path2_nodes[lca_index].label:
            lca_index += 1
        # Decrement to get the index of the last common node (the LCA)
        lca_index -= 1

        # Construct the path: from_node -> ... -> LCA -> ... -> to_node

        # Part 1: Path from from_node up to LCA
        # Slice path1 from the LCA index onwards
        path_up_nodes = path1_nodes[lca_index:]
        # Get labels and reverse to get the order from_node -> ... -> LCA
        path_up_labels = [node.label for node in path_up_nodes]
        path_up_labels.reverse()

        # Part 2: Path from LCA down to to_node
        # Slice path2 from the node *after* the LCA down to the target node
        path_down_nodes = path2_nodes[lca_index + 1:]
        path_down_labels = [node.label for node in path_down_nodes]

        # Combine the paths: (from_node -> LCA) + (LCA -> to_node, excluding LCA)
        final_path = path_up_labels + path_down_labels
        return final_path
