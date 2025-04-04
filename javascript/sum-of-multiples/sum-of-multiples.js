//
// This is only a SKELETON file for the 'Sum Of Multiples' exercise. It's been provided as a
// convenience to get you started writing code faster.
//

export const sum = (baseValues, level) => { // Corrected signature
  if (level <= 1) {
    return 0;
  }

  const multiples = new Set();

  for (const baseValue of baseValues) {
    // Skip 0 as it doesn't produce meaningful multiples in this context
    // and would cause an infinite loop if we tried to find multiples.
    if (baseValue === 0) {
      continue;
    }

    for (let i = baseValue; i < level; i += baseValue) {
      multiples.add(i);
    }
  }

  let totalSum = 0;
  for (const multiple of multiples) {
    totalSum += multiple;
  }

  return totalSum;
};
