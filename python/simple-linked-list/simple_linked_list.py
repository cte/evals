class EmptyListException(Exception):
    """Exception raised when trying to access an element in an empty list."""
    def __init__(self, message="The list is empty."):
        self.message = message
        super().__init__(self.message)


class Node:
    def __init__(self, value):
        self._value = value
        self._next = None

    def value(self):
        return self._value

    def next(self):
        return self._next


class LinkedList:
    def __init__(self, values=None):
        self._head = None
        self._size = 0
        if values:
            for value in values:
                self.push(value)

    def __iter__(self):
        current = self._head
        while current:
            yield current.value()
            current = current.next()

    def __len__(self):
        return self._size

    def head(self):
        if not self._head:
            raise EmptyListException("The list is empty.")
        return self._head

    def push(self, value):
        new_node = Node(value)
        new_node._next = self._head
        self._head = new_node
        self._size += 1

    def pop(self):
        if not self._head:
            raise EmptyListException("The list is empty.")
        value = self._head.value()
        self._head = self._head.next()
        self._size -= 1
        return value

    def reversed(self):
        # Create a new list with elements in reverse order
        # The __iter__ yields elements from head (last pushed) to tail (first pushed)
        # list(self) will produce [last_pushed, ..., first_pushed]
        # Initializing a new LinkedList with this list will push them in order,
        # resulting in the head being the first_pushed element.
        return LinkedList(list(self))
