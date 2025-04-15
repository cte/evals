import re

class SgfTree:
    def __init__(self, properties=None, children=None):
        self.properties = properties or {}
        self.children = children or []

    def __eq__(self, other):
        if not isinstance(other, SgfTree):
            return False
        # Compare properties (order doesn't matter)
        if self.properties != other.properties:
             return False
        # Compare children (order matters)
        if len(self.children) != len(other.children):
            return False
        for child, other_child in zip(self.children, other.children):
            if child != other_child:
                return False
        return True

    def __ne__(self, other):
        return not self == other

    # Optional: Add a __repr__ for easier debugging if needed
    def __repr__(self):
        return f"SgfTree(properties={self.properties}, children={self.children})"


def _parse_value(text, index):
    """Parses a single SGF property value like "[...]".

    Handles escapes and whitespace according to SGF rules:
    - \\ + newline -> remove newline
    - \\ + other whitespace -> convert to space
    - \\ + non-whitespace -> insert char as-is
    - newline (unescaped) -> keep newline
    - other whitespace (unescaped) -> convert to space

    Args:
        text: The input SGF string.
        index: The starting index of the value (at '[').

    Returns:
        A tuple containing the parsed value string and the index
        immediately after the closing ']'.

    Raises:
        ValueError: If the value format is invalid (missing delimiters).
    """
    if index >= len(text) or text[index] != '[':
        raise ValueError("properties without delimiter")

    index += 1  # Consume '['
    value = ""
    start_index = index
    while index < len(text):
        char = text[index]
        if char == ']':
            # Found the end
            return value, index + 1
        elif char == '\\':
            index += 1
            if index >= len(text):  # Dangling escape
                raise ValueError("properties without delimiter")

            escaped_char = text[index]
            if escaped_char == '\n':
                # Escaped newline: remove it (do nothing)
                pass
            elif escaped_char in ' \t\r\f\v':
                # Escaped other whitespace: treat as normal whitespace -> convert to space
                value += ' '
            else:
                # Escaped non-whitespace: insert as-is
                value += escaped_char
        elif char == '\n':
            # Unescaped newline: keep as newline
            value += '\n'
        elif char in ' \t\r\f\v':
            # Unescaped other whitespace: convert to space
            value += ' '
        else:
            # Regular character
            value += char
        index += 1

    # If loop finishes without finding ']', it's an error
    raise ValueError("properties without delimiter")


