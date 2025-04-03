from json import dumps


class Tree:
    def __init__(self, label, children=None):
        self.label = label
        self.children = children if children is not None else []

    def __dict__(self):
        return {self.label: [c.__dict__() for c in sorted(self.children)]}

    def __str__(self, indent=None):
        return dumps(self.__dict__(), indent=indent)

    def __lt__(self, other):
        return self.label < other.label

    def __eq__(self, other):
        return self.__dict__() == other.__dict__()

    def from_pov(self, from_node):
        path = self._find_path(self, from_node)
        if not path:
            raise ValueError("Tree could not be reoriented")
        return self._reroot(path)

    def path_to(self, from_node, to_node):
        new_root = self.from_pov(from_node)
        path = self._find_path(new_root, to_node)
        if not path:
            raise ValueError("No path found")
        return [node.label for node in path]

    def _find_path(self, node, target):
        if node.label == target:
            return [node]
        for child in node.children:
            subpath = self._find_path(child, target)
            if subpath:
                return [node] + subpath
        return None

    def _reroot(self, path):
        new_root = path[-1]
        current = new_root
        for parent in reversed(path[:-1]):
            # Remove current from parent's children
            parent.children.remove(current)
            # Add parent as child of current
            current.children.append(parent)
            # Move to next up
            current = parent
        return new_root
