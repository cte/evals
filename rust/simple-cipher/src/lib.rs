use rand::{distributions::Alphanumeric, Rng};

fn is_valid_key(key: &str) -> bool {
    !key.is_empty() && key.chars().all(|c| c.is_ascii_lowercase())
}

pub fn encode(key: &str, s: &str) -> Option<String> {
    if !is_valid_key(key) {
        return None;
    }
    Some(
        s.chars()
            .zip(key.chars().cycle())
            .map(|(p, k)| {
                let shift = (k as u8) - b'a';
                if p.is_ascii_lowercase() {
                    let encoded = ((p as u8 - b'a' + shift) % 26) + b'a';
                    encoded as char
                } else {
                    p
                }
            })
            .collect(),
    )
}

pub fn decode(key: &str, s: &str) -> Option<String> {
    if !is_valid_key(key) {
        return None;
    }
    Some(
        s.chars()
            .zip(key.chars().cycle())
            .map(|(c, k)| {
                let shift = (k as u8) - b'a';
                if c.is_ascii_lowercase() {
                    let decoded = ((26 + c as u8 - b'a' - shift) % 26) + b'a';
                    decoded as char
                } else {
                    c
                }
            })
            .collect(),
    )
}

pub fn encode_random(s: &str) -> (String, String) {
    let mut rng = rand::thread_rng();
    let key: String = (0..100)
        .map(|_| (b'a' + (rng.gen_range(0..26)) as u8) as char)
        .collect();
    let encoded = encode(&key, s).unwrap();
    (key, encoded)
}
