import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

class WordProblemSolver {

    private static final String ERR_UNKNOWN_QUESTION = "I'm sorry, I don't understand the question!";
    private static final String ERR_SYNTAX = "Syntax error!"; // Keep for specific cases if needed, but tests prefer the other one. Let's use ERR_UNKNOWN_QUESTION mostly.
    private static final String ERR_DIV_ZERO = "Cannot divide by zero!"; // Specific error for this case.

    int solve(final String wordProblem) {
        if (wordProblem == null || !wordProblem.startsWith("What is") || !wordProblem.endsWith("?")) {
            // Keep specific error for fundamentally wrong structure
            throw new IllegalArgumentException("I'm sorry, I don't understand the question!");
        }

        String expression = wordProblem.substring("What is".length(), wordProblem.length() - 1).trim();

        if (expression.isEmpty()) {
             // Empty expression is a form of unknown question/syntax error
             throw new IllegalArgumentException(ERR_UNKNOWN_QUESTION);
        }

        // Replace word operators for easier splitting/parsing later if needed, or handle directly
        expression = expression.replace("multiplied by", "multiplied")
                               .replace("divided by", "divided"); // Keep single words for splitting

        List<String> parts = new ArrayList<>(Arrays.asList(expression.split(" ")));
        parts = parts.stream().filter(s -> !s.isEmpty()).collect(Collectors.toList()); // Handle multiple spaces

        if (parts.isEmpty()) {
             throw new IllegalArgumentException(ERR_UNKNOWN_QUESTION); // Should be caught by earlier empty check, but belt-and-suspenders
        }

        // Try parsing the first part as a number
        int result;
        try {
            result = Integer.parseInt(parts.get(0));
            parts.remove(0); // Consume the first number
        } catch (NumberFormatException e) {
            // If the first part isn't a number, it's an invalid question format
            throw new IllegalArgumentException(ERR_UNKNOWN_QUESTION);
        }

        // If there are no more parts, it was just a number question
        if (parts.isEmpty()) {
            return result;
        }

        // Process operations
        while (!parts.isEmpty()) {
            if (parts.size() < 2) {
                // Need at least an operator and a number
                throw new IllegalArgumentException(ERR_UNKNOWN_QUESTION);
            }

            String operation = parts.get(0);
            String numStr = parts.get(1);
            int nextNum;

            try {
                nextNum = Integer.parseInt(numStr);
            } catch (NumberFormatException e) {
                // Expected a number after the operator
                throw new IllegalArgumentException(ERR_UNKNOWN_QUESTION);
            }

            switch (operation) {
                case "plus":
                    result += nextNum;
                    break;
                case "minus":
                    result -= nextNum;
                    break;
                case "multiplied": // Was "multiplied by"
                    result *= nextNum;
                    break;
                case "divided": // Was "divided by"
                    if (nextNum == 0) {
                        throw new IllegalArgumentException(ERR_DIV_ZERO); // Specific error
                    }
                    result /= nextNum;
                    break;
                default:
                    // Unknown operation or unexpected token (e.g., another number)
                    throw new IllegalArgumentException(ERR_UNKNOWN_QUESTION);
            }

            // Consume the processed operator and number
            parts.remove(0);
            parts.remove(0);
        }

        // If loop finishes correctly, parts should be empty
        // If not empty here, something was wrong (e.g. trailing operator) - though caught by size check inside loop

        return result;
    }
}
