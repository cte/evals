package paasio

import (
	"io"
	"sync"
)

// readCounter implements ReadCounter.
type readCounter struct {
	reader io.Reader
	mu     sync.Mutex
	n      int64 // total bytes read
	nops   int   // total read operations
}

// writeCounter implements WriteCounter.
type writeCounter struct {
	writer io.Writer
	mu     sync.Mutex
	n      int64 // total bytes written
	nops   int   // total write operations
}

// readWriteCounter implements ReadWriteCounter.
type readWriteCounter struct {
	ReadCounter
	WriteCounter
}

// NewWriteCounter creates a new WriteCounter wrapping the given writer.
func NewWriteCounter(writer io.Writer) WriteCounter {
	return &writeCounter{writer: writer}
}

// NewReadCounter creates a new ReadCounter wrapping the given reader.
func NewReadCounter(reader io.Reader) ReadCounter {
	return &readCounter{reader: reader}
}

// NewReadWriteCounter creates a new ReadWriteCounter wrapping the given ReadWriter.
func NewReadWriteCounter(readwriter io.ReadWriter) ReadWriteCounter {
	// We need to create separate counters because ReadWriteCounter embeds
	// ReadCounter and WriteCounter, which have their own mutexes.
	// If we embedded readCounter and writeCounter directly, they would share
	// the same underlying readwriter, but have separate counts and mutexes.
	// Instead, we compose it from a NewReadCounter and NewWriteCounter.
	rc := NewReadCounter(readwriter)
	wc := NewWriteCounter(readwriter)
	return &readWriteCounter{ReadCounter: rc, WriteCounter: wc}
}

// Read implements the io.Reader interface for readCounter.
func (rc *readCounter) Read(p []byte) (int, error) {
	n, err := rc.reader.Read(p)

	rc.mu.Lock()
	defer rc.mu.Unlock()
	rc.n += int64(n)
	rc.nops++

	return n, err
}

// ReadCount returns the total bytes read and the number of read operations.
func (rc *readCounter) ReadCount() (int64, int) {
	rc.mu.Lock()
	defer rc.mu.Unlock()
	return rc.n, rc.nops
}

// Write implements the io.Writer interface for writeCounter.
func (wc *writeCounter) Write(p []byte) (int, error) {
	n, err := wc.writer.Write(p)

	wc.mu.Lock()
	defer wc.mu.Unlock()
	wc.n += int64(n)
	wc.nops++

	return n, err
}

// WriteCount returns the total bytes written and the number of write operations.
func (wc *writeCounter) WriteCount() (int64, int) {
	wc.mu.Lock()
	defer wc.mu.Unlock()
	return wc.n, wc.nops
}
