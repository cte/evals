class ResistorColorTrio {
    String label(String[] colors) {
        // Map color names to digit values
        java.util.Map<String, Integer> colorMap = java.util.Map.of(
            "black", 0,
            "brown", 1,
            "red", 2,
            "orange", 3,
            "yellow", 4,
            "green", 5,
            "blue", 6,
            "violet", 7,
            "grey", 8,
            "white", 9
        );

        // Extract first three colors
        int firstDigit = colorMap.get(colors[0]);
        int secondDigit = colorMap.get(colors[1]);
        int multiplierPower = colorMap.get(colors[2]);

        // Compute significant value and multiplier
        int significant = firstDigit * 10 + secondDigit;
        long multiplier = (long) Math.pow(10, multiplierPower);

        long resistance = significant * multiplier;

        String unit;
        double displayValue;

        if (resistance >= 1_000_000_000) {
            displayValue = resistance / 1_000_000_000.0;
            unit = "gigaohms";
        } else if (resistance >= 1_000_000) {
            displayValue = resistance / 1_000_000.0;
            unit = "megaohms";
        } else if (resistance >= 1_000) {
            displayValue = resistance / 1_000.0;
            unit = "kiloohms";
        } else {
            displayValue = resistance;
            unit = "ohms";
        }

        // Remove decimal if whole number
        String valueStr;
        if (displayValue == (long) displayValue) {
            valueStr = String.format("%d", (long) displayValue);
        } else {
            valueStr = String.format("%.0f", displayValue);
        }

        return valueStr + " " + unit;
    }
}
