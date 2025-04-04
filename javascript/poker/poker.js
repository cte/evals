const RANKS = '23456789TJQKA';
const SUITS = 'SCDH';

// Parses a card string (e.g., 'KH', '10S') into rank and suit values
const parseCard = (cardStr) => {
  const suitStr = cardStr.slice(-1);
  const rankStr = cardStr.length === 3 && cardStr.startsWith('10') ? 'T' : cardStr.slice(0, -1); // Handle '10' specifically
  const rank = RANKS.indexOf(rankStr);
  const suit = SUITS.indexOf(suitStr);
  if (rank === -1 || suit === -1 || (cardStr.length === 3 && !cardStr.startsWith('10'))) { // Added check for invalid length like '100S'
    throw new Error(`Invalid card: ${cardStr}`);
  }
  // Return the original rank string ('10') if it was 10, otherwise the single char
  const originalRankStr = cardStr.length === 3 && cardStr.startsWith('10') ? '10' : rankStr;
  return { rank, suit, rankStr: originalRankStr, suitStr };
};

// Parses a hand string (e.g., '4S 5S 7H 8D JC') into sorted cards
const parseHand = (handStr) => {
  const cards = handStr.split(' ').map(parseCard);
  if (cards.length !== 5) {
    throw new Error(`Invalid hand size: ${handStr}`);
  }
  // Sort cards by rank descending
  cards.sort((a, b) => b.rank - a.rank);
  return cards;
};

// Gets counts of each rank and suit
const getCounts = (cards) => {
  const rankCounts = Array(RANKS.length).fill(0);
  const suitCounts = Array(SUITS.length).fill(0);
  const ranks = [];
  cards.forEach(card => {
    rankCounts[card.rank]++;
    suitCounts[card.suit]++;
    ranks.push(card.rank);
  });
  return { rankCounts, suitCounts, ranks };
};

// Determines the rank and tie-breaking values for a hand
const getHandDetails = (handStr) => {
  const cards = parseHand(handStr);
  const { rankCounts, suitCounts, ranks } = getCounts(cards);

  const isFlush = suitCounts.some(count => count >= 5);
  // Check for Ace-low straight (A, 2, 3, 4, 5)
  const isAceLowStraight = ranks.toString() === '12,3,2,1,0'; // A, 5, 4, 3, 2 sorted descending
  const ranksForStraightCheck = isAceLowStraight ? [3, 2, 1, 0, -1] : ranks; // Use 5,4,3,2,A (represented by indices 3,2,1,0,-1) for Ace-low
  
  let isStraight = true;
  for (let i = 0; i < ranksForStraightCheck.length - 1; i++) {
    if (ranksForStraightCheck[i] !== ranksForStraightCheck[i+1] + 1) {
      isStraight = false;
      break;
    }
  }
  
  const straightHighCard = isAceLowStraight ? 3 : ranksForStraightCheck[0]; // High card is 5 (index 3) for A-low

  if (isStraight && isFlush) {
    return { rank: 8, values: [straightHighCard], handStr }; // Straight Flush (or Royal Flush if high card is Ace)
  }

  const counts = rankCounts.filter(count => count > 0).sort((a, b) => b - a);
  const rankGroups = rankCounts
    .map((count, rank) => ({ rank, count }))
    .filter(group => group.count > 0)
    .sort((a, b) => b.count - a.count || b.rank - a.rank); // Sort by count desc, then rank desc

  const groupValues = rankGroups.map(g => g.rank);

  if (counts[0] === 4) {
    return { rank: 7, values: groupValues, handStr }; // Four of a Kind
  }
  if (counts[0] === 3 && counts[1] === 2) {
    return { rank: 6, values: groupValues, handStr }; // Full House
  }
  if (isFlush) {
    return { rank: 5, values: ranks, handStr }; // Flush
  }
  if (isStraight) {
    return { rank: 4, values: [straightHighCard], handStr }; // Straight
  }
  if (counts[0] === 3) {
    return { rank: 3, values: groupValues, handStr }; // Three of a Kind
  }
  if (counts[0] === 2 && counts[1] === 2) {
    return { rank: 2, values: groupValues, handStr }; // Two Pair
  }
  if (counts[0] === 2) {
    return { rank: 1, values: groupValues, handStr }; // One Pair
  }
  return { rank: 0, values: ranks, handStr }; // High Card
};

// Compares two hands based on rank and tie-breaking values
const compareHands = (handA, handB) => {
  if (handA.rank !== handB.rank) {
    return handB.rank - handA.rank;
  }
  for (let i = 0; i < handA.values.length; i++) {
    if (handA.values[i] !== handB.values[i]) {
      return handB.values[i] - handA.values[i];
    }
  }
  return 0; // Hands are equal
};

export const bestHands = (hands) => {
  if (!hands || hands.length === 0) {
    return [];
  }

  const handDetails = hands.map(getHandDetails);
  handDetails.sort(compareHands);

  const bestRank = handDetails[0].rank;
  const bestValues = handDetails[0].values;

  const winningHands = handDetails
    .filter(hand => hand.rank === bestRank && compareHands(hand, handDetails[0]) === 0)
    .map(hand => hand.handStr);

  return winningHands;
};
