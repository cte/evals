package poker

import (
	"errors"
	"sort"
	"strings"
)

type card struct {
	rank int
	suit rune
}

type hand struct {
	cards []card
	raw   string
	rank  int
	tiebreakers []int
}

var rankMap = map[string]int{
	"2": 2, "3": 3, "4": 4, "5": 5, "6": 6, "7": 7, "8": 8, "9": 9, "10": 10,
	"J": 11, "Q": 12, "K": 13, "A": 14,
}

var validSuits = map[rune]bool{'♤': true, '♡': true, '♢': true, '♧': true}

func parseCard(s string) (card, error) {
	s = strings.TrimSpace(s)
	runes := []rune(s)
	if len(runes) < 2 {
		return card{}, errors.New("invalid card length")
	}
	suit := runes[len(runes)-1]
	rankStr := string(runes[:len(runes)-1])
	rankStr = strings.TrimSpace(rankStr)
	rank, ok := rankMap[rankStr]
	if !ok {
		return card{}, errors.New("invalid rank: " + rankStr)
	}
	if !validSuits[suit] {
		return card{}, errors.New("invalid suit")
	}
	return card{rank, suit}, nil
}

func parseHand(raw string) (hand, error) {
	parts := strings.Split(raw, " ")
	if len(parts) != 5 {
		return hand{}, errors.New("hand must have 5 cards")
	}
	cards := make([]card, 5)
	for i, p := range parts {
		c, err := parseCard(p)
		if err != nil {
			return hand{}, err
		}
		cards[i] = c
	}
	sort.Slice(cards, func(i, j int) bool { return cards[i].rank < cards[j].rank })
	h := hand{cards: cards, raw: raw}
	h.rank, h.tiebreakers = evaluateHand(cards)
	return h, nil
}

func evaluateHand(cs []card) (int, []int) {
	isFlush := true
	for i := 1; i < 5; i++ {
		if cs[i].suit != cs[0].suit {
			isFlush = false
			break
		}
	}
	ranks := make([]int, 5)
	for i, c := range cs {
		ranks[i] = c.rank
	}
	isStraight := true
	for i := 1; i < 5; i++ {
		if ranks[i] != ranks[i-1]+1 {
			isStraight = false
			break
		}
	}
	// Special case: Ace-low straight (A 2 3 4 5)
	if !isStraight && ranks[4] == 14 && ranks[0] == 2 && ranks[1] == 3 && ranks[2] == 4 && ranks[3] == 5 {
		isStraight = true
		ranks[4] = 1 // treat Ace as 1 for tie-breaking
		sort.Ints(ranks)
	}

	counts := make(map[int]int)
	for _, r := range ranks {
		counts[r]++
	}

	var pairs, trips, quads int
	var pairRanks, tripRanks, quadRanks []int
	for r, cnt := range counts {
		switch cnt {
		case 4:
			quads++
			quadRanks = append(quadRanks, r)
		case 3:
			trips++
			tripRanks = append(tripRanks, r)
		case 2:
			pairs++
			pairRanks = append(pairRanks, r)
		}
	}

	sort.Sort(sort.Reverse(sort.IntSlice(pairRanks)))
	sort.Sort(sort.Reverse(sort.IntSlice(tripRanks)))
	sort.Sort(sort.Reverse(sort.IntSlice(quadRanks)))

	switch {
	case isStraight && isFlush:
		return 8, []int{ranks[4]} // straight flush
	case quads == 1:
		kickers := getKickers(ranks, quadRanks)
		return 7, append(quadRanks, kickers...)
	case trips == 1 && pairs == 1:
		return 6, append(tripRanks, pairRanks...)
	case isFlush:
		return 5, reverseSort(ranks)
	case isStraight:
		return 4, []int{ranks[4]}
	case trips == 1:
		kickers := getKickers(ranks, tripRanks)
		return 3, append(tripRanks, kickers...)
	case pairs == 2:
		kickers := getKickers(ranks, pairRanks)
		return 2, append(pairRanks, kickers...)
	case pairs == 1:
		kickers := getKickers(ranks, pairRanks)
		return 1, append(pairRanks, kickers...)
	default:
		return 0, reverseSort(ranks)
	}
}

func getKickers(ranks []int, exclude []int) []int {
	m := make(map[int]bool)
	for _, e := range exclude {
		m[e] = true
	}
	var kickers []int
	for i := len(ranks) - 1; i >= 0; i-- {
		if !m[ranks[i]] {
			kickers = append(kickers, ranks[i])
		}
	}
	return kickers
}

func reverseSort(ranks []int) []int {
	out := make([]int, len(ranks))
	copy(out, ranks)
	sort.Sort(sort.Reverse(sort.IntSlice(out)))
	return out
}

func BestHand(hands []string) ([]string, error) {
	parsed := make([]hand, 0, len(hands))
	for _, raw := range hands {
		h, err := parseHand(raw)
		if err != nil {
			return nil, err
		}
		parsed = append(parsed, h)
	}
	bestRank := -1
	var bestHands []hand
	for _, h := range parsed {
		if h.rank > bestRank {
			bestRank = h.rank
			bestHands = []hand{h}
		} else if h.rank == bestRank {
			bestHands = append(bestHands, h)
		}
	}
	// tie-breaker
	var winners []string
	for _, h := range bestHands {
		if len(winners) == 0 {
			winners = []string{h.raw}
			continue
		}
		comp := compareTiebreakers(h.tiebreakers, parsedHandByRaw(winners[0], parsed).tiebreakers)
		if comp > 0 {
			winners = []string{h.raw}
		} else if comp == 0 {
			winners = append(winners, h.raw)
		}
	}
	return winners, nil
}

func parsedHandByRaw(raw string, hands []hand) hand {
	for _, h := range hands {
		if h.raw == raw {
			return h
		}
	}
	return hand{}
}

func compareTiebreakers(a, b []int) int {
	for i := 0; i < len(a) && i < len(b); i++ {
		if a[i] > b[i] {
			return 1
		} else if a[i] < b[i] {
			return -1
		}
	}
	return 0
}
