import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

class OpticalCharacterReader {

    private static final int DIGIT_HEIGHT = 4;
    private static final int DIGIT_WIDTH = 3;
    private static final Map<String, String> DIGIT_PATTERNS = new HashMap<>();

    static {
        DIGIT_PATTERNS.put(
            " _ " +
            "| |" +
            "|_|" +
            "   ", "0");
        DIGIT_PATTERNS.put(
            "   " +
            "  |" +
            "  |" +
            "   ", "1");
        DIGIT_PATTERNS.put(
            " _ " +
            " _|" +
            "|_ " +
            "   ", "2");
        DIGIT_PATTERNS.put(
            " _ " +
            " _|" +
            " _|" +
            "   ", "3");
        DIGIT_PATTERNS.put(
            "   " +
            "|_|" +
            "  |" +
            "   ", "4");
        DIGIT_PATTERNS.put(
            " _ " +
            "|_ " +
            " _|" +
            "   ", "5");
        DIGIT_PATTERNS.put(
            " _ " +
            "|_ " +
            "|_|" +
            "   ", "6");
        DIGIT_PATTERNS.put(
            " _ " +
            "  |" +
            "  |" +
            "   ", "7");
        DIGIT_PATTERNS.put(
            " _ " +
            "|_|" +
            "|_|" +
            "   ", "8");
        DIGIT_PATTERNS.put(
            " _ " +
            "|_|" +
            " _|" +
            "   ", "9");
    }

    String parse(List<String> input) {
        if (input.size() % DIGIT_HEIGHT != 0) {
            throw new IllegalArgumentException("Number of input rows must be a positive multiple of 4");
        }

        List<String> resultNumbers = new ArrayList<>();
        int lineCount = input.size();

        for (int i = 0; i < lineCount; i += DIGIT_HEIGHT) {
            List<String> currentNumberLines = input.subList(i, i + DIGIT_HEIGHT);
            resultNumbers.add(parseSingleNumber(currentNumberLines));
        }

        return String.join(",", resultNumbers);
    }

    private String parseSingleNumber(List<String> lines) {
        if (lines.size() != DIGIT_HEIGHT) {
             // This should ideally not happen due to the check in parse()
             throw new IllegalArgumentException("Internal error: Incorrect number of lines for single number.");
        }

        int lineWidth = lines.get(0).length();
        if (lineWidth % DIGIT_WIDTH != 0) {
            throw new IllegalArgumentException("Number of input columns must be a positive multiple of 3");
        }

        // Validate all lines in the block have the same length
        for (String line : lines) {
            if (line.length() != lineWidth) {
                 throw new IllegalArgumentException("Input lines within a block must have the same length");
            }
        }


        StringBuilder recognizedNumber = new StringBuilder();
        int numDigits = lineWidth / DIGIT_WIDTH;

        for (int digitIndex = 0; digitIndex < numDigits; digitIndex++) {
            StringBuilder currentDigitPattern = new StringBuilder();
            int startCol = digitIndex * DIGIT_WIDTH;
            int endCol = startCol + DIGIT_WIDTH;

            for (int row = 0; row < DIGIT_HEIGHT; row++) {
                 // Ensure substring indices are valid
                if (endCol > lines.get(row).length()) {
                     throw new IllegalArgumentException("Input line too short for expected width");
                }
                currentDigitPattern.append(lines.get(row).substring(startCol, endCol));
            }

            recognizedNumber.append(DIGIT_PATTERNS.getOrDefault(currentDigitPattern.toString(), "?"));
        }

        return recognizedNumber.toString();
    }
}