#[derive(Debug, PartialEq, Eq)]
pub enum Error {
    IncompleteNumber,
}

/// Convert a list of numbers to a stream of bytes encoded with variable length encoding.
pub fn to_bytes(values: &[u32]) -> Vec<u8> {
    let mut bytes = Vec::new();
    for &num in values {
        if num == 0 {
            bytes.push(0);
            continue;
        }
        let mut parts = Vec::new();
        let mut n = num;
        while n > 0 {
            parts.push((n & 0x7F) as u8);
            n >>= 7;
        }
        for i in (0..parts.len()).rev() {
            let mut byte = parts[i];
            if i != 0 {
                byte |= 0x80; // set continuation bit
            }
            bytes.push(byte);
        }
    }
    bytes
}

/// Given a stream of bytes, extract all numbers which are encoded in there.
pub fn from_bytes(bytes: &[u8]) -> Result<Vec<u32>, Error> {
    let mut numbers = Vec::new();
    let mut current: u32 = 0;
    for &byte in bytes {
        current = (current << 7) | (byte & 0x7F) as u32;
        if byte & 0x80 == 0 {
            numbers.push(current);
            current = 0;
        }
    }
    if bytes.last().map_or(false, |b| b & 0x80 != 0) {
        return Err(Error::IncompleteNumber);
    }
    Ok(numbers)
}
