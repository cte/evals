//
// This is only a SKELETON file for the 'Say' exercise. It's been provided as a
// convenience to get you started writing code faster.
//

export const say = (n) => {
  if (n < 0 || n > 999999999999) {
    throw new Error('Number must be between 0 and 999,999,999,999.');
  }
  if (n === 0) return 'zero';

  const units = [
    '', 'one', 'two', 'three', 'four', 'five', 'six', 'seven', 'eight', 'nine',
  ];
  const teens = [
    'ten', 'eleven', 'twelve', 'thirteen', 'fourteen', 'fifteen',
    'sixteen', 'seventeen', 'eighteen', 'nineteen',
  ];
  const tens = [
    '', '', 'twenty', 'thirty', 'forty', 'fifty',
    'sixty', 'seventy', 'eighty', 'ninety',
  ];
  const scales = [
    '', 'thousand', 'million', 'billion',
  ];

  const chunkToWords = (num) => {
    let words = [];

    const hundred = Math.floor(num / 100);
    const rest = num % 100;

    if (hundred > 0) {
      words.push(units[hundred]);
      words.push('hundred');
    }

    if (rest > 0) {
      if (rest < 10) {
        words.push(units[rest]);
      } else if (rest < 20) {
        words.push(teens[rest - 10]);
      } else {
        const ten = Math.floor(rest / 10);
        const unit = rest % 10;
        if (unit === 0) {
          words.push(tens[ten]);
        } else {
          words.push(`${tens[ten]}-${units[unit]}`);
        }
      }
    }

    return words.join(' ');
  };

  const chunks = [];
  let num = n;
  while (num > 0) {
    chunks.push(num % 1000);
    num = Math.floor(num / 1000);
  }

  const words = [];
  for (let i = chunks.length - 1; i >= 0; i--) {
    const chunk = chunks[i];
    if (chunk === 0) continue;
    const chunkWords = chunkToWords(chunk);
    if (scales[i]) {
      words.push(`${chunkWords} ${scales[i]}`);
    } else {
      words.push(chunkWords);
    }
  }

  return words.join(' ').trim();
};
