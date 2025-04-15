//
// This is only a SKELETON file for the 'Phone Number' exercise. It's been provided as a
// convenience to get you started writing code faster.
//

export const clean = (phoneNumber) => {
  // Check for letters
  if (/[a-zA-Z]/.test(phoneNumber)) {
    throw new Error('Letters not permitted');
  }

  // Check for disallowed punctuation
  // Allowed: digits, (, ), -, ., +, space
  if (/[^0-9().\-+ ]/.test(phoneNumber)) {
     throw new Error('Punctuations not permitted');
  }

  // Remove non-digit characters (now that we know only digits and allowed punctuation/space remain)
  let digits = phoneNumber.replace(/\D/g, '');

  // Check for country code 1
  if (digits.length === 11) {
    if (digits.startsWith('1')) {
      digits = digits.substring(1);
    } else {
      // If 11 digits but doesn't start with 1, it's invalid based on NANP rules for this exercise
      throw new Error('11 digits must start with 1');
    }
  }

  // Check length after potential country code removal
  if (digits.length !== 10) {
     if (digits.length > 10) {
       throw new Error('More than 11 digits'); // Or should this be caught by the punctuation check? Test implies this.
     } else { // length < 10
       throw new Error('Incorrect number of digits');
     }
  }

  // Validate Area Code (NXX) - N cannot be 0 or 1
  if (digits[0] === '0') {
    throw new Error('Area code cannot start with zero');
  }
  if (digits[0] === '1') {
    throw new Error('Area code cannot start with one');
  }

  // Validate Exchange Code (NXX) - N cannot be 0 or 1
  if (digits[3] === '0') {
    throw new Error('Exchange code cannot start with zero');
  }
  if (digits[3] === '1') {
    throw new Error('Exchange code cannot start with one');
  }

  return digits;
};
