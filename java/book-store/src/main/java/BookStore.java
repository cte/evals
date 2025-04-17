import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Collections;

class BookStore {

    double calculateBasketCost(List<Integer> books) {
        if (books == null || books.isEmpty()) {
            return 0.0;
        }

        // Count occurrences of each book
        Map<Integer, Integer> bookCounts = new HashMap<>();
        for (Integer book : books) {
            bookCounts.put(book, bookCounts.getOrDefault(book, 0) + 1);
        }

        List<Integer> groupSizes = new ArrayList<>();

        // Form groups greedily
        while (!bookCounts.isEmpty()) {
            int groupSize = bookCounts.size();
            groupSizes.add(groupSize);

            // Decrease count for each book in this group
            List<Integer> booksToRemove = new ArrayList<>();
            for (Integer book : bookCounts.keySet()) {
                bookCounts.put(book, bookCounts.get(book) - 1);
                if (bookCounts.get(book) == 0) {
                    booksToRemove.add(book);
                }
            }
            for (Integer book : booksToRemove) {
                bookCounts.remove(book);
            }
        }

        // Optimize groups: replace each (5,3) pair with (4,4) pair
        while (groupSizes.contains(5) && groupSizes.contains(3)) {
            groupSizes.remove(Integer.valueOf(5));
            groupSizes.remove(Integer.valueOf(3));
            groupSizes.add(4);
            groupSizes.add(4);
        }

        double total = 0.0;
        for (int size : groupSizes) {
            double discount = 0.0;
            switch (size) {
                case 2:
                    discount = 0.05;
                    break;
                case 3:
                    discount = 0.10;
                    break;
                case 4:
                    discount = 0.20;
                    break;
                case 5:
                    discount = 0.25;
                    break;
                default:
                    discount = 0.0;
            }
            total += size * 8 * (1 - discount);
        }

        return total;
    }

}