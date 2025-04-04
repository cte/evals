import java.util.Arrays;

class StateOfTicTacToe {
    public GameState determineState(String[] board) {
        // Validate board dimensions
        if (board == null || board.length != 3) {
            throw new IllegalArgumentException("Board must be a 3-element array representing rows.");
        }
        for (String row : board) {
            if (row == null || row.length() != 3) {
                throw new IllegalArgumentException("Each row string must be 3 characters long.");
            }
        }

        char[][] grid = new char[3][3];
        int xCount = 0;
        int oCount = 0;
        int emptyCount = 0;

        // Parse board and count marks
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                char mark = board[i].charAt(j);
                grid[i][j] = mark;
                if (mark == 'X') {
                    xCount++;
                } else if (mark == 'O') {
                    oCount++;
                } else if (mark == ' ') {
                    emptyCount++;
                } else {
                    throw new IllegalArgumentException("Invalid character found on board: " + mark);
                }
            }
        }

        // Validate turn counts (X starts) - Specific messages first
        if (oCount > xCount) {
            throw new IllegalArgumentException("Wrong turn order: O started");
        }
        if (xCount > oCount + 1) {
            throw new IllegalArgumentException("Wrong turn order: X went twice");
        }

        boolean xWins = checkWin(grid, 'X');
        boolean oWins = checkWin(grid, 'O');

        // Validate win conditions based on test expectations
        // Test expects "game should have ended" for impossible win scenarios like both win or wrong turn count for winner
        if (xWins && oWins) {
             // Test cases like {"XOX", "OXO", "XOX"} expect this message
            throw new IllegalArgumentException("Impossible board: game should have ended after the game was won");
        }
        if (xWins && xCount == oCount) {
            // X wins, but counts are equal (O must have played last)
            throw new IllegalArgumentException("Impossible board: game should have ended after the game was won");
        }
        if (oWins && xCount > oCount) {
             // O wins, but X count is greater (X must have played last)
            throw new IllegalArgumentException("Impossible board: game should have ended after the game was won");
        }

        // Determine game state
        if (xWins || oWins) {
            // If we reach here, the win is valid according to turn counts
            return GameState.WIN;
        } else if (emptyCount == 0) {
            // No winner and board is full
            return GameState.DRAW;
        } else {
            // No winner and board is not full
            return GameState.ONGOING;
        }
    }

    private boolean checkWin(char[][] grid, char player) {
        // Check rows
        for (int i = 0; i < 3; i++) {
            if (grid[i][0] == player && grid[i][1] == player && grid[i][2] == player) {
                return true;
            }
        }

        // Check columns
        for (int j = 0; j < 3; j++) {
            if (grid[0][j] == player && grid[1][j] == player && grid[2][j] == player) {
                return true;
            }
        }

        // Check diagonals
        if (grid[0][0] == player && grid[1][1] == player && grid[2][2] == player) {
            return true;
        }
        if (grid[0][2] == player && grid[1][1] == player && grid[2][0] == player) {
            return true;
        }

        return false;
    }
}
