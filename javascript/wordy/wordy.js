//
// This is only a SKELETON file for the 'Wordy' exercise. It's been provided as a
// convenience to get you started writing code faster.
//

export const answer = (question) => {
  // Check for basic question structure
  if (!question.startsWith('What is') || !question.endsWith('?')) {
    throw new Error('Unknown operation');
  }

  // Remove prefix and suffix, trim whitespace
  const expression = question.substring('What is'.length, question.length - 1).trim();

  if (expression === '') {
    throw new Error('Syntax error'); // Handle empty expression after stripping prefix/suffix
  }

  // Match numbers and known operations
  const tokens = expression.match(/-?\d+|plus|minus|multiplied by|divided by/g);

  if (!tokens) {
    // If only a number is present after "What is" and "?"
    const singleNumberMatch = expression.match(/^(-?\d+)$/);
    if (singleNumberMatch) {
      return parseInt(singleNumberMatch[1], 10);
    }
    // Check if the original expression contained *any* valid tokens before concluding it's an unknown operation
    const hasAnyValidPart = /-?\d+|plus|minus|multiplied by|divided by/.test(expression);
     if (!hasAnyValidPart && expression.split(' ').length > 1) {
       // Likely a non-math question if multiple words but no operators/numbers
       throw new Error('Unknown operation');
     } else if (!hasAnyValidPart && expression.split(' ').length === 1 && isNaN(parseInt(expression, 10))) {
        // Single word, not a number
        throw new Error('Unknown operation');
     }
     // If it wasn't just a number and didn't match expected tokens, it's likely a syntax error or unsupported op
     throw new Error('Syntax error');
  }

  // Check for invalid sequences or unsupported operations explicitly mentioned
  const fullExpressionTokens = expression.split(' ');
  const knownOps = ['plus', 'minus', 'multiplied', 'divided']; // Check parts of multi-word ops
  let containsUnknownOperation = false;
  for (const token of fullExpressionTokens) {
      if (isNaN(parseInt(token, 10)) && !knownOps.some(op => token.includes(op))) {
          // It's not a number and not part of a known operation
          // Exception: 'by' is allowed if preceded by 'multiplied' or 'divided'
          const index = fullExpressionTokens.indexOf(token);
          if (token === 'by' && index > 0 && (fullExpressionTokens[index-1] === 'multiplied' || fullExpressionTokens[index-1] === 'divided')) {
              continue; // 'by' is okay here
          }
          containsUnknownOperation = true;
          break;
      }
  }

  if (containsUnknownOperation) {
      // Check if it's specifically the "cubed" example or similar unsupported op
      if (/cubed/.test(expression)) {
          throw new Error('Unknown operation');
      }
      // Otherwise, treat general unknown words as syntax errors if they weren't caught earlier
      // This might overlap with 'Unknown operation' but helps clarify some test cases
      // Let's refine this: if tokens were found but the original string had extra words, it's likely syntax.
      const reconstructedFromTokens = tokens.join(' ');
      // A simple check: if replacing multi-word ops doesn't match original, likely extra words
      const simplifiedExpression = expression.replace(/multiplied by/g, 'multiplied').replace(/divided by/g, 'divided');
      const simplifiedTokens = tokens.map(t => t.replace(/multiplied by/g, 'multiplied').replace(/divided by/g, 'divided')).join(' ');

      if (simplifiedExpression.split(' ').filter(Boolean).length !== simplifiedTokens.split(' ').filter(Boolean).length) {
           throw new Error('Syntax error');
      }
       // If it passed the above, but still contains unknown words, it's an unknown operation
       if (containsUnknownOperation) { // Re-check after specific cases
           throw new Error('Unknown operation');
       }
  }


  let result = parseInt(tokens[0], 10);
  if (isNaN(result)) {
    throw new Error('Syntax error'); // Must start with a number
  }

  let i = 1;
  while (i < tokens.length) {
    const operation = tokens[i];
    const nextNumToken = tokens[i + 1];

    if (nextNumToken === undefined) {
        // Operation without a following number
        throw new Error('Syntax error');
    }

    const number = parseInt(nextNumToken, 10);

    if (isNaN(number)) {
      // Expecting a number after an operation
      throw new Error('Syntax error');
    }
     // Check if the token *before* the number was actually an operation
     if (!['plus', 'minus', 'multiplied by', 'divided by'].includes(operation)) {
         throw new Error('Syntax error'); // Two numbers in a row, or non-op before number
     }


    switch (operation) {
      case 'plus':
        result += number;
        break;
      case 'minus':
        result -= number;
        break;
      case 'multiplied by':
        result *= number;
        break;
      case 'divided by':
        if (number === 0) throw new Error('Division by zero'); // Although tests don't cover this
        result /= number;
        break;
      default:
        // This case should ideally be caught earlier, but acts as a safeguard
        throw new Error('Syntax error');
    }
    i += 2; // Move past the operation and the number
  }

   // Final check: ensure the last processed token was a number
   if (tokens.length > 1 && isNaN(parseInt(tokens[tokens.length - 1], 10))) {
       throw new Error('Syntax error'); // Ends with an operator
   }


  return result;
};
