//
// This is only a SKELETON file for the 'Transpose' exercise. It's been provided as a
// convenience to get you started writing code faster.
//

export const transpose = (lines) => {
  if (lines.length === 0) {
    return [];
  }

  const maxLength = Math.max(...lines.map(line => line.length));
  const result = [];

  for (let col = 0; col < maxLength; col++) {
    // Find the last row that has any character (including space) at this column index
    let lastRowWithChar = lines.length - 1;
    while (lastRowWithChar >= 0) {
      if (col < lines[lastRowWithChar].length) {
        break;
      }
      lastRowWithChar--;
    }

    let transposedLine = '';
    for (let row = 0; row <= lastRowWithChar; row++) {
      transposedLine += (lines[row][col] || ' ');
    }
    result.push(transposedLine);
  }

  return result;
};
