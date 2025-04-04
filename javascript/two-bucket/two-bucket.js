export class TwoBucket {
  constructor(bucketOneCap, bucketTwoCap, goal, startBucket) {
    if (goal > Math.max(bucketOneCap, bucketTwoCap)) {
      throw new Error('Goal cannot be bigger than both buckets');
    }
    // Check if goal is achievable (Euclidean algorithm for GCD)
    const gcd = (a, b) => b === 0 ? a : gcd(b, a % b);
    if (goal % gcd(bucketOneCap, bucketTwoCap) !== 0 && goal !== bucketOneCap && goal !== bucketTwoCap && goal !== 0) {
       throw new Error('Goal is not achievable with these bucket sizes');
    }
     if (startBucket !== 'one' && startBucket !== 'two') {
       throw new Error('Start bucket must be "one" or "two"');
     }


    this.capacities = [bucketOneCap, bucketTwoCap];
    this.goal = goal;
    this.startBucketIndex = startBucket === 'one' ? 0 : 1; // 0 for one, 1 for two

    // Validate the rule: if goal is the capacity of the non-starting bucket,
    // and the starting bucket is empty, this is disallowed immediately.
    // This specific scenario seems unlikely to be the *first* move's result,
    // but the rule applies after *any* action.
    // Let's handle this check within the BFS loop.
  }

  solve() {
    const queue = [];
    const visited = new Set();

    // Initial state: fill the start bucket
    const initialState = [0, 0];
    initialState[this.startBucketIndex] = this.capacities[this.startBucketIndex];
    const startStateKey = initialState.join(',');

    // The first move is filling the start bucket
    queue.push({ state: initialState, moves: 1 });
    visited.add(startStateKey);

    // Check if goal is met immediately by filling the start bucket
    if (initialState[this.startBucketIndex] === this.goal) {
        const otherBucketIndex = 1 - this.startBucketIndex;
        return {
            moves: 1,
            goalBucket: this.startBucketIndex === 0 ? 'one' : 'two',
            otherBucket: initialState[otherBucketIndex],
        };
    }
     // Check if the goal is 0 - trivial case? The tests likely cover this.
     // If goal is 0, it's arguably 0 moves, but the problem implies actions start.
     // Let BFS handle finding 0 if possible through emptying.


    while (queue.length > 0) {
      const { state, moves } = queue.shift();
      const [b1, b2] = state;

      // --- Possible Moves ---

      // 1. Empty bucket one
      this.tryAddState([0, b2], moves + 1, visited, queue);
      // 2. Empty bucket two
      this.tryAddState([b1, 0], moves + 1, visited, queue);
      // 3. Fill bucket one
      this.tryAddState([this.capacities[0], b2], moves + 1, visited, queue);
      // 4. Fill bucket two
      this.tryAddState([b1, this.capacities[1]], moves + 1, visited, queue);
      // 5. Pour bucket one into bucket two
      const pour1to2Amount = Math.min(b1, this.capacities[1] - b2);
      this.tryAddState([b1 - pour1to2Amount, b2 + pour1to2Amount], moves + 1, visited, queue);
      // 6. Pour bucket two into bucket one
      const pour2to1Amount = Math.min(b2, this.capacities[0] - b1);
      this.tryAddState([b1 + pour2to1Amount, b2 - pour2to1Amount], moves + 1, visited, queue);

      // Check if any newly added state is the solution
      const lastAdded = queue[queue.length - 1]; // Check the most recent additions first
      if (lastAdded) {
          const [nb1, nb2] = lastAdded.state;
          if (nb1 === this.goal) {
              return { moves: lastAdded.moves, goalBucket: 'one', otherBucket: nb2 };
          }
          if (nb2 === this.goal) {
              return { moves: lastAdded.moves, goalBucket: 'two', otherBucket: nb1 };
          }
      }
       // Check older states in queue too (BFS guarantees shortest path)
       for (const item of queue) {
           const [nb1, nb2] = item.state;
           if (nb1 === this.goal) {
               return { moves: item.moves, goalBucket: 'one', otherBucket: nb2 };
           }
           if (nb2 === this.goal) {
               return { moves: item.moves, goalBucket: 'two', otherBucket: nb1 };
           }
       }
    }

    // Should not happen if goal is possible and validation is correct
    throw new Error("Solution not found - goal might be impossible or logic error.");
  }

  // Helper to add state if valid and not visited
  tryAddState(newState, moves, visited, queue) {
    const [b1, b2] = newState;
    const stateKey = newState.join(',');

    // Rule check: Cannot end in state where startBucket is empty and other is full
    const startBucketIsEmpty = newState[this.startBucketIndex] === 0;
    const otherBucketIsFull = newState[1 - this.startBucketIndex] === this.capacities[1 - this.startBucketIndex];

    if (startBucketIsEmpty && otherBucketIsFull) {
        // This state is disallowed by the rules
        return;
    }


    if (!visited.has(stateKey)) {
      visited.add(stateKey);
      queue.push({ state: newState, moves });

      // Check immediately if this new state is the goal
      if (b1 === this.goal) {
        // We found the goal, but BFS needs to complete the current level.
        // The check after the loop handles returning the correct result.
        // No, we can return immediately because BFS guarantees shortest path.
        // Let's move the return logic here.
        // return { moves: moves, goalBucket: 'one', otherBucket: b2 }; // This would break the outer loop structure
      }
      if (b2 === this.goal) {
        // return { moves: moves, goalBucket: 'two', otherBucket: b1 }; // Same issue
      }
    }
  }
}
