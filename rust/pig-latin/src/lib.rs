fn is_vowel(c: char) -> bool {
    // Helper function to check if a character is a vowel (a, e, i, o, u).
    // Note: 'y' is handled specially based on its position in the word.
    matches!(c.to_ascii_lowercase(), 'a' | 'e' | 'i' | 'o' | 'u')
}

fn translate_word(word: &str) -> String {
    // Translates a single word to Pig Latin based on the rules.
    if word.is_empty() {
        return String::new(); // Handle empty input word.
    }

    // Rule 1: Starts with a vowel sound (a, e, i, o, u) or "xr" or "yt".
    // We check the lowercase version for vowel check consistency.
    let first_char = word.chars().next().unwrap();
    if is_vowel(first_char) || word.starts_with("xr") || word.starts_with("yt") {
        return format!("{word}ay");
    }

    // Find the end of the initial consonant cluster.
    // This involves checking for consonants, "qu", and "y" rules.
    let mut split_index = 0;
    let mut chars = word.char_indices().peekable();

    while let Some((i, c)) = chars.next() {
        // Rule 3: Handle "qu" as a single unit.
        if c == 'q' {
            // Check if the next character is 'u'. The pattern needs to match the tuple inside the Option<&(usize, char)>
            if let Some(&(_, 'u')) = chars.peek() {
                // If 'qu' is found, the consonant cluster includes 'u'.
                split_index = i + 'q'.len_utf8() + 'u'.len_utf8();
                break; // Found the end of the cluster ('qu').
            }
        }

        // Rule 4: Handle 'y' as a vowel if it's not the first letter.
        if c == 'y' && i > 0 {
            // If 'y' is encountered after the first letter, the consonant cluster ends before it.
            break;
        }

        // Rule 2: Stop at the first vowel (a, e, i, o, u).
        if is_vowel(c) {
            // If a standard vowel is found, the consonant cluster ends before it.
            break;
        }

        // If none of the above conditions are met, it's part of the consonant cluster.
        // Update split_index to the position *after* the current consonant.
        split_index = i + c.len_utf8();
    }


    // Apply the transformation: move the consonant cluster to the end and add "ay".
    let consonants = &word[0..split_index];
    let rest = &word[split_index..];
    format!("{rest}{consonants}ay")
}

/// Translates text into Pig Latin.
///
/// Takes an English text string as input and returns the Pig Latin translation.
/// The function processes each word in the input string according to Pig Latin rules.
pub fn translate(input: &str) -> String {
    // Split the input string into words based on whitespace.
    input
        .split_whitespace()
        // Translate each word using the helper function.
        .map(translate_word)
        // Collect the translated words into a vector of strings.
        .collect::<Vec<String>>()
        // Join the translated words back into a single string, separated by spaces.
        .join(" ")
}
