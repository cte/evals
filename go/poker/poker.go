package poker

import (
	"fmt"
	"sort"
	"strings"
	"unicode/utf8" // Import utf8 package
)

// Define constants for suits and ranks (using runes)
const (
	Spade   = '♤'
	Heart   = '♡'
	Diamond = '♢'
	Club    = '♧'
)

// Define ranks numerically for easier comparison
const (
	rank2 = iota + 2
	rank3
	rank4
	rank5
	rank6
	rank7
	rank8
	rank9
	rankT // 10
	rankJ // Jack
	rankQ // Queen
	rankK // King
	rankA // Ace
)

// Map string ranks to numerical ranks
var rankMap = map[string]int{ // Use string keys for rank part ("2", "10", "K")
	"2": rank2, "3": rank3, "4": rank4, "5": rank5, "6": rank6, "7": rank7, "8": rank8, "9": rank9,
	"T": rankT, "10": rankT, "J": rankJ, "Q": rankQ, "K": rankK, "A": rankA,
}

// Map numerical ranks back to string for potential debugging/output (optional)
var rankStrMap = map[int]string{
	rank2: "2", rank3: "3", rank4: "4", rank5: "5", rank6: "6", rank7: "7", rank8: "8", rank9: "9",
	rankT: "T", rankJ: "J", rankQ: "Q", rankK: "K", rankA: "A",
}

// Card represents a single playing card
type Card struct {
	Rank int
	Suit rune
}

// Hand represents a parsed poker hand with its evaluation
type Hand struct {
	Original string   // Original string representation
	Cards    []Card   // Parsed cards
	Category int      // Hand category rank (e.g., Straight Flush, Four of a Kind)
	TieRanks []int    // Ranks used for tie-breaking, in order of importance
}

// Define hand categories
const (
	HighCard = iota
	OnePair
	TwoPair
	ThreeOfAKind
	Straight
	Flush
	FullHouse
	FourOfAKind
	StraightFlush
)

// parseCard parses a string representation of a card (e.g., "K♡", "10S")
func parseCard(s string) (Card, error) {
	if len(s) < 2 {
		return Card{}, fmt.Errorf("invalid card format (too short): %s", s)
	}

	// Decode the last rune (suit) and get its width
	suit, width := utf8.DecodeLastRuneInString(s)
	if suit == utf8.RuneError {
		return Card{}, fmt.Errorf("invalid UTF-8 encoding in card: %s", s)
	}
	if suit != Spade && suit != Heart && suit != Diamond && suit != Club {
		return Card{}, fmt.Errorf("invalid card suit: %c in %s", suit, s)
	}

	// The rank is the part before the suit
	rankStr := s[:len(s)-width]

	// Look up the rank string in the map
	rank, ok := rankMap[rankStr]
	if !ok {
		return Card{}, fmt.Errorf("invalid card rank: %s in %s", rankStr, s)
	}

	return Card{Rank: rank, Suit: suit}, nil
}


// parseHand parses a string representation of a hand into a slice of Cards
func parseHand(handStr string) ([]Card, error) {
	cardStrs := strings.Fields(handStr)
	if len(cardStrs) != 5 {
		return nil, fmt.Errorf("invalid hand size: expected 5 cards, got %d in '%s'", len(cardStrs), handStr)
	}
	cards := make([]Card, 5)
	for i, cs := range cardStrs {
		card, err := parseCard(cs)
		if err != nil {
			return nil, fmt.Errorf("error parsing hand '%s': %w", handStr, err)
		}
		cards[i] = card
	}
	return cards, nil
}

