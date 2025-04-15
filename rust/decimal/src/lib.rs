use std::cmp::Ordering;
use std::ops::{Add, Mul, Sub};

/// Type implementing arbitrary-precision decimal arithmetic
#[derive(Debug, Clone)]
pub struct Decimal {
    // Digits stored in reverse order (least significant first) for easier math
    digits: Vec<u8>,
    scale: usize, // Using usize for scale now
    is_negative: bool,
}

impl Decimal {
    // Helper to create a zero decimal
    fn zero() -> Self {
        Decimal {
            digits: vec![0],
            scale: 0,
            is_negative: false,
        }
    }

    // Internal constructor - assumes valid inputs, performs normalization
    fn from_parts(mut digits: Vec<u8>, scale: usize, is_negative: bool) -> Self {
        // 1. Normalize digits: Remove leading zeros (most significant end of reversed vec)
        while digits.len() > 1 && digits.last() == Some(&0) {
            digits.pop();
        }

        // Handle the case where input was all zeros (e.g., "000.00")
        if digits.len() == 1 && digits[0] == 0 {
            return Decimal::zero();
        }

        // 2. Normalize scale: Remove trailing zeros from original number (leading zeros in reversed vec)
        //    and adjust scale accordingly.
        let mut effective_scale = scale;
        let mut leading_zeros_to_remove = 0;
        for &digit in digits.iter() {
            if digit == 0 && effective_scale > 0 {
                leading_zeros_to_remove += 1;
                effective_scale -= 1;
            } else {
                break;
            }
        }

        // Only drain if it doesn't remove all digits (unless the number is truly zero, handled above)
        if leading_zeros_to_remove > 0 && leading_zeros_to_remove < digits.len() {
             digits.drain(0..leading_zeros_to_remove);
        }


        // Final check for zero after normalization
        if digits.len() == 1 && digits[0] == 0 {
            return Decimal::zero();
        }

// Determine final sign *before* moving digits
let final_is_negative = is_negative && !(digits.len() == 1 && digits[0] == 0);

Decimal {
    digits,
    scale: effective_scale,
    is_negative: final_is_negative,
}
        }
    }

    pub fn try_from(input: &str) -> Option<Decimal> {
        let input = input.trim();
        if input.is_empty() {
            return None;
        }

        let mut is_negative = false;
        let mut num_part = input;

        if input.starts_with('-') {
            is_negative = true;
            num_part = &input[1..];
        } else if input.starts_with('+') {
            num_part = &input[1..];
        }

        // Handle cases like "." or "-." -> treat as 0
        if num_part == "." {
            return Some(Decimal::zero());
        }
         // Ensure there's at least one digit if only sign is present
        if num_part.is_empty() && input.len() == 1 && (input == "-" || input == "+") {
             return None; // Invalid: just a sign
        }

        let mut parts = num_part.splitn(2, '.');
        let integer_part_str = parts.next().unwrap_or("");
        let fractional_part_str = parts.next().unwrap_or("");

        // Validate parts contain only digits
        let is_integer_valid = integer_part_str.is_empty() || integer_part_str.chars().all(|c| c.is_ascii_digit());
        let is_fractional_valid = fractional_part_str.is_empty() || fractional_part_str.chars().all(|c| c.is_ascii_digit());

        if !is_integer_valid || !is_fractional_valid {
             return None; // Invalid characters found
        }

        // Handle case where both parts are empty (e.g. input was just "+" or "-") which is invalid
        if integer_part_str.is_empty() && fractional_part_str.is_empty() && num_part != "." {
             if num_part == "0" {
                 // Valid: "0", "+0", "-0"
             } else {
                return None;
             }
        }

        // Remove leading zeros from integer part unless it's just "0"
        let int_trimmed = if integer_part_str.len() > 1 && integer_part_str.starts_with('0') {
            integer_part_str.trim_start_matches('0')
        } else {
            integer_part_str
        };
        // If trimming resulted in empty string, but original wasn't empty, it means it was all zeros.
        let int_final = if int_trimmed.is_empty() && !integer_part_str.is_empty() {
            "0"
        } else {
            int_trimmed
        };


        let combined_str = format!("{}{}", int_final, fractional_part_str);
        let scale = fractional_part_str.len();

        // Handle empty combined string (e.g., "0", "+0", "-0", ".")
        if combined_str.is_empty() || combined_str.chars().all(|c| c == '0') {
             // Check if the original input was valid before returning zero
             // e.g. reject "" or "-" or "+" which might lead here
             if num_part == "0" || num_part == "." || (!integer_part_str.is_empty() || !fractional_part_str.is_empty()) {
                return Some(Decimal::zero());
             } else {
                 return None; // Invalid input like "-" or "+"
             }
        }


        // Convert combined string to digits vector (reversed)
        let digits: Vec<u8> = combined_str
            .chars()
            .filter_map(|c| c.to_digit(10).map(|d| d as u8)) // Ensure only digits are processed
            .rev() // Reverse to store least significant digit first
            .collect();

        // If after filtering digits, the vector is empty, it means input was invalid (e.g., "abc")
        if digits.is_empty() && !combined_str.is_empty() && !combined_str.chars().all(|c| c == '0') {
            return None;
        }
        // Handle case where digits might become just [0] after filtering/reversing
        if digits.is_empty() || (digits.len() == 1 && digits[0] == 0) {
             return Some(Decimal::zero());
        }


        Some(Decimal::from_parts(digits, scale, is_negative))
    }

    // Helper function to normalize two Decimals to a common scale and length
    // Returns normalized digit vectors (reversed) and the common scale.
    fn normalize<'a>(d1: &'a Decimal, d2: &'a Decimal) -> (Vec<u8>, Vec<u8>, usize) {
        let common_scale = d1.scale.max(d2.scale);
        let mut digits1 = d1.digits.clone();
        let mut digits2 = d2.digits.clone();

        // 1. Adjust scale by adding leading zeros (representing trailing zeros in the number)
        if d1.scale < common_scale {
            let diff = common_scale - d1.scale;
            for _ in 0..diff {
                digits1.insert(0, 0); // Insert at the beginning (least significant end)
            }
        }
        if d2.scale < common_scale {
             let diff = common_scale - d2.scale;
            for _ in 0..diff {
                digits2.insert(0, 0); // Insert at the beginning
            }
        }

        // 2. Pad the shorter vector with trailing zeros (most significant end)
        //    so they have the same length for comparison/arithmetic.
        let len1 = digits1.len();
        let len2 = digits2.len();
        if len1 < len2 {
            digits1.resize(len2, 0); // Pad end (most significant) with 0s
        } else if len2 < len1 {
            digits2.resize(len1, 0); // Pad end (most significant) with 0s
        }


        (digits1, digits2, common_scale)
    }
}

