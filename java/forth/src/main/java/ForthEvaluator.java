import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

class ForthEvaluator {
    private Deque<Integer> stack; // Initialize in evaluateProgram
    private Map<String, List<String>> userDefinedWords; // Initialize in evaluateProgram

    // Public method to evaluate a list of Forth commands
    public List<Integer> evaluateProgram(List<String> input) {
        // Initialize state for this evaluation run
        this.stack = new ArrayDeque<>();
        this.userDefinedWords = new HashMap<>();

        // Process each line of the input program
        for (String line : input) {
            processLine(line.toLowerCase()); // Process lines case-insensitively
        }

        // Return the final state of the stack (bottom to top)
        List<Integer> result = new ArrayList<>(stack);
        java.util.Collections.reverse(result); // Reverse to match test expectations (bottom-to-top)
        return result;
    }

    // Processes a single line of Forth code
    private void processLine(String line) {
        List<String> tokens = tokenize(line);
        boolean definingWord = false; // Flag to track if we are currently defining a new word
        String newWordName = null; // Stores the name of the word being defined
        List<String> newWordDefinition = new ArrayList<>(); // Stores the definition tokens

        for (String token : tokens) {
            if (definingWord) {
                // Inside a word definition
                if (token.equals(";")) {
                    // End of definition
                    if (newWordName == null) {
                        throw new IllegalArgumentException("Invalid definition: Missing word name");
                    }
                    // Check if the word being defined is actually a number
                    try {
                        Integer.parseInt(newWordName);
                        // If parsing succeeds, it's a number, which is illegal to redefine
                        throw new IllegalArgumentException("Cannot redefine numbers");
                    } catch (NumberFormatException e) {
                        // It's not a number, proceed with definition.
                        // Expand any user-defined words within the definition *now*.
                        List<String> expandedDefinition = expandDefinition(newWordDefinition);
                        userDefinedWords.put(newWordName, expandedDefinition);
                    }
                    // Reset definition state
                    definingWord = false;
                    newWordName = null;
                    newWordDefinition.clear();
                } else if (newWordName == null) {
                    // First token after ':' is the word name
                    newWordName = token;
                } else {
                    // Subsequent tokens are part of the definition
                    newWordDefinition.add(token);
                }
            } else if (token.equals(":")) {
                // Start of a word definition
                definingWord = true;
            } else {
                // Not defining a word, evaluate the token normally
                evaluateToken(token);
            }
        }

        // Check if a definition was started but not terminated
        if (definingWord) {
            throw new IllegalArgumentException("Invalid definition: Definition not terminated with ;");
        }
    }

    // Splits a line into tokens based on whitespace
    private List<String> tokenize(String line) {
        String trimmedLine = line.trim();
        if (trimmedLine.isEmpty()) {
            return List.of(); // Return empty list for empty or whitespace-only lines
        }
        // Split by one or more whitespace characters
        return List.of(trimmedLine.split("\\s+"));
    }

    // Evaluates a single token (number, built-in word, or user-defined word)
    private void evaluateToken(String token) {
        try {
            // Attempt to parse the token as an integer
            int number = Integer.parseInt(token);
            stack.push(number);
        } catch (NumberFormatException e) {
            // If not a number, treat it as a word
            if (userDefinedWords.containsKey(token)) {
                // Execute user-defined word by evaluating its definition tokens
                List<String> definition = userDefinedWords.get(token);
                for (String defToken : definition) {
                    evaluateToken(defToken); // Recursive evaluation
                }
            } else {
                // Execute built-in word
                executeBuiltIn(token);
            }
        }
    }