def _parse_recursive(text, index):
    """Recursively parses an SGF tree structure "(...)" or sequence.

    Args:
        text: The input SGF string.
        index: The starting index (should be after '(').

    Returns:
        A tuple containing the root SgfTree of the parsed tree/subtree
        and the index immediately after the corresponding ')'.

    Raises:
        ValueError: For various parsing errors (missing delimiters,
                    invalid structure, invalid properties, etc.).
    """
    if index >= len(text) or text[index-1] != '(': # Check caller consumed '('
         # This case should ideally be caught by the caller or initial checks
         raise ValueError("tree missing") # Or internal error

    if index >= len(text) or text[index] == ')': # Check for empty tree '()'
        raise ValueError("tree with no nodes")

    root_node = None
    current_node = None # The last node created at this level, owns subsequent variations/nodes

    while index < len(text):
        if text[index] == ';':
            index += 1  # Consume ';'

            # Check for tree like '(;)' - this is treated as a node with no properties by the tests
            if index >= len(text) or text[index] == ')':
                # This represents a node with no properties, e.g., "(;)" or "(;B[aa];)"
                # The tests expect this to parse successfully as a node with empty properties.
                # Create the node, but don't advance current_node yet if it's the root.
                new_node = SgfTree(properties={})
                if root_node is None:
                    root_node = new_node
                    current_node = root_node
                else:
                    # This case might be like (;B[aa];) - the empty node follows B[aa]
                    current_node.children.append(new_node)
                    current_node = new_node
                # Continue parsing from here (likely hitting ')' next)
                continue # Skip property parsing for this empty node

            # --- Start of Node Property Parsing ---
            properties = {}
            # Loop to parse all properties belonging to this node.
            # Properties must start with an uppercase letter, but we read all letters first for validation.
            while index < len(text) and text[index].isalpha():
                key_start = index
                # Read the *entire* sequence of adjacent letters for the potential key identifier.
                while index < len(text) and text[index].isalpha():
                    index += 1
                potential_key = text[key_start:index]

                # Validate the potential key *before* looking for values.
                if not potential_key: # Should not happen if outer loop condition is isalpha()
                    raise ValueError(f"Internal error: Empty key parsed at {key_start}")
                if not potential_key.isupper():
                    # Found a non-uppercase letter in the property identifier.
                    raise ValueError("property must be in uppercase")

                # Key is valid (all uppercase). Now parse values.
                key = potential_key

                # Check for duplicate key *within this node*
                if key in properties:
                    raise ValueError(f"duplicate property '{key}' in node")

                # Parse property values
                values = []
                if index >= len(text) or text[index] != '[':
                    # Missing '[' after property identifier
                    raise ValueError("properties without delimiter")

                while index < len(text) and text[index] == '[':
                    try:
                        value, index = _parse_value(text, index)
                        values.append(value)
                    except ValueError as e:
                         # Propagate specific value parsing errors
                         raise e

                if not values: # Should have at least one value
                    raise ValueError("properties without delimiter") # Or maybe "property with no values"?

                # Store the valid property and its values
                properties[key] = values
            # --- End of Node Property Parsing ---

            # If after trying to parse properties, we didn't find any (e.g. ";(..." or ";)")
            # This check might be redundant if the outer loop handles non-alpha chars correctly.
            # However, the case "(;)" is handled earlier. This handles cases like "(; B[ab])"
            # where the loop `while index < len(text) and 'A' <= text[index] <= 'Z':` never enters.
            # We still need to create a node in this case.
            # The check `if not properties and not (index < len(text) and text[index] in ';()'):`
            # might be needed if the logic allows reaching here without parsing properties incorrectly.
            # Let's assume for now the logic correctly advances index past properties or stops if none are found.

            # Create the SgfTree node with the collected properties
            new_node = SgfTree(properties=properties)

            if root_node is None:
                # This is the first node in this sequence/tree
                root_node = new_node
                current_node = root_node
            else:
                # This node follows the 'current_node' in the game sequence
                current_node.children.append(new_node)
                current_node = new_node # This new node becomes the current one

        elif text[index] == '(':
            # Start of a variation/subtree
            if current_node is None:
                # Variation cannot appear before the first node in a sequence
                raise ValueError("variation before node")

            # Recursively parse the subtree. The result is the root of that subtree.
            # It becomes a child of the node that *preceded* it (current_node).
            try:
                 # Pass index + 1 because _parse_recursive expects to start *after* '('
                 subtree_root, index = _parse_recursive(text, index + 1)
                 current_node.children.append(subtree_root)
            except ValueError as e:
                 # Propagate errors from subtree parsing
                 raise e


        elif text[index] == ')':
            # End of the current tree/sequence
            index += 1  # Consume ')'
            # If root_node is still None here, it means we had something like '()'
            # which is handled by the check at the start of the function.
            # Or it could be '(;)' which is now handled after the ';' check.
            # If we somehow reach here with root_node as None, it's an invalid structure.
            if root_node is None:
                 raise ValueError("tree missing or malformed structure") # Should not happen with prior checks
            # Return the *first* node created in this sequence as the root of this (sub)tree
            return root_node, index

        # Optional whitespace is ignored according to instructions
        # elif text[index].isspace():
        #      index += 1

        else:
            # Invalid character (not ';', '(', ')', or uppercase letter for property)
            if text[index].islower():
                 # Check specifically for lowercase property keys
                 raise ValueError("property must be in uppercase")
            raise ValueError(f"unexpected character '{text[index]}' at index {index}")

    # If we exit the loop because we ran out of text but expected ')'
    raise ValueError("tree missing")


def parse(input_string):
    """
    Parses an SGF (Smart Game Format) string into a tree structure.

    Args:
        input_string: The SGF string to parse.

    Returns:
        An SgfTree object representing the root of the parsed SGF tree.

    Raises:
        ValueError: If the input string is invalid SGF according to the
                    simplified rules specified in the exercise. Specific error
                    messages are used for different failure conditions:
                    - "tree missing": Input is empty, doesn't start/end with '()',
                                      or is prematurely terminated.
                    - "tree with no nodes": Input is '()' or contains sequences like '(;)'
                    - "properties without delimiter": Property values are missing '[' or ']'.
                    - "property must be in uppercase": Property identifiers contain lowercase letters.
                    - Other specific messages for duplicate properties, variations before nodes, etc.
    """
    if not input_string:
        raise ValueError("tree missing")

    # Basic structural check (must start with '(' and end with ')')
    # Helps catch simple errors early and simplifies recursion base cases.
    if not input_string.startswith('(') or not input_string.endswith(')'):
        raise ValueError("tree missing")

    try:
        # Start parsing right after the initial '('
        root_node, end_index = _parse_recursive(input_string, 1)

        # Check if the parser consumed the entire string up to the final ')'
        # The end_index returned by _parse_recursive is *after* the closing ')'
        # for the tree it parsed. For the top-level call, this should match len(input_string).
        if end_index != len(input_string):
            # This implies extra characters after the main tree structure, e.g., " SGF() extra"
            raise ValueError("extra characters after tree")

        return root_node

    except IndexError:
        # Catch potential index errors during parsing, likely due to malformed structure
        # or incorrect index handling within the parser logic.
        raise ValueError("malformed tree structure") # Generic error for unexpected index issues
    # ValueError exceptions raised within helpers are propagated directly.
