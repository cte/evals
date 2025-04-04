//
// This is only a SKELETON file for the 'Twelve Days' exercise. It's been provided as a
// convenience to get you started writing code faster.
//

const gifts = [
  'a Partridge in a Pear Tree',
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

const ordinals = [
  'first', 'second', 'third', 'fourth', 'fifth', 'sixth',
  'seventh', 'eighth', 'ninth', 'tenth', 'eleventh', 'twelfth'
];

const getVerse = (day) => {
  const dayIndex = day - 1;
  const dayOrdinal = ordinals[dayIndex];
  let verseGifts = '';

  // Build the list of gifts for the current day, in reverse order
  const currentDayGifts = gifts.slice(0, day).reverse();

  if (day === 1) {
    verseGifts = currentDayGifts[0];
  } else {
    // Join all gifts except the last one with commas
    verseGifts = currentDayGifts.slice(0, -1).join(', ');
    // Add the last gift (Partridge) with 'and'
    verseGifts += ', and ' + currentDayGifts[currentDayGifts.length - 1];
  }


  return `On the ${dayOrdinal} day of Christmas my true love gave to me: ${verseGifts}.\n`;
};

export const recite = (startVerse, endVerse) => {
  // If endVerse is not provided, default it to startVerse to handle single verse case
  const finalEndVerse = endVerse === undefined ? startVerse : endVerse;

  let result = '';
  for (let i = startVerse; i <= finalEndVerse; i++) {
    result += getVerse(i);
    if (i < finalEndVerse) {
      result += '\n'; // Add an extra newline between verses
    }
  }
  return result;
};
