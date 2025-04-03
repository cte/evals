#[derive(PartialEq, Eq, Debug)]
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
    use std::collections::{HashSet, VecDeque};

    // Impossible if goal > both buckets
    if goal > capacity_1.max(capacity_2) {
        return None;
    }

    // Impossible if goal is not multiple of gcd
    fn gcd(a: u8, b: u8) -> u8 {
        if b == 0 { a } else { gcd(b, a % b) }
    }
    if goal % gcd(capacity_1, capacity_2) != 0 {
        return None;
    }

    #[derive(Clone, Copy, Debug, PartialEq, Eq, Hash)]
    struct State {
        b1: u8,
        b2: u8,
    }

    let mut visited = HashSet::new();
    let mut queue = VecDeque::new();

    let initial = match start_bucket {
        Bucket::One => State { b1: capacity_1, b2: 0 },
        Bucket::Two => State { b1: 0, b2: capacity_2 },
    };
    queue.push_back((initial, 1)); // first fill counts as move 1
    visited.insert(initial);

    let mut best: Option<BucketStats> = None;

    while let Some((state, moves)) = queue.pop_front() {
        match start_bucket {
            Bucket::One => {
                // Accept goal in either bucket
                if state.b1 == goal {
                    let stats = BucketStats {
                        moves,
                        goal_bucket: Bucket::One,
                        other_bucket: state.b2,
                    };
                    match &best {
                        None => best = Some(stats),
                        Some(current_best) => {
                            if moves < current_best.moves {
                                best = Some(stats);
                            }
                        }
                    }
                }
                if state.b2 == goal {
                    let stats = BucketStats {
                        moves,
                        goal_bucket: Bucket::Two,
                        other_bucket: state.b1,
                    };
                    match &best {
                        None => best = Some(stats),
                        Some(current_best) => {
                            if moves < current_best.moves {
                                best = Some(stats);
                            }
                        }
                    }
                }
            }
            Bucket::Two => {
                // Accept goal only in bucket two
                if state.b2 == goal {
                    let stats = BucketStats {
                        moves,
                        goal_bucket: Bucket::Two,
                        other_bucket: state.b1,
                    };
                    match &best {
                        None => best = Some(stats),
                        Some(current_best) => {
                            if moves < current_best.moves {
                                best = Some(stats);
                            }
                        }
                    }
                }
            }
        }

        let mut next_states = Vec::new();

        // Fill bucket 1
        next_states.push(State { b1: capacity_1, b2: state.b2 });
        // Fill bucket 2
        next_states.push(State { b1: state.b1, b2: capacity_2 });
        // Empty bucket 1
        next_states.push(State { b1: 0, b2: state.b2 });
        // Empty bucket 2
        next_states.push(State { b1: state.b1, b2: 0 });
        // Pour 1 -> 2
        let pour = capacity_2.saturating_sub(state.b2).min(state.b1);
        next_states.push(State { b1: state.b1 - pour, b2: state.b2 + pour });
        // Pour 2 -> 1
        let pour = capacity_1.saturating_sub(state.b1).min(state.b2);
        next_states.push(State { b1: state.b1 + pour, b2: state.b2 - pour });

        for next in next_states {
            if visited.insert(next) {
                queue.push_back((next, moves + 1));
            }
        }
    }

    best
}
