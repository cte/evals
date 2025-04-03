//
// This is only a SKELETON file for the 'BookStore' exercise. It's been provided as a
// convenience to get you started writing code faster.
//

export const cost = (books) => {
  const PRICE = 800;
  const DISCOUNTS = {
    1: 1.0,
    2: 0.95,
    3: 0.9,
    4: 0.8,
    5: 0.75,
  };

  // Count occurrences of each book
  const counts = {};
  for (const book of books) {
    counts[book] = (counts[book] || 0) + 1;
  }

  const groups = [];

  // Form groups greedily
  while (Object.keys(counts).some(k => counts[k] > 0)) {
    const group = [];
    for (const book in counts) {
      if (counts[book] > 0) {
        group.push(book);
        counts[book]--;
      }
    }
    groups.push(group.length);
  }

  // Optimization: swap one group of 5 and one of 3 for two groups of 4
  let fives = groups.filter(g => g === 5).length;
  let threes = groups.filter(g => g === 3).length;
  const minSwap = Math.min(fives, threes);
  for (let i = 0; i < minSwap; i++) {
    groups.splice(groups.indexOf(5), 1, 4);
    groups.splice(groups.indexOf(3), 1, 4);
  }

  // Calculate total
  return groups.reduce((total, size) => {
    return total + PRICE * size * DISCOUNTS[size];
  }, 0);
};
