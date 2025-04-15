// book-store.js
const BOOK_PRICE = 800; // Price in cents
const DISCOUNTS = {
  1: 1.0,  // 0% discount
  2: 0.95, // 5% discount
  3: 0.90, // 10% discount
  4: 0.80, // 20% discount
  5: 0.75, // 25% discount
};

export const cost = (books) => {
  if (!books || books.length === 0) {
    return 0;
  }

  // Count occurrences of each book
  const counts = {};
  for (const book of books) {
    counts[book] = (counts[book] || 0) + 1;
  }

  // Iteratively form sets of distinct books
  const setSizes = [];
  let distinctBooks = Object.keys(counts).filter(book => counts[book] > 0);

  while (distinctBooks.length > 0) {
    setSizes.push(distinctBooks.length);
    // Decrement count for books included in this set
    for (const book of distinctBooks) {
      counts[book]--;
    }
    // Update the list of distinct books available for the next set
    distinctBooks = Object.keys(counts).filter(book => counts[book] > 0);
  }

  // Apply the optimization: replace sets of 5 and 3 with two sets of 4
  // This is cheaper: (5 * 0.75 + 3 * 0.90) * 800 = (3.75 + 2.70) * 800 = 6.45 * 800
  // vs (4 * 0.80 + 4 * 0.80) * 800 = (3.20 + 3.20) * 800 = 6.40 * 800
  while (setSizes.includes(5) && setSizes.includes(3)) {
    const indexOf5 = setSizes.indexOf(5);
    setSizes.splice(indexOf5, 1); // Remove one 5
    const indexOf3 = setSizes.indexOf(3);
    setSizes.splice(indexOf3, 1); // Remove one 3
    setSizes.push(4, 4); // Add two 4s
  }

  // Calculate the total cost
  let totalCost = 0;
  for (const size of setSizes) {
    totalCost += size * BOOK_PRICE * DISCOUNTS[size];
  }

  // Return the total cost in cents (should already be integer)
  return Math.round(totalCost); // Use Math.round just in case of floating point inaccuracies
};
