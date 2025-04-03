public class BaseConverter {

    private final int originalBase;
    private final int[] originalDigits;

    public BaseConverter(int originalBase, int[] originalDigits) {
        if (originalBase < 2) {
            throw new IllegalArgumentException("Bases must be at least 2.");
        }
        if (originalDigits == null) {
            throw new IllegalArgumentException("Digits may not be null.");
        }
        for (int digit : originalDigits) {
            if (digit < 0) {
                throw new IllegalArgumentException("Digits may not be negative.");
            }
            if (digit >= originalBase) {
                throw new IllegalArgumentException("All digits must be strictly less than the base.");
            }
        }
        this.originalBase = originalBase;
        this.originalDigits = originalDigits;
    }

    public int[] convertToBase(int newBase) {
        if (newBase < 2) {
            throw new IllegalArgumentException("Bases must be at least 2.");
        }

        // Convert original digits to integer value
        int value = 0;
        for (int digit : originalDigits) {
            value = value * originalBase + digit;
        }

        // Special case: zero or empty input
        if (originalDigits.length == 0 || value == 0) {
            return new int[]{0};
        }

        // Convert integer value to new base digits
        java.util.List<Integer> digitsList = new java.util.ArrayList<>();
        int tempValue = value;
        while (tempValue > 0) {
            digitsList.add(0, tempValue % newBase);
            tempValue /= newBase;
        }

        // Convert list to array
        int[] result = new int[digitsList.size()];
        for (int i = 0; i < digitsList.size(); i++) {
            result[i] = digitsList.get(i);
        }
        return result;
    }
}