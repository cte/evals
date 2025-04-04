import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

class Poker {
    private final List<Hand> hands;
    private final List<Hand> bestHands;

    Poker(List<String> handStrings) {
        this.hands = handStrings.stream().map(Hand::new).collect(Collectors.toList());
        this.bestHands = findBestHands();
    }

    List<String> getBestHands() {
        return bestHands.stream().map(Hand::toString).collect(Collectors.toList());
    }

    private List<Hand> findBestHands() {
        if (hands.isEmpty()) {
            return Collections.emptyList();
        }
        List<Hand> sortedHands = new ArrayList<>(hands);
        sortedHands.sort(Collections.reverseOrder()); // Sort descending, best hands first
        Hand bestHand = sortedHands.get(0);
        return sortedHands.stream()
                .filter(hand -> hand.compareTo(bestHand) == 0)
                .collect(Collectors.toList());
    }

    // --- Inner Classes ---

    enum Suit { S, H, D, C }

    enum HandRank {
        HIGH_CARD, ONE_PAIR, TWO_PAIR, THREE_OF_A_KIND, STRAIGHT,
        FLUSH, FULL_HOUSE, FOUR_OF_A_KIND, STRAIGHT_FLUSH
    }

    static class Card implements Comparable<Card> {
        final int rank; // 2-10, J=11, Q=12, K=13, A=14
        final Suit suit;
        final String representation; // e.g., "KH", "AS"

        Card(String cardStr) {
            this.representation = cardStr;
            char suitChar = cardStr.charAt(cardStr.length() - 1);
            String rankStr = cardStr.substring(0, cardStr.length() - 1);

            this.rank = switch (rankStr) {
                case "T" -> 10;
                case "J" -> 11;
                case "Q" -> 12;
                case "K" -> 13;
                case "A" -> 14;
                default -> Integer.parseInt(rankStr); // Handles 2-10
            };

            this.suit = switch (suitChar) {
                case 'S' -> Suit.S;
                case 'H' -> Suit.H;
                case 'D' -> Suit.D;
                case 'C' -> Suit.C;
                default -> throw new IllegalArgumentException("Invalid suit: " + suitChar);
            };
        }

        @Override
        public int compareTo(Card other) {
            return Integer.compare(this.rank, other.rank);
        }

        @Override
        public String toString() {
            return representation;
        }
    }

    static class Hand implements Comparable<Hand> {
        final List<Card> cards;
        final String originalString;
        final HandRank handRank;
        final List<Integer> tieBreakingRanks; // Ranks used for tie-breaking, highest first

        Hand(String handStr) {
            this.originalString = handStr;
            this.cards = Arrays.stream(handStr.split(" "))
                    .map(Card::new)
                    .sorted(Comparator.comparingInt((Card c) -> c.rank).reversed()) // Sort cards high to low
                    .collect(Collectors.toList());

            if (this.cards.size() != 5) {
                throw new IllegalArgumentException("Hand must contain 5 cards: " + handStr);
            }

            EvaluationResult eval = evaluateHand();
            this.handRank = eval.rank;
            this.tieBreakingRanks = eval.tieBreakingRanks;
        }

        private EvaluationResult evaluateHand() {
            boolean isFlush = cards.stream().map(c -> c.suit).distinct().count() == 1;
            boolean isStraight = isStraight(cards);

            if (isStraight && isFlush) {
                // Check for Ace-low straight flush (A 2 3 4 5)
                if (cards.get(0).rank == 14 && cards.get(1).rank == 5) {
                     return new EvaluationResult(HandRank.STRAIGHT_FLUSH, List.of(5)); // A-5 uses 5 as high card
                }
                return new EvaluationResult(HandRank.STRAIGHT_FLUSH, List.of(cards.get(0).rank));
            }

            Map<Integer, Long> rankCounts = cards.stream()
                    .collect(Collectors.groupingBy(c -> c.rank, Collectors.counting()));

            List<Integer> counts = rankCounts.values().stream().sorted(Comparator.reverseOrder()).map(Long::intValue).collect(Collectors.toList());
            List<Integer> ranksByFrequency = rankCounts.entrySet().stream()
                    .sorted(Map.Entry.<Integer, Long>comparingByValue().reversed()
                            .thenComparing(Map.Entry.comparingByKey(Comparator.reverseOrder())))
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList());

            if (counts.get(0) == 4) { // Four of a Kind
                return new EvaluationResult(HandRank.FOUR_OF_A_KIND, ranksByFrequency); // [Rank of 4, Kicker]
            }
            if (counts.get(0) == 3 && counts.get(1) == 2) { // Full House
                return new EvaluationResult(HandRank.FULL_HOUSE, ranksByFrequency); // [Rank of 3, Rank of 2]
            }
            if (isFlush) {
                return new EvaluationResult(HandRank.FLUSH, cards.stream().map(c -> c.rank).collect(Collectors.toList())); // All card ranks
            }
             if (isStraight) {
                // Check for Ace-low straight (A 2 3 4 5)
                if (cards.get(0).rank == 14 && cards.get(1).rank == 5) {
                     return new EvaluationResult(HandRank.STRAIGHT, List.of(5)); // A-5 uses 5 as high card
                }
                return new EvaluationResult(HandRank.STRAIGHT, List.of(cards.get(0).rank));
            }
            if (counts.get(0) == 3) { // Three of a Kind
                return new EvaluationResult(HandRank.THREE_OF_A_KIND, ranksByFrequency); // [Rank of 3, Kicker1, Kicker2]
            }
            if (counts.get(0) == 2 && counts.get(1) == 2) { // Two Pair
                return new EvaluationResult(HandRank.TWO_PAIR, ranksByFrequency); // [High Pair Rank, Low Pair Rank, Kicker]
            }
            if (counts.get(0) == 2) { // One Pair
                return new EvaluationResult(HandRank.ONE_PAIR, ranksByFrequency); // [Pair Rank, Kicker1, Kicker2, Kicker3]
            }
            // High Card
            return new EvaluationResult(HandRank.HIGH_CARD, cards.stream().map(c -> c.rank).collect(Collectors.toList())); // All card ranks
        }

        private boolean isStraight(List<Card> sortedCards) {
            // Check for Ace-low straight (A 2 3 4 5)
            boolean aceLowStraight = sortedCards.get(0).rank == 14 &&
                                     sortedCards.get(1).rank == 5 &&
                                     sortedCards.get(2).rank == 4 &&
                                     sortedCards.get(3).rank == 3 &&
                                     sortedCards.get(4).rank == 2;
            if (aceLowStraight) return true;

            // Check for regular straight
            return IntStream.range(0, sortedCards.size() - 1)
                    .allMatch(i -> sortedCards.get(i).rank == sortedCards.get(i + 1).rank + 1);
        }


        @Override
        public int compareTo(Hand other) {
            int rankComparison = this.handRank.compareTo(other.handRank);
            if (rankComparison != 0) {
                return rankComparison;
            }
            // Tie-breaking based on ranks
            for (int i = 0; i < this.tieBreakingRanks.size(); i++) {
                int tieComparison = Integer.compare(this.tieBreakingRanks.get(i), other.tieBreakingRanks.get(i));
                if (tieComparison != 0) {
                    return tieComparison;
                }
            }
            return 0; // Hands are exactly equal
        }

        @Override
        public String toString() {
            return originalString;
        }

        private record EvaluationResult(HandRank rank, List<Integer> tieBreakingRanks) {}
    }
}