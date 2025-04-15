package linkedlist

import "errors"

// Element represents a node in the linked list
type Element struct {
	data int
	next *Element
}

// List represents the linked list
type List struct {
	head *Element
	tail *Element // Keep track of tail for efficient Push
	size int
}

// New creates a new list from a slice of integers.
func New(elements []int) *List {
	l := &List{}
	if elements == nil {
		return l // Return empty list if input slice is nil
	}
	for _, elem := range elements {
		l.Push(elem)
	}
	return l
}

// Size returns the number of elements in the list.
func (l *List) Size() int {
	return l.size
}

// Push adds an element to the end of the list.
func (l *List) Push(element int) {
	newElement := &Element{data: element}
	if l.head == nil {
		l.head = newElement
		l.tail = newElement
	} else {
		l.tail.next = newElement
		l.tail = newElement
	}
	l.size++
}

// Pop removes and returns the last element from the list.
// Returns an error if the list is empty.
func (l *List) Pop() (int, error) {
	if l.head == nil {
		return 0, errors.New("cannot pop from an empty list")
	}

	var poppedData int
	l.size--

	// If only one element
	if l.head == l.tail {
		poppedData = l.head.data
		l.head = nil
		l.tail = nil
		return poppedData, nil
	}

	// Find the second to last element
	current := l.head
	for current.next != l.tail {
		current = current.next
	}

	poppedData = l.tail.data
	l.tail = current
	l.tail.next = nil

	return poppedData, nil
}

// Array converts the list to a slice of integers.
func (l *List) Array() []int {
	// Handle nil list gracefully, although New should prevent this
	if l == nil {
		return []int{}
	}
	arr := make([]int, 0, l.size)
	current := l.head
	for current != nil {
		arr = append(arr, current.data)
		current = current.next
	}
	return arr
}

// Reverse returns a new list with the elements in reverse order.
// The original list must not be modified.
func (l *List) Reverse() *List {
	reversedList := New(nil) // Create a new empty list
	// Push elements onto the new list in reverse order by prepending
	// This avoids creating an intermediate slice

	// Create a deep copy of the nodes for the new list
	copiedNodes := make([]*Element, 0, l.size)
	curr := l.head
	for curr != nil {
		copiedNodes = append(copiedNodes, &Element{data: curr.data})
		curr = curr.next
	}

	// Link the copied nodes in reverse order
	if len(copiedNodes) > 0 {
		reversedList.head = copiedNodes[len(copiedNodes)-1]
		reversedList.tail = copiedNodes[0] // Initial guess for tail
		reversedList.size = len(copiedNodes)

		for i := len(copiedNodes) - 1; i > 0; i-- {
			copiedNodes[i].next = copiedNodes[i-1]
		}
		// Ensure the last node's next is nil
		if len(copiedNodes) > 0 {
			copiedNodes[0].next = nil
			reversedList.tail = copiedNodes[0] // Correct tail
		} else {
			reversedList.tail = nil // Empty list case
		}
}

	return reversedList
}
