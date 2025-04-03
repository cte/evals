//
// This is only a SKELETON file for the 'Killer Sudoku Helper' exercise. It's been provided as a
// convenience to get you started writing code faster.
//

export const combinations = (cage) => {
  const { sum, size, exclude } = cage;
  const digits = [];
  for (let i = 1; i <= 9; i++) {
    if (!exclude.includes(i)) {
      digits.push(i);
    }
  }

  const results = [];

  function backtrack(start, path, pathSum) {
    if (path.length === size) {
      if (pathSum === sum) {
        results.push([...path]);
      }
      return;
    }

    for (let i = start; i < digits.length; i++) {
      const nextDigit = digits[i];
      if (pathSum + nextDigit > sum) continue;
      path.push(nextDigit);
      backtrack(i + 1, path, pathSum + nextDigit);
      path.pop();
    }
  }

  backtrack(0, [], 0);
  return results;
};