// --- Trait implementations ---

impl PartialEq for Decimal {
     fn eq(&self, other: &Self) -> bool {
         // Handle 0 == -0
         let self_is_zero = self.digits.len() == 1 && self.digits[0] == 0;
         let other_is_zero = other.digits.len() == 1 && other.digits[0] == 0;
         if self_is_zero && other_is_zero {
             return true;
         }

         // If signs differ (and not zero), they are not equal
         if self.is_negative != other.is_negative {
             return false;
         }

         // Signs are the same (and non-zero), normalize and compare digits
         let (norm_digits1, norm_digits2, _) = Decimal::normalize(self, other);
         norm_digits1 == norm_digits2
     }
}
impl Eq for Decimal {}

impl PartialOrd for Decimal {
     fn partial_cmp(&self, other: &Self) -> Option<Ordering> {
         Some(self.cmp(other)) // Delegate to Ord::cmp
     }
}

impl Ord for Decimal {
    fn cmp(&self, other: &Self) -> Ordering {
        // Handle zeros first
        let self_is_zero = self.digits.len() == 1 && self.digits[0] == 0;
        let other_is_zero = other.digits.len() == 1 && other.digits[0] == 0;
        if self_is_zero && other_is_zero {
            return Ordering::Equal;
        }

        // Compare signs
        match (self.is_negative, other.is_negative) {
            (true, false) => return Ordering::Less,    // Negative < Positive (or Zero)
            (false, true) => return Ordering::Greater, // Positive (or Zero) > Negative
            _ => {} // Same sign, continue comparison
        }

        // Signs are the same, normalize
        let (norm_digits1, norm_digits2, _) = Decimal::normalize(self, other);

        // Compare digit vectors lexicographically from most significant digit (end of vec)
        let mut order = Ordering::Equal;
        for i in (0..norm_digits1.len()).rev() { // Iterate from end (MSD) to start (LSD)
             match norm_digits1[i].cmp(&norm_digits2[i]) {
                 Ordering::Equal => continue,
                 Ordering::Greater => {
                     order = Ordering::Greater;
                     break;
                 }
                 Ordering::Less => {
                     order = Ordering::Less;
                     break;
                 }
             }
        }


        // If negative, reverse the order (e.g., -2 < -1, but 2 > 1)
        if self.is_negative {
            order.reverse()
        } else {
            order
        }
    }
}


// --- Arithmetic Stubs ---
// These require significant implementation using vector arithmetic

