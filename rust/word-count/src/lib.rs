use std::collections::HashMap;

/// Count occurrences of words.
pub fn word_count(words: &str) -> HashMap<String, u32> {
    let mut counts = HashMap::new();
    // Split by characters that are NOT alphanumeric and NOT an apostrophe.
    // This keeps contractions like "don't" together but splits on other punctuation.
    for word_slice in words.split(|c: char| !c.is_alphanumeric() && c != '\'') {
        // Trim leading/trailing apostrophes. This handles cases like "'word'"
        // without affecting apostrophes inside contractions like "don't".
        let trimmed_word = word_slice.trim_matches('\'');

        // Ensure the word is not empty after trimming (e.g., if the input was just "'")
        if !trimmed_word.is_empty() {
            // Convert to lowercase for case-insensitive counting.
            let lower_word = trimmed_word.to_lowercase();
            // Increment the count for the word.
            *counts.entry(lower_word).or_insert(0) += 1;
        }
    }
    counts
}
