use std::cmp::Ordering;
use std::collections::HashMap;

#[derive(Debug, Clone, Copy, PartialEq, Eq, Hash, PartialOrd, Ord)]
enum Suit {
    Spade,
    Heart,
    Diamond,
    Club,
}

#[derive(Debug, Clone, Copy, PartialEq, Eq, Hash, PartialOrd, Ord)]
enum Rank {
    Two,
    Three,
    Four,
    Five,
    Six,
    Seven,
    Eight,
    Nine,
    Ten,
    Jack,
    Queen,
    King,
    Ace,
}

impl Rank {
    fn from_char(c: char) -> Option<Rank> {
        match c {
            '2' => Some(Rank::Two),
            '3' => Some(Rank::Three),
            '4' => Some(Rank::Four),
            '5' => Some(Rank::Five),
            '6' => Some(Rank::Six),
            '7' => Some(Rank::Seven),
            '8' => Some(Rank::Eight),
            '9' => Some(Rank::Nine),
            'T' => Some(Rank::Ten),
            'J' => Some(Rank::Jack),
            'Q' => Some(Rank::Queen),
            'K' => Some(Rank::King),
            'A' => Some(Rank::Ace),
            _ => None,
        }
    }
}

impl Suit {
    fn from_char(c: char) -> Option<Suit> {
        match c {
            'S' => Some(Suit::Spade),
            'H' => Some(Suit::Heart),
            'D' => Some(Suit::Diamond),
            'C' => Some(Suit::Club),
            _ => None,
        }
    }
}

#[derive(Debug, Clone, Copy, PartialEq, Eq, Hash)]
struct Card {
    rank: Rank,
    suit: Suit,
}

impl Card {
    fn from_str(s: &str) -> Option<Card> {
        if s.len() < 2 || s.len() > 3 { return None; } // Handle "10S" etc.
        let (rank_str, suit_char) = if s.len() == 3 && &s[0..2] == "10" {
            ("T", s.chars().nth(2)?)
        } else {
             (&s[0..1], s.chars().nth(1)?)
        };

        let rank = Rank::from_char(rank_str.chars().next()?)?;
        let suit = Suit::from_char(suit_char)?;
        Some(Card { rank, suit })
    }
}

// Manual Ord implementation for Card based on Rank only
impl Ord for Card {
    fn cmp(&self, other: &Self) -> Ordering {
        self.rank.cmp(&other.rank)
    }
}

impl PartialOrd for Card {
    fn partial_cmp(&self, other: &Self) -> Option<Ordering> {
        Some(self.cmp(other))
    }
}


#[derive(Debug, PartialEq, Eq, PartialOrd, Ord)]
enum HandRank {
    HighCard,
    OnePair,
    TwoPair,
    ThreeOfAKind,
    Straight,
    Flush,
    FullHouse,
    FourOfAKind,
    StraightFlush,
}

#[derive(Debug, Eq)]
struct Hand<'a> {
    original: &'a str,
    cards: Vec<Card>,
    rank: HandRank,
    // Tie breakers: Store ranks in descending order of importance
    // e.g., For Two Pair (Ks, Kh, 5s, 5c, Jd): [King, Five, Jack]
    // e.g., For Full House (As, Ah, Ac, 7d, 7c): [Ace, Seven]
    // e.g., For Straight Flush (9h, 8h, 7h, 6h, 5h): [Nine]
    tie_breakers: Vec<Rank>,
}

