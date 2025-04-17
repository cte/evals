//
// This is only a SKELETON file for the 'Sum Of Multiples' exercise. It's been provided as a
// convenience to get you started writing code faster.
//

export const sum = (factors, limit) => {
  if (!factors || factors.length === 0) {
    return 0;
  }

  const multiples = new Set();

  for (let i = 1; i < limit; i++) {
    for (const factor of factors) {
      if (factor !== 0 && i % factor === 0) {
        multiples.add(i);
        break; // No need to check other factors
      }
    }
  }

  let total = 0;
  for (const num of multiples) {
    total += num;
  }

  return total;
};
