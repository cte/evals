export class Palindromes {
  static generate({ minFactor, maxFactor }) {
    if (minFactor > maxFactor) {
      throw new Error('min must be <= max');
    }

    let smallest = { value: null, factors: [] };
    let largest = { value: null, factors: [] };
    const palindromes = new Map(); // Store product -> factors mapping

    for (let i = minFactor; i <= maxFactor; i++) {
      for (let j = i; j <= maxFactor; j++) {
        const product = i * j;

        if (this.isPalindrome(product)) {
          if (!palindromes.has(product)) {
            palindromes.set(product, []);
          }
          palindromes.get(product).push([i, j]);

          // Update smallest
          if (smallest.value === null || product < smallest.value) {
            smallest = { value: product, factors: [[i, j]] };
          } else if (product === smallest.value) {
            smallest.factors.push([i, j]);
          }

          // Update largest
          if (largest.value === null || product > largest.value) {
            largest = { value: product, factors: [[i, j]] };
          } else if (product === largest.value) {
            largest.factors.push([i, j]);
          }
        }
      }
    }

    // Ensure factors are correctly populated from the map if needed
    // (This handles cases where the initial assignment might miss other factor pairs for the same smallest/largest value found later)
    if (smallest.value !== null) {
        smallest.factors = palindromes.get(smallest.value);
    }
     if (largest.value !== null) {
        largest.factors = palindromes.get(largest.value);
    }


    return { smallest, largest };
  }

  static isPalindrome(num) {
    const str = String(num);
    return str === str.split('').reverse().join('');
  }
}
