import collections
from itertools import combinations
from functools import lru_cache

PRICE = 800
DISCOUNTS = {
    1: 1.00,  # 0%
    2: 0.95,  # 5%
    3: 0.90,  # 10%
    4: 0.80,  # 20%
    5: 0.75,  # 25%
}
# Calculate cost per group size (integer cents)
GROUP_COSTS = {
    size: int(size * PRICE * discount) for size, discount in DISCOUNTS.items()
}
GROUP_COSTS[0] = 0 # Cost of an empty group is 0

# Use lru_cache for memoization. The state is defined by the counts of each book.
# We need a hashable representation of the counts, like a tuple.
@lru_cache(maxsize=None)
def calculate_min_cost(book_counts_tuple):
    """
    Recursively calculates the minimum cost for the given book counts.

    Args:
        book_counts_tuple: A tuple representing the counts of each book (e.g., (2, 1, 3, 0, 1)).
                           The order corresponds to book IDs 1 through 5.

    Returns:
        The minimum cost in cents.
    """
    if sum(book_counts_tuple) == 0:
        return 0

    min_total = float('inf')

    # Convert tuple back to a mutable dictionary or Counter for easier manipulation
    # Ensure keys 1-5 exist for consistent iteration later
    current_counts_dict = {i: book_counts_tuple[i-1] for i in range(1, 6)}

    # Find books currently available (count > 0)
    available_books = [book for book, count in current_counts_dict.items() if count > 0]
    num_distinct_available = len(available_books)

    # Iterate through possible group sizes to form (from 1 up to num_distinct_available)
    # Optimization: Start from largest possible group size? Might prune search space faster.
    # Let's stick to 1..N for now for correctness.
    for group_size in range(1, num_distinct_available + 1):
        # Iterate through all combinations of 'group_size' distinct books from the available ones
        for group_combination in combinations(available_books, group_size):
            # Create the next state of counts after forming this group
            next_counts_list = list(book_counts_tuple) # Mutable copy
            for book in group_combination:
                next_counts_list[book-1] -= 1 # Decrement count at the correct index

            next_counts_tuple = tuple(next_counts_list)

            # Recursive call
            cost = GROUP_COSTS[group_size] + calculate_min_cost(next_counts_tuple)
            min_total = min(min_total, cost)

    # If min_total remained infinity, something went wrong (shouldn't happen if input is valid)
    return min_total if min_total != float('inf') else 0


def total(basket):
    """
    Calculates the minimum cost for a basket of books applying group discounts.

    Args:
        basket: A list of integers representing book IDs (1 to 5).

    Returns:
        The minimum total cost in cents.
    """
    if not basket:
        return 0

    initial_counts = collections.Counter(basket)
    # Ensure counts for all 5 book types exist, even if 0, for consistent tuple representation
    initial_counts_tuple = tuple(initial_counts.get(i, 0) for i in range(1, 6))

    # Clear cache before each top-level call if running multiple tests in one process
    # This is important if the test runner reuses the same process.
    calculate_min_cost.cache_clear()

    return calculate_min_cost(initial_counts_tuple)
