//
// This is only a SKELETON file for the 'Parallel Letter Frequency' exercise. It's been provided as a
// convenience to get you started writing code faster.
//

export const parallelLetterFrequency = async (texts) => {
  const countsArray = await Promise.all(
    texts.map(async (text) => {
      const counts = {};
      const letters = text.toLowerCase().match(/\p{L}/gu) || [];
      for (const letter of letters) {
        counts[letter] = (counts[letter] || 0) + 1;
      }
      return counts;
    })
  );

  const finalCounts = {};
  for (const counts of countsArray) {
    for (const [letter, count] of Object.entries(counts)) {
      finalCounts[letter] = (finalCounts[letter] || 0) + count;
    }
  }

  return finalCounts;
};
