//
// This is only a SKELETON file for the 'Pig Latin' exercise. It's been provided as a
// convenience to get you started writing code faster.
//

const VOWELS = /^[aeiou]|^(xr)|^(yt)/i; // Rule 1: Starts with vowel sound or xr, yt
const CONSONANT_QU = /^([^aeiou]*qu)(.*)/i; // Rule 3: Starts with consonants? + qu
const CONSONANT_Y = /^([^aeiou]+)(y.*)/i; // Rule 4: Starts with consonants + y (y acts as vowel)
const CONSONANT = /^([^aeiou]+)(.*)/i; // Rule 2: Starts with consonants

const translateWord = (word) => {
  if (VOWELS.test(word)) {
    return word + 'ay';
  }
  if (CONSONANT_QU.test(word)) {
    return word.replace(CONSONANT_QU, '$2$1ay');
  }
  // Rule 4 needs to be checked before Rule 2 for words like 'rhythm'
  // where 'y' acts as the first vowel sound after consonants.
  if (CONSONANT_Y.test(word)) {
    return word.replace(CONSONANT_Y, '$2$1ay');
  }
  if (CONSONANT.test(word)) {
    return word.replace(CONSONANT, '$2$1ay');
  }
  // Should not be reached for valid English words according to the rules
  return word;
};

export const translate = (phrase) => {
  if (!phrase) {
    return '';
  }
  return phrase.split(' ').map(translateWord).join(' ');
};
