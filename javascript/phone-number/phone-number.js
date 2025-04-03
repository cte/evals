//
// This is only a SKELETON file for the 'Phone Number' exercise. It's been provided as a
// convenience to get you started writing code faster.
//

export const clean = (input) => {
  // Check for letters
  if (/[a-zA-Z]/.test(input)) {
    throw new Error('Letters not permitted');
  }

  // Check for invalid punctuations (allow only digits, spaces, (), +, ., -)
  if (/[^0-9\s().+-]/.test(input)) {
    throw new Error('Punctuations not permitted');
  }

  // Remove all non-digit characters
  let digits = input.replace(/\D/g, '');

  if (digits.length < 10) {
    throw new Error('Incorrect number of digits');
  } else if (digits.length === 11) {
    if (digits[0] !== '1') {
      throw new Error('11 digits must start with 1');
    }
    digits = digits.slice(1);
  } else if (digits.length > 11) {
    throw new Error('More than 11 digits');
  } else if (digits.length !== 10) {
    throw new Error('Incorrect number of digits');
  }

  // Validate area code
  if (digits[0] === '0') {
    throw new Error('Area code cannot start with zero');
  }
  if (digits[0] === '1') {
    throw new Error('Area code cannot start with one');
  }

  // Validate exchange code
  if (digits[3] === '0') {
    throw new Error('Exchange code cannot start with zero');
  }
  if (digits[3] === '1') {
    throw new Error('Exchange code cannot start with one');
  }

  return digits;
};
