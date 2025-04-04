// Define the parts of the rhyme
const parts = [
  { subject: 'the house that Jack built.' }, // Index 0 (Verse 1)
  { subject: 'the malt', action: 'that lay in' }, // Index 1 (Verse 2)
  { subject: 'the rat', action: 'that ate' }, // Index 2 (Verse 3)
  { subject: 'the cat', action: 'that killed' }, // Index 3 (Verse 4)
  { subject: 'the dog', action: 'that worried' }, // Index 4 (Verse 5)
  { subject: 'the cow with the crumpled horn', action: 'that tossed' }, // Index 5 (Verse 6)
  { subject: 'the maiden all forlorn', action: 'that milked' }, // Index 6 (Verse 7)
  { subject: 'the man all tattered and torn', action: 'that kissed' }, // Index 7 (Verse 8)
  { subject: 'the priest all shaven and shorn', action: 'that married' }, // Index 8 (Verse 9)
  { subject: 'the rooster that crowed in the morn', action: 'that woke' }, // Index 9 (Verse 10)
  { subject: 'the farmer sowing his corn', action: 'that kept' }, // Index 10 (Verse 11)
  { subject: 'the horse and the hound and the horn', action: 'that belonged to' }, // Index 11 (Verse 12)
];

export class House {
  static verse(n) {
    // Adjust n to be 0-indexed for accessing the parts array
    const index = n - 1;
    if (index < 0 || index >= parts.length) {
      // Although tests don't cover this, it's good practice
      throw new Error('Verse number out of range');
    }

    let verseLines = [`This is ${parts[index].subject}`];

    // Build the recursive part by iterating backwards from the current part
    for (let i = index; i > 0; i--) {
      verseLines.push(`${parts[i].action} ${parts[i - 1].subject}`);
    }

    return verseLines;
  }

  static verses(startVerse, endVerse) {
    if (startVerse < 1 || endVerse > parts.length || startVerse > endVerse) {
      // Although tests don't cover this, it's good practice
      throw new Error('Verse range is invalid');
    }

    const allLines = [];
    for (let i = startVerse; i <= endVerse; i++) {
      const verseLines = House.verse(i);
      allLines.push(...verseLines);
      // Add an empty string separator if it's not the last verse
      if (i < endVerse) {
        allLines.push('');
      }
    }
    return allLines;
  }
}
