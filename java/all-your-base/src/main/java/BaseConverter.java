import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

class BaseConverter {

    private final int base10Value;
    private final int originalBase; // Keep original base

    BaseConverter(int originalBase, int[] originalDigits) {
        // 1. Base validation
        if (originalBase < 2) {
            throw new IllegalArgumentException("Bases must be at least 2.");
        }
        // 2. Null check
        if (originalDigits == null) {
             throw new IllegalArgumentException("Digits array cannot be null.");
        }

        // 3. Calculate value using Horner's method (left-to-right) while validating digits
        int calculatedValue = 0;
        for (int digit : originalDigits) {
            if (digit < 0) {
                throw new IllegalArgumentException("Digits may not be negative.");
            }
            if (digit >= originalBase) {
                throw new IllegalArgumentException("All digits must be strictly less than the base.");
            }
            // Horner's method: value = value * base + digit
            calculatedValue = calculatedValue * originalBase + digit;
        }

        // The leading zero check was removed as tests indicate inputs like [0, 6, 0] are valid.
        // Horner's method correctly calculates the value.

        this.base10Value = calculatedValue;
        this.originalBase = originalBase;
    }

    int[] convertToBase(int newBase) {
        if (newBase < 2) {
            throw new IllegalArgumentException("Bases must be at least 2.");
        }

        // Handles empty input, [0], [0, 0], etc. because they all result in base10Value = 0
        if (base10Value == 0) {
            return new int[]{0};
        }

        List<Integer> newDigits = new ArrayList<>();
        int currentValue = base10Value;

        while (currentValue > 0) {
            int remainder = currentValue % newBase;
            newDigits.add(remainder);
            currentValue /= newBase;
        }

        Collections.reverse(newDigits);

        // Convert List<Integer> to int[]
        return newDigits.stream().mapToInt(Integer::intValue).toArray();
    }
}