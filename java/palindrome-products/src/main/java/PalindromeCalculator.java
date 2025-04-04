import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

class PalindromeCalculator {

    SortedMap<Long, List<List<Integer>>> getPalindromeProductsWithFactors(int minFactor, int maxFactor) {
        if (minFactor > maxFactor) {
            throw new IllegalArgumentException("invalid input: min must be <= max");
        }

        SortedMap<Long, List<List<Integer>>> palindromes = new TreeMap<>();

        for (int i = minFactor; i <= maxFactor; i++) {
            // Start j from i to avoid duplicate pairs (like [3, 5] and [5, 3])
            // and redundant calculations.
            for (int j = i; j <= maxFactor; j++) {
                long product = (long) i * j;
                if (isPalindrome(product)) {
                    List<Integer> factors = Arrays.asList(i, j);
                    palindromes.computeIfAbsent(product, k -> new ArrayList<>()).add(factors);
                }
            }
        }

        return palindromes;
    }

    private boolean isPalindrome(long number) {
        String original = Long.toString(number);
        String reversed = new StringBuilder(original).reverse().toString();
        return original.equals(reversed);
    }

}