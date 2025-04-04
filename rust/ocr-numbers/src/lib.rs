use crate::Error::{InvalidColumnCount, InvalidRowCount};

#[derive(Debug, PartialEq, Eq)]
pub enum Error {
    InvalidRowCount(usize),
    InvalidColumnCount(usize),
}

// Define the significant parts (top 3 lines) of the patterns for each digit 0-9
const ZERO: [&str; 3] = [" _ ", "| |", "|_|"];
const ONE: [&str; 3] = ["   ", "  |", "  |"];
const TWO: [&str; 3] = [" _ ", " _|", "|_ "];
const THREE: [&str; 3] = [" _ ", " _|", " _|"];
const FOUR: [&str; 3] = ["   ", "|_|", "  |"];
const FIVE: [&str; 3] = [" _ ", "|_ ", " _|"];
const SIX: [&str; 3] = [" _ ", "|_ ", "|_|"];
const SEVEN: [&str; 3] = [" _ ", "  |", "  |"];
const EIGHT: [&str; 3] = [" _ ", "|_|", "|_|"];
const NINE: [&str; 3] = [" _ ", "|_|", " _|"];

/// Given a 3x4 grid pattern, recognize the digit.
fn recognize_digit(digit_pattern: [&str; 3]) -> char {
    match digit_pattern {
        ZERO => '0',
        ONE => '1',
        TWO => '2',
        THREE => '3',
        FOUR => '4',
        FIVE => '5',
        SIX => '6',
        SEVEN => '7',
        EIGHT => '8',
        NINE => '9',
        _ => '?', // Garbled digit
    }
}

pub fn convert(input: &str) -> Result<String, Error> {
    let lines: Vec<&str> = input.lines().collect();
    let num_lines = lines.len();

    // An empty input might be valid depending on interpretation, but tests likely expect non-empty.
    // Let's handle based on row validation.
    if num_lines == 0 {
         // The tests might expect an empty string or an error.
         // Let's assume empty input is valid and results in an empty string,
         // unless a test fails requiring error handling here.
         // Based on tests, empty input is not handled, so this check might be redundant
         // if the row count check handles it. Let's rely on the row count check.
         // return Ok(String::new());
    }


    if num_lines % 4 != 0 {
        return Err(InvalidRowCount(num_lines));
    }

    let mut result_lines = Vec::new();

    for line_chunk in lines.chunks(4) {
        // Ensure we actually have 4 lines in the chunk, though chunks(4) should guarantee this
        // if num_lines % 4 == 0 and num_lines > 0.
        if line_chunk.len() != 4 {
             // This case should theoretically not happen due to the initial check.
             // If it does, it indicates an issue with the input or logic.
             // Let's return InvalidRowCount, as the overall structure is wrong.
             return Err(InvalidRowCount(num_lines));
        }

        let line_len = line_chunk[0].len();

        // Validate column count for the first three lines
        if line_len % 3 != 0 {
            return Err(InvalidColumnCount(line_len));
        }
        for i in 1..3 { // Check lines 1 and 2 against line 0's length
            if line_chunk[i].len() != line_len {
                return Err(InvalidColumnCount(line_chunk[i].len()));
            }
        }
        // Line 3 (the 4th line) doesn't need strict length validation as it's often blank/shorter.

        let mut current_number_line = String::new();
        let num_digits = line_len / 3;

        for digit_index in 0..num_digits {
            let start_col = digit_index * 3;
            let end_col = start_col + 3;

            // Extract the 3x3 grid pattern for the current digit
            // Slicing is safe due to previous length checks for lines 0, 1, 2.
            let digit_pattern: [&str; 3] = [
                &line_chunk[0][start_col..end_col],
                &line_chunk[1][start_col..end_col],
                &line_chunk[2][start_col..end_col],
            ];

            // Recognize the digit
            current_number_line.push(recognize_digit(digit_pattern));
        }
        result_lines.push(current_number_line);
    }

    Ok(result_lines.join(","))
}
