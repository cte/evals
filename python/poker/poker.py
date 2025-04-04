import collections

# Card ranks mapping (T=10, J=11, Q=12, K=13, A=14)
# Ace can also be low (1) for A-2-3-4-5 straights, handled separately.
RANKS = {str(n): n for n in range(2, 10)}
RANKS.update({'T': 10, 'J': 11, 'Q': 12, 'K': 13, 'A': 14})
RANK_ORDER = '23456789TJQKA'

# Hand ranks (higher is better)
HIGH_CARD = 0
ONE_PAIR = 1
TWO_PAIR = 2
THREE_OF_A_KIND = 3
STRAIGHT = 4
FLUSH = 5
FULL_HOUSE = 6
FOUR_OF_A_KIND = 7
STRAIGHT_FLUSH = 8

def parse_card(card_str):
    """Parses a card string like '4S' into (rank, suit)."""
    if len(card_str) == 3: # Handle '10' rank
        rank_str = 'T' # Use 'T' for rank 10
        suit = card_str[2]
    else:
        rank_str = card_str[0]
        suit = card_str[1]
    rank = RANKS[rank_str]
    return rank, suit

def score_hand(hand_str):
    """Scores a poker hand string. Returns a tuple for comparison.
    Format: (hand_rank, kicker_ranks...)
    Higher hand_rank wins. If tied, compare kickers in order.
    """
    cards = sorted([parse_card(c) for c in hand_str.split()], key=lambda x: x[0], reverse=True)
    ranks = [card[0] for card in cards]
    suits = [card[1] for card in cards]

    is_flush = len(set(suits)) == 1
    # Check for A-2-3-4-5 straight (ranks are 14, 5, 4, 3, 2)
    is_low_ace_straight = ranks == [14, 5, 4, 3, 2]
    # Check for normal straight
    is_straight = all(ranks[i] == ranks[0] - i for i in range(5)) or is_low_ace_straight

    # Adjust ranks for A-2-3-4-5 straight for comparison (treat Ace as low)
    if is_low_ace_straight:
        ranks = [5, 4, 3, 2, 1] # Use 1 for Ace's rank in this specific straight

    # Straight Flush
    if is_straight and is_flush:
        # Highest card determines rank (use adjusted ranks for A-5 straight)
        return (STRAIGHT_FLUSH, ranks[0])

    rank_counts = collections.Counter(ranks)
    counts = sorted(rank_counts.values(), reverse=True)
    most_common_ranks = sorted(rank_counts.keys(), key=lambda r: (rank_counts[r], r), reverse=True)

    # Four of a Kind
    if counts == [4, 1]:
        four_rank = most_common_ranks[0]
        kicker = most_common_ranks[1]
        return (FOUR_OF_A_KIND, four_rank, kicker)

    # Full House
    if counts == [3, 2]:
        three_rank = most_common_ranks[0]
        pair_rank = most_common_ranks[1]
        return (FULL_HOUSE, three_rank, pair_rank)

    # Flush
    if is_flush:
        return (FLUSH,) + tuple(ranks)

    # Straight
    if is_straight:
         # Highest card determines rank (use adjusted ranks for A-5 straight)
        return (STRAIGHT, ranks[0])

    # Three of a Kind
    if counts == [3, 1, 1]:
        three_rank = most_common_ranks[0]
        kickers = sorted([r for r in ranks if r != three_rank], reverse=True)
        return (THREE_OF_A_KIND, three_rank) + tuple(kickers)

    # Two Pair
    if counts == [2, 2, 1]:
        pair_ranks = sorted([r for r in most_common_ranks if rank_counts[r] == 2], reverse=True)
        kicker = most_common_ranks[2]
        return (TWO_PAIR,) + tuple(pair_ranks) + (kicker,)

    # One Pair
    if counts == [2, 1, 1, 1]:
        pair_rank = most_common_ranks[0]
        kickers = sorted([r for r in ranks if r != pair_rank], reverse=True)
        return (ONE_PAIR, pair_rank) + tuple(kickers)

    # High Card
    return (HIGH_CARD,) + tuple(ranks)


def best_hands(hands):
    """Given a list of poker hands, return the best hand(s)."""
    if not hands:
        return []
    if len(hands) == 1:
        return hands

    scored_hands = []
    for hand_str in hands:
        score = score_hand(hand_str)
        scored_hands.append((score, hand_str))

    # Find the maximum score
    max_score = max(scored_hands, key=lambda x: x[0])[0]

    # Return all hands that match the maximum score
    winners = [hand_str for score, hand_str in scored_hands if score == max_score]
    return winners
