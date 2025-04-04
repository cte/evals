import java.util.Arrays;

class CircularBuffer<T> {

    private final T[] buffer;
    private final int capacity;
    private int head = 0; // Index of the oldest element
    private int tail = 0; // Index where the next element will be written
    private int size = 0; // Current number of elements in the buffer

    @SuppressWarnings("unchecked")
    CircularBuffer(final int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Buffer capacity must be positive");
        }
        this.capacity = capacity;
        // Using Object[] and casting is a common way to handle generic arrays in Java
        this.buffer = (T[]) new Object[capacity];
    }

    T read() throws BufferIOException {
        if (isEmpty()) {
            throw new BufferIOException("Tried to read from empty buffer");
        }
        T item = buffer[head];
        buffer[head] = null; // Allow GC
        head = (head + 1) % capacity;
        size--;
        return item;
    }

    void write(T data) throws BufferIOException {
        if (isFull()) {
            throw new BufferIOException("Tried to write to full buffer");
        }
        buffer[tail] = data;
        tail = (tail + 1) % capacity;
        size++;
    }

    void overwrite(T data) {
        if (!isFull()) {
            try {
                write(data);
            } catch (BufferIOException e) {
                // This should theoretically not happen if !isFull() check passes,
                // but added for robustness.
                // In a real-world scenario, might log this unexpected state.
            }
        } else {
            // Overwrite the oldest element (at head)
            buffer[head] = data;
            // Advance both head and tail as the oldest is replaced and a new element is effectively added at the end
            head = (head + 1) % capacity;
            // Tail should point to the next available slot, which is now the same as head after overwriting the oldest
            // This logic was slightly off before. When full and overwriting, the 'tail' effectively stays where it was
            // relative to the *new* head position. Since head moved forward, tail also needs to move forward.
            tail = (tail + 1) % capacity; // Corrected tail update logic
        }
    }


    void clear() {
        Arrays.fill(buffer, null); // Clear references for GC
        head = 0;
        tail = 0;
        size = 0;
    }

    private boolean isEmpty() {
        return size == 0;
    }

    private boolean isFull() {
        return size == capacity;
    }
}