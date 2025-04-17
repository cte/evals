def can_chain(dominoes):
    if not dominoes:
        return []

    from collections import defaultdict, deque, Counter

    # Build adjacency list and degree count
    adj = defaultdict(list)
    degree = Counter()

    for idx, (a, b) in enumerate(dominoes):
        adj[a].append((b, idx))
        adj[b].append((a, idx))
        degree[a] += 1
        degree[b] += 1

    # Check all degrees are even
    for deg in degree.values():
        if deg % 2 != 0:
            return None

    # Check graph connectivity (ignoring isolated vertices)
    visited = set()
    nodes_with_edges = {k for k, v in adj.items() if v}
    if not nodes_with_edges:
        return []

    def dfs(node):
        stack = [node]
        while stack:
            curr = stack.pop()
            if curr not in visited:
                visited.add(curr)
                for neighbor, _ in adj[curr]:
                    if neighbor not in visited:
                        stack.append(neighbor)

    start_node = next(iter(nodes_with_edges))
    dfs(start_node)
    if visited != nodes_with_edges:
        return None

    # Backtracking to find Eulerian circuit
    used = set()
    path = []

    def backtrack(node, path_so_far):
        if len(path_so_far) == len(dominoes):
            # Check if loop closes
            if path_so_far[0][0] == path_so_far[-1][1]:
                return path_so_far
            else:
                return None

        for neighbor, idx in adj[node]:
            if idx in used:
                continue
            used.add(idx)
            a, b = dominoes[idx]
            # Try original orientation
            if a == node:
                res = backtrack(b, path_so_far + [(a, b)])
                if res:
                    return res
            # Try reversed orientation
            if b == node:
                res = backtrack(a, path_so_far + [(b, a)])
                if res:
                    return res
            used.remove(idx)
        return None

    return backtrack(start_node, [])
