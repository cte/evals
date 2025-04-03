package dominoes

type Domino [2]int

func MakeChain(input []Domino) ([]Domino, bool) {
	if len(input) == 0 {
		return []Domino{}, true
	}
	if len(input) == 1 {
		if input[0][0] == input[0][1] {
			return input, true
		}
		return nil, false
	}

	// Build adjacency map and degree count
	adj := make(map[int][]int)
	degree := make(map[int]int)
	for _, d := range input {
		a, b := d[0], d[1]
		adj[a] = append(adj[a], b)
		adj[b] = append(adj[b], a)
		degree[a]++
		degree[b]++
	}

	// Check all vertices have even degree
	for _, deg := range degree {
		if deg%2 != 0 {
			return nil, false
		}
	}

	// Check graph connectivity using DFS
	visited := make(map[int]bool)
	var dfs func(int)
	dfs = func(v int) {
		visited[v] = true
		for _, w := range adj[v] {
			if !visited[w] {
				dfs(w)
			}
		}
	}
	// Start DFS from any vertex with degree > 0
	var start int
	for v := range degree {
		start = v
		break
	}
	dfs(start)
	for v := range degree {
		if !visited[v] {
			return nil, false
		}
	}

	// Backtracking search for Eulerian circuit
	used := make([]bool, len(input))
	var chain []Domino

	var search func(path []Domino, end int) bool
	search = func(path []Domino, end int) bool {
		if len(path) == len(input) {
			// Check circularity
			if path[0][0] == path[len(path)-1][1] {
				chain = append([]Domino{}, path...)
				return true
			}
			return false
		}
		for i, d := range input {
			if used[i] {
				continue
			}
			// Try original orientation
			if d[0] == end {
				used[i] = true
				if search(append(path, d), d[1]) {
					return true
				}
				used[i] = false
			}
			// Try flipped orientation
			if d[1] == end && d[0] != d[1] {
				used[i] = true
				if search(append(path, Domino{d[1], d[0]}), d[0]) {
					return true
				}
				used[i] = false
			}
		}
		return false
	}

	// Try each domino as starting point
	for i, d := range input {
		used[i] = true
		// Original orientation
		if search([]Domino{d}, d[1]) {
			return chain, true
		}
		// Flipped orientation
		if d[0] != d[1] {
			if search([]Domino{{d[1], d[0]}}, d[0]) {
				return chain, true
			}
		}
		used[i] = false
	}

	return nil, false
}
