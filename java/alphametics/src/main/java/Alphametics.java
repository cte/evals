import java.util.*;
import java.util.stream.Collectors;

class Alphametics {

    private final List<String> addends;
    private final String result;
    private final Set<Character> uniqueLetters;
    private final List<Character> letterList;
    private final Set<Character> leadingLetters;

    Alphametics(String userInput) {
        String[] parts = userInput.split("==");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid puzzle format");
        }

        this.addends = Arrays.stream(parts[0].split("\\+"))
                .map(String::trim)
                .collect(Collectors.toList());
        this.result = parts[1].trim();

        this.uniqueLetters = new HashSet<>();
        this.leadingLetters = new HashSet<>();

        for (String word : addends) {
            if (!word.isEmpty()) {
                leadingLetters.add(word.charAt(0));
                for (char c : word.toCharArray()) {
                    uniqueLetters.add(c);
                }
            }
        }

        if (!result.isEmpty()) {
            leadingLetters.add(result.charAt(0));
            for (char c : result.toCharArray()) {
                uniqueLetters.add(c);
            }
        }

        if (uniqueLetters.size() > 10) {
            throw new IllegalArgumentException("Too many unique letters");
        }

        this.letterList = new ArrayList<>(uniqueLetters);
    }

    Map<Character, Integer> solve() throws UnsolvablePuzzleException {
        List<Integer> digits = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            digits.add(i);
        }

        List<List<Integer>> permutations = generatePermutations(digits, letterList.size());

        for (List<Integer> perm : permutations) {
            Map<Character, Integer> candidate = new HashMap<>();
            for (int i = 0; i < letterList.size(); i++) {
                candidate.put(letterList.get(i), perm.get(i));
            }

            // Check for leading zeros
            boolean leadingZero = false;
            for (Character lead : leadingLetters) {
                if (candidate.get(lead) == 0) {
                    leadingZero = true;
                    break;
                }
            }
            if (leadingZero) continue;

            long sum = 0;
            for (String addend : addends) {
                long val = wordToNumber(addend, candidate);
                sum += val;
            }
            long resultVal = wordToNumber(result, candidate);

            if (sum == resultVal) {
                return candidate;
            }
        }

        throw new UnsolvablePuzzleException();
    }

    private long wordToNumber(String word, Map<Character, Integer> mapping) {
        long value = 0;
        for (char c : word.toCharArray()) {
            value = value * 10 + mapping.get(c);
        }
        return value;
    }

    private List<List<Integer>> generatePermutations(List<Integer> digits, int length) {
        List<List<Integer>> result = new ArrayList<>();
        permuteHelper(digits, new ArrayList<>(), result, length);
        return result;
    }

    private void permuteHelper(List<Integer> digits, List<Integer> current, List<List<Integer>> result, int length) {
        if (current.size() == length) {
            result.add(new ArrayList<>(current));
            return;
        }
        for (Integer digit : digits) {
            if (current.contains(digit)) continue;
            current.add(digit);
            permuteHelper(digits, current, result, length);
            current.remove(current.size() - 1);
        }
    }
}