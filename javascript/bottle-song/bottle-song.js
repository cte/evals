//
// This is only a SKELETON file for the 'Bottle Song' exercise. It's been provided as a
// convenience to get you started writing code faster.
//

const numberToWords = (num, capitalize = true) => {
  const words = {
    10: 'ten',
    9: 'nine',
    8: 'eight',
    7: 'seven',
    6: 'six',
    5: 'five',
    4: 'four',
    3: 'three',
    2: 'two',
    1: 'one',
    0: 'no',
  };
  let word = words[num];
  if (!word) word = num.toString();
  if (capitalize && word !== 'no') {
    word = word.charAt(0).toUpperCase() + word.slice(1);
  }
  return word;
};

export const recite = (initialBottlesCount, takeDownCount) => {
  const verses = [];

  for (let i = 0; i < takeDownCount; i++) {
    const current = initialBottlesCount - i;
    const next = current - 1;

    const currentWord = numberToWords(current);
    const nextWord = numberToWords(next, false);

    const currentBottleStr = current === 1 ? 'One green bottle' : `${currentWord} green bottles`;
    let nextBottleStr;
    if (next === 0) {
      nextBottleStr = 'no green bottles';
    } else if (next === 1) {
      nextBottleStr = 'one green bottle';
    } else {
      nextBottleStr = `${nextWord} green bottles`;
    }

    verses.push(
      `${currentBottleStr} hanging on the wall,`,
      `${currentBottleStr} hanging on the wall,`,
      `And if one green bottle should accidentally fall,`,
      `There'll be ${nextBottleStr} hanging on the wall.`
    );

    if (i < takeDownCount - 1) {
      verses.push('');
    }
  }

  return verses;
};
