use std::borrow::Borrow;
#[cfg(feature = "io")]
use std::io::{Read, Result as IoResult, Write};

/// A munger which XORs a key with some data
#[derive(Clone)] // Keep Clone for now, might need adjustment for non-allocating iterators if required later.
pub struct Xorcism<'a> {
    key: &'a [u8],
    key_pos: usize,
}

impl<'a> Xorcism<'a> {
    /// Create a new Xorcism munger from a key
    ///
    /// Should accept anything which has a cheap conversion to a byte slice.
    // Added ?Sized constraint to allow unsized types like &str
    pub fn new<Key: AsRef<[u8]> + ?Sized>(key: &'a Key) -> Self {
        Xorcism {
            key: key.as_ref(),
            key_pos: 0,
        }
    }

    /// XOR each byte of the input buffer with a byte from the key.
    ///
    /// Note that this is stateful: repeated calls are likely to produce different results,
    /// even with identical inputs.
    pub fn munge_in_place(&mut self, data: &mut [u8]) {
        if self.key.is_empty() {
            return; // Avoid division by zero and do nothing if key is empty
        }
        let key_len = self.key.len();
        for byte in data.iter_mut() {
            *byte ^= self.key[self.key_pos];
            self.key_pos = (self.key_pos + 1) % key_len;
        }
    }

    /// XOR each byte of the data with a byte from the key.
    ///
    /// Note that this is stateful: repeated calls are likely to produce different results,
    /// even with identical inputs.
    ///
    /// Should accept anything which has a cheap conversion to a byte iterator.
    /// Shouldn't matter whether the byte iterator's values are owned or borrowed.
    pub fn munge<'b, Data, Item>(&'b mut self, data: Data) -> XorcismIter<'a, 'b, Data::IntoIter>
    where
        Data: IntoIterator<Item = Item>,
        Item: Borrow<u8>,
        'a: 'b, // Key lifetime must outlive the mutable borrow
        Data::IntoIter: 'b, // Ensure the inner iterator also lives long enough
    {
        XorcismIter {
            xorcism: self,
            inner_iter: data.into_iter(),
        }
    }
}

#[cfg(feature = "io")]
impl<'a> Xorcism<'a> {
    /// Create a stream adapter that reads from `inner` and XORs the bytes.
    pub fn reader<R: Read>(self, inner: R) -> XorcismReader<'a, R> {
        XorcismReader { xorcism: self, inner }
    }

    /// Create a stream adapter that XORs bytes and writes them to `inner`.
    pub fn writer<W: Write>(self, inner: W) -> XorcismWriter<'a, W> {
        XorcismWriter { xorcism: self, inner }
    }
}

// Helper struct for the stateful iterator returned by munge
pub struct XorcismIter<'a, 'b, I>
where
    I: Iterator,
    'a: 'b,
{
    xorcism: &'b mut Xorcism<'a>,
    inner_iter: I,
}

impl<'a, 'b, I, Item> Iterator for XorcismIter<'a, 'b, I>
where
    I: Iterator<Item = Item>,
    Item: Borrow<u8>,
    'a: 'b,
{
    type Item = u8;

    fn next(&mut self) -> Option<Self::Item> {
        self.inner_iter.next().map(|item| {
            let byte = *item.borrow();
            let key_len = self.xorcism.key.len();
            if key_len == 0 {
                byte // Return original byte if key is empty
            } else {
                let key_byte = self.xorcism.key[self.xorcism.key_pos];
                // Update the key position state within the borrowed Xorcism struct
                self.xorcism.key_pos = (self.xorcism.key_pos + 1) % key_len;
                byte ^ key_byte
            }
        })
    } // Closes next()
} // Closes impl Iterator

#[cfg(feature = "io")]
pub struct XorcismReader<'a, R: Read> {
    xorcism: Xorcism<'a>,
    inner: R,
}

#[cfg(feature = "io")]
impl<'a, R: Read> Read for XorcismReader<'a, R> {
    fn read(&mut self, buf: &mut [u8]) -> IoResult<usize> {
        let bytes_read = self.inner.read(buf)?;
        if bytes_read > 0 {
            self.xorcism.munge_in_place(&mut buf[..bytes_read]);
        }
        Ok(bytes_read)
    }
}

#[cfg(feature = "io")]
pub struct XorcismWriter<'a, W: Write> {
    xorcism: Xorcism<'a>,
    inner: W,
}

#[cfg(feature = "io")]
impl<'a, W: Write> Write for XorcismWriter<'a, W> {
    fn write(&mut self, buf: &[u8]) -> IoResult<usize> {
        // Create a temporary buffer to hold the munged data.
        // This avoids modifying the input buffer and allows correct state handling.
        let mut munged_buf = buf.to_vec(); // Allocation happens here, as required by Write trait signature
        self.xorcism.munge_in_place(&mut munged_buf);
        self.inner.write(&munged_buf)
        // Note: The number of bytes written by inner.write might be less than buf.len().
        // The Write trait contract handles this; the caller is responsible for retrying.
        // We return the result of inner.write directly. The number of *input* bytes
        // consumed conceptually matches the number of *output* bytes written.
    }

    fn flush(&mut self) -> IoResult<()> {
        self.inner.flush()
    }
}
