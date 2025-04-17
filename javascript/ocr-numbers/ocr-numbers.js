//
// This is only a SKELETON file for the 'OCR Numbers' exercise. It's been provided as a
// convenience to get you started writing code faster.
//

const DIGITS = {
  ' _ | ||_|   ': '0',
  '     |  |   ': '1',
  ' _  _||_    ': '2',
  ' _  _| _|   ': '3',
  '   |_|  |   ': '4',
  ' _ |_  _|   ': '5',
  ' _ |_ |_|   ': '6',
  ' _   |  |   ': '7',
  ' _ |_||_|   ': '8',
  ' _ |_| _|   ': '9',
};

export const convert = (input) => {
  const lines = input.split('\n');

  const blocks = [];
  for (let i = 0; i < lines.length; i += 4) {
    const blockLines = lines.slice(i, i + 4);
    if (blockLines.length < 4) continue;

    const numDigits = Math.ceil(blockLines[0].length / 3);
    let digits = '';

    for (let d = 0; d < numDigits; d++) {
      let pattern = '';
      for (let l = 0; l < 4; l++) {
        pattern += (blockLines[l].substr(d * 3, 3) || '   ');
      }
      digits += DIGITS[pattern] || '?';
    }
    blocks.push(digits);
  }

  return blocks.join(',');
};
