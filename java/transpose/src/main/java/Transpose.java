public class Transpose {
    public String transpose(String toTranspose) {
        if (toTranspose.isEmpty()) {
            return "";
        }

        String[] lines = toTranspose.split("\n", -1);
        int maxLen = 0;
        for (String line : lines) {
            if (line.length() > maxLen) {
                maxLen = line.length();
            }
        }

        // Pad lines to maxLen
        for (int i = 0; i < lines.length; i++) {
            if (lines[i].length() < maxLen) {
                lines[i] = String.format("%-" + maxLen + "s", lines[i]);
            }
        }

        StringBuilder[] transposed = new StringBuilder[maxLen];
        for (int i = 0; i < maxLen; i++) {
            transposed[i] = new StringBuilder();
        }

        for (String line : lines) {
            for (int col = 0; col < maxLen; col++) {
                transposed[col].append(line.charAt(col));
            }
        }

        StringBuilder result = new StringBuilder();
        for (int i = 0; i < maxLen; i++) {
            if (i > 0) {
                result.append("\n");
            }
            String s = transposed[i].toString();
            int lastNonSpace = -1;
            boolean hasNonSpace = false;
            for (int j = s.length() - 1; j >= 0; j--) {
                if (s.charAt(j) != ' ') {
                    lastNonSpace = j;
                    hasNonSpace = true;
                    break;
                }
            }
            if (!hasNonSpace) {
                // line is all spaces, preserve full length
                result.append(s);
            } else {
                result.append(s.substring(0, lastNonSpace + 1));
            }
        }

        return result.toString();
    }
}
