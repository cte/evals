const COLOR_VALUES = {
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

export class ResistorColorTrio {
  constructor(colors) {
    if (colors.length !== 3) {
      throw new Error('Exactly three colors are required.');
    }
    this.colors = colors.map(color => {
      const lowerCaseColor = color.toLowerCase();
      if (COLOR_VALUES[lowerCaseColor] === undefined) {
        throw new Error(`Invalid color: ${color}`);
      }
      return lowerCaseColor;
    });
  }

  label() {
    const [color1, color2, color3] = this.colors;
    const value1 = COLOR_VALUES[color1];
    const value2 = COLOR_VALUES[color2];
    const exponent = COLOR_VALUES[color3];

    let resistance = (value1 * 10 + value2) * (10 ** exponent);
    let unit = 'ohms';

    if (resistance >= 1000) {
      resistance /= 1000;
      unit = 'kiloohms';
    }

    return `Resistor value: ${resistance} ${unit}`;
  }
}
