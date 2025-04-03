//
// This is only a SKELETON file for the 'Food Chain' exercise. It's been provided as a
// convenience to get you started writing code faster.
//

export class Song {
  #animals = [
    {
      name: 'fly',
      comment: '',
    },
    {
      name: 'spider',
      comment: 'It wriggled and jiggled and tickled inside her.',
      catchSuffix: ' that wriggled and jiggled and tickled inside her',
    },
    {
      name: 'bird',
      comment: 'How absurd to swallow a bird!',
    },
    {
      name: 'cat',
      comment: 'Imagine that, to swallow a cat!',
    },
    {
      name: 'dog',
      comment: 'What a hog, to swallow a dog!',
    },
    {
      name: 'goat',
      comment: 'Just opened her throat and swallowed a goat!',
    },
    {
      name: 'cow',
      comment: "I don't know how she swallowed a cow!",
    },
    {
      name: 'horse',
      comment: "She's dead, of course!",
    },
  ];

  verse(n) {
    const idx = n - 1;
    const animal = this.#animals[idx];

    let lines = [];

    lines.push(`I know an old lady who swallowed a ${animal.name}.`);

    if (animal.comment) {
      lines.push(animal.comment);
    }

    if (animal.name === 'horse') {
      // terminal verse
      return lines.join('\n') + '\n';
    }

    // cumulative part
    for (let i = idx; i > 0; i--) {
      const curr = this.#animals[i];
      const prev = this.#animals[i - 1];

      let suffix = '';
      if (prev.name === 'spider' && prev.catchSuffix) {
        suffix = prev.catchSuffix;
      }

      lines.push(
        `She swallowed the ${curr.name} to catch the ${prev.name}${suffix}.`
      );
    }

    lines.push("I don't know why she swallowed the fly. Perhaps she'll die.");

    return lines.join('\n') + '\n';
  }

  verses(start, end) {
    const versesArr = [];
    for (let i = start; i <= end; i++) {
      versesArr.push(this.verse(i).trimEnd());
    }
    return versesArr.join('\n\n') + '\n\n';
  }
}
