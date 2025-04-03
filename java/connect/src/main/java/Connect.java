import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

class Connect {

    private final char[][] grid;
    private final int rows;

    public Connect(String[] board) {
        this.rows = board.length;
        this.grid = new char[rows][];
        for (int row = 0; row < rows; row++) {
            String line = board[row].replace(" ", "");
            grid[row] = line.toCharArray();
        }
    }

    public Winner computeWinner() {
        if (hasPath('X')) {
            return Winner.PLAYER_X;
        }
        if (hasPath('O')) {
            return Winner.PLAYER_O;
        }
        return Winner.NONE;
    }

    private boolean hasPath(char player) {
        Set<String> visited = new HashSet<>();
        Stack<int[]> stack = new Stack<>();

        if (player == 'X') {
            // Start from left edge
            for (int row = 0; row < rows; row++) {
                if (grid[row].length > 0 && grid[row][0] == 'X') {
                    stack.push(new int[]{row, 0});
                    visited.add(row + "," + 0);
                }
            }
            // Search for right edge
            while (!stack.isEmpty()) {
                int[] pos = stack.pop();
                int r = pos[0], c = pos[1];
                if (c == grid[r].length - 1) {
                    return true;
                }
                for (int[] n : neighbors(r, c)) {
                    int nr = n[0], nc = n[1];
                    if (inBounds(nr, nc) && grid[nr][nc] == player && !visited.contains(nr + "," + nc)) {
                        visited.add(nr + "," + nc);
                        stack.push(new int[]{nr, nc});
                    }
                }
            }
        } else {
            // player == 'O', start from top edge
            if (rows == 0) return false;
            for (int col = 0; col < grid[0].length; col++) {
                if (grid[0][col] == 'O') {
                    stack.push(new int[]{0, col});
                    visited.add(0 + "," + col);
                }
            }
            // Search for bottom edge
            while (!stack.isEmpty()) {
                int[] pos = stack.pop();
                int r = pos[0], c = pos[1];
                if (r == rows - 1) {
                    return true;
                }
                for (int[] n : neighbors(r, c)) {
                    int nr = n[0], nc = n[1];
                    if (inBounds(nr, nc) && grid[nr][nc] == player && !visited.contains(nr + "," + nc)) {
                        visited.add(nr + "," + nc);
                        stack.push(new int[]{nr, nc});
                    }
                }
            }
        }
        return false;
    }

    private boolean inBounds(int r, int c) {
        return r >= 0 && r < rows && c >= 0 && c < grid[r].length;
    }

    private int[][] neighbors(int r, int c) {
        return new int[][]{
            {r - 1, c},     // up
            {r - 1, c + 1}, // up-right
            {r, c - 1},     // left
            {r, c + 1},     // right
            {r + 1, c - 1}, // down-left
            {r + 1, c}      // down
        };
    }
}
