// Helper function to count letter frequencies in a single text
const countLetters = (text) => {
  const frequency = {};
  // Convert to lowercase and iterate over characters
  for (const char of text.toLowerCase()) {
    // Check if it's a letter using regex
    if (/\p{L}/u.test(char)) {
      frequency[char] = (frequency[char] || 0) + 1;
    }
  }
  return frequency;
};

// Main function to calculate frequency concurrently
export const parallelLetterFrequency = async (texts) => {
  if (!texts || texts.length === 0) {
    return {}; // Handle empty or null input
  }

  // Create an array of promises, each calculating frequency for one text
  const promises = texts.map(text => {
    // Wrap the synchronous countLetters call in a Promise
    // to make it execute concurrently when awaited with Promise.all
    // Handle potential null/undefined texts within the array
    return Promise.resolve().then(() => countLetters(text || ''));
  });

  // Wait for all promises to resolve
  const results = await Promise.all(promises);

  // Merge the results into a single frequency map
  const totalFrequency = {};
  for (const frequency of results) {
    for (const letter in frequency) {
      totalFrequency[letter] = (totalFrequency[letter] || 0) + frequency[letter];
    }
  }

  return totalFrequency;
};
