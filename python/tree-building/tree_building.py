class Record:
    def __init__(self, record_id, parent_id):
        self.record_id = record_id
        self.parent_id = parent_id


class Node:
    def __init__(self, node_id):
        self.node_id = node_id
        self.children = []


def BuildTree(records):
    if not records:
        return None

    # Sort records by record_id for easier processing and validation
    records.sort(key=lambda x: x.record_id)

    # --- Validation ---
    n = len(records)

    # Check root node properties - Test expects "Record id is invalid or out of order."
    # for both missing root and invalid root parent.
    if records[0].record_id != 0:
         raise ValueError("Record id is invalid or out of order.")
    # Test expects "Node parent_id should be smaller than it's record_id." for root with parent != 0
    if records[0].parent_id != 0:
        raise ValueError("Node parent_id should be smaller than it's record_id.")

    # Check if IDs are continuous from 0 to n-1
    if records[-1].record_id != n - 1:
         # This check implicitly covers gaps and IDs starting > 0
        raise ValueError("Record id is invalid or out of order.")

    nodes = {} # Dictionary to store nodes keyed by node_id for O(1) lookup

    for i, record in enumerate(records):
        # Check for non-root node pointing to itself (direct cycle)
        if record.record_id != 0 and record.record_id == record.parent_id:
            raise ValueError("Only root should have equal record and parent id.")

        # Check parent ID validity for non-root nodes
        # Check parent ID validity for non-root nodes (parent must be smaller)
        if record.record_id > 0 and record.parent_id >= record.record_id:
            raise ValueError("Node parent_id should be smaller than it's record_id.")

        # --- Tree Building ---
        # Create node and add to dictionary
        node = Node(record.record_id)
        nodes[record.record_id] = node

        # Link to parent if not the root node
        if record.record_id > 0:
            parent_node = nodes.get(record.parent_id)
            # Parent node lookup should succeed if previous checks passed
            parent_node = nodes[record.parent_id]
            parent_node.children.append(node)

    # Return the root node (node with ID 0)
    return nodes.get(0)
