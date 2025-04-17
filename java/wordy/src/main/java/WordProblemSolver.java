class WordProblemSolver {
    int solve(final String wordProblem) {
        if (wordProblem == null || !wordProblem.startsWith("What is ") || !wordProblem.endsWith("?")) {
            throw new IllegalArgumentException("I'm sorry, I don't understand the question!");
        }

        String content = wordProblem.substring(8, wordProblem.length() - 1).trim();
        if (content.isEmpty()) {
            throw new IllegalArgumentException("I'm sorry, I don't understand the question!");
        }

        // Replace multi-word operators with single tokens
        content = content.replaceAll("multiplied by", "multiplied_by");
        content = content.replaceAll("divided by", "divided_by");

        String[] tokens = content.split("\\s+");
        if (tokens.length == 0) {
            throw new IllegalArgumentException("I'm sorry, I don't understand the question!");
        }

        // If the first token is an operator, reject prefix notation
        if (tokens[0].equals("plus") || tokens[0].equals("minus") || tokens[0].equals("multiplied_by") || tokens[0].equals("divided_by")) {
            throw new IllegalArgumentException("I'm sorry, I don't understand the question!");
        }

        int index = 0;
        Integer result = null;
        String pendingOp = null;

        while (index < tokens.length) {
            String token = tokens[index];

            int number;
            try {
                number = Integer.parseInt(token);
            } catch (NumberFormatException e) {
                // Not a number, check if it's an operator
                if (token.equals("plus") || token.equals("minus") || token.equals("multiplied_by") || token.equals("divided_by")) {
                    if (pendingOp != null) {
                        // Two operators in a row
                        throw new IllegalArgumentException("I'm sorry, I don't understand the question!");
                    }
                    pendingOp = token;
                    index++;
                    continue;
                } else {
                    // Unknown word
                    throw new IllegalArgumentException("I'm sorry, I don't understand the question!");
                }
            }

            if (result == null) {
                result = number;
            } else {
                if (pendingOp == null) {
                    // Two numbers in a row
                    throw new IllegalArgumentException("I'm sorry, I don't understand the question!");
                }
                switch (pendingOp) {
                    case "plus":
                        result += number;
                        break;
                    case "minus":
                        result -= number;
                        break;
                    case "multiplied_by":
                        result *= number;
                        break;
                    case "divided_by":
                        result /= number;
                        break;
                    default:
                        throw new IllegalArgumentException("I'm sorry, I don't understand the question!");
                }
                pendingOp = null;
            }
            index++;
        }

        if (result == null || pendingOp != null) {
            throw new IllegalArgumentException("I'm sorry, I don't understand the question!");
        }

        return result;
    }
}
