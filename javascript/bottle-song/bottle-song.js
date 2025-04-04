const numberToWord = (n) => {
  const words = [
    'No', 'One', 'Two', 'Three', 'Four', 'Five', 'Six', 'Seven', 'Eight', 'Nine', 'Ten'
  ];
  return words[n] || n.toString(); // Fallback to number if > 10, though not needed for this song
};

const bottles = (n) => (n === 1 ? 'bottle' : 'bottles');

export const recite = (initialBottlesCount, takeDownCount) => {
  const verses = [];
  let currentBottles = initialBottlesCount;

  for (let i = 0; i < takeDownCount; i++) {
    if (i > 0) {
      verses.push(''); // Add empty line between verses
    }

    const currentWord = numberToWord(currentBottles);
    const nextBottles = currentBottles - 1;
    const nextWordLower = numberToWord(nextBottles).toLowerCase();

    verses.push(`${currentWord} green ${bottles(currentBottles)} hanging on the wall,`);
    verses.push(`${currentWord} green ${bottles(currentBottles)} hanging on the wall,`);
    verses.push('And if one green bottle should accidentally fall,');
    verses.push(`There'll be ${nextWordLower} green ${bottles(nextBottles)} hanging on the wall.`);

    currentBottles--;
  }

  return verses;
};
