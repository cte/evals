//
// This is only a SKELETON file for the 'Wordy' exercise. It's been provided as a
// convenience to get you started writing code faster.
//

export const answer = (question) => {
  if (typeof question !== 'string' || !question.startsWith('What is') || !question.endsWith('?')) {
    throw new Error('Unknown operation');
  }

  const content = question.slice(7, -1).trim();

  if (content.length === 0) {
    throw new Error('Syntax error');
  }

  // Supported operators
  const operators = {
    'plus': (a, b) => a + b,
    'minus': (a, b) => a - b,
    'multiplied by': (a, b) => a * b,
    'divided by': (a, b) => a / b,
  };

  // Regex to match numbers and operators
  const tokenPattern = /(-?\d+|plus|minus|multiplied by|divided by)/g;
  const tokens = content.match(tokenPattern);

  if (!tokens) {
    throw new Error('Unknown operation');
  }

  // Check for any unsupported words left in the content
  const cleaned = content.replace(tokenPattern, '').replace(/\s+/g, '');
  if (cleaned.length > 0) {
    throw new Error('Unknown operation');
  }

  // Convert tokens to parsed array
  const parsed = [];
  for (const token of tokens) {
    if (/^-?\d+$/.test(token)) {
      parsed.push({ type: 'number', value: parseInt(token, 10) });
    } else if (operators[token]) {
      parsed.push({ type: 'operator', value: token });
    } else {
      throw new Error('Unknown operation');
    }
  }

  if (parsed.length === 0) {
    throw new Error('Syntax error');
  }

  // Validate sequence: number, (operator, number)*
  if (parsed[0].type !== 'number') {
    throw new Error('Syntax error');
  }

  for (let i = 1; i < parsed.length; i += 2) {
    if (parsed[i] === undefined || parsed[i].type !== 'operator') {
      throw new Error('Syntax error');
    }
    if (parsed[i + 1] === undefined || parsed[i + 1].type !== 'number') {
      throw new Error('Syntax error');
    }
  }

  // If odd length, last must be number
  if (parsed.length % 2 === 0) {
    throw new Error('Syntax error');
  }

  // Evaluate left-to-right
  let result = parsed[0].value;
  for (let i = 1; i < parsed.length; i += 2) {
    const op = parsed[i].value;
    const nextNum = parsed[i + 1].value;
    result = operators[op](result, nextNum);
  }

  return result;
};
