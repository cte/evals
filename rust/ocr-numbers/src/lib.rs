// The code below is a stub. Just enough to satisfy the compiler.
// In order to pass the tests you can add-to or change any of this code.

#[derive(Debug, PartialEq, Eq)]
pub enum Error {
    InvalidRowCount(usize),
    InvalidColumnCount(usize),
}

pub fn convert(input: &str) -> Result<String, Error> {
    let lines: Vec<&str> = input.lines().collect();
    let row_count = lines.len();

    if row_count % 4 != 0 {
        return Err(Error::InvalidRowCount(row_count));
    }

    if row_count == 0 {
        return Ok(String::new());
    }

    let col_count = lines[0].len();
    if col_count % 3 != 0 {
        return Err(Error::InvalidColumnCount(col_count));
    }

    for line in &lines {
        if line.len() != col_count {
            return Err(Error::InvalidColumnCount(line.len()));
        }
    }

    use std::collections::HashMap;
    let mut patterns = HashMap::new();
    patterns.insert(
        " _ \n| |\n|_|\n   ",
        '0',
    );
    patterns.insert(
        "   \n  |\n  |\n   ",
        '1',
    );
    patterns.insert(
        " _ \n _|\n|_ \n   ",
        '2',
    );
    patterns.insert(
        " _ \n _|\n _|\n   ",
        '3',
    );
    patterns.insert(
        "   \n|_|\n  |\n   ",
        '4',
    );
    patterns.insert(
        " _ \n|_ \n _|\n   ",
        '5',
    );
    patterns.insert(
        " _ \n|_ \n|_|\n   ",
        '6',
    );
    patterns.insert(
        " _ \n  |\n  |\n   ",
        '7',
    );
    patterns.insert(
        " _ \n|_|\n|_|\n   ",
        '8',
    );
    patterns.insert(
        " _ \n|_|\n _|\n   ",
        '9',
    );

    let mut result_lines = Vec::new();

    for chunk in lines.chunks(4) {
        let mut line_digits = String::new();
        for col in (0..col_count).step_by(3) {
            let mut parts = Vec::new();
            for row in 0..4 {
                parts.push(&chunk[row][col..col + 3]);
            }
            let pattern = parts.join("\n");
            let digit = patterns.get(pattern.as_str()).copied().unwrap_or('?');
            line_digits.push(digit);
        }
        result_lines.push(line_digits);
    }

    Ok(result_lines.join(","))
}
