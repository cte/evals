// Function to generate permutations
// Refined permutations function (Heap's algorithm variation for k elements is complex, using standard recursive approach)
function getPermutations(arr, k) {
    const result = [];
    const n = arr.length;

    function generatePerms(elements, currentPerm) {
        if (currentPerm.length === k) {
            result.push([...currentPerm]);
            return;
        }

        for (let i = 0; i < elements.length; i++) {
            const nextElement = elements[i];
            const remainingElements = [...elements.slice(0, i), ...elements.slice(i + 1)];
            currentPerm.push(nextElement);
            generatePerms(remainingElements, currentPerm);
            currentPerm.pop(); // Backtrack
        }
    }

    generatePerms(arr, []);
    return result;
}

// Assign the refined function
const permutations = getPermutations;


export const solve = (puzzle) => {
  const parts = puzzle.split(' == ');
  const summands = parts[0].split(' + ');
  const resultWord = parts[1];
  const allWords = [...summands, resultWord];

  const uniqueLetters = [...new Set(puzzle.match(/[A-Z]/g))].sort();
  const leadingLetters = new Set(allWords.filter(w => w.length > 1).map(w => w[0]));

  const digits = [0, 1, 2, 3, 4, 5, 6, 7, 8, 9];
  const numUniqueLetters = uniqueLetters.length;

  if (numUniqueLetters > 10) {
    return null; // More letters than digits
  }

  // Generate permutations of 'numUniqueLetters' digits from the 'digits' array
  const digitPermutations = permutations(digits, numUniqueLetters);

  for (const perm of digitPermutations) {
    const mapping = {};
    let isValidPermutation = true;

    for (let i = 0; i < numUniqueLetters; i++) {
      const letter = uniqueLetters[i];
      const digit = perm[i];
      mapping[letter] = digit;

      // Check leading zero constraint
      if (digit === 0 && leadingLetters.has(letter)) {
        isValidPermutation = false;
        break;
      }
    }

    if (!isValidPermutation) {
      continue;
    }

    // Convert words to numbers
    const toNumber = (word) => {
        if (word.length > 1 && mapping[word[0]] === 0) return NaN; // Should be caught above, but double check
        return parseInt(word.split('').map(l => mapping[l]).join(''), 10);
    }

    const summandValues = summands.map(toNumber);
    const resultValue = toNumber(resultWord);

    if (summandValues.some(isNaN) || isNaN(resultValue)) {
        continue; // Skip if any conversion failed (e.g., leading zero missed)
    }

    const sum = summandValues.reduce((acc, val) => acc + val, 0);

    if (sum === resultValue) {
      // Found a solution
      const solution = {};
      uniqueLetters.forEach(l => solution[l] = mapping[l]);
      return solution;
    }
  }

  return null; // No solution found
};

// Removed duplicate permutation function definition
