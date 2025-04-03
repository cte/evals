from collections import Counter

CARD_ORDER = {'2': 2, '3': 3, '4': 4, '5': 5, '6': 6, '7': 7,
              '8': 8, '9': 9, '10': 10, 'J': 11, 'Q': 12, 'K': 13, 'A': 14}

HAND_RANKS = {
    'high_card': 0,
    'one_pair': 1,
    'two_pair': 2,
    'three_of_a_kind': 3,
    'straight': 4,
    'flush': 5,
    'full_house': 6,
    'four_of_a_kind': 7,
    'straight_flush': 8,
}


def best_hands(hands):
    def parse_hand(hand):
        cards = hand.split()
        ranks = []
        suits = []
        for card in cards:
            if len(card) == 3:  # 10X
                rank = '10'
                suit = card[2]
            else:
                rank = card[0]
                suit = card[1]
            ranks.append(rank)
            suits.append(suit)
        rank_values = sorted([CARD_ORDER[r] for r in ranks], reverse=True)
        return rank_values, suits

    def is_straight(values):
        # Handle wheel straight (A-2-3-4-5)
        if values == [14, 5, 4, 3, 2]:
            return True, 5
        for i in range(4):
            if values[i] - 1 != values[i + 1]:
                return False, None
        return True, values[0]

    def classify_hand(hand):
        values, suits = parse_hand(hand)
        counts = Counter(values)
        counts_by_freq = sorted(counts.items(), key=lambda x: (-x[1], -x[0]))
        is_flush = len(set(suits)) == 1
        straight, high_straight = is_straight(values)

        if straight and is_flush:
            return (HAND_RANKS['straight_flush'], [high_straight])
        if counts_by_freq[0][1] == 4:
            # Four of a kind
            four = counts_by_freq[0][0]
            kicker = [v for v in values if v != four]
            return (HAND_RANKS['four_of_a_kind'], [four] + kicker)
        if counts_by_freq[0][1] == 3 and counts_by_freq[1][1] == 2:
            # Full house
            three = counts_by_freq[0][0]
            pair = counts_by_freq[1][0]
            return (HAND_RANKS['full_house'], [three, pair])
        if is_flush:
            return (HAND_RANKS['flush'], values)
        if straight:
            return (HAND_RANKS['straight'], [high_straight])
        if counts_by_freq[0][1] == 3:
            # Three of a kind
            three = counts_by_freq[0][0]
            kickers = [v for v in values if v != three]
            return (HAND_RANKS['three_of_a_kind'], [three] + kickers)
        if counts_by_freq[0][1] == 2 and counts_by_freq[1][1] == 2:
            # Two pair
            high_pair = counts_by_freq[0][0]
            low_pair = counts_by_freq[1][0]
            kicker = [v for v in values if v != high_pair and v != low_pair]
            return (HAND_RANKS['two_pair'], [high_pair, low_pair] + kicker)
        if counts_by_freq[0][1] == 2:
            # One pair
            pair = counts_by_freq[0][0]
            kickers = [v for v in values if v != pair]
            return (HAND_RANKS['one_pair'], [pair] + kickers)
        # High card
        return (HAND_RANKS['high_card'], values)

    scored_hands = []
    for hand in hands:
        rank, tiebreaker = classify_hand(hand)
        scored_hands.append((rank, tiebreaker, hand))

    max_rank = max(scored_hands, key=lambda x: (x[0], x[1]))
    best = [h[2] for h in scored_hands if (h[0], h[1]) == (max_rank[0], max_rank[1])]
    return best
