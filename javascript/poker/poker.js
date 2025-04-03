//
// This is only a SKELETON file for the 'Poker' exercise. It's been provided as a
// convenience to get you started writing code faster.
//

const RANK_ORDER = {'2':2,'3':3,'4':4,'5':5,'6':6,'7':7,'8':8,'9':9,'10':10,'J':11,'Q':12,'K':13,'A':14};

function parseHand(handStr) {
  const cards = handStr.split(' ').map(card => {
    const match = card.match(/^(10|[2-9JQKA])([SHDC])$/);
    return { rank: RANK_ORDER[match[1]], suit: match[2] };
  });
  cards.sort((a, b) => b.rank - a.rank);
  return cards;
}

function isFlush(cards) {
  return cards.every(c => c.suit === cards[0].suit);
}

function isStraight(cards) {
  const ranks = cards.map(c => c.rank);
  for (let i = 0; i < 4; i++) {
    if (ranks[i] - 1 !== ranks[i + 1]) {
      // Check for wheel straight (A-2-3-4-5)
      if (i === 0 && ranks[0] === 14 && ranks[1] === 5 && ranks[2] === 4 && ranks[3] === 3 && ranks[4] === 2) {
        return true;
      }
      return false;
    }
  }
  return true;
}

function groupByRank(cards) {
  const groups = {};
  for (const c of cards) {
    groups[c.rank] = (groups[c.rank] || 0) + 1;
  }
  const sorted = Object.entries(groups).map(([rank, count]) => ({rank: parseInt(rank), count}));
  sorted.sort((a, b) => b.count - a.count || b.rank - a.rank);
  return sorted;
}

function handScore(cards) {
  const flush = isFlush(cards);
  const straight = isStraight(cards);
  const groups = groupByRank(cards);

  if (straight && flush) return [8, cards[0].rank === 14 && cards[1].rank === 5 ? 5 : cards[0].rank];
  if (groups[0].count === 4) return [7, groups[0].rank, groups[1].rank];
  if (groups[0].count === 3 && groups[1].count === 2) return [6, groups[0].rank, groups[1].rank];
  if (flush) return [5, ...cards.map(c => c.rank)];
  if (straight) return [4, cards[0].rank === 14 && cards[1].rank === 5 ? 5 : cards[0].rank];
  if (groups[0].count === 3) return [3, groups[0].rank, ...groups.slice(1).map(g => g.rank)];
  if (groups[0].count === 2 && groups[1].count === 2) return [2, groups[0].rank, groups[1].rank, ...groups.slice(2).map(g => g.rank)];
  if (groups[0].count === 2) return [1, groups[0].rank, ...groups.slice(1).map(g => g.rank)];
  return [0, ...cards.map(c => c.rank)];
}

export const bestHands = (hands) => {
  const scored = hands.map(h => {
    const cards = parseHand(h);
    return { hand: h, score: handScore(cards) };
  });

  scored.sort((a, b) => {
    for (let i = 0; i < a.score.length; i++) {
      if ((b.score[i] ?? 0) !== (a.score[i] ?? 0)) {
        return (b.score[i] ?? 0) - (a.score[i] ?? 0);
      }
    }
    return 0;
  });

  const best = scored[0].score;
  return scored.filter(s => s.score.every((v, i) => v === best[i])).map(s => s.hand);
};
