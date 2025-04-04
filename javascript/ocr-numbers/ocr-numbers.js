const DIGIT_PATTERNS = {
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

const ROWS_PER_DIGIT = 4;
const COLS_PER_DIGIT = 3;

const parseDigit = (lines, startCol) => {
  let pattern = '';
  for (let i = 0; i < ROWS_PER_DIGIT; i++) {
    pattern += lines[i].substring(startCol, startCol + COLS_PER_DIGIT);
  }
  return DIGIT_PATTERNS[pattern] || '?';
};

const parseLine = (lines) => {
  if (lines[0].length % COLS_PER_DIGIT !== 0) {
    throw new Error('Number of columns is not a multiple of 3');
  }

  let result = '';
  for (let col = 0; col < lines[0].length; col += COLS_PER_DIGIT) {
    result += parseDigit(lines, col);
  }
  return result;
};

export const convert = (input) => {
  const lines = input.split('\n');

  // Remove trailing newline if present, which can happen with some inputs
  if (lines[lines.length - 1] === '') {
    lines.pop();
  }

  if (lines.length % ROWS_PER_DIGIT !== 0) {
    throw new Error('Number of lines is not a multiple of 4');
  }

  const results = [];
  for (let i = 0; i < lines.length; i += ROWS_PER_DIGIT) {
    const digitLines = lines.slice(i, i + ROWS_PER_DIGIT);
    results.push(parseLine(digitLines));
  }

  return results.join(',');
};