// Helper function for vector addition (used by Add and Sub)
// Assumes digits1 >= digits2 in magnitude. Returns result digits and carry.
// Operates on reversed digit vectors.
fn add_vecs(digits1: &[u8], digits2: &[u8]) -> (Vec<u8>, u8) {
    let n = digits1.len();
    let m = digits2.len();
    let mut result = Vec::with_capacity(n + 1);
    let mut carry = 0u8;

    for i in 0..n {
        let d1 = digits1[i];
        let d2 = if i < m { digits2[i] } else { 0 }; // Pad shorter vec with 0
        let sum = d1 + d2 + carry;
        result.push(sum % 10);
        carry = sum / 10;
    }

    if carry > 0 {
        result.push(carry);
    }
    (result, carry) // Carry here usually means carry out of the MSD
}

// Helper function for vector subtraction (used by Add and Sub)
// Assumes digits1 >= digits2 in magnitude. Returns result digits.
// Operates on reversed digit vectors.
fn sub_vecs(digits1: &[u8], digits2: &[u8]) -> Vec<u8> {
    let n = digits1.len();
    let m = digits2.len();
    let mut result = Vec::with_capacity(n);
    let mut borrow = 0i8; // Use i8 for borrow

    for i in 0..n {
        let d1 = digits1[i] as i8;
        let d2 = if i < m { digits2[i] as i8 } else { 0 };
        let mut diff = d1 - d2 - borrow;
        if diff < 0 {
            diff += 10;
            borrow = 1;
        } else {
            borrow = 0;
        }
        result.push(diff as u8);
    }

    // Remove trailing zeros (which are leading zeros in the original number)
    while result.len() > 1 && result.last() == Some(&0) {
        result.pop();
    }
    result
}


impl Add for Decimal {
    type Output = Self;
    fn add(self, other: Self) -> Self {
        let (digits1, digits2, common_scale) = Decimal::normalize(&self, &other);

        let result_is_negative: bool;
        let result_digits: Vec<u8>;

        if self.is_negative == other.is_negative {
            // Same sign: Add magnitudes, keep sign
            result_is_negative = self.is_negative;
            let (digits, _) = add_vecs(&digits1, &digits2);
            result_digits = digits;
        } else {
            // Different signs: Subtract smaller magnitude from larger magnitude
            // Compare magnitudes first (ignoring sign)
            let mag_cmp = {
                 let mut order = Ordering::Equal;
                 for i in (0..digits1.len()).rev() {
                     match digits1[i].cmp(&digits2[i]) {
                         Ordering::Equal => continue,
                         ord => { order = ord; break; }
                     }
                 }
                 order
            };


            match mag_cmp {
                Ordering::Less => {
                    // other has larger magnitude
                    result_is_negative = other.is_negative;
                    result_digits = sub_vecs(&digits2, &digits1);
                }
                Ordering::Greater | Ordering::Equal => {
                    // self has larger or equal magnitude
                    result_is_negative = self.is_negative;
                    result_digits = sub_vecs(&digits1, &digits2);
                }
            }
        }

        Decimal::from_parts(result_digits, common_scale, result_is_negative)
    }
}

impl Sub for Decimal {
    type Output = Self;
    fn sub(self, other: Self) -> Self {
         // Subtraction is addition with the sign flipped on the second operand
         let flipped_other = Decimal {
             digits: other.digits,
             scale: other.scale,
             is_negative: !other.is_negative,
         };
         self.add(flipped_other)
    }
}

impl Mul for Decimal {
    type Output = Self;
    fn mul(self, other: Self) -> Self {
        let self_is_zero = self.digits.len() == 1 && self.digits[0] == 0;
        let other_is_zero = other.digits.len() == 1 && other.digits[0] == 0;
        if self_is_zero || other_is_zero {
            return Decimal::zero();
        }

        let n = self.digits.len();
        let m = other.digits.len();
        let mut result_digits_rev = vec![0u8; n + m]; // Max possible length

        // Standard multiplication algorithm
        for i in 0..n {
            let d1 = self.digits[i];
            if d1 == 0 { continue; } // Optimization
            let mut carry = 0u32; // Use u32 for intermediate sum/carry
            for j in 0..m {
                let d2 = other.digits[j];
                let product = (d1 as u32) * (d2 as u32) + (result_digits_rev[i + j] as u32) + carry;
                result_digits_rev[i + j] = (product % 10) as u8;
                carry = product / 10;
            }
            // Handle carry propagating beyond the inner loop
            let mut k = i + m;
            while carry > 0 {
                 if k >= result_digits_rev.len() { result_digits_rev.push(0); } // Should not happen with n+m allocation?
                 let sum = (result_digits_rev[k] as u32) + carry;
                 result_digits_rev[k] = (sum % 10) as u8;
                 carry = sum / 10;
                 k += 1;
            }
        }

        let result_scale = self.scale + other.scale;
        let result_is_negative = self.is_negative != other.is_negative;

        Decimal::from_parts(result_digits_rev, result_scale, result_is_negative)
    }
}
