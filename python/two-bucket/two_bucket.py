import math

def measure(bucket_one_cap, bucket_two_cap, goal, start_bucket):
    """
    Solves the Two Bucket problem using a direct simulation approach
    following specific rules based on the starting bucket.

    Args:
        bucket_one_cap: Capacity of the first bucket.
        bucket_two_cap: Capacity of the second bucket.
        goal: The target amount of water.
        start_bucket: Which bucket to prioritize actions for ("one" or "two").

    Returns:
        A tuple: (number_of_moves, goal_bucket_name, other_bucket_level).

    Raises:
        ValueError: If the goal is impossible to reach.
    """

    # --- Initial Validation ---
    if goal > max(bucket_one_cap, bucket_two_cap):
        raise ValueError("Goal is larger than the larger bucket.")
    if goal % math.gcd(bucket_one_cap, bucket_two_cap) != 0:
        raise ValueError("Goal cannot be reached with these bucket sizes.")
    # Edge case: If goal is 0, it takes 0 moves? The tests don't cover this,
    # but logically it's already met. However, the problem implies actions.
    # Let's assume goal > 0 based on tests.

    # Check if goal is one of the capacities, reachable in 1 move if starting there.
    # This needs careful handling as the simulation might take more steps.
    if start_bucket == 'one' and goal == bucket_one_cap:
        # If goal is cap1, starting one -> fill one (1 move). Bucket two might need filling first if goal is also cap2?
        # Test case: measure(1, 3, 3, "two") -> (1, "two", 0) - suggests 1 move if goal=cap and start=that bucket.
        # Test case: measure(2, 3, 3, "one") -> (2, "two", 2) - suggests goal=cap2 starting one takes 2 moves.
        # Let the simulation handle these.
        pass
    if start_bucket == 'two' and goal == bucket_two_cap:
         return (1, "two", 0) # Based on test_measure_one_step_using_bucket_one_of_size_1_and_bucket_two_of_size_3_start_with_bucket_two

    # --- Simulation Setup ---
    moves = 0
    b1 = 0
    b2 = 0
    visited = set()

    # --- Simulation Loop ---
    while True:
        # Check if goal reached
        if b1 == goal:
            return (moves, "one", b2)
        if b2 == goal:
            return (moves, "two", b1)

        # Check for impossible cycles (redundant with GCD check but safe)
        current_state = (b1, b2)
        if current_state in visited:
            raise ValueError("Goal cannot be reached (cycle detected).")
        visited.add(current_state)

        # Determine next action based on start_bucket priority
        if start_bucket == "one":
            if b1 == 0:             # If bucket one is empty, fill it
                b1 = bucket_one_cap
            elif b2 == bucket_two_cap: # Else if bucket two is full, empty it
                b2 = 0
            else:                   # Else pour bucket one into bucket two
                pour = min(b1, bucket_two_cap - b2)
                b1 -= pour
                b2 += pour
        else: # start_bucket == "two"
            if b2 == 0:             # If bucket two is empty, fill it
                b2 = bucket_two_cap
            elif b1 == bucket_one_cap: # Else if bucket one is full, empty it
                b1 = 0
            else:                   # Else pour bucket two into bucket one
                pour = min(b2, bucket_one_cap - b1)
                b2 -= pour
                b1 += pour

        moves += 1
