const animals = [
  { name: 'fly', remark: null },
  { name: 'spider', remark: 'It wriggled and jiggled and tickled inside her.' },
  { name: 'bird', remark: 'How absurd to swallow a bird!' },
  { name: 'cat', remark: 'Imagine that, to swallow a cat!' },
  { name: 'dog', remark: 'What a hog, to swallow a dog!' },
  { name: 'goat', remark: 'Just opened her throat and swallowed a goat!' },
  { name: 'cow', remark: "I don't know how she swallowed a cow!" },
  { name: 'horse', remark: null }, // Horse has a special ending
];

const firstLine = (animal) => `I know an old lady who swallowed a ${animal}.\n`;
const lastLine = "I don't know why she swallowed the fly. Perhaps she'll die.\n";
const horseEnding = "She's dead, of course!\n";

const swallowLine = (currentAnimal, previousAnimal) => {
  let line = `She swallowed the ${currentAnimal} to catch the ${previousAnimal}`;
  if (previousAnimal === 'spider') {
    // The test suite expects the 'that wriggled...' part only when catching the spider from the bird verse onwards.
    // However, the example lyrics show it immediately in the spider verse. Let's adjust based on the example.
    // Re-reading the example: The spider verse itself has "It wriggled...", the *following* verses have "She swallowed the bird to catch the spider that wriggled..."
    // Let's stick to the logic derived from the full song example.
    line += ' that wriggled and jiggled and tickled inside her';
  }
  return line + '.\n';
};


export class Song {
  verse(n) {
    const index = n - 1;
    if (index < 0 || index >= animals.length) {
        throw new Error("Verse number out of range");
    }
    const animal = animals[index];
    let result = firstLine(animal.name);

    if (animal.name === 'horse') {
      result += horseEnding;
      return result;
    }

    if (animal.remark) {
      result += animal.remark + '\n';
    }

    // The first verse (fly) only has the first and last lines.
    if (animal.name === 'fly') {
        result += lastLine;
        return result;
    }

    // Build the cumulative part
    for (let i = index; i > 0; i--) {
      result += swallowLine(animals[i].name, animals[i - 1].name);
    }

    result += lastLine;
    return result;
  }

  verses(start, end) {
    if (start > end || start < 1 || end > animals.length) {
        throw new Error("Invalid verse range");
    }
    let result = '';
    for (let i = start; i <= end; i++) {
      result += this.verse(i);
      // Always add an extra newline for the blank line separation / final newline
      result += '\n';
    }
    return result;
  }
}
