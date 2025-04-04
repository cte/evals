pub fn answer(command: &str) -> Option<i32> {
    // Use `strip_prefix` and `strip_suffix` which return Option<&str>
    // Chain them with `?` to return None early if they fail.
    let core_command = command
        .strip_prefix("What is")?
        .strip_suffix('?')?
        .trim();

    // Handle empty string after stripping (e.g., "What is?")
    if core_command.is_empty() {
        return None;
    }

    // Split into words
    let words = core_command.split_whitespace().collect::<Vec<&str>>();
    let mut word_iter = words.into_iter(); // Use an iterator

    // Must start with a number
    // Use `next()` on the iterator and `?` to handle missing first word
    // Use `parse::<i32>().ok()?` to parse and return None if parsing fails
    let mut current_result = word_iter.next()?.parse::<i32>().ok()?;

    // Process remaining words (operator, number pairs)
    while let Some(operator_word) = word_iter.next() {
        // Determine the operation and how many words it consumes (1 for plus/minus, 2 for multiplied/divided by)
        let (operation, consumes_by): (fn(i32, i32) -> Option<i32>, bool) = match operator_word {
            "plus" => (i32::checked_add, false),
            "minus" => (i32::checked_sub, false),
            "multiplied" => (i32::checked_mul, true),
            "divided" => (i32::checked_div, true),
            // Any other word here is an invalid operator or sequence
            _ => return None,
        };

        // If the operator consumes "by", check and consume it
        if consumes_by {
            // Use `next()` and check if it's "by". Return None if missing or not "by".
            if word_iter.next()? != "by" {
                return None;
            }
        }

        // Expecting a number next
        // Use `next()` to get the number word, `?` if missing
        // Use `parse::<i32>().ok()?` to parse and return None if parsing fails
        let number = word_iter.next()?.parse::<i32>().ok()?;

        // Perform the operation using the checked method
        // Use `?` to return None if the operation results in None (e.g., overflow, div by zero)
        current_result = operation(current_result, number)?;
    }

    // If we successfully processed all words in the iterator
    Some(current_result)
}
