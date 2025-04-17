import java.util.List;
import java.util.SortedMap;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

class PalindromeCalculator {

    SortedMap<Long, List<List<Integer>>> getPalindromeProductsWithFactors(int minFactor, int maxFactor) {
        if (minFactor > maxFactor) {
            throw new IllegalArgumentException("invalid input: min must be <= max");
        }

        SortedMap<Long, List<List<Integer>>> palindromeMap = new TreeMap<>();

        for (int i = minFactor; i <= maxFactor; i++) {
            for (int j = i; j <= maxFactor; j++) {
                long product = (long) i * (long) j;
                if (isPalindrome(product)) {
                    palindromeMap.computeIfAbsent(product, k -> new ArrayList<>())
                                 .add(List.of(i, j));
                }
            }
        }

        return palindromeMap;
    }

    private boolean isPalindrome(long number) {
        String str = Long.toString(number);
        int left = 0;
        int right = str.length() - 1;
        while (left < right) {
            if (str.charAt(left) != str.charAt(right)) {
                return false;
            }
            left++;
            right--;
        }
        return true;
    }
}