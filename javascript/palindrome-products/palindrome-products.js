//
// This is only a SKELETON file for the 'Palindrome Products' exercise. It's been provided as a
// convenience to get you started writing code faster.
//

export class Palindromes {
  static generate({ minFactor, maxFactor }) {
    if (minFactor > maxFactor) {
      throw new Error('min must be <= max');
    }

    const isPalindrome = (num) => {
      const str = String(num);
      return str === str.split('').reverse().join('');
    };

    const palindromes = new Map();

    for (let i = minFactor; i <= maxFactor; i++) {
      for (let j = i; j <= maxFactor; j++) {
        const product = i * j;
        if (isPalindrome(product)) {
          if (!palindromes.has(product)) {
            palindromes.set(product, []);
          }
          palindromes.get(product).push([i, j]);
        }
      }
    }

    if (palindromes.size === 0) {
      return { smallest: { value: null, factors: [] }, largest: { value: null, factors: [] } };
    }

    const palindromeValues = Array.from(palindromes.keys()).sort((a, b) => a - b);
    const smallestValue = palindromeValues[0];
    const largestValue = palindromeValues[palindromeValues.length - 1];

    return {
      smallest: { value: smallestValue, factors: palindromes.get(smallestValue) },
      largest: { value: largestValue, factors: palindromes.get(largestValue) },
    };
  }
}
