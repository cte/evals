class CircularBuffer<T> {

    private final Object[] buffer;
    private final int capacity;
    private int head = 0;
    private int tail = 0;
    private int size = 0;

    CircularBuffer(final int size) {
        this.capacity = size;
        this.buffer = new Object[size];
    }

    @SuppressWarnings("unchecked")
    T read() throws BufferIOException {
        if (size == 0) {
            throw new BufferIOException("Tried to read from empty buffer");
        }
        T value = (T) buffer[head];
        buffer[head] = null;
        head = (head + 1) % capacity;
        size--;
        return value;
    }

    void write(T data) throws BufferIOException {
        if (size == capacity) {
            throw new BufferIOException("Tried to write to full buffer");
        }
        buffer[tail] = data;
        tail = (tail + 1) % capacity;
        size++;
    }

    void overwrite(T data) {
        if (size == capacity) {
            buffer[tail] = data;
            tail = (tail + 1) % capacity;
            head = tail;
        } else {
            buffer[tail] = data;
            tail = (tail + 1) % capacity;
            size++;
        }
    }

    void clear() {
        for (int i = 0; i < capacity; i++) {
            buffer[i] = null;
        }
        head = 0;
        tail = 0;
        size = 0;
    }

}