    // Executes a built-in Forth operation
    private void executeBuiltIn(String word) {
        switch (word) {
            case "+":
                ensureStackSize(2, "Addition requires that the stack contain at least 2 values");
                int addB = stack.pop();
                int addA = stack.pop();
                stack.push(addA + addB);
                break;
            case "-":
                ensureStackSize(2, "Subtraction requires that the stack contain at least 2 values");
                int subB = stack.pop();
                int subA = stack.pop();
                stack.push(subA - subB);
                break;
            case "*":
                ensureStackSize(2, "Multiplication requires that the stack contain at least 2 values");
                int mulB = stack.pop();
                int mulA = stack.pop();
                stack.push(mulA * mulB);
                break;
            case "/":
                ensureStackSize(2, "Division requires that the stack contain at least 2 values");
                int divB = stack.pop();
                int divA = stack.pop();
                if (divB == 0) {
                    throw new IllegalArgumentException("Division by 0 is not allowed"); // Match test message
                }
                // Ensure integer division behavior matches tests if needed (Java '/' is already integer division)
                stack.push(divA / divB);
                break;
            case "dup":
                ensureStackSize(1, "Duplicating requires that the stack contain at least 1 value");
                stack.push(Objects.requireNonNull(stack.peek())); // Use peek() and handle potential null if stack was empty (though ensureStackSize prevents this)
                break;
            case "drop":
                ensureStackSize(1, "Dropping requires that the stack contain at least 1 value");
                stack.pop();
                break;
            case "swap":
                ensureStackSize(2, "Swapping requires that the stack contain at least 2 values");
                int swapB = stack.pop();
                int swapA = stack.pop();
                stack.push(swapB);
                stack.push(swapA);
                break;
            case "over":
                ensureStackSize(2, "Over requires that the stack contain at least 2 values");
                int overB = stack.pop();
                int overA = stack.pop();
                stack.push(overA);
                stack.push(overB);
                stack.push(overA);
                break;
            default:
                // Handle unknown words
                throw new IllegalArgumentException("No definition available for operator \"" + word + "\"");
        }
    }

    // Helper method to check if the stack has enough elements for an operation
    private void ensureStackSize(int requiredSize, String baseErrorMessage) {
        if (stack.size() < requiredSize) {
            // Adjust error messages slightly for specific operations if needed by tests
            String specificMessage = baseErrorMessage;
            if (baseErrorMessage.startsWith("Over")) {
                 specificMessage = "Overing requires that the stack contain at least " + requiredSize + " values";
            } else if (baseErrorMessage.startsWith("Duplicating")) {
                 specificMessage = "Duplicating requires that the stack contain at least " + requiredSize + " value" + (requiredSize > 1 ? "s" : "");
            } else if (baseErrorMessage.startsWith("Dropping")) {
                 specificMessage = "Dropping requires that the stack contain at least " + requiredSize + " value" + (requiredSize > 1 ? "s" : "");
            } else if (baseErrorMessage.startsWith("Swapping")) {
                 specificMessage = "Swapping requires that the stack contain at least " + requiredSize + " values";
            } else if (baseErrorMessage.startsWith("Division")) {
                 specificMessage = "Division requires that the stack contain at least " + requiredSize + " values";
                 // The division by zero message is handled separately
            } else if (baseErrorMessage.startsWith("Subtraction")) {
                 specificMessage = "Subtraction requires that the stack contain at least " + requiredSize + " values";
            } else if (baseErrorMessage.startsWith("Multiplication")) {
                 specificMessage = "Multiplication requires that the stack contain at least " + requiredSize + " values";
            } else if (baseErrorMessage.startsWith("Addition")) {
                 specificMessage = "Addition requires that the stack contain at least " + requiredSize + " values";
            }
            throw new IllegalArgumentException(specificMessage);
        }
    }

    // Expands user-defined words within a definition list
    private List<String> expandDefinition(List<String> definition) {
        List<String> expanded = new ArrayList<>();
        for (String token : definition) {
            if (userDefinedWords.containsKey(token)) {
                // If token is a known user word, add its *current* definition tokens
                expanded.addAll(userDefinedWords.get(token));
            } else {
                // Otherwise, add the token as is (number or built-in)
                expanded.add(token);
            }
        }
        return expanded;
    }

}
