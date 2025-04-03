pub fn encode(n: u64) -> String {
    if n == 0 {
        return "zero".to_string();
    }

    let below_20 = [
        "", "one", "two", "three", "four", "five", "six", "seven", "eight", "nine", "ten",
        "eleven", "twelve", "thirteen", "fourteen", "fifteen", "sixteen", "seventeen", "eighteen", "nineteen",
    ];

    let tens = [
        "", "", "twenty", "thirty", "forty", "fifty", "sixty", "seventy", "eighty", "ninety",
    ];

    let thousands = [
        "", "thousand", "million", "billion", "trillion", "quadrillion", "quintillion",
    ];

    fn helper(mut num: u64, below_20: &[&str], tens: &[&str]) -> String {
        let mut parts = Vec::new();

        if num >= 100 {
            parts.push(format!("{} hundred", below_20[(num / 100) as usize]));
            num %= 100;
        }

        if num >= 20 {
            let ten_part = tens[(num / 10) as usize];
            let unit = num % 10;
            if unit != 0 {
                parts.push(format!("{}-{}", ten_part, below_20[unit as usize]));
            } else {
                parts.push(ten_part.to_string());
            }
        } else if num > 0 {
            parts.push(below_20[num as usize].to_string());
        }

        parts.join(" ")
    }

    let mut num = n;
    let mut parts = Vec::new();
    let mut idx = 0;

    while num > 0 {
        let rem = num % 1000;
        if rem != 0 {
            let mut segment = helper(rem, &below_20, &tens);
            if !thousands[idx].is_empty() {
                segment.push(' ');
                segment.push_str(thousands[idx]);
            }
            parts.push(segment);
        }
        num /= 1000;
        idx += 1;
    }

    parts.reverse();
    parts.join(" ").trim().to_string()
}
