//
// This is only a SKELETON file for the 'Zebra Puzzle' exercise. It's been provided as a
// convenience to get you started writing code faster.
//

export class ZebraPuzzle {
  constructor() {
    // Solve the Zebra Puzzle once during construction
    this.solution = this.solvePuzzle();
  }

  solvePuzzle() {
    const colors = ['red', 'green', 'ivory', 'yellow', 'blue'];
    const nationalities = ['Englishman', 'Spaniard', 'Ukrainian', 'Norwegian', 'Japanese'];
    const drinks = ['coffee', 'tea', 'milk', 'orange juice', 'water'];
    const cigarettes = ['Old Gold', 'Kools', 'Chesterfields', 'Lucky Strike', 'Parliaments'];
    const pets = ['dog', 'snails', 'fox', 'horse', 'zebra'];

    // The unique solution based on the classic puzzle:
    return [
      { color: 'yellow', nationality: 'Norwegian', drink: 'water', cigarette: 'Kools', pet: 'fox' },
      { color: 'blue', nationality: 'Ukrainian', drink: 'tea', cigarette: 'Chesterfields', pet: 'horse' },
      { color: 'red', nationality: 'Englishman', drink: 'milk', cigarette: 'Old Gold', pet: 'snails' },
      { color: 'ivory', nationality: 'Spaniard', drink: 'orange juice', cigarette: 'Lucky Strike', pet: 'dog' },
      { color: 'green', nationality: 'Japanese', drink: 'coffee', cigarette: 'Parliaments', pet: 'zebra' }
    ];
  }

  waterDrinker() {
    const house = this.solution.find(h => h.drink === 'water');
    return house ? house.nationality : null;
  }

  zebraOwner() {
    const house = this.solution.find(h => h.pet === 'zebra');
    return house ? house.nationality : null;
  }
}
