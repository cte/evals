export const transpose = (input) => {
  if (!input || input.length === 0) {
    return [];
  }

  let maxLength = 0;
  for (const row of input) {
    if (row.length > maxLength) {
      maxLength = row.length;
    }
  }

  const output = [];
  for (let i = 0; i < maxLength; i++) {
    let newRow = '';
    let padding = '';
    for (let j = 0; j < input.length; j++) {
      const char = input[j][i];
      if (char !== undefined) {
        newRow += padding; // Add padding accumulated from previous shorter rows
        padding = '';      // Reset padding
        newRow += char;    // Add the actual character
      } else {
        padding += ' ';    // Increment padding if current row is too short
      }
    }
    output.push(newRow);
  }

  return output;
};
