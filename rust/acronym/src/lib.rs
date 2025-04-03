pub fn abbreviate(phrase: &str) -> String {
    let mut acronym = String::new();
    let mut new_word = true;
    let mut prev_is_upper = false;

    for c in phrase.chars() {
        if c.is_alphanumeric() {
            if new_word {
                acronym.push(c.to_ascii_uppercase());
                prev_is_upper = c.is_uppercase();
                new_word = false;
            } else if c.is_uppercase() && !prev_is_upper {
                // add uppercase letter only if previous was not uppercase (CamelCase)
                acronym.push(c);
                prev_is_upper = true;
            } else {
                prev_is_upper = c.is_uppercase();
            }
        } else if c != '\'' {
            new_word = true;
            prev_is_upper = false;
        }
        // else, apostrophe inside word, do not reset new_word or prev_is_upper
    }

    acronym
}
