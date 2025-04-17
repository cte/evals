import io


class MeteredFile(io.BufferedRandom):
    """Implement using a subclassing model."""

    def __init__(self, *args, **kwargs):
        super().__init__(*args, **kwargs)
        self._read_bytes = 0
        self._read_ops = 0
        self._write_bytes = 0
        self._write_ops = 0
        self._closed = False

    def __enter__(self):
        return self

    def __exit__(self, exc_type, exc_val, exc_tb):
        self._closed = True
        return super().__exit__(exc_type, exc_val, exc_tb)

    def __iter__(self):
        return self

    def __next__(self):
        line = super().readline()
        if line == b'':
            raise StopIteration
        self._read_ops += 1
        self._read_bytes += len(line)
        return line

    def read(self, size=-1):
        if self._closed:
            raise ValueError("I/O operation on closed file.")
        data = super().read(size)
        self._read_ops += 1
        self._read_bytes += len(data)
        return data

    @property
    def read_bytes(self):
        return self._read_bytes

    @property
    def read_ops(self):
        return self._read_ops

    def write(self, b):
        if self._closed:
            raise ValueError("I/O operation on closed file.")
        written = super().write(b)
        self._write_ops += 1
        self._write_bytes += written
        return written

    @property
    def write_bytes(self):
        return self._write_bytes

    @property
    def write_ops(self):
        return self._write_ops


class MeteredSocket:
    """Implement using a delegation model."""

    def __init__(self, socket):
        self._socket = socket
        self._recv_bytes = 0
        self._recv_ops = 0
        self._send_bytes = 0
        self._send_ops = 0
        self._closed = False

    def __enter__(self):
        return self

    def __exit__(self, exc_type, exc_val, exc_tb):
        self._closed = True
        return self._socket.__exit__(exc_type, exc_val, exc_tb)

    def recv(self, bufsize, flags=0):
        if self._closed:
            import errno
            raise OSError(errno.EBADF, "Bad file descriptor")
        data = self._socket.recv(bufsize, flags)
        self._recv_ops += 1
        self._recv_bytes += len(data)
        return data

    @property
    def recv_bytes(self):
        return self._recv_bytes

    @property
    def recv_ops(self):
        return self._recv_ops

    def send(self, data, flags=0):
        if self._closed:
            import errno
            raise OSError(errno.EBADF, "Bad file descriptor")
        sent = self._socket.send(data, flags)
        self._send_ops += 1
        self._send_bytes += sent
        return sent

    @property
    def send_bytes(self):
        return self._send_bytes

    @property
    def send_ops(self):
        return self._send_ops
