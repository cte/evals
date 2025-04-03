package linkedlist

import "errors"

type Element struct {
	Value int
	Next  *Element
	Prev  *Element
}

type List struct {
	head *Element
	tail *Element
	size int
}

func New(elements []int) *List {
	list := &List{}
	for _, v := range elements {
		list.Push(v)
	}
	return list
}

func (l *List) Size() int {
	return l.size
}

func (l *List) Push(value int) {
	newElem := &Element{Value: value}
	if l.tail == nil {
		l.head = newElem
		l.tail = newElem
	} else {
		l.tail.Next = newElem
		newElem.Prev = l.tail
		l.tail = newElem
	}
	l.size++
}

func (l *List) Pop() (int, error) {
	if l.tail == nil {
		return 0, errors.New("pop from empty list")
	}
	val := l.tail.Value
	if l.tail.Prev != nil {
		l.tail = l.tail.Prev
		l.tail.Next = nil
	} else {
		l.head = nil
		l.tail = nil
	}
	l.size--
	return val, nil
}

func (l *List) Array() []int {
	result := make([]int, 0, l.size)
	for curr := l.head; curr != nil; curr = curr.Next {
		result = append(result, curr.Value)
	}
	return result
}

func (l *List) Reverse() *List {
	reversed := &List{}
	for curr := l.tail; curr != nil; curr = curr.Prev {
		reversed.Push(curr.Value)
	}
	return reversed
}
