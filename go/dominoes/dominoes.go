package dominoes

// Domino represents a domino stone with two numbers.
type Domino [2]int

// findChain is a recursive helper function to build the domino chain using backtracking.
// It tries to extend the chain of length k, ending with lastNum, using unused dominoes.
// input: the original list of dominoes
// n: the total number of dominoes required in the chain (len(input))
// used: a boolean slice tracking which dominoes from input have been used
// chain: the current chain being built
// k: the current length of the chain built so far
// lastNum: the number on the open end of the current chain
func findChain(input []Domino, n int, used []bool, chain []Domino, k int, lastNum int) bool {
	// Base case: If the chain has reached the desired length
	if k == n {
		// Check if the first number of the first domino matches the last number
		return chain[0][0] == lastNum
	}

	// Iterate through all available dominoes
	for i := 0; i < n; i++ {
		if !used[i] {
			d := input[i]
			// Try matching d[0] with lastNum
			if d[0] == lastNum {
				used[i] = true
				chain[k] = Domino{d[0], d[1]} // Place domino as [lastNum | d[1]]
				// Recursively try to extend the chain with the next number d[1]
				if findChain(input, n, used, chain, k+1, d[1]) {
					return true // Found a valid chain
				}
				used[i] = false // Backtrack: unmark domino as used
			} else if d[1] == lastNum { // Try matching d[1] with lastNum (rotated domino)
				used[i] = true
				chain[k] = Domino{d[1], d[0]} // Place domino as [lastNum | d[0]]
				// Recursively try to extend the chain with the next number d[0]
				if findChain(input, n, used, chain, k+1, d[0]) {
					return true // Found a valid chain
				}
				used[i] = false // Backtrack: unmark domino as used
			}
		}
	}

	// No valid extension found from this state
	return false
}

// MakeChain attempts to arrange the input dominoes into a valid chain.
// A valid chain means adjacent dominoes match, and the ends of the chain also match.
func MakeChain(input []Domino) ([]Domino, bool) {
	n := len(input)

	// Handle empty input: An empty chain is considered valid.
	if n == 0 {
		return []Domino{}, true
	}

	// Check degrees: For a valid closed chain (Eulerian circuit),
	// every number must appear an even number of times across all domino halves.
	degrees := make(map[int]int)
	for _, d := range input {
		degrees[d[0]]++
		degrees[d[1]]++
	}
	for _, deg := range degrees {
		if deg%2 != 0 {
			return nil, false // Odd degree means no Eulerian circuit possible
		}
	}

    // Handle single domino case after degree check
	if n == 1 {
        // If n==1 and we passed the degree check, it must be valid ([a|a]).
        return input, true
	}


	// Prepare for backtracking search
	used := make([]bool, n)
	chain := make([]Domino, n)

	// Try starting the chain with each domino in both orientations
	for i := 0; i < n; i++ {
		d := input[i]

		// Try starting with orientation [d[0] | d[1]]
		used[i] = true
		chain[0] = Domino{d[0], d[1]}
		if findChain(input, n, used, chain, 1, d[1]) {
			return chain, true // Found a valid chain
		}
		used[i] = false // Backtrack starting choice

		// Try starting with orientation [d[1] | d[0]] if different
		if d[0] != d[1] {
			used[i] = true
			chain[0] = Domino{d[1], d[0]}
			if findChain(input, n, used, chain, 1, d[0]) {
				return chain, true // Found a valid chain
			}
			used[i] = false // Backtrack starting choice
		}
	}

	// If no valid chain is found after trying all starting points
	return nil, false
}
