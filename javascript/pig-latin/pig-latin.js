//
// This is only a SKELETON file for the 'Pig Latin' exercise. It's been provided as a
// convenience to get you started writing code faster.
//

export const translate = (input) => {
  const words = input.split(' ');

  return words.map(word => {
    // Special vowel-like clusters
    if (/^(xr|yt)/.test(word)) {
      return word + 'ay';
    }

    // Starts with vowel
    if (/^[aeiou]/.test(word)) {
      return word + 'ay';
    }

    // Starts with 'y' treated as consonant
    if (/^y/.test(word)) {
      return word.slice(1) + 'y' + 'ay';
    }

    // Handle 'squ' at start
    if (/^squ/.test(word)) {
      return word.slice(3) + 'squay';
    }

    // Handle 'qu' at start
    if (/^qu/.test(word)) {
      return word.slice(2) + 'quay';
    }

    // Find the first vowel or 'y' (if not at start)
    const firstVowelY = word.search(/[aeiou]|y/);

    if (firstVowelY > 0) {
      return word.slice(firstVowelY) + word.slice(0, firstVowelY) + 'ay';
    }

    // Default fallback (shouldn't be hit)
    return word + 'ay';
  }).join(' ');
};
