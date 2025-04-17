class StateOfTicTacToe {
    public GameState determineState(String[] board) {
        int xCount = 0;
        int oCount = 0;
        boolean xWins = false;
        boolean oWins = false;

        // Count X's and O's
        for (String row : board) {
            for (char c : row.toCharArray()) {
                if (c == 'X') xCount++;
                else if (c == 'O') oCount++;
            }
        }

        // Validate turn order
        if (oCount > xCount) {
            throw new IllegalArgumentException("Wrong turn order: O started");
        }
        if (xCount > oCount + 1) {
            throw new IllegalArgumentException("Wrong turn order: X went twice");
        }

        // Check rows and columns for wins
        for (int i = 0; i < 3; i++) {
            // Rows
            if (board[i].charAt(0) != ' ' &&
                board[i].charAt(0) == board[i].charAt(1) &&
                board[i].charAt(1) == board[i].charAt(2)) {
                if (board[i].charAt(0) == 'X') xWins = true;
                if (board[i].charAt(0) == 'O') oWins = true;
            }
            // Columns
            if (board[0].charAt(i) != ' ' &&
                board[0].charAt(i) == board[1].charAt(i) &&
                board[1].charAt(i) == board[2].charAt(i)) {
                if (board[0].charAt(i) == 'X') xWins = true;
                if (board[0].charAt(i) == 'O') oWins = true;
            }
        }

        // Check diagonals
        if (board[0].charAt(0) != ' ' &&
            board[0].charAt(0) == board[1].charAt(1) &&
            board[1].charAt(1) == board[2].charAt(2)) {
            if (board[0].charAt(0) == 'X') xWins = true;
            if (board[0].charAt(0) == 'O') oWins = true;
        }
        if (board[0].charAt(2) != ' ' &&
            board[0].charAt(2) == board[1].charAt(1) &&
            board[1].charAt(1) == board[2].charAt(0)) {
            if (board[0].charAt(2) == 'X') xWins = true;
            if (board[0].charAt(2) == 'O') oWins = true;
        }

        // Check for invalid states
        if (xWins && oWins) {
            throw new IllegalArgumentException("Impossible board: game should have ended after the game was won");
        }

        if (xWins) {
            // X just won, so XCount == OCount +1
            if (xCount != oCount + 1) {
                throw new IllegalArgumentException("Impossible board: game should have ended after the game was won");
            }
            return GameState.WIN;
        }

        if (oWins) {
            // O just won, so XCount == OCount
            if (xCount != oCount) {
                throw new IllegalArgumentException("Impossible board: game should have ended after the game was won");
            }
            return GameState.WIN;
        }

        // No winner, check for draw or ongoing
        boolean hasEmpty = false;
        for (String row : board) {
            if (row.contains(" ")) {
                hasEmpty = true;
                break;
            }
        }

        if (hasEmpty) {
            return GameState.ONGOING;
        } else {
            return GameState.DRAW;
        }
    }
}
