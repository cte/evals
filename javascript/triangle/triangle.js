//
// This is only a SKELETON file for the 'Triangle' exercise. It's been provided as a
// convenience to get you started writing code faster.
//

export class Triangle {
  constructor(...sides) {
    if (sides.length !== 3) {
      throw new Error('Triangle must have exactly 3 sides');
    }
    this.sides = sides;

    const [a, b, c] = sides;

    // All sides must be > 0
    const allPositive = a > 0 && b > 0 && c > 0;

    // Triangle inequality: sum of any two > third
    const satisfiesInequality =
      a + b > c &&
      a + c > b &&
      b + c > a;

    this._isValid = allPositive && satisfiesInequality;
  }

  get isEquilateral() {
    if (!this._isValid) return false;
    const [a, b, c] = this.sides;
    return a === b && b === c;
  }

  get isIsosceles() {
    if (!this._isValid) return false;
    const [a, b, c] = this.sides;
    return a === b || b === c || a === c;
  }

  get isScalene() {
    if (!this._isValid) return false;
    const [a, b, c] = this.sides;
    return a !== b && b !== c && a !== c;
  }
}
