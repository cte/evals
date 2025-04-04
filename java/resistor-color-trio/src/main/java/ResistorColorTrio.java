import java.util.Map;
import java.util.HashMap;
import java.math.BigDecimal;
import java.math.RoundingMode;

class ResistorColorTrio {

    private enum Color {
        BLACK(0), BROWN(1), RED(2), ORANGE(3), YELLOW(4),
        GREEN(5), BLUE(6), VIOLET(7), GREY(8), WHITE(9);

        private final int value;

        Color(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    private static final Map<String, Color> colorMap = new HashMap<>();
    static {
        for (Color c : Color.values()) {
            colorMap.put(c.name().toLowerCase(), c);
        }
    }

    String label(String[] colors) {
        if (colors == null || colors.length < 3) {
            throw new IllegalArgumentException("Input must contain at least three colors.");
        }

        Color color1 = colorMap.get(colors[0].toLowerCase());
        Color color2 = colorMap.get(colors[1].toLowerCase());
        Color color3 = colorMap.get(colors[2].toLowerCase());

        if (color1 == null || color2 == null || color3 == null) {
            throw new IllegalArgumentException("Invalid color provided.");
        }

        long baseValue = color1.getValue() * 10L + color2.getValue();
        BigDecimal totalValue = BigDecimal.valueOf(baseValue)
                                        .multiply(BigDecimal.TEN.pow(color3.getValue()));

        String unit = "ohms";
        BigDecimal displayValue = totalValue;

        BigDecimal GIGA = BigDecimal.valueOf(1_000_000_000);
        BigDecimal MEGA = BigDecimal.valueOf(1_000_000);
        BigDecimal KILO = BigDecimal.valueOf(1_000);

        // Check for gigaohms (>= 1,000,000,000 ohms)
        if (totalValue.compareTo(GIGA) >= 0 && totalValue.remainder(GIGA).compareTo(BigDecimal.ZERO) == 0) {
            displayValue = totalValue.divide(GIGA);
            unit = "gigaohms";
        // Check for megaohms (>= 1,000,000 ohms)
        } else if (totalValue.compareTo(MEGA) >= 0 && totalValue.remainder(MEGA).compareTo(BigDecimal.ZERO) == 0) {
            displayValue = totalValue.divide(MEGA);
            unit = "megaohms";
        // Check for kiloohms (>= 1,000 ohms)
        } else if (totalValue.compareTo(KILO) >= 0 && totalValue.remainder(KILO).compareTo(BigDecimal.ZERO) == 0) {
            displayValue = totalValue.divide(KILO);
            unit = "kiloohms";
        }


        return displayValue.stripTrailingZeros().toPlainString() + " " + unit;
    }
}
