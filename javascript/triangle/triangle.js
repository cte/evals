//
// This is only a SKELETON file for the 'Triangle' exercise. It's been provided as a
// convenience to get you started writing code faster.
//

export class Triangle {
  constructor(...sides) {
    this.sides = sides.sort((a, b) => a - b); // Sort sides for easier comparison
    [this.a, this.b, this.c] = this.sides;

    // Check for invalid side lengths (<= 0)
    const hasInvalidSide = this.sides.some(side => side <= 0);

    // Check triangle inequality (a + b >= c after sorting)
    const violatesInequality = this.a + this.b < this.c;

    this.isValid = !hasInvalidSide && !violatesInequality;
  }

  get isEquilateral() {
    // Must be valid and all sides equal
    return this.isValid && this.a === this.c; // Since sorted, only need to check first and last
  }

  get isIsosceles() {
    // Must be valid and at least two sides equal
    // Includes equilateral triangles as per instructions
    return this.isValid && (this.a === this.b || this.b === this.c);
  }

  get isScalene() {
    // Must be valid and all sides different
    return this.isValid && this.a !== this.b && this.b !== this.c;
  }
}
