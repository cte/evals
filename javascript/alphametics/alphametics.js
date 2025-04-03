//
// This is only a SKELETON file for the 'Alphametics' exercise. It's been provided as a
// convenience to get you started writing code faster.
//

export const solve = (puzzle) => {
  const [left, right] = puzzle.split('==').map(side => side.trim());
  const addends = left.split('+').map(s => s.trim());
  const result = right;

  const words = [...addends, result];

  const uniqueLetters = Array.from(new Set(puzzle.replace(/[^A-Z]/g, '')));
  if (uniqueLetters.length > 10) return null; // impossible, more than 10 letters

  const leadingLetters = new Set(words.map(word => word[0]));

  function* permutations(arr) {
    if (arr.length === 0) yield [];
    else {
      for (let i = 0; i < arr.length; i++) {
        const rest = arr.slice(0, i).concat(arr.slice(i + 1));
        for (const perm of permutations(rest)) {
          yield [arr[i], ...perm];
        }
      }
    }
  }

  const digits = [0,1,2,3,4,5,6,7,8,9];

  for (const perm of permutations(digits).filter(p => p.length >= uniqueLetters.length)) {
    const candidate = perm.slice(0, uniqueLetters.length);
    const letterToDigit = Object.fromEntries(uniqueLetters.map((ch, idx) => [ch, candidate[idx]]));

    // skip if any leading letter is zero
    if ([...leadingLetters].some(ch => letterToDigit[ch] === 0)) continue;

    const wordToNumber = word => Number(word.split('').map(ch => letterToDigit[ch]).join(''));

    const addendSum = addends.reduce((sum, word) => sum + wordToNumber(word), 0);
    const resultNum = wordToNumber(result);

    if (addendSum === resultNum) {
      return letterToDigit;
    }
  }

  return null;
};
