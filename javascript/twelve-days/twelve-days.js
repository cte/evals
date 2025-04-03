//
// This is only a SKELETON file for the 'Twelve Days' exercise. It's been provided as a
// convenience to get you started writing code faster.
//

const ordinals = [
  'first',
  'second',
  'third',
  'fourth',
  'fifth',
  'sixth',
  'seventh',
  'eighth',
  'ninth',
  'tenth',
  'eleventh',
  'twelfth',
];

const gifts = [
  'a Partridge in a Pear Tree.',
  'two Turtle Doves',
  'three French Hens',
  'four Calling Birds',
  'five Gold Rings',
  'six Geese-a-Laying',
  'seven Swans-a-Swimming',
  'eight Maids-a-Milking',
  'nine Ladies Dancing',
  'ten Lords-a-Leaping',
  'eleven Pipers Piping',
  'twelve Drummers Drumming',
];

export function recite(start, end) {
  if (end === undefined) {
    end = start;
  }

  const verses = [];

  for (let day = start; day <= end; day++) {
    const intro = `On the ${ordinals[day - 1]} day of Christmas my true love gave to me: `;

    const dayGifts = [];
    for (let i = day - 1; i >= 0; i--) {
      if (i === 0 && day !== 1) {
        dayGifts.push('and ' + gifts[i]);
      } else {
        dayGifts.push(gifts[i]);
      }
    }

    const verse = intro + dayGifts.join(', ') + '\n';
    verses.push(verse);
  }

  return verses.join('\n');
}
