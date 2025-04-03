use std::collections::HashMap;

/// Count occurrences of words.
pub fn word_count(words: &str) -> HashMap<String, u32> {
    let mut counts = HashMap::new();
    let mut word = String::new();

    for c in words.to_lowercase().chars() {
        if c.is_alphanumeric() {
            word.push(c);
        } else if c == '\'' {
            if !word.is_empty() && word.chars().last().unwrap().is_alphanumeric() {
                word.push(c);
            } else {
                if !word.is_empty() {
                    let trimmed = word.trim_matches('\'');
                    if !trimmed.is_empty() {
                        *counts.entry(trimmed.to_string()).or_insert(0) += 1;
                    }
                    word.clear();
                }
            }
        } else {
            if !word.is_empty() {
                let trimmed = word.trim_matches('\'');
                if !trimmed.is_empty() {
                    *counts.entry(trimmed.to_string()).or_insert(0) += 1;
                }
                word.clear();
            }
        }
    }

    if !word.is_empty() {
        let trimmed = word.trim_matches('\'');
        if !trimmed.is_empty() {
            *counts.entry(trimmed.to_string()).or_insert(0) += 1;
        }
    }

    counts
}
