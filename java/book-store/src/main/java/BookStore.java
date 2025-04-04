import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.HashMap;

class BookStore {

    private static final double BOOK_PRICE = 8.0;
    // Discounts map: group size -> discount multiplier
    private static final Map<Integer, Double> DISCOUNTS = Map.of(
            1, 1.0,   // 0%
            2, 0.95,  // 5%
            3, 0.90,  // 10%
            4, 0.80,  // 20%
            5, 0.75   // 25%
    );

    /**
     * Calculates the minimum cost for a basket of books, applying discounts
     * for sets of different books. It optimizes for the case where two groups
     * of 4 are cheaper than a group of 5 and a group of 3.
     *
     * @param books A list of integers representing the book IDs (assumed to be 1-5).
     * @return The minimum possible cost for the basket.
     */
    double calculateBasketCost(List<Integer> books) {
        if (books == null || books.isEmpty()) {
            return 0.0;
        }

        // 1. Count occurrences of each book type
        Map<Integer, Integer> counts = new HashMap<>();
        for (int book : books) {
            // We only care about the distinct books mentioned in the problem (1-5)
            // Although the test cases might use other numbers, the discount logic
            // only applies based on the *number* of distinct books per set.
            counts.put(book, counts.getOrDefault(book, 0) + 1);
        }

        // 2. Greedily form discount groups of distinct books
        List<Integer> groupSizes = new ArrayList<>();
        while (!counts.isEmpty()) {
            int currentSetSize = counts.size(); // Number of distinct books remaining
            groupSizes.add(currentSetSize);

            // Decrement the count for each distinct book type included in this set
            List<Integer> booksToRemoveFromMap = new ArrayList<>();
            for (Map.Entry<Integer, Integer> entry : counts.entrySet()) {
                int book = entry.getKey();
                int currentCount = entry.getValue();
                if (currentCount == 1) {
                    // If this was the last copy, mark it for removal from the map
                    booksToRemoveFromMap.add(book);
                } else {
                    // Otherwise, just decrement the count
                    counts.put(book, currentCount - 1);
                }
            }
            // Remove books that have no copies left
            for (int book : booksToRemoveFromMap) {
                counts.remove(book);
            }
        }

        // 3. Apply the optimization: Replace (5, 3) pairs with (4, 4) pairs
        //    as long as possible, because 2*Cost(4) < Cost(5) + Cost(3)
        while (groupSizes.contains(5) && groupSizes.contains(3)) {
            groupSizes.remove(Integer.valueOf(5)); // Remove one instance of 5
            groupSizes.remove(Integer.valueOf(3)); // Remove one instance of 3
            groupSizes.add(4);                     // Add a 4
            groupSizes.add(4);                     // Add another 4
        }

        // 4. Calculate the total cost based on the final group sizes
        double totalCost = 0.0;
        for (int size : groupSizes) {
             if (size > 0 && DISCOUNTS.containsKey(size)) {
                 // Apply discount based on the size of the group
                 totalCost += size * BOOK_PRICE * DISCOUNTS.get(size);
             } else if (size > 0) {
                 // Should theoretically only be size 1 if not in DISCOUNTS map,
                 // or potentially > 5 if input books were outside 1-5 range,
                 // but discount only applies up to 5 distinct books.
                 // Treat as individual books if size isn't in discount map.
                 totalCost += size * BOOK_PRICE;
             }
             // size == 0 is ignored
        }

        // Let's return the raw double, tests might handle precision.
        return totalCost;
    }
}