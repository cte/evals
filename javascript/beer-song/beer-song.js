//
// This is only a SKELETON file for the 'Beer Song' exercise. It's been provided as a
// convenience to get you started writing code faster.
//

const verseLines = (n) => {
  if (n > 2) {
    return [
      `${n} bottles of beer on the wall, ${n} bottles of beer.`,
      `Take one down and pass it around, ${n - 1} bottles of beer on the wall.`
    ];
  } else if (n === 2) {
    return [
      `2 bottles of beer on the wall, 2 bottles of beer.`,
      `Take one down and pass it around, 1 bottle of beer on the wall.`
    ];
  } else if (n === 1) {
    return [
      `1 bottle of beer on the wall, 1 bottle of beer.`,
      `Take it down and pass it around, no more bottles of beer on the wall.`
    ];
  } else { // n === 0
    return [
      `No more bottles of beer on the wall, no more bottles of beer.`,
      `Go to the store and buy some more, 99 bottles of beer on the wall.`
    ];
  }
};

export const recite = (initialBottlesCount, takeDownCount) => {
  const resultLines = [];
  for (let i = 0; i < takeDownCount; i++) {
    const currentVerseLines = verseLines(initialBottlesCount - i);
    resultLines.push(...currentVerseLines);
    // Add an empty line between verses, but not after the last one
    if (i < takeDownCount - 1) {
      resultLines.push('');
    }
  }
  return resultLines;
};