impl<'a> Hand<'a> {
     fn from_str(s: &'a str) -> Option<Hand<'a>> {
        let card_strs: Vec<&str> = s.split_whitespace().collect();
        if card_strs.len() != 5 { return None; }

        let mut cards: Vec<Card> = card_strs.iter()
            .map(|cs| Card::from_str(cs))
            .collect::<Option<Vec<Card>>>()?;

        cards.sort_unstable_by(|a, b| b.cmp(a)); // Sort descending by rank

        let (rank, tie_breakers) = Hand::evaluate(&cards)?;

        Some(Hand { original: s, cards, rank, tie_breakers })
    }

    // Evaluates a sorted (desc) Vec<Card>
    fn evaluate(cards: &[Card]) -> Option<(HandRank, Vec<Rank>)> {
        if cards.len() != 5 { return None; }

        let is_flush = cards.windows(2).all(|w| w[0].suit == w[1].suit);

        // Check for Ace-low straight (A, 5, 4, 3, 2)
        let is_ace_low_straight = cards[0].rank == Rank::Ace &&
                                  cards[1].rank == Rank::Five &&
                                  cards[2].rank == Rank::Four &&
                                  cards[3].rank == Rank::Three &&
                                  cards[4].rank == Rank::Two;

        let is_straight = cards.windows(2).all(|w| w[0].rank as usize == w[1].rank as usize + 1) || is_ace_low_straight;

        // Rank counts
        let mut counts: HashMap<Rank, usize> = HashMap::new();
        for card in cards {
            *counts.entry(card.rank).or_insert(0) += 1;
        }
        // Sorted by count (desc), then rank (desc)
        let mut sorted_groups: Vec<(Rank, usize)> = counts.into_iter().collect();
        sorted_groups.sort_unstable_by(|a, b| b.1.cmp(&a.1).then_with(|| b.0.cmp(&a.0)));

        // --- Determine Hand Rank ---

        if is_straight && is_flush {
            // Handle Ace-low straight flush (5 high)
            let high_card = if is_ace_low_straight { Rank::Five } else { cards[0].rank };
            Some((HandRank::StraightFlush, vec![high_card]))
        } else if sorted_groups[0].1 == 4 {
            // Four of a Kind
            let four_rank = sorted_groups[0].0;
            let kicker = sorted_groups[1].0;
            Some((HandRank::FourOfAKind, vec![four_rank, kicker]))
        } else if sorted_groups[0].1 == 3 && sorted_groups[1].1 == 2 {
            // Full House
            let three_rank = sorted_groups[0].0;
            let pair_rank = sorted_groups[1].0;
            Some((HandRank::FullHouse, vec![three_rank, pair_rank]))
        } else if is_flush {
            // Flush
            let ranks = cards.iter().map(|c| c.rank).collect();
            Some((HandRank::Flush, ranks))
        } else if is_straight {
             // Handle Ace-low straight (5 high)
            let high_card = if is_ace_low_straight { Rank::Five } else { cards[0].rank };
            Some((HandRank::Straight, vec![high_card]))
        } else if sorted_groups[0].1 == 3 {
            // Three of a Kind
            let three_rank = sorted_groups[0].0;
            let kickers: Vec<Rank> = sorted_groups.iter().skip(1).map(|g| g.0).collect();
            let mut tie_breakers = vec![three_rank];
            tie_breakers.extend(kickers);
            Some((HandRank::ThreeOfAKind, tie_breakers))
        } else if sorted_groups[0].1 == 2 && sorted_groups[1].1 == 2 {
            // Two Pair
            let high_pair = sorted_groups[0].0;
            let low_pair = sorted_groups[1].0;
            let kicker = sorted_groups[2].0;
            Some((HandRank::TwoPair, vec![high_pair, low_pair, kicker]))
        } else if sorted_groups[0].1 == 2 {
            // One Pair
            let pair_rank = sorted_groups[0].0;
            let kickers: Vec<Rank> = sorted_groups.iter().skip(1).map(|g| g.0).collect();
             let mut tie_breakers = vec![pair_rank];
            tie_breakers.extend(kickers);
            Some((HandRank::OnePair, tie_breakers))
        } else {
            // High Card
            let ranks = cards.iter().map(|c| c.rank).collect();
            Some((HandRank::HighCard, ranks))
        }
    }
}

// --- Comparison Logic for Hand ---

impl<'a> Ord for Hand<'a> {
    fn cmp(&self, other: &Self) -> Ordering {
        self.rank.cmp(&other.rank)
            .then_with(|| self.tie_breakers.cmp(&other.tie_breakers))
    }
}

impl<'a> PartialOrd for Hand<'a> {
    fn partial_cmp(&self, other: &Self) -> Option<Ordering> {
        Some(self.cmp(other))
    }
}

impl<'a> PartialEq for Hand<'a> {
    fn eq(&self, other: &Self) -> bool {
        self.rank == other.rank && self.tie_breakers == other.tie_breakers
    }
}


/// Given a list of poker hands, return a list of those hands which win.
///
/// Note the type signature: this function should return _the same_ reference to
/// the winning hand(s) as were passed in, not reconstructed strings which happen to be equal.
pub fn winning_hands<'a>(hands: &[&'a str]) -> Vec<&'a str> {
    if hands.is_empty() {
        return Vec::new();
    }

    let mut parsed_hands: Vec<Hand<'a>> = hands.iter()
        .filter_map(|h_str| Hand::from_str(h_str))
        .collect();

    if parsed_hands.is_empty() {
        // Handle case where no hands could be parsed (or input was valid but empty after filtering)
        // Although the outer check should catch empty input, this handles parse failures.
        return Vec::new();
    }

    // Sort hands descending (best first)
    parsed_hands.sort_unstable_by(|a, b| b.cmp(a));

    let best_rank = &parsed_hands[0].rank;
    let best_tie_breakers = &parsed_hands[0].tie_breakers;

    parsed_hands.iter()
        .filter(|h| &h.rank == best_rank && &h.tie_breakers == best_tie_breakers)
        .map(|h| h.original)
        .collect()
}
