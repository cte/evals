//
// This is only a SKELETON file for the 'House' exercise. It's been provided as a
// convenience to get you started writing code faster.
//

export class House {
  static #subjects = [
    'the house that Jack built.',
    'the malt',
    'the rat',
    'the cat',
    'the dog',
    'the cow with the crumpled horn',
    'the maiden all forlorn',
    'the man all tattered and torn',
    'the priest all shaven and shorn',
    'the rooster that crowed in the morn',
    'the farmer sowing his corn',
    'the horse and the hound and the horn',
  ];

  static #actions = [
    null,
    'that lay in',
    'that ate',
    'that killed',
    'that worried',
    'that tossed',
    'that milked',
    'that kissed',
    'that married',
    'that woke',
    'that kept',
    'that belonged to',
  ];

  static verse(n) {
    const lines = [];
    const idx = n - 1;
    lines.push('This is ' + House.#subjects[idx]);
    for (let i = idx; i > 0; i--) {
      lines.push(`${House.#actions[i]} ${House.#subjects[i - 1]}`);
    }
    return lines;
  }

  static verses(start, end) {
    const allVerses = [];
    for (let v = start; v <= end; v++) {
      allVerses.push(...House.verse(v));
      if (v !== end) {
        allVerses.push('');
      }
    }
    return allVerses;
  }
}
