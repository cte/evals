//
// This is only a SKELETON file for the 'Beer Song' exercise. It's been provided as a
// convenience to get you started writing code faster.
//

export const recite = (initialBottlesCount, takeDownCount) => {
  const verses = [];

  for (let i = 0; i < takeDownCount; i++) {
    const count = initialBottlesCount - i;

    if (count > 2) {
      verses.push(
        `${count} bottles of beer on the wall, ${count} bottles of beer.`,
        `Take one down and pass it around, ${count - 1} bottles of beer on the wall.`
      );
    } else if (count === 2) {
      verses.push(
        '2 bottles of beer on the wall, 2 bottles of beer.',
        'Take one down and pass it around, 1 bottle of beer on the wall.'
      );
    } else if (count === 1) {
      verses.push(
        '1 bottle of beer on the wall, 1 bottle of beer.',
        'Take it down and pass it around, no more bottles of beer on the wall.'
      );
    } else {
      verses.push(
        'No more bottles of beer on the wall, no more bottles of beer.',
        'Go to the store and buy some more, 99 bottles of beer on the wall.'
      );
    }

    if (i < takeDownCount - 1) {
      verses.push('');
    }
  }

  return verses;
};
