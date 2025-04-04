use std::collections::{HashMap, HashSet};
use itertools::Itertools;

// Converts a word (like "SEND") to its numerical value based on the letter-digit mapping.
fn word_to_value(word: &str, mapping: &HashMap<char, u8>) -> Option<u64> {
    let mut value = 0u64;
    for (i, c) in word.chars().rev().enumerate() {
        let digit = *mapping.get(&c)? as u64;
        if i == word.len() - 1 && digit == 0 && word.len() > 1 {
            return None; // Leading zero detected
        }
        value += digit * 10u64.pow(i as u32);
    }
    Some(value)
}

pub fn solve(input: &str) -> Option<HashMap<char, u8>> {
    let parts: Vec<&str> = input.split(" == ").collect();
    if parts.len() != 2 { return None; } // Invalid format

    let addends: Vec<&str> = parts[0].split(" + ").collect();
    let result_word = parts[1];

    let mut unique_letters = HashSet::new();
    let mut leading_letters = HashSet::new();

    for word in addends.iter().chain(std::iter::once(&result_word)) {
        if let Some(first_char) = word.chars().next() {
             if word.len() > 1 { // Only consider leading letters for multi-digit numbers
                 leading_letters.insert(first_char);
             }
        }
        for c in word.chars() {
            if c.is_alphabetic() {
                unique_letters.insert(c);
            } else if !c.is_whitespace() && c != '+' && c != '=' {
                 return None; // Invalid character
            }
        }
    }

    let letters: Vec<char> = unique_letters.into_iter().collect();
    let num_letters = letters.len();

    if num_letters > 10 { return None; } // More letters than digits

    for p in (0..=9u8).permutations(num_letters) {
        let mapping: HashMap<char, u8> = letters.iter().cloned().zip(p.iter().cloned()).collect();

        // Check for leading zeros
        if leading_letters.iter().any(|&c| mapping.get(&c) == Some(&0)) {
            continue;
        }

        // Calculate values
        let addends_values: Option<Vec<u64>> = addends
            .iter()
            .map(|word| word_to_value(word, &mapping))
            .collect();

        let result_value = word_to_value(result_word, &mapping);

        if let (Some(add_vals), Some(res_val)) = (addends_values, result_value) {
             // Check sum
            if add_vals.iter().sum::<u64>() == res_val {
                // Found a solution
                return Some(mapping);
            }
        }
    }

    None // No solution found
}
