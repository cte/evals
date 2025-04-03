import java.util.List;
import java.util.Map;
import java.util.HashMap;

class OpticalCharacterReader {

    private static final Map<String, String> DIGIT_MAP = new HashMap<>();

    static {
        DIGIT_MAP.put(
                " _ " +
                "| |" +
                "|_|" +
                "   ", "0");
        DIGIT_MAP.put(
                "   " +
                "  |" +
                "  |" +
                "   ", "1");
        DIGIT_MAP.put(
                " _ " +
                " _|" +
                "|_ " +
                "   ", "2");
        DIGIT_MAP.put(
                " _ " +
                " _|" +
                " _|" +
                "   ", "3");
        DIGIT_MAP.put(
                "   " +
                "|_|" +
                "  |" +
                "   ", "4");
        DIGIT_MAP.put(
                " _ " +
                "|_ " +
                " _|" +
                "   ", "5");
        DIGIT_MAP.put(
                " _ " +
                "|_ " +
                "|_|" +
                "   ", "6");
        DIGIT_MAP.put(
                " _ " +
                "  |" +
                "  |" +
                "   ", "7");
        DIGIT_MAP.put(
                " _ " +
                "|_|" +
                "|_|" +
                "   ", "8");
        DIGIT_MAP.put(
                " _ " +
                "|_|" +
                " _|" +
                "   ", "9");
    }

    String parse(List<String> input) {
        if (input == null || input.isEmpty() || input.size() % 4 != 0) {
            throw new IllegalArgumentException("Number of input rows must be a positive multiple of 4");
        }
        int numRows = input.size();
        int numCols = input.get(0).length();
        if (numCols == 0 || numCols % 3 != 0) {
            throw new IllegalArgumentException("Number of input columns must be a positive multiple of 3");
        }
        for (String line : input) {
            if (line.length() != numCols) {
                throw new IllegalArgumentException("All input lines must have the same length");
            }
        }

        StringBuilder result = new StringBuilder();
        for (int rowBlock = 0; rowBlock < numRows; rowBlock += 4) {
            if (rowBlock > 0) {
                result.append(",");
            }
            int digitsInRow = numCols / 3;
            for (int digitIdx = 0; digitIdx < digitsInRow; digitIdx++) {
                StringBuilder digitPattern = new StringBuilder();
                for (int rowOffset = 0; rowOffset < 4; rowOffset++) {
                    String line = input.get(rowBlock + rowOffset);
                    int startIdx = digitIdx * 3;
                    digitPattern.append(line.substring(startIdx, startIdx + 3));
                }
                result.append(DIGIT_MAP.getOrDefault(digitPattern.toString(), "?"));
            }
        }
        return result.toString();
    }

}