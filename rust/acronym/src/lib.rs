pub fn abbreviate(phrase: &str) -> String {
    let mut acronym = String::new();
    // Initialize with a non-letter character to correctly handle the first letter of the phrase.
    let mut prev_char = ' ';

    for current_char in phrase.chars() {
        // Check if the current character should start a new part of the acronym.
        // This happens if:
        // 1. The previous character was not alphabetic (start of a word) AND the current is alphabetic.
        // 2. Or, the previous character was lowercase AND the current is uppercase (camelCase).
        if (!prev_char.is_alphabetic() && current_char.is_alphabetic()) ||
           (prev_char.is_lowercase() && current_char.is_uppercase()) {
            acronym.push(current_char.to_ascii_uppercase());
        }

        // Update prev_char for the next iteration.
        // Crucially, ignore apostrophes when updating prev_char. This allows them
        // to be treated as part of a word rather than a separator.
        if current_char != '\'' {
            prev_char = current_char;
        }
    }

    acronym
}
