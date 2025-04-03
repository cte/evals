class SgfTree:
    def __init__(self, properties=None, children=None):
        self.properties = properties or {}
        self.children = children or []

    def __eq__(self, other):
        if not isinstance(other, SgfTree):
            return False
        for key, value in self.properties.items():
            if key not in other.properties:
                return False
            if other.properties[key] != value:
                return False
        for key in other.properties.keys():
            if key not in self.properties:
                return False
        if len(self.children) != len(other.children):
            return False
        for child, other_child in zip(self.children, other.children):
            if child != other_child:
                return False
        return True

    def __ne__(self, other):
        return not self == other


def parse(input_string):
    if not input_string:
        raise ValueError("tree missing")
    input_string = input_string.strip()
    if not input_string.startswith("(") or not input_string.endswith(")"):
        raise ValueError("tree missing")
    if input_string == "()":
        raise ValueError("tree with no nodes")

    def parse_tree(s, index):
        if s[index] != "(":
            raise ValueError("tree missing")
        index += 1
        root = None
        current_node = None

        while index < len(s):
            c = s[index]
            if c == ";":
                node, index = parse_node(s, index + 1)
                if root is None:
                    root = node
                    current_node = root
                else:
                    current_node.children.append(node)
                    current_node = node
            elif c == "(":
                child, index = parse_tree(s, index)
                if current_node is None:
                    raise ValueError("tree missing")
                current_node.children.append(child)
            elif c == ")":
                index += 1
                if root is None:
                    raise ValueError("tree with no nodes")
                return root, index
            elif c in " \t\r\n":
                index += 1
            else:
                raise ValueError("tree missing")
        raise ValueError("tree missing")

    def parse_node(s, index):
        props = {}
        while index < len(s):
            # Skip whitespace
            while index < len(s) and s[index] in " \t\r\n":
                index += 1
            if index >= len(s) or s[index] in ";()":
                break
            # Parse property key
            start = index
            while index < len(s) and s[index].isalpha():
                index += 1
            key = s[start:index]
            if not key:
                break
            if not key.isupper():
                raise ValueError("property must be in uppercase")
            # Skip whitespace before values
            while index < len(s) and s[index] in " \t\r\n":
                index += 1
            if index >= len(s) or s[index] != "[":
                raise ValueError("properties without delimiter")
            values = []
            while index < len(s) and s[index] == "[":
                index += 1
                val = ""
                while index < len(s):
                    c = s[index]
                    if c == "]":
                        index += 1
                        break
                    elif c == "\\":
                        index += 1
                        if index >= len(s):
                            break
                        next_c = s[index]
                        if next_c == "]":
                            val += "]"
                        elif next_c == "\\":
                            val += "\\"
                        elif next_c == "\t":
                            val += " "
                        elif next_c == "\n":
                            pass  # remove escaped newline
                        else:
                            val += next_c
                        index += 1
                    else:
                        if c == "\t":
                            val += " "
                        else:
                            val += c
                        index += 1
                values.append(val)
            props[key] = values
        return SgfTree(properties=props), index

    tree, _ = parse_tree(input_string, 0)
    return tree
