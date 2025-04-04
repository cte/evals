import java.util.ArrayDeque;
import java.util.Deque;

class Connect {

    private final char[][] grid;
    private final int height;
    private final int width;
    private static final char PLAYER_X = 'X';
    private static final char PLAYER_O = 'O';
    private static final char EMPTY = '.';

    public Connect(String[] board) {
        // Determine dimensions and parse the board, removing spaces
        height = board.length;
        if (height == 0) {
            width = 0;
            grid = new char[0][0];
            return;
        }
        // Calculate width based on the first row after removing spaces
        width = board[0].replace(" ", "").length();
        grid = new char[height][width];

        for (int i = 0; i < height; i++) {
            String row = board[i].replace(" ", "");
            for (int j = 0; j < width; j++) {
                grid[i][j] = row.charAt(j);
            }
        }
    }

    public Winner computeWinner() {
        if (height == 0 || width == 0) {
            return Winner.NONE;
        }

        if (checkWin(PLAYER_X)) {
            return Winner.PLAYER_X;
        }
        if (checkWin(PLAYER_O)) {
            return Winner.PLAYER_O;
        }
        return Winner.NONE;
    }

    private boolean checkWin(char player) {
        boolean[][] visited = new boolean[height][width];
        Deque<int[]> stack = new ArrayDeque<>();

        // Initialize stack based on player
        if (player == PLAYER_X) {
            // Start DFS from the left edge (first column)
            for (int i = 0; i < height; i++) {
                if (grid[i][0] == player) {
                    stack.push(new int[]{i, 0});
                    visited[i][0] = true;
                }
            }
        } else { // PLAYER_O
            // Start DFS from the top edge (first row)
            for (int j = 0; j < width; j++) {
                if (grid[0][j] == player) {
                    stack.push(new int[]{0, j});
                    visited[0][j] = true;
                }
            }
        }

        // Define the 6 possible neighbor offsets for hexagonal grid
        int[][] neighbors = {
            {-1, 0}, {-1, 1}, {0, -1}, {0, 1}, {1, -1}, {1, 0}
        };

        while (!stack.isEmpty()) {
            int[] current = stack.pop();
            int r = current[0];
            int c = current[1];

            // Check win condition based on player
            if (player == PLAYER_X && c == width - 1) {
                return true; // Reached the right edge
            }
            if (player == PLAYER_O && r == height - 1) {
                return true; // Reached the bottom edge
            }

            // Explore neighbors
            for (int[] offset : neighbors) {
                int nr = r + offset[0];
                int nc = c + offset[1];

                // Check bounds and if it's a valid move for the player
                if (nr >= 0 && nr < height && nc >= 0 && nc < width &&
                    grid[nr][nc] == player && !visited[nr][nc]) {
                    visited[nr][nc] = true;
                    stack.push(new int[]{nr, nc});
                }
            }
        }

        return false; // No winning path found
    }
}
