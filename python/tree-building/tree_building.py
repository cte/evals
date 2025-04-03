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

    # Sort records by record_id
    records.sort(key=lambda r: r.record_id)

    # Validate continuous IDs starting from 0
    for idx, rec in enumerate(records):
        if rec.record_id != idx:
            raise ValueError("Record id is invalid or out of order.")

    nodes = {}

    # Create nodes and validate parent relationships
    for rec in records:
        if rec.record_id == 0:
            if rec.parent_id != 0:
                raise ValueError("Node parent_id should be smaller than it's record_id.")
        else:
            if rec.record_id == rec.parent_id:
                raise ValueError("Only root should have equal record and parent id.")
            if rec.parent_id >= rec.record_id:
                raise ValueError("Node parent_id should be smaller than it's record_id.")

        nodes[rec.record_id] = Node(rec.record_id)

    # Link children to parents
    for rec in records:
        if rec.record_id == 0:
            continue  # root node
        parent_id = rec.parent_id
        if parent_id not in nodes:
            raise ValueError("Record id is invalid or out of order.")
        nodes[parent_id].children.append(nodes[rec.record_id])

    return nodes[0]
