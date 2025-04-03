import java.util.*;
import java.util.stream.Collectors;

class Poker {

    private final List<String> hands;
    private final List<Hand> parsedHands;

    Poker(List<String> hands) {
        this.hands = hands;
        this.parsedHands = hands.stream().map(Hand::new).collect(Collectors.toList());
    }

    List<String> getBestHands() {
        List<Hand> sorted = new ArrayList<>(parsedHands);
        sorted.sort(Collections.reverseOrder());
        Hand best = sorted.get(0);
        return sorted.stream()
                .filter(h -> h.compareTo(best) == 0)
                .map(h -> h.original)
                .collect(Collectors.toList());
    }

    private static class Hand implements Comparable<Hand> {
        final String original;
        final List<Card> cards;
        final HandRank rank;

        Hand(String handStr) {
            this.original = handStr;
            this.cards = Arrays.stream(handStr.split(" "))
                    .map(Card::new)
                    .sorted(Comparator.comparingInt((Card c) -> c.rank).reversed())
                    .collect(Collectors.toList());
            this.rank = evaluate();
        }

        private HandRank evaluate() {
            boolean flush = cards.stream().allMatch(c -> c.suit == cards.get(0).suit);
            List<Integer> ranks = cards.stream().map(c -> c.rank).collect(Collectors.toList());

            StraightResult straightResult = checkStraight(ranks);

            Map<Integer, Long> counts = ranks.stream()
                    .collect(Collectors.groupingBy(r -> r, Collectors.counting()));

            List<Long> countValues = counts.values().stream()
                    .sorted(Comparator.reverseOrder())
                    .collect(Collectors.toList());

            List<Integer> orderedRanks = counts.entrySet().stream()
                    .sorted((a, b) -> {
                        int cmp = Long.compare(b.getValue(), a.getValue());
                        if (cmp != 0) return cmp;
                        return Integer.compare(b.getKey(), a.getKey());
                    })
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList());

            if (straightResult.isStraight && flush) return new HandRank(8, List.of(straightResult.highCard));
            if (countValues.equals(List.of(4L,1L))) return new HandRank(7, orderedRanks);
            if (countValues.equals(List.of(3L,2L))) return new HandRank(6, orderedRanks);
            if (flush) return new HandRank(5, orderedRanks);
            if (straightResult.isStraight) return new HandRank(4, List.of(straightResult.highCard));
            if (countValues.equals(List.of(3L,1L,1L))) return new HandRank(3, orderedRanks);
            if (countValues.equals(List.of(2L,2L,1L))) return new HandRank(2, orderedRanks);
            if (countValues.equals(List.of(2L,1L,1L,1L))) return new HandRank(1, orderedRanks);
            return new HandRank(0, orderedRanks);
        }

        private static class StraightResult {
            final boolean isStraight;
            final int highCard;

            StraightResult(boolean isStraight, int highCard) {
                this.isStraight = isStraight;
                this.highCard = highCard;
            }
        }

        private StraightResult checkStraight(List<Integer> ranks) {
            List<Integer> sorted = new ArrayList<>(new HashSet<>(ranks));
            Collections.sort(sorted);
            if (sorted.size() != 5) return new StraightResult(false, -1);
            // Check high-Ace straight
            if (sorted.get(4) - sorted.get(0) == 4) {
                return new StraightResult(true, sorted.get(4));
            }
            // Check low-Ace straight (A=14 treated as 1)
            if (sorted.contains(14)) {
                sorted.remove(Integer.valueOf(14));
                sorted.add(0,1);
                Collections.sort(sorted);
                if (sorted.get(4) - sorted.get(0) == 4) {
                    return new StraightResult(true, 5);
                }
            }
            return new StraightResult(false, -1);
        }

        @Override
        public int compareTo(Hand o) {
            int cmp = Integer.compare(this.rank.rankType, o.rank.rankType);
            if (cmp != 0) return cmp;
            for (int i = 0; i < this.rank.ranks.size(); i++) {
                cmp = Integer.compare(this.rank.ranks.get(i), o.rank.ranks.get(i));
                if (cmp != 0) return cmp;
            }
            return 0;
        }
    }

    private static class HandRank {
        final int rankType; // 8=straight flush, 7=four, 6=full, 5=flush, 4=straight, 3=three, 2=two pair, 1=pair, 0=high card
        final List<Integer> ranks;

        HandRank(int rankType, List<Integer> ranks) {
            this.rankType = rankType;
            this.ranks = ranks;
        }
    }

    private static class Card {
        final int rank;
        final char suit;

        Card(String s) {
            if (s.length() == 3 && s.startsWith("10")) {
                this.rank = 10;
                this.suit = s.charAt(2);
            } else {
                this.rank = parseRank(s.charAt(0));
                this.suit = s.charAt(1);
            }
        }

        private int parseRank(char c) {
            return switch (c) {
                case '2' -> 2;
                case '3' -> 3;
                case '4' -> 4;
                case '5' -> 5;
                case '6' -> 6;
                case '7' -> 7;
                case '8' -> 8;
                case '9' -> 9;
                case 'T' -> 10;
                case 'J' -> 11;
                case 'Q' -> 12;
                case 'K' -> 13;
                case 'A' -> 14;
                default -> throw new IllegalArgumentException("Invalid rank: " + c);
            };
        }
    }
}