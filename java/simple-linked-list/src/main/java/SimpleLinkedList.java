public class SimpleLinkedList<T> {
    private static class Node<T> {
        T data;
        Node<T> next;

        Node(T data) {
            this.data = data;
        }
    }

    private Node<T> head;
    private int size;

    public SimpleLinkedList() {
        this.head = null;
        this.size = 0;
    }

    public SimpleLinkedList(T[] values) {
        this();
        if (values != null) {
            for (int i = values.length - 1; i >= 0; i--) {
                push(values[i]);
            }
        }
    }

    public void push(T value) {
        Node<T> newNode = new Node<>(value);
        newNode.next = head;
        head = newNode;
        size++;
    }

    public T pop() {
        if (head == null) {
            throw new java.util.NoSuchElementException("List is empty");
        }
        T value = head.data;
        head = head.next;
        size--;
        return value;
    }

    public void reverse() {
        Node<T> prev = null;
        Node<T> current = head;
        while (current != null) {
            Node<T> nextNode = current.next;
            current.next = prev;
            prev = current;
            current = nextNode;
        }
        head = prev;
    }

    @SuppressWarnings("unchecked")
    public T[] asArray(Class<T> clazz) {
        T[] array = (T[]) java.lang.reflect.Array.newInstance(clazz, size);
        Node<T> current = head;
        int index = 0;
        while (current != null) {
            array[index++] = current.data;
            current = current.next;
        }
        return array;
    }

    public int size() {
        return size;
    }
}
