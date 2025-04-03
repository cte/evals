def total(basket):
    from collections import Counter

    # Discount rates by group size
    discounts = {
        1: 1.0,
        2: 0.95,
        3: 0.9,
        4: 0.8,
        5: 0.75,
    }

    def price(groups):
        return sum(800 * len(group) * discounts[len(group)] for group in groups)

    def group_books(counter):
        # Greedy approach with optimization for known edge case
        groups = []
        counter = counter.copy()

        while any(counter.values()):
            group = set()
            for book in list(counter):
                if counter[book] > 0:
                    group.add(book)
                    counter[book] -= 1
            groups.append(group)

        # Optimization: replace (5,3) groups with (4,4) groups if cheaper
        # Count group sizes
        from collections import Counter as Cntr
        sizes = Cntr(len(g) for g in groups)
        while sizes[5] > 0 and sizes[3] > 0:
            # Current price for one 5-group and one 3-group
            current = 800 * 5 * 0.75 + 800 * 3 * 0.9
            # New price for two 4-groups
            new = 2 * 800 * 4 * 0.8
            if new < current:
                sizes[5] -= 1
                sizes[3] -= 1
                sizes[4] += 2
            else:
                break

        # Rebuild groups from sizes
        total_price = (
            sizes[5] * 800 * 5 * 0.75 +
            sizes[4] * 800 * 4 * 0.8 +
            sizes[3] * 800 * 3 * 0.9 +
            sizes[2] * 800 * 2 * 0.95 +
            sizes[1] * 800 * 1
        )
        return total_price

    counter = Counter(basket)
    return int(group_books(counter))
