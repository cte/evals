#[derive(Debug, PartialEq, Eq)]
pub enum Error {
    IncompleteNumber,
    Overflow, // Added for handling potential overflows during decoding
}

/// Convert a list of numbers to a stream of bytes encoded with variable length encoding.
pub fn to_bytes(values: &[u32]) -> Vec<u8> {
    let mut bytes = Vec::new();
    for &value in values {
        let mut num = value;
        let mut encoded_bytes = Vec::new();

        // Handle the 0 case explicitly
        if num == 0 {
            encoded_bytes.push(0x00);
        } else {
            // Extract 7-bit chunks until number is 0
            loop {
                let byte = (num & 0x7F) as u8;
                 num >>= 7;
                 // The first byte extracted is the least significant
                 // If num > 0 after shift, this wasn't the most significant 7 bits
                 if num > 0 || !encoded_bytes.is_empty() {
                    // This implies more bytes will follow this one in the final sequence
                    // But we are adding them in reverse order of significance here
                 }
                 encoded_bytes.push(byte);
                 if num == 0 {
                     break;
                 }
            }

            // Reverse to get the correct order (most significant first)
            encoded_bytes.reverse();

            // Set MSB for all but the last byte (least significant)
            for i in 0..encoded_bytes.len() - 1 {
                encoded_bytes[i] |= 0x80;
            }
        }
        bytes.extend(encoded_bytes);
    }
    bytes
}

/// Given a stream of bytes, extract all numbers which are encoded in there.
pub fn from_bytes(bytes: &[u8]) -> Result<Vec<u32>, Error> {
    let mut values = Vec::new();
    let mut current_num: u32 = 0;
    let mut in_number = false; // Track if we are currently parsing a number

    for &byte in bytes {
        in_number = true;
        // Check for potential overflow before shifting and adding
        // If the top 7 bits of current_num are already set, shifting left by 7 will overflow
        if (current_num >> (32 - 7)) > 0 {
             return Err(Error::Overflow);
        }

        current_num <<= 7;
        current_num |= (byte & 0x7F) as u32;

        // Check if this is the last byte for the current number
        if byte & 0x80 == 0 {
            values.push(current_num);
            current_num = 0;
            in_number = false; // Finished parsing this number
        }
    }

    // If the loop finishes while still parsing a number (last byte had MSB set), it's incomplete
    if in_number {
        Err(Error::IncompleteNumber)
    } else {
        Ok(values)
    }
}
