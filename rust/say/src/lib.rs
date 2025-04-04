const SMALL_NUMBERS: [&str; 20] = [
    "zero", "one", "two", "three", "four", "five", "six", "seven", "eight", "nine", "ten",
    "eleven", "twelve", "thirteen", "fourteen", "fifteen", "sixteen", "seventeen", "eighteen", "nineteen",
];

const TENS: [&str; 8] = [
    "twenty", "thirty", "forty", "fifty", "sixty", "seventy", "eighty", "ninety",
];

const SCALES: [(&str, u64); 6] = [
    ("quintillion", 1_000_000_000_000_000_000),
    ("quadrillion", 1_000_000_000_000_000),
    ("trillion", 1_000_000_000_000),
    ("billion", 1_000_000_000),
    ("million", 1_000_000),
    ("thousand", 1_000),
];

// Encodes a number between 0 and 999
fn encode_chunk(n: u64) -> String {
    let mut chunk_str = String::new();
    let hundreds = n / 100;
    let remainder = n % 100;

    if hundreds > 0 {
        chunk_str.push_str(SMALL_NUMBERS[hundreds as usize]);
        chunk_str.push_str(" hundred");
    }

    if remainder > 0 {
        if hundreds > 0 {
            chunk_str.push(' ');
        }
        if remainder < 20 {
            chunk_str.push_str(SMALL_NUMBERS[remainder as usize]);
        } else {
            let tens_digit = remainder / 10;
            let units_digit = remainder % 10;
            chunk_str.push_str(TENS[tens_digit as usize - 2]); // -2 because TENS starts at twenty
            if units_digit > 0 {
                chunk_str.push('-');
                chunk_str.push_str(SMALL_NUMBERS[units_digit as usize]);
            }
        }
    }

    // Handle case where n is 0 within a chunk (e.g., 1000 -> chunks 1 and 0)
    // The main encode function handles the overall n=0 case.
    // If n is 0..999 and results in an empty string here, it must be 0.
    // However, the main loop logic prevents calling encode_chunk(0) unless n itself is 0 initially.
    // So, an empty chunk_str shouldn't happen unless n was 0 initially, which is handled before the loop.

    chunk_str
}


pub fn encode(mut n: u64) -> String {
    if n == 0 {
        return String::from(SMALL_NUMBERS[0]);
    }

    let mut parts = Vec::new();

    for (scale_name, scale_value) in SCALES {
        if n >= scale_value {
            let count = n / scale_value;
            parts.push(format!("{} {}", encode_chunk(count), scale_name));
            n %= scale_value;
        }
    }

    if n > 0 {
        parts.push(encode_chunk(n));
    }

    parts.join(" ")
}
