pub fn answer(command: &str) -> Option<i32> {
    let mut s = command.trim();

    if !s.starts_with("What is ") || !s.ends_with('?') {
        return None;
    }

    s = &s[8..s.len() - 1]; // remove "What is " and trailing '?'
    let s = s.trim();

    if s.is_empty() {
        return None;
    }

    let mut tokens = s.split_whitespace().peekable();

    let parse_number = |token: &str| token.parse::<i32>().ok();

    let mut result = match tokens.next() {
        Some(token) => parse_number(token)?,
        None => return None,
    };

    while let Some(op) = tokens.next() {
        match op {
            "plus" => {
                let next = tokens.next()?;
                let num = parse_number(next)?;
                result += num;
            }
            "minus" => {
                let next = tokens.next()?;
                let num = parse_number(next)?;
                result -= num;
            }
            "multiplied" => {
                if tokens.next()? != "by" {
                    return None;
                }
                let next = tokens.next()?;
                let num = parse_number(next)?;
                result *= num;
            }
            "divided" => {
                if tokens.next()? != "by" {
                    return None;
                }
                let next = tokens.next()?;
                let num = parse_number(next)?;
                result /= num;
            }
            #[cfg(feature = "exponentials")]
            "raised" => {
                if tokens.next()? != "to" {
                    return None;
                }
                let ordinal = tokens.next()?;
                // remove "th", "st", "nd", "rd" suffix
                let power_str = ordinal.trim_end_matches(|c: char| c.is_alphabetic());
                let power = power_str.parse::<u32>().ok()?;
                if tokens.next()? != "power" {
                    return None;
                }
                result = result.pow(power) as i32;
            }
            _ => return None,
        }
    }

    Some(result)
}
