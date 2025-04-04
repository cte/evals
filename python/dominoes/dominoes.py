import copy

def _find_chain(current_chain, remaining_dominoes):
    """
    Recursive helper function to find a valid domino chain.
    """
    if not remaining_dominoes:
        # Check if the chain loops back (first and last ends match)
        if current_chain[0][0] == current_chain[-1][1]:
            return current_chain
        else:
            return None

    last_num = current_chain[-1][1]

    for i, domino in enumerate(remaining_dominoes):
        next_remaining = remaining_dominoes[:i] + remaining_dominoes[i+1:]

        # Try matching the domino as is
        if domino[0] == last_num:
            result = _find_chain(current_chain + [list(domino)], next_remaining)
            if result:
                return result

        # Try matching the domino reversed (if not a double)
        if domino[1] == last_num and domino[0] != domino[1]:
            reversed_domino = [domino[1], domino[0]]
            result = _find_chain(current_chain + [reversed_domino], next_remaining)
            if result:
                return result

    # No valid chain found from this path
    return None

def can_chain(dominoes):
    """
    Checks if a list of dominoes can form a chain.

    Args:
        dominoes: A list of tuples representing dominoes, e.g., [(1, 2), (2, 3)].

    Returns:
        A list representing the valid chain if one exists, otherwise None.
        Returns [] for an empty input list.
    """
    if not dominoes:
        return []

    # Convert tuples to lists to allow modification (reversing) if needed by helper
    # Although the helper now creates new lists, keeping this structure is fine.
    dominoes_list = [list(d) for d in dominoes]

    if len(dominoes_list) == 1:
        if dominoes_list[0][0] == dominoes_list[0][1]:
            return [tuple(dominoes_list[0])] # Return as tuple list
        else:
            return None

    # Try starting the chain with each domino
    for i, start_domino in enumerate(dominoes_list):
        remaining = dominoes_list[:i] + dominoes_list[i+1:]

        # Try starting with the domino as is
        result = _find_chain([start_domino], copy.deepcopy(remaining)) # Use deepcopy
        if result:
            # Convert back to list of tuples for the final result
            return [tuple(d) for d in result]

        # Try starting with the domino reversed (if not a double)
        if start_domino[0] != start_domino[1]:
            reversed_start = [start_domino[1], start_domino[0]]
            result = _find_chain([reversed_start], copy.deepcopy(remaining)) # Use deepcopy
            if result:
                 # Convert back to list of tuples for the final result
                return [tuple(d) for d in result]

    # No valid chain found
    return None
