import java.util.*;

class ForthEvaluator {
    List<Integer> evaluateProgram(List<String> input) {
        Deque<Integer> stack = new ArrayDeque<>();
        Map<String, List<String>> userDefinitions = new HashMap<>();

        for (String line : input) {
            List<String> tokens = new ArrayList<>(Arrays.asList(line.toLowerCase().split("\\s+")));
            processTokens(tokens, stack, userDefinitions, new HashSet<>());
        }

        List<Integer> result = new ArrayList<>(stack);
        Collections.reverse(result);
        return result;
    }

    private void processTokens(List<String> tokens, Deque<Integer> stack, Map<String, List<String>> userDefinitions, Set<String> ignored) {
        Deque<String> tokenQueue = new ArrayDeque<>(tokens);

        while (!tokenQueue.isEmpty()) {
            String token = tokenQueue.pollFirst();

            if (token.equals(":")) {
                if (tokenQueue.isEmpty()) {
                    throw new IllegalArgumentException("Invalid word definition");
                }
                String wordName = tokenQueue.pollFirst();
                if (isInteger(wordName)) {
                    throw new IllegalArgumentException("Cannot redefine numbers");
                }
                List<String> definition = new ArrayList<>();
                boolean foundTerminator = false;
                while (!tokenQueue.isEmpty()) {
                    String defToken = tokenQueue.pollFirst();
                    if (defToken.equals(";")) {
                        foundTerminator = true;
                        break;
                    }
                    definition.add(defToken);
                }
                if (!foundTerminator) {
                    throw new IllegalArgumentException("Invalid word definition");
                }
                userDefinitions.put(wordName, new ArrayList<>(definition));
            } else if (userDefinitions.containsKey(token)) {
                List<String> definition = userDefinitions.get(token);
                // Insert definition tokens at the front of the queue for iterative expansion
                for (int j = definition.size() - 1; j >= 0; j--) {
                    tokenQueue.addFirst(definition.get(j));
                }
            } else if (isInteger(token)) {
                stack.push(Integer.parseInt(token));
            } else {
                switch (token) {
                    case "+":
                        requireStackSize(stack, 2, "Addition requires that the stack contain at least 2 values");
                        int bAdd = stack.pop();
                        int aAdd = stack.pop();
                        stack.push(aAdd + bAdd);
                        break;
                    case "-":
                        requireStackSize(stack, 2, "Subtraction requires that the stack contain at least 2 values");
                        int bSub = stack.pop();
                        int aSub = stack.pop();
                        stack.push(aSub - bSub);
                        break;
                    case "*":
                        requireStackSize(stack, 2, "Multiplication requires that the stack contain at least 2 values");
                        int bMul = stack.pop();
                        int aMul = stack.pop();
                        stack.push(aMul * bMul);
                        break;
                    case "/":
                        requireStackSize(stack, 2, "Division requires that the stack contain at least 2 values");
                        int bDiv = stack.pop();
                        int aDiv = stack.pop();
                        if (bDiv == 0) {
                            throw new IllegalArgumentException("Division by 0 is not allowed");
                        }
                        stack.push(aDiv / bDiv);
                        break;
                    case "dup":
                        requireStackSize(stack, 1, "Duplicating requires that the stack contain at least 1 value");
                        int dupVal = stack.peek();
                        stack.push(dupVal);
                        break;
                    case "drop":
                        requireStackSize(stack, 1, "Dropping requires that the stack contain at least 1 value");
                        stack.pop();
                        break;
                    case "swap":
                        requireStackSize(stack, 2, "Swapping requires that the stack contain at least 2 values");
                        int first = stack.pop();
                        int second = stack.pop();
                        stack.push(first);
                        stack.push(second);
                        break;
                    case "over":
                        requireStackSize(stack, 2, "Overing requires that the stack contain at least 2 values");
                        int top = stack.pop();
                        int next = stack.peek();
                        stack.push(top);
                        stack.push(next);
                        break;
                    default:
                        throw new IllegalArgumentException("No definition available for operator \"" + token + "\"");
                }
            }
        }
    }

    private boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void requireStackSize(Deque<Integer> stack, int size, String message) {
        if (stack.size() < size) {
            throw new IllegalArgumentException(message);
        }
    }
}
