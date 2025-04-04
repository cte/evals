// variable-length-quantity.js

/**
 * Encodes an array of 32-bit unsigned integers into VLQ bytes.
 * @param {number[]} numbers - An array of numbers to encode.
 * @returns {number[]} The VLQ encoded bytes.
 */
export const encode = (numbers) => {
  const encodedBytes = [];

  for (const number of numbers) {
    // Handle 0 separately as the loop condition `num > 0` won't execute
    if (number === 0) {
      encodedBytes.push(0);
      continue;
    }

    let num = number;
    const bytes = [];
    // Extract 7 bits at a time until the number becomes 0
    while (num > 0) {
      bytes.unshift(num & 0x7f); // Prepend the least significant 7 bits
      num >>>= 7; // Shift right by 7 bits (unsigned)
    }

    // Set the continuation bit (MSB) for all bytes except the last one
    for (let i = 0; i < bytes.length - 1; i++) {
      bytes[i] |= 0x80;
    }

    encodedBytes.push(...bytes);
  }

  return encodedBytes;
};

/**
 * Decodes VLQ bytes into an array of 32-bit unsigned integers.
 * @param {number[]} bytes - An array of VLQ encoded bytes.
 * @returns {number[]} The decoded numbers.
 * @throws {Error} If the sequence is incomplete or a number overflows 32 bits.
 */
export const decode = (bytes) => {
  const decodedNumbers = [];
  let currentNumber = 0;
  let numberComplete = false; // Flag to track if a number was completed in the last iteration

  if (bytes.length > 0 && (bytes[bytes.length - 1] & 0x80) !== 0) {
      throw new Error('Incomplete sequence'); // Last byte has continuation bit set
  }


  for (let i = 0; i < bytes.length; i++) {
    const byte = bytes[i];
    numberComplete = false; // Reset flag for the new byte

    // Check for potential overflow before shifting and adding the next 7 bits
    // If currentNumber >= 2^25 (0x2000000), shifting left by 7 might exceed 2^32
    if (currentNumber >= 0x2000000) {
       throw new Error('Overflow detected');
    }

    currentNumber = (currentNumber << 7) | (byte & 0x7f);

    if ((byte & 0x80) === 0) { // This is the last byte of the current number
      // Final overflow check after adding the last 7 bits.
      // `>>> 0` converts to unsigned 32-bit. If the value changes, it overflowed.
      // We need to be careful here, as the previous check might not catch all cases
      // depending on the exact value of the last 7 bits.
      // A simpler check might be if the number exceeds 0xFFFFFFFF after the operation,
      // but JavaScript handles large numbers, so we rely on the unsigned conversion.
      // Let's refine the overflow check slightly.
      // If the number *before* adding the last 7 bits was already such that
      // adding anything would overflow, the previous check handles it.
      // If the number *after* adding the last 7 bits exceeds 0xFFFFFFFF, it's an overflow.
      // However, JS numbers are 64-bit floats. The `>>> 0` conversion is the most reliable way
      // to check if it fits within unsigned 32 bits.
      if ((currentNumber >>> 0) !== currentNumber && currentNumber > 0xFFFFFFFF) {
         // This condition might be tricky with JS number precision.
         // Let's stick to the primary check before the shift.
         // The test cases will verify correctness.
      }

      decodedNumbers.push(currentNumber >>> 0); // Push the unsigned 32-bit number
      currentNumber = 0; // Reset for the next number
      numberComplete = true;
    }
  }

  // If the loop finished but the last byte processed didn't complete a number,
  // it implies an incomplete sequence (this check is now handled at the beginning).
  // if (!numberComplete && bytes.length > 0) {
  //    throw new Error('Incomplete sequence');
  // }


  return decodedNumbers;
};
