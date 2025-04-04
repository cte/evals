// killer-sudoku-helper.js

/**
 * Helper function to generate combinations recursively.
 * It finds combinations of 'count' unique digits starting from 'startDigit',
 * summing up to 'targetSum', and avoiding digits in 'excludeSet'.
 *
 * @param {number} targetSum - The desired sum of the combination.
 * @param {number} count - The desired number of digits in the combination.
 * @param {number} startDigit - The minimum digit value to include in the rest of the combination.
 * @param {number[]} currentCombo - The combination built so far.
 * @param {number} currentSum - The sum of digits in currentCombo.
 * @param {Set<number>} excludeSet - A set of digits that cannot be used.
 * @param {number[][]} results - An array to store the valid combinations found.
 */
const findCombinationsRecursive = (
  targetSum,
  count,
  startDigit,
  currentCombo,
  currentSum,
  excludeSet,
  results
) => {
  // Base case: Combination has the required size
  if (currentCombo.length === count) {
    if (currentSum === targetSum) {
      // Check if any digit in the combination is excluded
      // This check is technically redundant if we check exclusions before adding a digit,
      // but it's a final safeguard.
      if (!currentCombo.some(digit => excludeSet.has(digit))) {
        results.push([...currentCombo]); // Add a copy
      }
    }
    return; // Stop exploring further down this path
  }

  // Pruning: If the current sum already exceeds targetSum, stop.
  if (currentSum >= targetSum) {
    return;
  }

  // Recursive step: Try adding digits from startDigit up to 9
  for (let digit = startDigit; digit <= 9; digit++) {
    // Check if this digit is excluded
    if (excludeSet.has(digit)) {
      continue;
    }

    // Optimization: Check if enough digits are left to form a combination of 'count' size
    const remainingDigitsNeeded = count - currentCombo.length - 1; // -1 because we are adding 'digit' now
    const maxPossibleRemainingDigit = 9;
    if (maxPossibleRemainingDigit - digit < remainingDigitsNeeded) {
      // Not enough unique digits left (e.g., need 3 more, current digit is 8, only 9 is left)
      break; // Since digits increase, no further digit will work either
    }

    // Optimization: Check if the smallest possible sum using this digit exceeds the target
    let minPossibleSum = currentSum + digit;
    for (let i = 1; i <= remainingDigitsNeeded; i++) {
      minPossibleSum += (digit + i); // Smallest subsequent unique digits
    }
    if (minPossibleSum > targetSum) {
      // Adding this digit and the smallest possible subsequent ones already exceeds the sum.
      // Since we iterate digits upwards, trying larger digits will only make the sum larger.
      break; // No need to check larger digits for the current position
    }

    // Optimization: Check if the largest possible sum is less than the target
    let maxPossibleSum = currentSum + digit;
    for (let i = 0; i < remainingDigitsNeeded; i++) {
      maxPossibleSum += (maxPossibleRemainingDigit - i); // Largest subsequent unique digits
    }
    if (maxPossibleSum < targetSum) {
      // Adding this digit and the largest possible subsequent ones is not enough.
      // Continue to the next digit, as a larger digit might make it possible.
      continue;
    }


    // Add the digit and recurse
    currentCombo.push(digit);
    // Next digit must be greater than the current one to ensure uniqueness and sorted order
    findCombinationsRecursive(
      targetSum,
      count,
      digit + 1, // Next digit must be larger
      currentCombo,
      currentSum + digit, // Update sum
      excludeSet,
      results
    );
    currentCombo.pop(); // Backtrack
  }
};

/**
 * Finds all valid combinations of unique digits (1-9) for a Killer Sudoku cage.
 *
 * @param {object} cage - An object describing the cage constraints.
 * @param {number} cage.sum - The target sum of the digits in the cage.
 * @param {number} cage.size - The number of cells (digits) in the cage.
 * @param {number[]} cage.exclude - An array of digits that cannot be used in the cage.
 * @returns {number[][]} A sorted array of sorted valid combinations.
 */
export const combinations = (cage) => {
  const { sum: targetSum, size: count, exclude } = cage;

  // Basic validation/edge cases
  if (count < 1 || count > 9 || targetSum < 1) {
      return [];
  }

  // Min possible sum for 'count' unique digits (1 + 2 + ... + count)
  const minSum = count * (count + 1) / 2;
  if (targetSum < minSum) return [];

  // Max possible sum for 'count' unique digits (9 + 8 + ... + (9 - count + 1))
  let maxSum = 0;
  for (let i = 0; i < count; i++) maxSum += (9 - i);
  if (targetSum > maxSum) return [];

  const results = [];
  const excludeSet = new Set(exclude);

  // Handle trivial case: size 1 separately for clarity/efficiency
  // Although the recursive function handles it, this is slightly faster.
  if (count === 1) {
    if (targetSum >= 1 && targetSum <= 9 && !excludeSet.has(targetSum)) {
      return [[targetSum]];
    } else {
      return [];
    }
  }

  findCombinationsRecursive(targetSum, count, 1, [], 0, excludeSet, results);

  // Sort the final list of combinations lexicographically.
  // The combinations themselves are already sorted internally due to the generation order (startDigit + 1).
  results.sort((a, b) => {
    for (let i = 0; i < a.length; i++) {
      if (a[i] !== b[i]) {
        return a[i] - b[i];
      }
    }
    return 0; // Should not happen if combinations are unique and correctly generated
  });

  return results;
};
