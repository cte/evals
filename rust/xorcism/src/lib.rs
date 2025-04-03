use std::borrow::Borrow;

/// A munger which XORs a key with some data
#[derive(Clone)]
pub struct Xorcism {
    key: Vec<u8>,
    pos: usize,
}

impl Xorcism {
    /// Create a new Xorcism munger from a key
    ///
    /// Should accept anything which has a cheap conversion to a byte slice.
    pub fn new<Key>(key: &Key) -> Xorcism
    where
        Key: ?Sized + AsRef<[u8]>,
    {
        Xorcism {
            key: key.as_ref().to_vec(),
            pos: 0,
        }
    }

    /// XOR each byte of the input buffer with a byte from the key.
    ///
    /// Note that this is stateful: repeated calls are likely to produce different results,
    /// even with identical inputs.
    pub fn munge_in_place(&mut self, data: &mut [u8]) {
        let key_len = self.key.len();
        for byte in data.iter_mut() {
            let key_byte = self.key[self.pos % key_len];
            *byte ^= key_byte;
            self.pos = self.pos.wrapping_add(1);
        }
    }

    /// XOR each byte of the data with a byte from the key.
    ///
    /// Note that this is stateful: repeated calls are likely to produce different results,
    /// even with identical inputs.
    ///
    /// Should accept anything which has a cheap conversion to a byte iterator.
    /// Shouldn't matter whether the byte iterator's values are owned or borrowed.
    pub fn munge<'b, Data>(&'b mut self, data: Data) -> impl Iterator<Item = u8> + 'b
    where
        Data: IntoIterator,
        Data::Item: Copy + std::borrow::Borrow<u8>,
        <Data as IntoIterator>::IntoIter: 'b,
    {
        let key_len = self.key.len();
        data.into_iter().map(move |b| {
            let key_byte = self.key[self.pos % key_len];
            let out = *b.borrow() ^ key_byte;
            self.pos = self.pos.wrapping_add(1);
            out
        })
    }
}
