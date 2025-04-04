const numbersToWords = {
  0: 'zero', 1: 'one', 2: 'two', 3: 'three', 4: 'four', 5: 'five', 6: 'six', 7: 'seven', 8: 'eight', 9: 'nine', 10: 'ten',
  11: 'eleven', 12: 'twelve', 13: 'thirteen', 14: 'fourteen', 15: 'fifteen', 16: 'sixteen', 17: 'seventeen', 18: 'eighteen', 19: 'nineteen',
  20: 'twenty', 30: 'thirty', 40: 'forty', 50: 'fifty', 60: 'sixty', 70: 'seventy', 80: 'eighty', 90: 'ninety',
};

const scales = ['', 'thousand', 'million', 'billion']; // No trillion needed as per instructions (max 999,999,999,999)

// Helper function to convert a number less than 1000 to words
const convertChunk = (num) => {
  if (num === 0) return '';

  let words = '';

  if (num >= 100) {
    words += numbersToWords[Math.floor(num / 100)] + ' hundred';
    num %= 100;
    if (num > 0) {
      words += ' ';
    }
  }

  if (num > 0) {
    if (num < 20) {
      words += numbersToWords[num];
    } else {
      words += numbersToWords[Math.floor(num / 10) * 10];
      if (num % 10 > 0) {
        words += '-' + numbersToWords[num % 10];
      }
    }
  }
  return words;
};

export const say = (n) => {
  if (n < 0 || n > 999999999999) {
    throw new Error('Number must be between 0 and 999,999,999,999.');
  }

  if (n === 0) {
    return numbersToWords[0];
  }

  let words = '';
  let chunkIndex = 0;

  while (n > 0) {
    if (n % 1000 !== 0) {
      const chunkWords = convertChunk(n % 1000);
      const scaleWord = scales[chunkIndex];
      words = chunkWords + (scaleWord ? ' ' + scaleWord : '') + (words ? ' ' + words : '');
    }
    n = Math.floor(n / 1000);
    chunkIndex++;
  }

  return words.trim();
};
