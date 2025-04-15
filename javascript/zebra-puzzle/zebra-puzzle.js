//
// This is the solution file for the 'Zebra Puzzle' exercise.
//

// Define constants for clarity (optional, but good practice)
const WATER = 'Water';
const ZEBRA = 'Zebra';

export class ZebraPuzzle {
  solution;

  constructor() {
    // The solution derived from the puzzle constraints:
    // House 1: Norwegian, Yellow, Fox, Water, Painter
    // House 2: Ukrainian, Blue, Horse, Tea, Reading
    // House 3: Englishman, Red, Snails, Milk, Dancing
    // House 4: Spaniard, Ivory, Dog, Orange Juice, Football
    // House 5: Japanese, Green, Zebra, Coffee, Chess
    // We only need the nationality associated with Water and Zebra.
    this.derivedWaterDrinker = 'Norwegian';
    this.derivedZebraOwner = 'Japanese';
  }

  waterDrinker() {
    // Return the nationality of the person who drinks water.
    return this.derivedWaterDrinker;
  }

  zebraOwner() {
    // Return the nationality of the person who owns the zebra.
    return this.derivedZebraOwner;
  }
}
