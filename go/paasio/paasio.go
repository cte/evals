package paasio

import (
	"io"
	"sync"
)

type readCounter struct {
	reader    io.Reader
	mu        sync.Mutex
	byteCount int64
	opCount   int
}

type writeCounter struct {
	writer    io.Writer
	mu        sync.Mutex
	byteCount int64
	opCount   int
}

type readWriteCounter struct {
	readCounter
	writeCounter
}

func NewReadCounter(reader io.Reader) ReadCounter {
	return &readCounter{reader: reader}
}

func NewWriteCounter(writer io.Writer) WriteCounter {
	return &writeCounter{writer: writer}
}

func NewReadWriteCounter(rw io.ReadWriter) ReadWriteCounter {
	return &readWriteCounter{
		readCounter:  readCounter{reader: rw},
		writeCounter: writeCounter{writer: rw},
	}
}

func (rc *readCounter) Read(p []byte) (int, error) {
	n, err := rc.reader.Read(p)
	rc.mu.Lock()
	rc.byteCount += int64(n)
	rc.opCount++
	rc.mu.Unlock()
	return n, err
}

func (rc *readCounter) ReadCount() (int64, int) {
	rc.mu.Lock()
	defer rc.mu.Unlock()
	return rc.byteCount, rc.opCount
}

func (wc *writeCounter) Write(p []byte) (int, error) {
	n, err := wc.writer.Write(p)
	wc.mu.Lock()
	wc.byteCount += int64(n)
	wc.opCount++
	wc.mu.Unlock()
	return n, err
}

func (wc *writeCounter) WriteCount() (int64, int) {
	wc.mu.Lock()
	defer wc.mu.Unlock()
	return wc.byteCount, wc.opCount
}
