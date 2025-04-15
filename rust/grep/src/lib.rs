use anyhow::Error;
use std::collections::HashSet; // Needed for -l flag logic later
use std::fs; // Needed for file reading later

/// While using `&[&str]` to handle flags is convenient for exercise purposes,
/// and resembles the output of [`std::env::args`], in real-world projects it is
/// both more convenient and more idiomatic to contain runtime configuration in
/// a dedicated struct. Therefore, we suggest that you do so in this exercise.
///
/// [`std::env::args`]: https://doc.rust-lang.org/std/env/fn.args.html
#[derive(Debug, Default, Clone, Copy)] // Added Default, Clone, Copy
pub struct Flags {
    line_number: bool,    // -n
    only_files: bool,     // -l
    case_insensitive: bool, // -i
    invert: bool,         // -v
    entire_line: bool,    // -x
}

impl Flags {
    pub fn new(flags: &[&str]) -> Self {
        let mut f = Flags::default(); // Use default initialization
        for flag in flags {
            match *flag { // Dereference flag
                "-n" => f.line_number = true,
                "-l" => f.only_files = true,
                "-i" => f.case_insensitive = true,
                "-v" => f.invert = true,
                "-x" => f.entire_line = true,
                _ => (), // Ignore unknown flags
            }
        }
        f
    }
}

// Helper function for matching logic
fn line_matches(pattern: &str, line: &str, flags: &Flags) -> bool {
    // Apply case insensitivity if needed
    let (pattern_to_match, line_to_check) = if flags.case_insensitive {
        (pattern.to_lowercase(), line.to_lowercase())
    } else {
        (pattern.to_string(), line.to_string()) // Avoid unnecessary allocation if not insensitive
    };

    // Perform the match based on -x flag
    let is_match = if flags.entire_line {
        line_to_check == pattern_to_match
    } else {
        line_to_check.contains(&pattern_to_match)
    };

    // Apply inversion if needed
    if flags.invert { !is_match } else { is_match }
}


pub fn grep(pattern: &str, flags: &Flags, files: &[&str]) -> Result<Vec<String>, Error> {
    let mut results = Vec::new();
    let mut matched_files = HashSet::new(); // Use HashSet for efficient duplicate handling with -l
    let multiple_files = files.len() > 1;

    for file_path in files {
        // Use anyhow's context for better error messages
        let content = fs::read_to_string(file_path)
            .map_err(|e| anyhow::anyhow!("Failed to read file {}: {}", file_path, e))?;

        for (line_num, line) in content.lines().enumerate() {
            if line_matches(pattern, line, flags) {
                if flags.only_files {
                    // If -l, just record the filename and stop processing this file
                    matched_files.insert(file_path.to_string());
                    break; // Move to the next file immediately after first match
                } else {
                    // Format the output line
                    let mut result_line = String::new();
                    if multiple_files {
                        result_line.push_str(file_path);
                        result_line.push(':');
                    }
                    if flags.line_number {
                        result_line.push_str(&(line_num + 1).to_string()); // line_num is 0-based
                        result_line.push(':');
                    }
                    result_line.push_str(line);
                    results.push(result_line);
                }
            }
        }
    }

    if flags.only_files {
        // If -l, convert the HashSet to a Vec and sort it
        let mut sorted_files: Vec<String> = matched_files.into_iter().collect();
        sorted_files.sort_unstable(); // Sort for deterministic output
        Ok(sorted_files)
    } else {
        // Otherwise, return the collected matching lines
        Ok(results)
    }
}
