import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

class Alphametics {

    private final String[] addends;
    private final String sum;
    private final List<Character> uniqueLetters;
    private final Set<Character> leadingLetters;
    private final Map<Character, Integer> solution = new HashMap<>();
    private final boolean[] usedDigits = new boolean[10]; // To track used digits 0-9

    Alphametics(String puzzle) {
        // 1. Parse the input string (e.g., "I + BB == ILL")
        // Remove any potential whitespace around operators
        puzzle = puzzle.replaceAll("\\s*\\+\\s*", "+").replaceAll("\\s*==\\s*", "==");
        String[] parts = puzzle.split("==");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Puzzle must contain '==' separating addends and sum.");
        }
        this.sum = parts[1];
        // Split addends by '+'
        this.addends = parts[0].split("\\+");

        // 2. Identify all unique letters and leading letters
        Set<Character> letters = new HashSet<>();
        this.leadingLetters = new HashSet<>();

        for (String addend : addends) {
             if (addend.isEmpty()) {
                 throw new IllegalArgumentException("Empty addend found in puzzle.");
             }
            if (addend.length() > 1) {
                leadingLetters.add(addend.charAt(0));
            }
            for (char c : addend.toCharArray()) {
                 if (!Character.isUpperCase(c)) {
                     throw new IllegalArgumentException("Puzzle must only contain uppercase letters.");
                 }
                letters.add(c);
            }
        }
         if (sum.isEmpty()) {
             throw new IllegalArgumentException("Empty sum word found in puzzle.");
         }
        if (sum.length() > 1) {
            leadingLetters.add(sum.charAt(0));
        }
        for (char c : sum.toCharArray()) {
             if (!Character.isUpperCase(c)) {
                 throw new IllegalArgumentException("Puzzle must only contain uppercase letters.");
             }
            letters.add(c);
        }

        this.uniqueLetters = new ArrayList<>(letters);
        if (uniqueLetters.size() > 10) {
             throw new IllegalArgumentException("Puzzle has more than 10 unique letters.");
        }
    }

    Map<Character, Integer> solve() throws UnsolvablePuzzleException {
        if (findSolution(0)) {
            // Ensure the solution map only contains the letters present in the puzzle
            Map<Character, Integer> finalSolution = new HashMap<>();
            for(char letter : uniqueLetters) {
                finalSolution.put(letter, solution.get(letter));
            }
            return finalSolution;
        } else {
            throw new UnsolvablePuzzleException();
        }
    }

    private boolean findSolution(int letterIndex) {
        // Base case: All letters have been assigned a digit
        if (letterIndex == uniqueLetters.size()) {
            return checkEquation();
        }

        char currentLetter = uniqueLetters.get(letterIndex);

        // Try assigning digits 0-9 to the current letter
        for (int digit = 0; digit < 10; digit++) {
            // Constraint: Leading letters cannot be 0
            if (digit == 0 && leadingLetters.contains(currentLetter)) {
                continue;
            }

            // Constraint: Digit must not already be used
            if (!usedDigits[digit]) {
                usedDigits[digit] = true;
                solution.put(currentLetter, digit);

                // Recurse for the next letter
                if (findSolution(letterIndex + 1)) {
                    return true; // Solution found
                }

                // Backtrack: Unassign the digit and mark it as unused
                // No need to remove from solution map explicitly, it will be overwritten
                // or removed if we backtrack further up. Just mark digit as unused.
                usedDigits[digit] = false;
            }
        }
         // Backtrack: remove the assignment for the current letter if no solution found down this path
         solution.remove(currentLetter);
        // No valid digit found for this letter at this level
        return false;
    }

    private boolean checkEquation() {
        // Check leading zero constraint explicitly before calculating numbers
        for (char leading : leadingLetters) {
            if (solution.get(leading) == 0) {
                return false;
            }
        }

        long addendsValue = 0;
        for (String addend : addends) {
            addendsValue += wordToNumber(addend);
        }
        long sumValue = wordToNumber(sum);

        return addendsValue == sumValue;
    }

    private long wordToNumber(String word) {
        long number = 0;
        long power = 1;
        // Check leading zero just in case (should be caught by checkEquation)
        if (word.length() > 1 && solution.get(word.charAt(0)) == 0) {
             return -1; // Invalid number representation
        }
        for (int i = word.length() - 1; i >= 0; i--) {
            char c = word.charAt(i);
            // Ensure the letter exists in the solution map before accessing
            if (!solution.containsKey(c)) {
                 // This should not happen in the backtracking flow but good for safety
                 return -2; // Indicate an error state
            }
            int digit = solution.get(c);
            number += (long)digit * power;
            // Check for potential overflow, although unlikely with typical alphametics
            if (number < 0) return Long.MIN_VALUE;
            if (i > 0) { // Avoid multiplying power for the last digit if it causes overflow
                 if (Long.MAX_VALUE / 10 < power) return Long.MIN_VALUE; // Check before multiplication
                 power *= 10;
            }
        }
        return number;
    }
}