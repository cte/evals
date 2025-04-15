import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

class WordSearcher {

    // Directions: dx, dy pairs (Right, Left, Down, Up, Down-Right, Down-Left, Up-Right, Up-Left)
    private static final int[] dx = {1, -1, 0, 0, 1, -1, 1, -1};
    private static final int[] dy = {0, 0, 1, -1, 1, 1, -1, -1};

    Map<String, Optional<WordLocation>> search(final Set<String> words, final char[][] grid) {
        Map<String, Optional<WordLocation>> results = new HashMap<>();
        if (grid == null || grid.length == 0 || grid[0].length == 0) {
            for (String word : words) {
                results.put(word, Optional.empty());
            }
            return results;
        }

        int numRows = grid.length;
        int numCols = grid[0].length;

        for (String word : words) {
            results.put(word, Optional.empty()); // Initialize as not found

            if (word == null || word.isEmpty()) {
                continue; // Skip empty or null words
            }

            boolean found = false;
            for (int r = 0; r < numRows && !found; r++) {
                for (int c = 0; c < numCols && !found; c++) {
                    // Check if the first character matches
                    if (grid[r][c] == word.charAt(0)) {
                        // Try all 8 directions
                        for (int dir = 0; dir < 8; dir++) {
                            int endR = r + dy[dir] * (word.length() - 1);
                            int endC = c + dx[dir] * (word.length() - 1);

                            // Check if the end position is within bounds
                            if (endR >= 0 && endR < numRows && endC >= 0 && endC < numCols) {
                                boolean match = true;
                                // Check the rest of the word characters
                                for (int i = 1; i < word.length(); i++) {
                                    int currentR = r + dy[dir] * i;
                                    int currentC = c + dx[dir] * i;
                                    if (grid[currentR][currentC] != word.charAt(i)) {
                                        match = false;
                                        break;
                                    }
                                }

                                // If the word matched completely
                                if (match) {
                                    // Convert 0-based (r, c) to 1-based (x, y)
                                    Pair startCoord = new Pair(c + 1, r + 1);
                                    Pair endCoord = new Pair(endC + 1, endR + 1);
                                    results.put(word, Optional.of(new WordLocation(startCoord, endCoord)));
                                    found = true; // Found the word, move to the next word
                                    break; // Stop checking directions for this starting cell
                                }
                            }
                        }
                    }
                }
            }
        }
        return results;
    }
}
