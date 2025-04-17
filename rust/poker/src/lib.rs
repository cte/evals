use std::cmp::Ordering;

#[derive(Debug, PartialEq, Eq, PartialOrd, Ord, Clone, Copy)]
enum Suit {
    Clubs,
    Diamonds,
    Hearts,
    Spades,
}

#[derive(Debug, PartialEq, Eq, PartialOrd, Ord, Clone, Copy)]
struct Card {
    rank: u8, // 2-14 (Ace high)
    suit: Suit,
}

impl Card {
    fn from_str(s: &str) -> Self {
        let (rank_str, suit_char) = s.split_at(s.len() - 1);
        let rank = match rank_str {
            "2" => 2,
            "3" => 3,
            "4" => 4,
            "5" => 5,
            "6" => 6,
            "7" => 7,
            "8" => 8,
            "9" => 9,
            "10" => 10,
            "J" => 11,
            "Q" => 12,
            "K" => 13,
            "A" => 14,
            _ => panic!("Invalid rank: {}", rank_str),
        };
        let suit = match suit_char {
            "C" => Suit::Clubs,
            "D" => Suit::Diamonds,
            "H" => Suit::Hearts,
            "S" => Suit::Spades,
            _ => panic!("Invalid suit: {}", suit_char),
        };
        Card { rank, suit }
    }
}

#[derive(Debug, Clone)]
struct Hand<'a> {
    original: &'a str,
    cards: Vec<Card>,
    rank: HandRank,
}

#[derive(Debug, PartialEq, Eq, PartialOrd, Ord, Clone)]
enum HandRank {
    HighCard(Vec<u8>),
    OnePair(u8, Vec<u8>),
    TwoPair(u8, u8, u8),
    ThreeOfAKind(u8, Vec<u8>),
    Straight(u8),
    Flush(Vec<u8>),
    FullHouse(u8, u8),
    FourOfAKind(u8, u8),
    StraightFlush(u8),
}

impl<'a> Hand<'a> {
    fn from_str(s: &'a str) -> Self {
        let mut cards: Vec<Card> = s.split_whitespace().map(Card::from_str).collect();
        cards.sort_by(|a, b| b.rank.cmp(&a.rank)); // descending
        let is_flush = cards.iter().all(|c| c.suit == cards[0].suit);
        let mut ranks: Vec<u8> = cards.iter().map(|c| c.rank).collect();

        // Handle low-Ace straight (A-2-3-4-5)
        let is_straight = if ranks == [14, 5, 4, 3, 2] {
            ranks = vec![5, 4, 3, 2, 1];
            true
        } else {
            ranks.windows(2).all(|w| w[0] == w[1] + 1)
        };

        let mut counts = std::collections::HashMap::new();
        for &r in &ranks {
            *counts.entry(r).or_insert(0) += 1;
        }

        let mut count_vec: Vec<(u8, usize)> = counts.into_iter().collect();
        count_vec.sort_by(|a, b| b.1.cmp(&a.1).then(b.0.cmp(&a.0)));

        let rank = if is_straight && is_flush {
            HandRank::StraightFlush(ranks[0])
        } else if count_vec[0].1 == 4 {
            HandRank::FourOfAKind(count_vec[0].0, count_vec[1].0)
        } else if count_vec[0].1 == 3 && count_vec[1].1 == 2 {
            HandRank::FullHouse(count_vec[0].0, count_vec[1].0)
        } else if is_flush {
            HandRank::Flush(ranks.clone())
        } else if is_straight {
            HandRank::Straight(ranks[0])
        } else if count_vec[0].1 == 3 {
            let kickers: Vec<u8> = count_vec.iter().filter(|x| x.1 == 1).map(|x| x.0).collect();
            HandRank::ThreeOfAKind(count_vec[0].0, kickers)
        } else if count_vec[0].1 == 2 && count_vec[1].1 == 2 {
            let high_pair = count_vec[0].0.max(count_vec[1].0);
            let low_pair = count_vec[0].0.min(count_vec[1].0);
            let kicker = count_vec.iter().find(|x| x.1 == 1).unwrap().0;
            HandRank::TwoPair(high_pair, low_pair, kicker)
        } else if count_vec[0].1 == 2 {
            let kickers: Vec<u8> = count_vec.iter().filter(|x| x.1 == 1).map(|x| x.0).collect();
            HandRank::OnePair(count_vec[0].0, kickers)
        } else {
            HandRank::HighCard(ranks.clone())
        };

        Hand {
            original: s,
            cards,
            rank,
        }
    }
}

impl<'a> Ord for Hand<'a> {
    fn cmp(&self, other: &Self) -> Ordering {
        self.rank.cmp(&other.rank)
    }
}

impl<'a> PartialOrd for Hand<'a> {
    fn partial_cmp(&self, other: &Self) -> Option<Ordering> {
        Some(self.cmp(other))
    }
}

impl<'a> PartialEq for Hand<'a> {
    fn eq(&self, other: &Self) -> bool {
        self.rank == other.rank
    }
}

impl<'a> Eq for Hand<'a> {}

/// Given a list of poker hands, return a list of those hands which win.
///
/// Note the type signature: this function should return _the same_ reference to
/// the winning hand(s) as were passed in, not reconstructed strings which happen to be equal.
pub fn winning_hands<'a>(hands: &[&'a str]) -> Vec<&'a str> {
    let parsed_hands: Vec<Hand<'a>> = hands.iter().map(|&s| Hand::from_str(s)).collect();
    let max_hand = parsed_hands.iter().max().unwrap().rank.clone();
    parsed_hands
        .into_iter()
        .filter(|h| h.rank == max_hand)
        .map(|h| h.original)
        .collect()
}
