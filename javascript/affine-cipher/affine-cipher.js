const ALPHABET = 'abcdefghijklmnopqrstuvwxyz';
const M = ALPHABET.length;
const GROUP_SIZE = 5;

// Greatest Common Divisor (GCD) using Euclidean algorithm
const gcd = (a, b) => {
  while (b) {
    [a, b] = [b, a % b];
  }
  return a;
};

// Modular Multiplicative Inverse (MMI)
// Finds x such that (a * x) % m === 1
const mmi = (a, m) => {
  if (gcd(a, m) !== 1) {
    throw new Error('a and m must be coprime.');
  }
  for (let x = 1; x < m; x++) {
    if ((a * x) % m === 1) {
      return x;
    }
  }
  // Should not happen if gcd(a, m) === 1, but included for completeness
  throw new Error('MMI not found.');
};

// Helper to normalize and clean the input phrase
const cleanPhrase = (phrase) => {
  return phrase.toLowerCase().replace(/[^a-z0-9]/g, '');
};

export const encode = (phrase, key) => {
  const { a, b } = key;

  if (gcd(a, M) !== 1) {
    throw new Error('a and m must be coprime.');
  }

  const cleaned = cleanPhrase(phrase);
  let encoded = '';
  let count = 0;

  for (const char of cleaned) {
    if (ALPHABET.includes(char)) {
      const i = ALPHABET.indexOf(char);
      const encodedIndex = (a * i + b) % M;
      encoded += ALPHABET[encodedIndex];
      count++;
      if (count > 0 && count % GROUP_SIZE === 0) { // Add space after every GROUP_SIZE chars
        encoded += ' ';
      }
    } else if (/\d/.test(char)) {
      // Keep digits, don't encrypt them
      encoded += char;
      count++;
       if (count > 0 && count % GROUP_SIZE === 0) { // Add space after every GROUP_SIZE chars
        encoded += ' ';
      }
    }
    // Ignore other characters (punctuation, etc.)
  }

  // Remove trailing space if added
  return encoded.trimEnd();
};

export const decode = (cipher, key) => {
  const { a, b } = key;

  if (gcd(a, M) !== 1) {
    throw new Error('a and m must be coprime.');
  }

  const aInv = mmi(a, M);
  const cleanedCipher = cipher.replace(/\s/g, ''); // Remove spaces for decoding
  let decoded = '';

  for (const char of cleanedCipher) {
    if (ALPHABET.includes(char)) {
      const y = ALPHABET.indexOf(char);
      // Use ((y - b) % M + M) % M to handle potential negative results correctly
      const decodedIndex = (aInv * ((y - b) % M + M)) % M;
      decoded += ALPHABET[decodedIndex];
    } else if (/\d/.test(char)) {
      // Keep digits
      decoded += char;
    }
    // Ignore other characters
  }

  return decoded;
};
