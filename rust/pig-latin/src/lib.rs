pub fn translate(input: &str) -> String {
    input
        .split_whitespace()
        .map(|word| translate_word(word))
        .collect::<Vec<_>>()
        .join(" ")
}

fn translate_word(word: &str) -> String {
    let vowels = ["a", "e", "i", "o", "u"];
    let special_vowel_prefixes = ["xr", "yt"];

    if vowels.contains(&&word[0..1]) || special_vowel_prefixes.iter().any(|&p| word.starts_with(p)) {
        return format!("{word}ay");
    }

    let mut idx = 0;
    let chars: Vec<char> = word.chars().collect();
    let len = chars.len();

    while idx < len {
        let slice = &word[idx..];

        if idx > 0 && &slice[0..1] == "y" {
            break;
        }

        if vowels.contains(&&slice[0..1]) {
            break;
        }

        if slice.starts_with("qu") {
            idx += 2;
        } else if slice.starts_with("thr") || slice.starts_with("sch") {
            idx += 3;
        } else if slice.starts_with("ch") || slice.starts_with("th") {
            idx += 2;
        } else {
            idx += 1;
        }
    }

    format!("{}{}ay", &word[idx..], &word[..idx])
}
