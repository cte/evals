package twobucket

import "fmt"

// state represents the amount of water in each bucket and the steps taken.
type state struct {
	a, b  int // Amount in bucket one (a) and bucket two (b)
	steps int
}

// pair is used as a key in the visited map.
type pair struct {
	a, b int
}

// gcd computes the greatest common divisor of two integers.
func gcd(a, b int) int {
	for b != 0 {
		a, b = b, a%b
	}
	return a
}

// Solve finds the minimum number of steps to reach goalAmount in either bucket.
func Solve(sizeBucketOne, sizeBucketTwo, goalAmount int, startBucket string) (goalBucket string, numActions int, otherBucketLevel int, err error) {
	// Input validation
	if sizeBucketOne <= 0 || sizeBucketTwo <= 0 || goalAmount <= 0 {
		err = fmt.Errorf("bucket sizes and goal amount must be positive")
		return
	}
	if startBucket != "one" && startBucket != "two" {
		err = fmt.Errorf("startBucket must be 'one' or 'two'")
		return
	}
	if goalAmount > sizeBucketOne && goalAmount > sizeBucketTwo {
		err = fmt.Errorf("goal amount cannot be larger than both buckets")
		return
	}
	// Check if goal is reachable based on GCD
	if goalAmount%gcd(sizeBucketOne, sizeBucketTwo) != 0 {
		err = fmt.Errorf("goal amount cannot be reached with these bucket sizes")
		return
	}

	// BFS setup
	queue := []state{}
	visited := make(map[pair]bool)

	// Initial state
	var initialState state
	if startBucket == "one" {
		initialState = state{a: sizeBucketOne, b: 0, steps: 1}
		// Edge case: If goal is sizeBucketOne and we start with bucket one
		if goalAmount == sizeBucketOne {
			return "one", 1, 0, nil
		}
		// Edge case: If goal is sizeBucketTwo and we start with bucket one, but sizeBucketTwo is 0 (already handled by validation)
        // Or if goalAmount is 0 (already handled)
	} else { // startBucket == "two"
		initialState = state{a: 0, b: sizeBucketTwo, steps: 1}
		// Edge case: If goal is sizeBucketTwo and we start with bucket two
		if goalAmount == sizeBucketTwo {
			return "two", 1, 0, nil
		}
        // Edge case: If goal is sizeBucketOne and we start with bucket two, but sizeBucketOne is 0 (already handled)
	}

	queue = append(queue, initialState)
	visited[pair{initialState.a, initialState.b}] = true

	// Check the invalid initial state rule violation (unlikely but good practice)
	if startBucket == "one" && initialState.a == 0 && initialState.b == sizeBucketTwo {
		err = fmt.Errorf("invalid starting state based on rules") // Should not happen with positive sizes
		return
	}
	if startBucket == "two" && initialState.b == 0 && initialState.a == sizeBucketOne {
		err = fmt.Errorf("invalid starting state based on rules") // Should not happen with positive sizes
		return
	}


	// BFS loop
	for len(queue) > 0 {
		current := queue[0]
		queue = queue[1:]

		// Check if goal reached
		if current.a == goalAmount {
			return "one", current.steps, current.b, nil
		}
		if current.b == goalAmount {
			return "two", current.steps, current.a, nil
		}

		nextSteps := current.steps + 1

		// Generate next possible states
		nextStates := []state{}

		// 1. Fill bucket one
		nextStates = append(nextStates, state{a: sizeBucketOne, b: current.b, steps: nextSteps})
		// 2. Fill bucket two
		nextStates = append(nextStates, state{a: current.a, b: sizeBucketTwo, steps: nextSteps})
		// 3. Empty bucket one
		nextStates = append(nextStates, state{a: 0, b: current.b, steps: nextSteps})
		// 4. Empty bucket two
		nextStates = append(nextStates, state{a: current.a, b: 0, steps: nextSteps})

		// 5. Pour one to two
		pourAmount1to2 := current.a
		if current.a+current.b > sizeBucketTwo {
			pourAmount1to2 = sizeBucketTwo - current.b
		}
		nextStates = append(nextStates, state{a: current.a - pourAmount1to2, b: current.b + pourAmount1to2, steps: nextSteps})

		// 6. Pour two to one
		pourAmount2to1 := current.b
		if current.a+current.b > sizeBucketOne {
			pourAmount2to1 = sizeBucketOne - current.a
		}
		nextStates = append(nextStates, state{a: current.a + pourAmount2to1, b: current.b - pourAmount2to1, steps: nextSteps})

		// Process next states
		for _, next := range nextStates {
			nextPair := pair{next.a, next.b}

			// Check invalid state rule (Instruction line 14)
			invalidState := false
			if startBucket == "one" && next.a == 0 && next.b == sizeBucketTwo {
				invalidState = true
			}
			if startBucket == "two" && next.b == 0 && next.a == sizeBucketOne {
				invalidState = true
			}

			if !visited[nextPair] && !invalidState {
				visited[nextPair] = true
				queue = append(queue, next)
			}
		}
	}

	// Goal not reached
	err = fmt.Errorf("goal amount could not be reached")
	return
}
