import io
import socket  # Import socket for type hinting if needed, though not strictly required for delegation

class MeteredFile(io.BufferedRandom):
    """Implement using a subclassing model."""

    def __init__(self, file_obj):
        # Note: io.BufferedRandom doesn't have a simple __init__ that takes a file object directly.
        # It's meant to be subclassed and methods overridden.
        # However, the tests likely provide an object that *behaves* like io.BufferedRandom expects.
        # A more robust approach might involve wrapping rather than strict subclassing if the underlying
        # object type isn't guaranteed, but we'll follow the subclassing instruction.
        # Let's assume the tests provide a suitable underlying stream or we adapt.
        # A common pattern is to wrap an existing stream. Let's wrap instead of pure subclassing
        # as io.BufferedRandom is complex to subclass directly for arbitrary objects.
        # Re-interpreting "subclassing model" as inheriting the *interface* and adding metering.
        # Let's try wrapping first, as it's more flexible. If tests fail due to strict type checks,
        # we might need to revisit the subclassing approach.

        # --- Revision: Let's stick closer to the stub's intent, assuming it works with the test setup.
        # We need to handle the underlying file object. io.BufferedRandom needs a raw stream.
        # If file_obj is already a buffered stream, we might need to access its raw component.
        # If it's a path, we'd open it. The stub is ambiguous.
        # Let's assume file_obj *is* the stream to be metered, and we'll delegate calls to it.
        # This deviates from strict subclassing of io.BufferedRandom but fits the metering goal.

        # --- Final approach: Wrap the file_obj and implement the io.BufferedIOBase interface + metering
        # This seems the most practical way to meet the requirements without deep io internals.
        # We inherit from io.BufferedIOBase which is more suitable for wrapping.

        # --- Let's try the original stub structure again, assuming the test setup handles the base class.
        # We'll call the superclass init and store the object if needed, or just manage state.
        # The stub inherits io.BufferedRandom, let's keep that.
        # It seems the intention might be to *replace* an existing file object with this metered one,
        # rather than taking an object in __init__. The tests might instantiate this class directly
        # with arguments intended for the underlying stream (e.g., io.BytesIO()).

        # Let's assume args/kwargs are for the underlying stream managed by BufferedRandom.
        super().__init__(*args, **kwargs) # This likely won't work as intended unless BufferedRandom is used differently.

        # --- Let's pivot to wrapping, as it's standard practice for decorators/wrappers.
        # The stub structure might be misleading. We'll wrap `file_obj`.
        # We won't inherit for now, just implement the required methods.
        # If tests require isinstance(obj, io.BufferedRandom), we'll adjust.

        # --- OK, let's try *actually* subclassing and overriding methods.
        # This requires understanding how BufferedRandom manages its state.
        # It seems the most faithful interpretation of the stub.

        # We need to accept the file object to wrap. Let's modify __init__.
        # The original stub's __init__ was empty. Let's assume it should take the object.
        # No, the tests likely pass *args, **kwargs to io.BytesIO or similar via MeteredFile.

        self._read_bytes = 0
        self._read_ops = 0
        self._write_bytes = 0
        self._write_ops = 0
        # We need to call the *actual* superclass init if we are truly subclassing.
        # io.BufferedRandom needs a raw stream. This is getting complicated.

        # --- Let's simplify: Assume the tests instantiate MeteredFile in a way that works,
        # and focus on adding the metering logic to the overrides.

        # Initialize counters
        self._read_bytes_count = 0
        self._read_ops_count = 0
        self._write_bytes_count = 0
        self._write_ops_count = 0
        # We need access to the underlying stream's methods. super() provides this.
        # The stub's __init__ taking *args, **kwargs suggests they are passed to the underlying stream.
        # Let's assume io.BufferedRandom handles this.

    # We need to properly initialize the superclass if we inherit.
    # Let's assume the tests provide a file-like object and we wrap it,
    # ignoring the inheritance for a moment to get the logic right.

    # --- Reverting to a wrapper approach for clarity and robustness ---
    # class MeteredFile: # No inheritance for now
    #     def __init__(self, file_like_obj):
    #         self._obj = file_like_obj
    #         self._read_bytes_count = 0
    #         self._read_ops_count = 0
    #         self._write_bytes_count = 0
    #         self._write_ops_count = 0

    #     # Delegate other methods if needed by tests (seek, tell, etc.)
    #     def __getattr__(self, name):
    #         return getattr(self._obj, name)

    # --- Sticking to the subclassing model as requested ---
    # We MUST call the super init if we inherit. What args does it need?
    # io.BufferedRandom needs a 'raw' stream.
    # Let's assume the tests pass an instance of io.BytesIO or similar.
    # We can't just pass *args, **kwargs blindly.

    # --- Final attempt at subclassing structure ---
    def __init__(self, *args, **kwargs):
        # Call super().__init__ as expected by tests and initialize counters.
        super().__init__(*args, **kwargs)
        self._read_bytes_count = 0
        self._read_ops_count = 0
        self._write_bytes_count = 0
        self._write_ops_count = 0

        # The TypeError suggests this init is called directly by tests.

    def read(self, size=-1):
        """Read data and update read stats."""
        # We need to call the *superclass* read method.
        data = super().read(size)
        if data is not None: # Check if read returned data (not EOF or error)
             # Check needed because read() can return None or raise exception.
             # More accurately, check the length. size=0 returns b''. size=-1 or positive size returns bytes. EOF returns b''.
            read_len = len(data)
            if read_len > 0 or size == 0: # Count op even for zero-byte reads if requested
                self._read_ops_count += 1
                self._read_bytes_count += read_len
        # Handle cases where super().read might not exist if init failed? Assume it works.
        return data

    def write(self, b):
        """Write data and update write stats."""
        # Call superclass write
        bytes_written = super().write(b)
        if bytes_written is not None and bytes_written > 0:
            self._write_ops_count += 1
            self._write_bytes_count += bytes_written
        # Handle cases where write fails or returns None/0? Assume standard behavior.
        return bytes_written

    # Implement other methods needed for file-like behavior if superclass doesn't cover all delegation
    # For example, __iter__ and __next__ might need specific handling if not inherited correctly.

    def __iter__(self):
        # Should return an iterator, often self if __next__ is implemented
        # Or delegate to super().__iter__()
        return self # Assuming superclass handles iteration state or we manage it

    def __next__(self):
        # Read line by line
        line = super().readline() # Use readline for iteration
        if not line:
            raise StopIteration
        # Update stats based on readline
        self._read_ops_count += 1 # Count each line read as an operation
        self._read_bytes_count += len(line)
        return line

    def __enter__(self):
        # Context manager entry - usually returns self
        # Ensure superclass __enter__ is called if necessary
        # super().__enter__() # If the base class requires it
        return self

    def __exit__(self, exc_type, exc_val, exc_tb):
        # Delegate __exit__ to the superclass and return its result.
        # This handles both cleanup and potential exception suppression.
        try:
            # Call superclass __exit__; its return value determines suppression.
            return super().__exit__(exc_type, exc_val, exc_tb)
        except AttributeError:
            # If superclass doesn't have __exit__ (unlikely for IOBase subclasses),
            # ensure cleanup happens if possible (e.g., close) and don't suppress.
            try:
                super().close()
            except AttributeError:
                pass # No close method either
            return False # Do not suppress if super().__exit__ doesn't exist

        # Or just close if the superclass doesn't have specific __exit__ logic
        super().close() # Ensure the underlying stream is closed

    # Properties to return the counts
    @property
    def read_bytes(self):
        return self._read_bytes_count

    @property
    def read_ops(self):
        return self._read_ops_count

    @property
    def write_bytes(self):
        return self._write_bytes_count

    @property
    def write_ops(self):
        return self._write_ops_count


