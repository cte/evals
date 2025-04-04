//
// This is only a SKELETON file for the 'Binary' exercise. It's been provided as a
// convenience to get you started writing code faster.
//

export class Binary {
  constructor(binaryString) {
    this.binaryString = binaryString;
  }

  toDecimal() {
    // Validate input: Check for any characters other than '0' or '1'
    // Validate input: Check for any characters other than '0' or '1'
    if (/[^01]/.test(this.binaryString)) {
      // The test expects null for invalid input, not an error
      return null;
    }

    let decimalValue = 0;
    const len = this.binaryString.length;

    for (let i = 0; i < len; i++) {
      // Read the string from left to right
      const digit = parseInt(this.binaryString[i], 10);
      // Calculate the power based on position from the right end
      const power = len - 1 - i;
      decimalValue += digit * (2 ** power);
    }

    return decimalValue;
  }
}
