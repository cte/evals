import java.util.Map;
import java.util.Optional;
import java.util.Set;

class WordSearcher {
    private static final int[][] DIRECTIONS = {
        {0, 1},   // down
        {1, 0},   // right
        {0, -1},  // up
        {-1, 0},  // left
        {1, 1},   // down-right
        {-1, -1}, // up-left
        {1, -1},  // up-right
        {-1, 1}   // down-left
    };

    Map<String, Optional<WordLocation>> search(final Set<String> words, final char[][] grid) {
        Map<String, Optional<WordLocation>> result = new java.util.HashMap<>();
        for (String word : words) {
            result.put(word, Optional.empty());
        }

        int numRows = grid.length;
        int numCols = grid[0].length;

        for (String word : words) {
            outer:
            for (int row = 0; row < numRows; row++) {
                for (int col = 0; col < numCols; col++) {
                    for (int[] dir : DIRECTIONS) {
                        int dCol = dir[0];
                        int dRow = dir[1];
                        int endCol = col + (word.length() - 1) * dCol;
                        int endRow = row + (word.length() - 1) * dRow;

                        if (endCol < 0 || endCol >= numCols || endRow < 0 || endRow >= numRows) {
                            continue; // out of bounds
                        }

                        boolean match = true;
                        for (int k = 0; k < word.length(); k++) {
                            int c = col + k * dCol;
                            int r = row + k * dRow;
                            if (grid[r][c] != word.charAt(k)) {
                                match = false;
                                break;
                            }
                        }

                        if (match) {
                            Pair start = new Pair(col + 1, row + 1);
                            Pair end = new Pair(endCol + 1, endRow + 1);
                            result.put(word, Optional.of(new WordLocation(start, end)));
                            break outer;
                        }
                    }
                }
            }
        }
        return result;
    }
}
