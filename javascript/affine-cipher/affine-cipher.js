const m = 26;

function gcd(a, b) {
  while (b !== 0) {
    [a, b] = [b, a % b];
  }
  return a;
}

function modInverse(a, m) {
  a = ((a % m) + m) % m;
  for (let x = 1; x < m; x++) {
    if ((a * x) % m === 1) {
      return x;
    }
  }
  throw new Error('a and m must be coprime.');
}

function isCoprime(a, m) {
  return gcd(a, m) === 1;
}

function mod(n, m) {
  return ((n % m) + m) % m;
}

function cleanInput(phrase) {
  return phrase.toLowerCase().replace(/[^a-z0-9]/g, '');
}

function groupFive(text) {
  return text.replace(/(.{5})/g, '$1 ').trim();
}

export const encode = (phrase, key) => {
  const { a, b } = key;
  if (!isCoprime(a, m)) {
    throw new Error('a and m must be coprime.');
  }

  const cleaned = cleanInput(phrase);
  let encoded = '';

  for (const char of cleaned) {
    if (/[a-z]/.test(char)) {
      const x = char.charCodeAt(0) - 97;
      const encrypted = mod(a * x + b, m);
      encoded += String.fromCharCode(encrypted + 97);
    } else {
      encoded += char;
    }
  }

  return groupFive(encoded);
};

export const decode = (phrase, key) => {
  const { a, b } = key;
  if (!isCoprime(a, m)) {
    throw new Error('a and m must be coprime.');
  }

  const a_inv = modInverse(a, m);
  const cleaned = phrase.toLowerCase().replace(/[^a-z0-9]/g, '');
  let decoded = '';

  for (const char of cleaned) {
    if (/[a-z]/.test(char)) {
      const y = char.charCodeAt(0) - 97;
      const decrypted = mod(a_inv * (y - b), m);
      decoded += String.fromCharCode(decrypted + 97);
    } else {
      decoded += char;
    }
  }

  return decoded;
};
