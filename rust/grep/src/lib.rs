use anyhow::{anyhow, Error};
use std::fs::File;
use std::io::{BufRead, BufReader};

#[derive(Debug)]
pub struct Flags {
    pub print_line_numbers: bool,
    pub case_insensitive: bool,
    pub print_file_names: bool,
    pub match_entire_line: bool,
    pub invert_match: bool,
}

impl Flags {
    pub fn new(flags: &[&str]) -> Self {
        let mut f = Flags {
            print_line_numbers: false,
            case_insensitive: false,
            print_file_names: false,
            match_entire_line: false,
            invert_match: false,
        };
        for &flag in flags {
            match flag {
                "-n" => f.print_line_numbers = true,
                "-i" => f.case_insensitive = true,
                "-l" => f.print_file_names = true,
                "-x" => f.match_entire_line = true,
                "-v" => f.invert_match = true,
                _ => {}
            }
        }
        f
    }
}

pub fn grep(pattern: &str, flags: &Flags, files: &[&str]) -> Result<Vec<String>, Error> {
    let mut results = Vec::new();
    for &file_name in files {
        let file = File::open(file_name);
        if file.is_err() {
            return Err(anyhow!("File not found: {}", file_name));
        }
        let file = file.unwrap();
        let reader = BufReader::new(file);

        let mut file_has_match = false;
        for (idx, line_result) in reader.lines().enumerate() {
            let line = line_result?;
            let mut matched = if flags.case_insensitive {
                if flags.match_entire_line {
                    line.eq_ignore_ascii_case(pattern)
                } else {
                    line.to_lowercase().contains(&pattern.to_lowercase())
                }
            } else {
                if flags.match_entire_line {
                    line == pattern
                } else {
                    line.contains(pattern)
                }
            };

            if flags.invert_match {
                matched = !matched;
            }

            if matched {
                file_has_match = true;
                if flags.print_file_names {
                    break;
                }
                let mut output = String::new();
                if files.len() > 1 {
                    output.push_str(file_name);
                    output.push(':');
                }
                if flags.print_line_numbers {
                    output.push_str(&(idx + 1).to_string());
                    output.push(':');
                }
                output.push_str(&line);
                results.push(output);
            }
        }
        if flags.print_file_names && file_has_match {
            results.push(file_name.to_string());
        }
    }
    Ok(results)
}