// evaluateHand determines the category and tie-breaking ranks of a 5-card hand
func evaluateHand(cards []Card) (category int, tieRanks []int) {
	if len(cards) != 5 {
		panic("evaluateHand requires exactly 5 cards") // Should be caught by parseHand
	}

	// Sort cards by rank (descending) for easier evaluation
	sort.Slice(cards, func(i, j int) bool {
		return cards[i].Rank > cards[j].Rank
	})

	// --- Check for Flush and Straight ---
	isFlush := true
	for i := 1; i < 5; i++ {
		if cards[i].Suit != cards[0].Suit {
			isFlush = false
			break
		}
	}

	// Check for standard straight (A-K-Q-J-T down to 6-5-4-3-2)
	isStraight := true
	for i := 0; i < 4; i++ {
		if cards[i].Rank != cards[i+1].Rank+1 {
			isStraight = false
			break
		}
	}
	// Check for A-2-3-4-5 straight (wheel)
	isWheel := cards[0].Rank == rankA && cards[1].Rank == rank5 && cards[2].Rank == rank4 && cards[3].Rank == rank3 && cards[4].Rank == rank2
	if isWheel {
		isStraight = true
		// For ranking purposes, the Ace in a wheel acts as low card, so the highest card is 5.
        // Reorder cards for tie-breaking: 5, 4, 3, 2, A (becomes lowest)
        // Create a new slice to avoid modifying the original slice potentially used elsewhere
        wheelCards := []Card{cards[1], cards[2], cards[3], cards[4], cards[0]}
        cards = wheelCards // Use the reordered slice for evaluation from now on
	}


	// --- Determine Category based on Flush/Straight ---
	if isStraight && isFlush {
		category = StraightFlush
		tieRanks = []int{cards[0].Rank} // Highest card determines rank (5 for wheel)
		return
	}

	// --- Check for Kind Counts (Four, Three, Pairs) ---
	rankCounts := make(map[int]int)
	allRanks := make([]int, 5)
	for i, c := range cards {
		rankCounts[c.Rank]++
		allRanks[i] = c.Rank // Keep original sorted order for HighCard/Flush tiebreak
	}

	counts := make([]int, 0, 5) // Store the counts (e.g., [4, 1], [3, 2], [3, 1, 1])
	ranksByCount := make(map[int][]int) // Map count to list of ranks having that count

	for rank, count := range rankCounts {
		counts = append(counts, count)
		ranksByCount[count] = append(ranksByCount[count], rank)
	}
	// Sort ranks within each count group descending for consistent tie-breaking
	for _, ranks := range ranksByCount {
		sort.Sort(sort.Reverse(sort.IntSlice(ranks)))
	}
	sort.Sort(sort.Reverse(sort.IntSlice(counts))) // Sort counts descending (e.g., 4,1 or 3,2 or 2,2,1)


	// --- Determine Category based on Counts ---
	if counts[0] == 4 {
		category = FourOfAKind
		fourRank := ranksByCount[4][0]
		kickerRank := ranksByCount[1][0]
		tieRanks = []int{fourRank, kickerRank}
		return
	}

	if counts[0] == 3 && counts[1] == 2 {
		category = FullHouse
		threeRank := ranksByCount[3][0]
		pairRank := ranksByCount[2][0]
		tieRanks = []int{threeRank, pairRank}
		return
	}

	// Return Flush if detected earlier (and not Straight Flush)
	if isFlush {
		category = Flush
		tieRanks = allRanks // Use all 5 card ranks for tie-breaking (already sorted desc)
		return
	}

	// Return Straight if detected earlier (and not Straight Flush)
	if isStraight {
		category = Straight
		tieRanks = []int{cards[0].Rank} // Highest card determines rank (5 for wheel)
		return
	}

	if counts[0] == 3 {
		category = ThreeOfAKind
		threeRank := ranksByCount[3][0]
		kickers := ranksByCount[1] // Already sorted descending
		tieRanks = []int{threeRank, kickers[0], kickers[1]}
		return
	}

	if counts[0] == 2 && counts[1] == 2 {
		category = TwoPair
		pairs := ranksByCount[2] // Already sorted descending (high pair, low pair)
		kicker := ranksByCount[1][0]
		tieRanks = []int{pairs[0], pairs[1], kicker}
		return
	}

	if counts[0] == 2 {
		category = OnePair
		pairRank := ranksByCount[2][0]
		kickers := ranksByCount[1] // Already sorted descending
		tieRanks = []int{pairRank, kickers[0], kickers[1], kickers[2]}
		return
	}

	// Default to High Card
	category = HighCard
	tieRanks = allRanks // Use all 5 card ranks for tie-breaking (already sorted desc)
	return
}

// compareHands compares two evaluated hands. Returns:
// > 0 if h1 is better than h2
// < 0 if h2 is better than h1
// = 0 if they are equal
func compareHands(h1, h2 Hand) int {
	if h1.Category != h2.Category {
		return h1.Category - h2.Category
	}
	// Categories are the same, use tie-breaking ranks
	// Ensure tie ranks slices have the same length before comparing
	// (Should always be true based on evaluateHand logic, but good practice)
	if len(h1.TieRanks) != len(h2.TieRanks) {
		// This indicates a logic error in evaluateHand
		panic(fmt.Sprintf("Tie rank length mismatch: %v vs %v for category %d", h1.TieRanks, h2.TieRanks, h1.Category))
	}
	for i := 0; i < len(h1.TieRanks); i++ {
		if h1.TieRanks[i] != h2.TieRanks[i] {
			return h1.TieRanks[i] - h2.TieRanks[i]
		}
	}
	return 0 // Hands are equal
}

// BestHand finds the best hand(s) from a slice of hand strings
func BestHand(handStrs []string) ([]string, error) {
	if len(handStrs) == 0 {
		return []string{}, nil // No hands, no best hand
	}

	evaluatedHands := make([]Hand, 0, len(handStrs))
	for _, hs := range handStrs {
		cards, err := parseHand(hs)
		if err != nil {
			return nil, err // Propagate parsing errors
		}
		category, tieRanks := evaluateHand(cards)
		evaluatedHands = append(evaluatedHands, Hand{
			Original: hs,
			Cards:    cards, // Keep parsed cards
			Category: category,
			TieRanks: tieRanks,
		})
	}

	// Sort hands descending by strength
	sort.Slice(evaluatedHands, func(i, j int) bool {
		return compareHands(evaluatedHands[i], evaluatedHands[j]) > 0
	})

	// Find all hands equal to the best hand (the first one after sorting)
	bestHandsResult := []string{evaluatedHands[0].Original}
	for i := 1; i < len(evaluatedHands); i++ {
		if compareHands(evaluatedHands[0], evaluatedHands[i]) == 0 {
			bestHandsResult = append(bestHandsResult, evaluatedHands[i].Original)
		} else {
			break // Since they are sorted, no more ties possible
		}
	}

	// Ensure the order of tied hands in the output matches the order in the input
	// Create a map for quick lookup of original indices
	originalIndex := make(map[string]int)
	for i, hs := range handStrs {
		originalIndex[hs] = i
	}

	// Sort the bestHandsResult slice based on their original input order
	sort.Slice(bestHandsResult, func(i, j int) bool {
		return originalIndex[bestHandsResult[i]] < originalIndex[bestHandsResult[j]]
	})


	return bestHandsResult, nil
}
