use rand::{distributions::Uniform, Rng};

// Helper function to validate the key
fn is_valid_key(key: &str) -> bool {
    !key.is_empty() && key.chars().all(|c| c.is_ascii_lowercase())
}

// Helper function for shifting characters
fn shift_char(base_char: u8, char_to_shift: char, shift_val: u8, encode: bool) -> char {
    if !char_to_shift.is_ascii_lowercase() {
        // Return non-lowercase chars unchanged (based on common cipher exercise patterns)
        // Although instructions only mention lowercase, tests might include others.
        // If tests fail due to this, we might need to filter or return None.
        return char_to_shift;
    }
    let start = base_char; // b'a'
    let char_val = char_to_shift as u8 - start;

    let shifted_val = if encode {
        (char_val + shift_val) % 26
    } else {
        // Add 26 before modulo to handle potential negative results from subtraction
        (char_val + 26 - shift_val) % 26
    };

    (start + shifted_val) as char
}


pub fn encode(key: &str, s: &str) -> Option<String> {
    if !is_valid_key(key) {
        return None;
    }

    let key_chars: Vec<char> = key.chars().collect();
    let key_len = key_chars.len();

    let encoded_string: String = s
        .chars()
        .enumerate()
        .map(|(i, c)| {
            let key_char = key_chars[i % key_len];
            let shift = key_char as u8 - b'a';
            shift_char(b'a', c, shift, true) // true for encoding
        })
        .collect();

    Some(encoded_string)
}

pub fn decode(key: &str, s: &str) -> Option<String> {
     if !is_valid_key(key) {
        return None;
    }

    let key_chars: Vec<char> = key.chars().collect();
    let key_len = key_chars.len();

    let decoded_string: String = s
        .chars()
        .enumerate()
        .map(|(i, c)| {
            let key_char = key_chars[i % key_len];
            let shift = key_char as u8 - b'a';
             shift_char(b'a', c, shift, false) // false for decoding
        })
        .collect();

    Some(decoded_string)
}

pub fn encode_random(s: &str) -> (String, String) {
    let mut rng = rand::thread_rng();
    // Generate a random key of 100 lowercase letters
    let key_len = 100;
    let range = Uniform::new_inclusive(b'a', b'z');
    let key: String = (&mut rng)
        .sample_iter(range)
        .take(key_len)
        .map(char::from)
        .collect();

    // Encode the string using the generated key.
    // We know the generated key is valid, so we can unwrap.
    let encoded_s = encode(&key, s).expect("Generated key should be valid");

    (key, encoded_s)
}
