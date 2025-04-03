class EmptyListException(Exception):
    pass


class Node:
    def __init__(self, value, next_node=None):
        self._value = value
        self._next = next_node

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
        new_node = Node(value, self._head)
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
        reversed_list = LinkedList()
        current = self._head
        while current:
            reversed_list.push(current.value())
            current = current.next()
        return reversed_list
