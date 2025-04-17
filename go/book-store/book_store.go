package bookstore

func Cost(books []int) int {
	const bookPrice = 800
	discounts := map[int]float64{
		1: 1.00,
		2: 0.95,
		3: 0.90,
		4: 0.80,
		5: 0.75,
	}

	// Count occurrences of each book
	counts := make(map[int]int)
	for _, b := range books {
		counts[b]++
	}

	// Convert counts map to a slice of counts for memoization key
	var countSlice []int
	for i := 1; i <= 5; i++ {
		countSlice = append(countSlice, counts[i])
	}

	memo := make(map[[5]int]int)

	var minCost func([5]int) int
	minCost = func(state [5]int) int {
		// Check memo
		if val, ok := memo[state]; ok {
			return val
		}

		// Check if all zero
		allZero := true
		for _, c := range state {
			if c != 0 {
				allZero = false
				break
			}
		}
		if allZero {
			return 0
		}

		// Count how many groups of each size we can make greedily
		best := 1 << 30 // large number

		// Try all possible groupings recursively
		for size := 5; size >= 1; size-- {
			indices := []int{}
			for idx, c := range state {
				if c > 0 {
					indices = append(indices, idx)
				}
				if len(indices) == size {
					break
				}
			}
			if len(indices) < size {
				continue
			}

			newState := state
			for _, idx := range indices {
				newState[idx]--
			}

			cost := int(float64(size*bookPrice)*discounts[size]) + minCost(newState)
			if cost < best {
				best = cost
			}
		}

		memo[state] = best
		return best
	}

	// Helper to count groups in greedy approach
	groupCounter := func(state [5]int) map[int]int {
		counts := make(map[int]int)
		s := state
		for {
			unique := 0
			for _, c := range s {
				if c > 0 {
					unique++
				}
			}
			if unique == 0 {
				break
			}

			groupSize := unique
			if groupSize > 5 {
				groupSize = 5
			}

			// Form the largest group possible
			indices := []int{}
			for idx, c := range s {
				if c > 0 {
					indices = append(indices, idx)
				}
				if len(indices) == groupSize {
					break
				}
			}

			for _, idx := range indices {
				s[idx]--
			}
			counts[groupSize]++
		}
		return counts
	}

	var initialState [5]int
	copy(initialState[:], countSlice)

	groupCounts := groupCounter(initialState)

	// Adjust 5+3 -> 4+4 if it reduces cost
	minGroupCount := groupCounts[5]
	if groupCounts[3] < minGroupCount {
		minGroupCount = groupCounts[3]
	}

	adjustedCost := 0
	for size, count := range groupCounts {
		adjustedCost += int(float64(size*bookPrice)*discounts[size]) * count
	}

	// For each pair of 5 and 3, replace with two 4s
	adjustedCost -= minGroupCount * (int(float64(5*bookPrice)*discounts[5]) + int(float64(3*bookPrice)*discounts[3]))
	adjustedCost += minGroupCount * 2 * int(float64(4*bookPrice)*discounts[4])

	// Return the minimum of adjusted greedy cost and full recursive optimal
	return min(adjustedCost, minCost(initialState))
}

func min(a, b int) int {
	if a < b {
		return a
	}
	return b
}