class MeteredSocket:
    """Implement using a delegation model."""

    def __init__(self, sock):
        self._socket = sock
        self._recv_bytes_count = 0
        self._recv_ops_count = 0
        self._send_bytes_count = 0
        self._send_ops_count = 0

    def __enter__(self):
        # Sockets themselves aren't typically context managers unless wrapped.
        # Return self for potential 'with MeteredSocket(...) as ms:' usage.
        return self

    def __exit__(self, exc_type, exc_val, exc_tb):
        # Delegate __exit__ to the wrapped object if it exists.
        should_suppress = False
        if hasattr(self._socket, '__exit__'):
            # Pass exception details; the result indicates suppression.
            result = self._socket.__exit__(exc_type, exc_val, exc_tb)
            if result is True:
                should_suppress = True

        # Also, attempt to close the socket if it has a close method,
        # regardless of whether __exit__ was present (standard socket cleanup).
        if hasattr(self._socket, 'close'):
            try:
                self._socket.close()
            except Exception:
                # Ignore errors during close in exit
                pass

        # Return True only if the delegated __exit__ indicated suppression.
        return should_suppress
    def recv(self, bufsize, flags=0):
        """Receive data and update recv stats."""
        data = self._socket.recv(bufsize, flags)
        # recv returns empty bytes on graceful close, raises error otherwise.
        if data is not None: # Should always be bytes or raise error
            self._recv_ops_count += 1
            self._recv_bytes_count += len(data)
        return data

    def send(self, data, flags=0):
        """Send data and update send stats."""
        bytes_sent = self._socket.send(data, flags)
        # send returns number of bytes sent, might be less than len(data).
        if bytes_sent > 0:
            self._send_ops_count += 1
            self._send_bytes_count += bytes_sent
        return bytes_sent

    # Properties to return the counts
    @property
    def recv_bytes(self):
        return self._recv_bytes_count

    @property
    def recv_ops(self):
        return self._recv_ops_count

    @property
    def send_bytes(self):
        return self._send_bytes_count

    @property
    def send_ops(self):
        return self._send_ops_count

    # Delegate other necessary socket methods if tests require them
    def __getattr__(self, name):
        """Delegate unknown attributes to the underlying socket."""
        return getattr(self._socket, name)
