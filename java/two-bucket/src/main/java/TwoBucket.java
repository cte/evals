import java.util.Queue;
import java.util.LinkedList;
import java.util.Set;
import java.util.HashSet;
import java.util.Objects;

class TwoBucket {

    private final int bucketOneCapacity;
    private final int bucketTwoCapacity;
    private final int desiredLiters;
    private final String startBucket;

    private int totalMoves;
    private String finalBucket;
    private int otherBucketLevel;
    private boolean possible = false;

    // Helper class to represent the state of the buckets
    private static class State {
        final int bucketOne;
        final int bucketTwo;
        final int moves;
        final State previous; // To reconstruct path if needed, though not required by spec

        State(int b1, int b2, int m, State p) {
            this.bucketOne = b1;
            this.bucketTwo = b2;
            this.moves = m;
            this.previous = p;
        }

        // Simplified state for visited set (ignore moves and previous state)
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            State state = (State) o;
            return bucketOne == state.bucketOne && bucketTwo == state.bucketTwo;
        }

        @Override
        public int hashCode() {
            return Objects.hash(bucketOne, bucketTwo);
        }

        @Override
        public String toString() {
            return "(" + bucketOne + ", " + bucketTwo + ") moves=" + moves;
        }
    }

    TwoBucket(int bucketOneCap, int bucketTwoCap, int desiredLiters, String startBucket) {
        this.bucketOneCapacity = bucketOneCap;
        this.bucketTwoCapacity = bucketTwoCap;
        this.desiredLiters = desiredLiters;
        this.startBucket = startBucket;

        // Basic validation
        if (desiredLiters > Math.max(bucketOneCap, bucketTwoCap) && desiredLiters != bucketOneCap && desiredLiters != bucketTwoCap) {
             // Cannot reach the goal if it's larger than the largest bucket, unless the goal IS the capacity of a bucket
             // Let BFS determine impossibility naturally.
        }
        
        // Additional check: If desiredLiters is not reachable based on GCD
        if (desiredLiters % gcd(bucketOneCap, bucketTwoCap) != 0 && desiredLiters <= Math.max(bucketOneCap, bucketTwoCap)) {
            // If the desired amount is not a multiple of the GCD of capacities, it's impossible (unless it's 0 or a bucket capacity)
            // Let BFS handle this as well, it will exhaust possibilities.
        }


        solve();

        // Throw exception in constructor if solution not found after BFS
        if (!possible) {
            throw new IllegalArgumentException("Goal cannot be reached.");
        }
    }
    
    // Helper method for GCD
    private int gcd(int a, int b) {
        while (b != 0) {
            int temp = b;
            b = a % b;
            a = temp;
        }
        return Math.abs(a);
    }


    private void solve() {
        Queue<State> queue = new LinkedList<>();
        Set<State> visited = new HashSet<>();

        // Initial state (first move is filling the start bucket)
        State initialState;
        if ("one".equals(startBucket)) {
            // Check if desired liters is immediately met by filling bucket one
            if (bucketOneCapacity == desiredLiters) {
                 this.totalMoves = 1;
                 this.finalBucket = "one";
                 this.otherBucketLevel = 0;
                 this.possible = true;
                 return;
            }
            // Check if desired liters is immediately met by filling bucket two (if start is one, this is impossible in 1 move)
            // But if desired is 0, it's impossible anyway unless capacities are 0.
            
            initialState = new State(bucketOneCapacity, 0, 1, null);
        } else { // "two"
             // Check if desired liters is immediately met by filling bucket two
            if (bucketTwoCapacity == desiredLiters) {
                 this.totalMoves = 1;
                 this.finalBucket = "two";
                 this.otherBucketLevel = 0;
                 this.possible = true;
                 return;
            }
             // Check if desired liters is immediately met by filling bucket one (if start is two, this is impossible in 1 move)

            initialState = new State(0, bucketTwoCapacity, 1, null);
        }

        // Handle edge case where desiredLiters is 0 - this is impossible according to rules?
        // The tests likely cover this. Let BFS run. If 0 is desired, it won't be found unless initial state is (0,0) which isn't possible.

        queue.add(initialState);
        visited.add(initialState); // Add simplified state to visited

        while (!queue.isEmpty()) {
            State currentState = queue.poll();

            // Check if goal reached (Check *before* generating next states for efficiency)
             if (currentState.bucketOne == desiredLiters) {
                this.totalMoves = currentState.moves;
                this.finalBucket = "one";
                this.otherBucketLevel = currentState.bucketTwo;
                this.possible = true;
                return;
            }
            if (currentState.bucketTwo == desiredLiters) {
                this.totalMoves = currentState.moves;
                this.finalBucket = "two";
                this.otherBucketLevel = currentState.bucketOne;
                this.possible = true;
                return;
            }


            int b1 = currentState.bucketOne;
            int b2 = currentState.bucketTwo;
            int nextMoves = currentState.moves + 1;

            // Generate next possible states (actions)
            State[] nextStates = {
                // Pour bucket one to bucket two
                new State(Math.max(0, b1 - (bucketTwoCapacity - b2)), Math.min(bucketTwoCapacity, b2 + b1), nextMoves, currentState),
                // Pour bucket two to bucket one
                new State(Math.min(bucketOneCapacity, b1 + b2), Math.max(0, b2 - (bucketOneCapacity - b1)), nextMoves, currentState),
                // Empty bucket one
                new State(0, b2, nextMoves, currentState),
                // Empty bucket two
                new State(b1, 0, nextMoves, currentState),
                 // Fill bucket one
                new State(bucketOneCapacity, b2, nextMoves, currentState),
                // Fill bucket two
                new State(b1, bucketTwoCapacity, nextMoves, currentState)
            };

            for (State nextState : nextStates) {
                 // Check rule: Cannot end with start bucket empty and other full
                 boolean ruleViolated = false;
                 if ("one".equals(startBucket) && nextState.bucketOne == 0 && nextState.bucketTwo == bucketTwoCapacity) {
                     ruleViolated = true;
                 } else if ("two".equals(startBucket) && nextState.bucketOne == bucketOneCapacity && nextState.bucketTwo == 0) {
                     ruleViolated = true;
                 }

                // If state is valid and not visited, add to queue and visited set
                if (!ruleViolated && visited.add(nextState)) { // visited.add returns true if added (i.e., not present before)
                    
                    // Check if goal reached *after* adding to visited, to record the correct state
                    if (nextState.bucketOne == desiredLiters) {
                        this.totalMoves = nextState.moves;
                        this.finalBucket = "one";
                        this.otherBucketLevel = nextState.bucketTwo;
                        this.possible = true;
                        return; // Found shortest path
                    }
                    if (nextState.bucketTwo == desiredLiters) {
                        this.totalMoves = nextState.moves;
                        this.finalBucket = "two";
                        this.otherBucketLevel = nextState.bucketOne;
                        this.possible = true;
                        return; // Found shortest path
                    }
                    
                    queue.add(nextState);
                }
            }
        }
        // If queue is empty and goal not reached, possible remains false.
        // The exception is now thrown in the constructor after solve() returns.
    }


    int getTotalMoves() {
        // Exception is now thrown in constructor if not possible
        return totalMoves;
    }

    String getFinalBucket() {
        // Exception is now thrown in constructor if not possible
        return finalBucket;
    }

    int getOtherBucket() {
       // Exception is now thrown in constructor if not possible
        return otherBucketLevel;
    }
}
