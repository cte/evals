use std::collections::{HashSet, VecDeque};
use std::cmp::min;

#[derive(PartialEq, Eq, Debug, Clone, Copy, Hash)] // Add Clone, Copy, Hash for HashSet
pub enum Bucket {
    One,
    Two,
}

/// A struct to hold your results in.
#[derive(PartialEq, Eq, Debug)]
pub struct BucketStats {
    /// The total number of "moves" it should take to reach the desired number of liters, including
    /// the first fill.
    pub moves: u8,
    /// Which bucket should end up with the desired number of liters? (Either "one" or "two")
    pub goal_bucket: Bucket,
    /// How many liters are left in the other bucket?
    pub other_bucket: u8,
}

/// Solve the bucket problem
pub fn solve(
    capacity_1: u8,
    capacity_2: u8,
    goal: u8,
    start_bucket: &Bucket,
) -> Option<BucketStats> {
    // Basic checks: goal cannot be larger than both capacities.
    // Also handle the case where the goal is larger than the capacity of the bucket it must end in.
    if goal > capacity_1 && goal > capacity_2 {
        return None;
    }
    if goal > capacity_1 && *start_bucket == Bucket::Two { // Goal must be in bucket 2, but it's too large
         // This check is slightly flawed. Goal could be reached in bucket 1 even if start is Two.
         // Let's refine: If goal > capacity_1, it MUST end in bucket 2. If goal > capacity_2, it MUST end in bucket 1.
         // If goal > capacity_1 && goal > capacity_2, already handled.
         // If goal > capacity_1 (so must end in bucket 2) AND goal > capacity_2 -> impossible (handled)
         // If goal > capacity_2 (so must end in bucket 1) AND goal > capacity_1 -> impossible (handled)
         // So, the initial check `goal > capacity_1 && goal > capacity_2` is sufficient.
    }

    // If goal is 0, it's ambiguous. Assuming goal > 0 based on problem type.
    // If goal is 0, the tests would fail, and we'd adjust. Let's assume goal > 0.
    if goal == 0 {
        // If goal is 0, maybe return stats for (0,0)? Requires clarification or test cases.
        // For now, let's assume goal > 0 means we need a positive amount.
        return None; // Or perhaps return stats for achieving 0 in the start bucket? e.g. moves=2?
    }


    // State: (amount_1, amount_2)
    // Queue stores: ((amount_1, amount_2), moves)
    let mut queue: VecDeque<((u8, u8), u8)> = VecDeque::new();
    // Visited set stores: (amount_1, amount_2)
    let mut visited: HashSet<(u8, u8)> = HashSet::new();

    // Determine initial state and moves after the first fill action.
    let initial_state: (u8, u8);
    let initial_moves: u8 = 1;

    match start_bucket {
        Bucket::One => {
            // Check if goal is met immediately by filling bucket 1
            if goal == capacity_1 {
                // If goal is also 0 in bucket 2, this is the state.
                return Some(BucketStats {
                    moves: initial_moves,
                    goal_bucket: Bucket::One,
                    other_bucket: 0, // Bucket 2 starts empty
                });
            }
            // Check if the goal requires bucket 2, but bucket 2 is the goal capacity and start is One.
            // This needs 2 moves minimum: fill 1, pour 1->2.
            if goal == capacity_2 && capacity_1 >= capacity_2 {
                 return Some(BucketStats {
                    moves: 2,
                    goal_bucket: Bucket::Two,
                    other_bucket: capacity_1 - capacity_2,
                 });
            }
            // If goal == capacity_2 and capacity_1 < capacity_2, BFS will find it.

            initial_state = (capacity_1, 0);
        }
        Bucket::Two => {
            // Check if goal is met immediately by filling bucket 2
            if goal == capacity_2 {
                 return Some(BucketStats {
                    moves: initial_moves,
                    goal_bucket: Bucket::Two,
                    other_bucket: 0, // Bucket 1 starts empty
                });
            }
             // Check if the goal requires bucket 1, but bucket 1 is the goal capacity and start is Two.
             // This needs 2 moves minimum: fill 2, pour 2->1.
            if goal == capacity_1 && capacity_2 >= capacity_1 {
                 return Some(BucketStats {
                    moves: 2,
                    goal_bucket: Bucket::One,
                    other_bucket: capacity_2 - capacity_1,
                 });
            }
            // If goal == capacity_1 and capacity_2 < capacity_1, BFS will find it.

            initial_state = (0, capacity_2);
        }
    }

    // Add initial state to queue and visited set
    // Avoid adding if capacities are 0, leading to state (0,0) which might be confusing.
    // Assuming capacities > 0.
    if capacity_1 > 0 || capacity_2 > 0 { // Avoid issues if both capacities are 0
        queue.push_back((initial_state, initial_moves));
        visited.insert(initial_state);
    } else if goal == 0 { // If both caps are 0, goal must be 0.
        // Ambiguous case, return None or specific stats if defined.
        return None;
    } else { // Both caps 0, goal > 0 -> impossible
        return None;
    }


    // Forbidden state based on start_bucket (cannot reach this state *after* an action)
    let forbidden_state = match start_bucket {
        Bucket::One => (0, capacity_2), // Started with One, cannot end with One empty and Two full
        Bucket::Two => (capacity_1, 0), // Started with Two, cannot end with Two empty and One full
    };


    while let Some(((c1, c2), moves)) = queue.pop_front() {
        // Generate next possible states
        let next_moves = moves + 1;

        // Define potential actions leading to next states
        let actions = [
            // Fill 1
            (capacity_1, c2),
            // Fill 2
            (c1, capacity_2),
            // Empty 1
            (0, c2),
            // Empty 2
            (c1, 0),
            // Pour 1 -> 2
            {
                let transfer = min(c1, capacity_2.saturating_sub(c2)); // Use saturating_sub for safety
                (c1.saturating_sub(transfer), c2.saturating_add(transfer))
            },
            // Pour 2 -> 1
            {
                let transfer = min(c2, capacity_1.saturating_sub(c1)); // Use saturating_sub for safety
                (c1.saturating_add(transfer), c2.saturating_sub(transfer))
            },
        ];

        for next_state in actions.iter() {
            let (n1, n2) = *next_state;

            // Check rule: Cannot reach the forbidden state
            // Only apply forbidden state check if capacities > 0, otherwise (0,0) might be forbidden incorrectly.
            if capacity_1 > 0 && capacity_2 > 0 && *next_state == forbidden_state {
                continue;
            }

            // Check if visited
            if visited.contains(next_state) {
                continue;
            }

            // Check if goal reached
            if n1 == goal {
                // Goal reached in bucket 1. Ensure it's possible (n1 <= capacity_1)
                // This check should be inherently true if logic is correct, but good for safety.
                if n1 <= capacity_1 {
                   return Some(BucketStats {
                       moves: next_moves,
                       goal_bucket: Bucket::One,
                       other_bucket: n2,
                   });
                }
            }
            if n2 == goal {
                // Goal reached in bucket 2. Ensure it's possible (n2 <= capacity_2)
                if n2 <= capacity_2 {
                   return Some(BucketStats {
                       moves: next_moves,
                       goal_bucket: Bucket::Two,
                       other_bucket: n1,
                   });
                }
            }

            // Mark as visited and enqueue
            visited.insert(*next_state);
            queue.push_back((*next_state, next_moves));
        }
    }

    // Goal not reachable
    None
}
