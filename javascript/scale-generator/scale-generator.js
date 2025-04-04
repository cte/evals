// Define the base chromatic scales
const SHARP_SCALE = ['A', 'A#', 'B', 'C', 'C#', 'D', 'D#', 'E', 'F', 'F#', 'G', 'G#'];
const FLAT_SCALE = ['A', 'Bb', 'B', 'C', 'Db', 'D', 'Eb', 'E', 'F', 'Gb', 'G', 'Ab'];

// Define keys that use flats
const FLAT_KEYS = ['F', 'Bb', 'Eb', 'Ab', 'Db', 'Gb', 'd', 'g', 'c', 'f', 'bb', 'eb'];

// Helper function to capitalize note
const capitalize = (note) => {
  if (note.length > 1) {
    // Capitalize first letter, ensure 'b' in flats remains lowercase
    return note[0].toUpperCase() + note.substring(1).toLowerCase();
  }
  return note.toUpperCase();
};

export class Scale {
  constructor(tonic) {
    const originalTonic = tonic; // Preserve original case for lookup

    // Determine if we use flats or sharps based on the provided table/rules
    // C major and a minor use sharps (ascending)
    if (originalTonic === 'C' || originalTonic === 'a') {
      this.useFlats = false;
    } else {
      // Check the original tonic against the list of keys that use flats
      this.useFlats = FLAT_KEYS.includes(originalTonic);
    }

    // Set the chromatic scale based on the determination
    this.chromaticScale = this.useFlats ? FLAT_SCALE : SHARP_SCALE;

    // Capitalize the tonic for internal use and output consistency
    this.tonic = capitalize(originalTonic);

    // Note: No need to double-check/switch scales here.
    // The logic based on FLAT_KEYS and the C/a rule should be sufficient.
    // If a capitalized tonic isn't found later (e.g., in interval()),
    // it implies an issue with the base scales or capitalization, not the flat/sharp choice.
  }

  chromatic() {
    const tonicIndex = this.chromaticScale.indexOf(this.tonic);
    // If tonic is still not found after potential scale switch in constructor, something is wrong.
    // Assuming valid inputs based on instructions.
    if (tonicIndex === -1) return [];

    // Rotate the scale
    return [
      ...this.chromaticScale.slice(tonicIndex),
      ...this.chromaticScale.slice(0, tonicIndex),
    ];
  }

  interval(intervals) {
    const result = [this.tonic];
    let currentIndex = this.chromaticScale.indexOf(this.tonic);

     // If tonic not found initially (should be handled by constructor correction)
     if (currentIndex === -1) {
         // This indicates an unexpected state, possibly invalid tonic input.
         // Returning empty array as a safeguard. Tests will reveal issues.
         return [];
     }


    for (const interval of intervals) {
      let step = 0;
      switch (interval) {
        case 'm': // minor second (half step)
          step = 1;
          break;
        case 'M': // major second (whole step)
          step = 2;
          break;
        case 'A': // augmented second
          step = 3;
          break;
        default: // Should not happen with valid intervals
          step = 0; // Or throw an error? Let's default to 0 step.
      }
      currentIndex = (currentIndex + step) % 12;
      result.push(this.chromaticScale[currentIndex]);
    }
    return result;
  }
}
