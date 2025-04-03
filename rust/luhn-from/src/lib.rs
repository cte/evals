pub struct Luhn {
    number: String,
}

impl Luhn {
    pub fn is_valid(&self) -> bool {
        let digits: Vec<u32> = self
            .number
            .chars()
            .filter_map(|c| c.to_digit(10))
            .collect();

        if digits.len() <= 1 {
            return false;
        }

        let sum: u32 = digits
            .iter()
            .rev()
            .enumerate()
            .map(|(i, d)| {
                if i % 2 == 1 {
                    let dbl = d * 2;
                    if dbl > 9 {
                        dbl - 9
                    } else {
                        dbl
                    }
                } else {
                    *d
                }
            })
            .sum();

        sum % 10 == 0
    }
}

impl From<&str> for Luhn {
    fn from(input: &str) -> Self {
        let number = input.chars()
            .filter(|c| !c.is_whitespace())
            .collect::<String>();

        if number.chars().all(|c| c.is_ascii_digit()) {
            Luhn { number }
        } else {
            Luhn { number: String::new() }
        }
    }
}

impl From<String> for Luhn {
    fn from(input: String) -> Self {
        Luhn::from(input.as_str())
    }
}

macro_rules! impl_from_unsigned {
    ($($t:ty),*) => {
        $(
            impl From<$t> for Luhn {
                fn from(input: $t) -> Self {
                    Luhn { number: input.to_string() }
                }
            }
        )*
    };
}

impl_from_unsigned!(u8, u16, u32, u64, usize);
