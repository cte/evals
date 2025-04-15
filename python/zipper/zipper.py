import copy

class Zipper:
    def __init__(self, focus, breadcrumbs):
        # Use deepcopy to ensure immutability of the original tree structure
        # when modifications happen via set_value, set_left, set_right.
        # While navigation methods (left, right, up) don't strictly need deepcopy
        # for the focus itself (as they create new zippers), using it consistently
        # simplifies the mental model and prevents accidental mutation if the
        # internal structure were to change. Breadcrumbs store references to nodes
        # that might be modified later, so copying them is important too.
        self.focus = copy.deepcopy(focus)
        self.breadcrumbs = copy.deepcopy(breadcrumbs)

    @staticmethod
    def from_tree(tree):
        if tree is None:
            return None # Or perhaps raise an error, but tests imply None might be okay
        # Start with the root as focus and no breadcrumbs
        return Zipper(tree, [])

    def value(self):
        if self.focus:
            return self.focus.get("value")
        return None

    def set_value(self, value):
        if self.focus is None:
            # Cannot set value on a non-existent focus
            return self # Or raise error? Returning self seems safer for chaining
        new_focus = copy.deepcopy(self.focus)
        new_focus["value"] = value
        # Return a new Zipper with the updated focus
        return Zipper(new_focus, self.breadcrumbs)

    def left(self):
        if self.focus is None or self.focus.get("left") is None:
            return None
        
        new_focus = self.focus["left"]
        # Add a breadcrumb: (parent_node_info, direction_from_parent, sibling)
        # We only need enough info to reconstruct the parent on 'up()'.
        # Storing the full parent focus isn't strictly necessary if we store value/siblings.
        breadcrumb = ("left", self.focus["value"], self.focus.get("right"))
        new_breadcrumbs = self.breadcrumbs + [breadcrumb]
        return Zipper(new_focus, new_breadcrumbs)

    def set_left(self, tree):
        if self.focus is None:
            return self
        new_focus = copy.deepcopy(self.focus)
        new_focus["left"] = copy.deepcopy(tree) # Deepcopy the new subtree too
        # Return a new Zipper with the updated focus
        return Zipper(new_focus, self.breadcrumbs)

    def right(self):
        if self.focus is None or self.focus.get("right") is None:
            return None

        new_focus = self.focus["right"]
        breadcrumb = ("right", self.focus["value"], self.focus.get("left"))
        new_breadcrumbs = self.breadcrumbs + [breadcrumb]
        return Zipper(new_focus, new_breadcrumbs)

    def set_right(self, tree):
        if self.focus is None:
            return self
        new_focus = copy.deepcopy(self.focus)
        new_focus["right"] = copy.deepcopy(tree) # Deepcopy the new subtree too
        # Return a new Zipper with the updated focus
        return Zipper(new_focus, self.breadcrumbs)

    def up(self):
        if not self.breadcrumbs:
            return None # Already at the root

        direction, parent_value, sibling = self.breadcrumbs[-1]
        new_breadcrumbs = self.breadcrumbs[:-1]

        # Reconstruct the parent node
        if direction == "left":
            parent_focus = {"value": parent_value, "left": self.focus, "right": sibling}
        else: # direction == "right"
            parent_focus = {"value": parent_value, "left": sibling, "right": self.focus}

        return Zipper(parent_focus, new_breadcrumbs)

    def to_tree(self):
        # Navigate up to the root
        zipper = self
        while zipper.up() is not None:
            zipper = zipper.up()
        # The focus of the top-most zipper is the root of the full tree
        # Return a deepcopy to prevent external modification of the zipper's internal state
        return copy.deepcopy(zipper.focus)
