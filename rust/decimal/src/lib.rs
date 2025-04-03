use num_bigint::BigInt;
use num_traits::Zero;

/// Type implementing arbitrary-precision decimal arithmetic
#[derive(Debug, Clone)]
pub struct Decimal {
    sign: i8,        // 1 for positive, -1 for negative
    int_val: BigInt, // absolute value of digits, no decimal point
    scale: usize,    // number of digits after decimal point
}
impl PartialEq for Decimal {
    fn eq(&self, other: &Self) -> bool {
        if self.sign != other.sign {
            return self.is_zero() && other.is_zero();
        }

        let (lhs_int, rhs_int) = self.align_scales(other);
        lhs_int == rhs_int
    }
}

impl Eq for Decimal {}

impl PartialOrd for Decimal {
    fn partial_cmp(&self, other: &Self) -> Option<std::cmp::Ordering> {
        if self == other {
            return Some(std::cmp::Ordering::Equal);
        }

        if self.sign != other.sign {
            return Some(if self.sign > other.sign {
                std::cmp::Ordering::Greater
            } else {
                std::cmp::Ordering::Less
            });
        }

        let (lhs_int, rhs_int) = self.align_scales(other);

        let ord = lhs_int.cmp(&rhs_int);
        if self.sign == 1 {
            Some(ord)
        } else {
            Some(ord.reverse())
        }
    }
}

impl Ord for Decimal {
    fn cmp(&self, other: &Self) -> std::cmp::Ordering {
        self.partial_cmp(other).unwrap()
    }
}

impl Decimal {
    fn is_zero(&self) -> bool {
        self.int_val.is_zero()
    }

    fn align_scales(&self, other: &Self) -> (BigInt, BigInt) {
impl std::ops::Add for Decimal {
    type Output = Decimal;

    fn add(self, rhs: Decimal) -> Decimal {
        let (lhs_int, rhs_int) = self.align_scales(&rhs);
        let scale = self.scale.max(rhs.scale);

        if self.sign == rhs.sign {
            Decimal {
                sign: self.sign,
                int_val: lhs_int + rhs_int,
                scale,
            }
        } else {
            match lhs_int.cmp(&rhs_int) {
                Ordering::Greater => Decimal {
                    sign: self.sign,
                    int_val: lhs_int - rhs_int,
                    scale,
                },
                Ordering::Less => Decimal {
                    sign: rhs.sign,
                    int_val: rhs_int - lhs_int,
                    scale,
                },
                Ordering::Equal => Decimal {
                    sign: 1,
                    int_val: BigInt::zero(),
                    scale,
                },
            }
        }
    }
}

impl std::ops::Sub for Decimal {
    type Output = Decimal;

    fn sub(self, rhs: Decimal) -> Decimal {
        self + Decimal {
            sign: -rhs.sign,
            int_val: rhs.int_val,
            scale: rhs.scale,
        }
    }
}

impl std::ops::Mul for Decimal {
    type Output = Decimal;

    fn mul(self, rhs: Decimal) -> Decimal {
        Decimal {
            sign: self.sign * rhs.sign,
            int_val: self.int_val * rhs.int_val,
            scale: self.scale + rhs.scale,
        }
    }
}
        if self.scale == other.scale {
            (self.int_val.clone(), other.int_val.clone())
        } else if self.scale > other.scale {
            let scale_diff = self.scale - other.scale;
            let factor = BigInt::from(10u32).pow(scale_diff as u32);
            (self.int_val.clone(), &other.int_val * factor)
        } else {
            let scale_diff = other.scale - self.scale;
            let factor = BigInt::from(10u32).pow(scale_diff as u32);
            (&self.int_val * factor, other.int_val.clone())
        }
    }
}

use std::cmp::Ordering;
use std::ops::{Add, Sub, Mul};

impl Decimal {
    pub fn try_from(input: &str) -> Option<Decimal> {
        let input = input.trim();

        if input.is_empty() {
            return None;
        }

        let mut chars = input.chars().peekable();
        let mut sign = 1i8;

        if let Some(&c) = chars.peek() {
            if c == '-' {
                sign = -1;
                chars.next();
            } else if c == '+' {
                chars.next();
            }
        }

        let mut int_str = String::new();
        let mut scale = 0usize;
        let mut seen_dot = false;

        for c in chars {
            if c == '.' {
                if seen_dot {
                    return None; // multiple dots invalid
                }
                seen_dot = true;
            } else if c.is_ascii_digit() {
                int_str.push(c);
                if seen_dot {
                    scale += 1;
                }
            } else {
                return None; // invalid character
            }
        }

        if int_str.is_empty() {
            int_str.push('0');
            scale = 0;
        }

        // Remove leading zeros
        while int_str.starts_with('0') && int_str.len() > 1 {
            int_str.remove(0);
        }

        let int_val = int_str.parse::<BigInt>().ok()?;

        Some(Decimal {
            sign,
            int_val,
            scale,
        })
    }
}
