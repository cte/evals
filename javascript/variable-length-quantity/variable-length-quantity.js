//
// This is only a SKELETON file for the 'Variable Length Quantity' exercise. It's been provided as a
// convenience to get you started writing code faster.
//

export const encode = (numbers) => {
  const bytes = [];
  for (const number of numbers) {
    if (number === 0) {
      bytes.push(0);
      continue;
    }
    const groups = [];
    let n = number >>> 0; // ensure unsigned
    // Use division and modulo to avoid bitwise issues
    while (n > 0) {
      groups.unshift(n % 128);
      n = Math.floor(n / 128);
    }
    for (let i = 0; i < groups.length - 1; i++) {
      bytes.push(groups[i] | 0x80); // set MSB for all but last
    }
    bytes.push(groups[groups.length - 1]); // last byte, MSB=0
  }
  return bytes;
};

export const decode = (bytes) => {
  const numbers = [];
  let current = 0;
  for (let i = 0; i < bytes.length; i++) {
    const byte = bytes[i];
    current = current * 128 + (byte & 0x7F);
    if ((byte & 0x80) === 0) {
      // convert to unsigned 32-bit explicitly
      current = current >>> 0;
      numbers.push(current);
      current = 0;
    }
  }
  if ((bytes.length > 0) && ((bytes[bytes.length - 1] & 0x80) !== 0)) {
    throw new Error('Incomplete sequence');
  }
  return numbers;
};
