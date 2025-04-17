//
// This is only a SKELETON file for the 'Resistor Color Trio' exercise. It's been provided as a
// convenience to get you started writing code faster.
//

export class ResistorColorTrio {
  static COLOR_CODES = {
    black: 0,
    brown: 1,
    red: 2,
    orange: 3,
    yellow: 4,
    green: 5,
    blue: 6,
    violet: 7,
    grey: 8,
    white: 9,
  };

  constructor(colors) {
    if (!Array.isArray(colors) || colors.length !== 3) {
      throw new Error('invalid input');
    }

    const digits = colors.map(color => {
      if (!(color in ResistorColorTrio.COLOR_CODES)) {
        throw new Error('invalid color');
      }
      return ResistorColorTrio.COLOR_CODES[color];
    });

    const [first, second, multiplierPower] = digits;
    const resistanceValue = (first * 10 + second) * Math.pow(10, multiplierPower);

    let value, unit;
    if (resistanceValue >= 1_000_000) {
      value = resistanceValue / 1_000_000;
      unit = 'megaohms';
    } else if (resistanceValue >= 1000) {
      value = resistanceValue / 1000;
      unit = 'kiloohms';
    } else {
      value = resistanceValue;
      unit = 'ohms';
    }

    this.label = `Resistor value: ${value} ${unit}`;
  }
}
