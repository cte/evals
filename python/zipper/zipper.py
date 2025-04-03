class Zipper:
    def __init__(self, focus, breadcrumbs):
        self.focus = focus
        self.breadcrumbs = breadcrumbs

    @staticmethod
    def from_tree(tree):
        return Zipper(tree, [])

    def to_tree(self):
        node = self.focus
        crumbs = self.breadcrumbs.copy()
        while crumbs:
            direction, value, sibling = crumbs.pop()
            if direction == 'left':
                node = {
                    "value": value,
                    "left": node,
                    "right": sibling
                }
            else:  # direction == 'right'
                node = {
                    "value": value,
                    "left": sibling,
                    "right": node
                }
        return node

    def value(self):
        return self.focus["value"]

    def set_value(self, value):
        new_focus = {
            "value": value,
            "left": self.focus["left"],
            "right": self.focus["right"]
        }
        return Zipper(new_focus, self.breadcrumbs.copy())

    def left(self):
        left_subtree = self.focus["left"]
        if left_subtree is None:
            return None
        new_crumbs = self.breadcrumbs.copy()
        new_crumbs.append(('left', self.focus["value"], self.focus["right"]))
        return Zipper(left_subtree, new_crumbs)

    def right(self):
        right_subtree = self.focus["right"]
        if right_subtree is None:
            return None
        new_crumbs = self.breadcrumbs.copy()
        new_crumbs.append(('right', self.focus["value"], self.focus["left"]))
        return Zipper(right_subtree, new_crumbs)

    def up(self):
        if not self.breadcrumbs:
            return None
        crumbs = self.breadcrumbs.copy()
        direction, value, sibling = crumbs.pop()
        if direction == 'left':
            parent = {
                "value": value,
                "left": self.focus,
                "right": sibling
            }
        else:  # direction == 'right'
            parent = {
                "value": value,
                "left": sibling,
                "right": self.focus
            }
        return Zipper(parent, crumbs)

    def set_left(self, subtree):
        new_focus = {
            "value": self.focus["value"],
            "left": subtree,
            "right": self.focus["right"]
        }
        return Zipper(new_focus, self.breadcrumbs.copy())

    def set_right(self, subtree):
        new_focus = {
            "value": self.focus["value"],
            "left": self.focus["left"],
            "right": subtree
        }
        return Zipper(new_focus, self.breadcrumbs.copy())
