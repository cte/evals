//
// This is only a SKELETON file for the 'Binary' exercise. It's been provided as a
// convenience to get you started writing code faster.
//

export class Binary {
  constructor(value) {
    this.value = value;
  }

  toDecimal() {
    if (!/^[01]+$/.test(this.value)) {
      return null;
    }
    return parseInt(this.value, 2);
  }
}
