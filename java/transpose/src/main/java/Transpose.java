import java.util.ArrayList;
import java.util.List;

public class Transpose {
    public String transpose(String toTranspose) {
        if (toTranspose == null || toTranspose.isEmpty()) {
            return "";
        }

        String[] lines = toTranspose.split("\n", -1); // Use -1 limit to keep trailing empty strings
        int numRows = lines.length;
        int maxCol = 0;
        for (String line : lines) {
            maxCol = Math.max(maxCol, line.length());
        }

        StringBuilder result = new StringBuilder();
        for (int j = 0; j < maxCol; j++) {
            StringBuilder transposedRow = new StringBuilder();

            for (int i = 0; i < numRows; i++) {
                if (j < lines[i].length()) {
                    // Character exists at this position
                    transposedRow.append(lines[i].charAt(j));
                } else {
                    // Character does not exist, check if padding is needed
                    boolean subsequentCharExists = false;
                    for (int k = i + 1; k < numRows; k++) {
                        if (j < lines[k].length()) {
                            subsequentCharExists = true;
                            break;
                        }
                    }

                    if (subsequentCharExists) {
                        // Pad with space if a character exists in this column in a later row
                        transposedRow.append(" ");
                    }
                    // If !subsequentCharExists, we simply do nothing for this row i at column j.
                }
            }
            result.append(transposedRow);
            if (j < maxCol - 1) {
                result.append("\n");
            }
        }

        return result.toString();
    }
}
