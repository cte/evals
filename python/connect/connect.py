class ConnectGame:
    def __init__(self, board_str):
        lines = board_str.strip().split('\n')
        self.board = [list(line.replace(' ', '')) for line in lines]
        self.height = len(self.board)
        if self.height == 0:
            self.width = 0
        else:
            self.width = len(self.board[0])
        self._winner = None # Cache the winner

    def _get_neighbors(self, r, c):
        """Returns valid neighbor coordinates (r, c) for a cell."""
        potential_neighbors = [
            (r, c - 1), (r, c + 1),  # Left, Right
            (r - 1, c), (r - 1, c + 1),  # Top-Left, Top-Right
            (r + 1, c - 1), (r + 1, c)   # Bottom-Left, Bottom-Right
        ]
        neighbors = []
        for nr, nc in potential_neighbors:
            if 0 <= nr < self.height and 0 <= nc < self.width:
                neighbors.append((nr, nc))
        return neighbors

    def _dfs(self, r, c, player, visited):
        """Depth-First Search to find a path for the player."""
        if (r, c) in visited or self.board[r][c] != player:
            return False

        visited.add((r, c))

        # Check win conditions
        if player == 'X' and c == self.width - 1:
            return True
        if player == 'O' and r == self.height - 1:
            return True

        for nr, nc in self._get_neighbors(r, c):
            if self._dfs(nr, nc, player, visited):
                return True

        return False

    def get_winner(self):
        if self._winner is not None:
            return self._winner

        if self.width == 0 or self.height == 0:
            self._winner = ""
            return self._winner

        # Check for 'X' win (left edge to right edge)
        visited_x = set()
        for r in range(self.height):
            if self.board[r][0] == 'X':
                if self._dfs(r, 0, 'X', visited_x):
                    self._winner = 'X'
                    return self._winner

        # Check for 'O' win (top edge to bottom edge)
        visited_o = set()
        for c in range(self.width):
             if self.board[0][c] == 'O':
                if self._dfs(0, c, 'O', visited_o):
                    self._winner = 'O'
                    return self._winner

        # No winner
        self._winner = ""
        return self._winner
