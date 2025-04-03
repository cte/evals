
class ConnectGame:
    def __init__(self, board):
        # Parse the input string into a 2D list
        self.board = []
        for line in board.splitlines():
            # Remove leading spaces and split by spaces
            row = line.lstrip().split()
            if row:
                self.board.append(row)
        self.size = len(self.board)

    def get_winner(self):
        if self._has_path('X'):
            return 'X'
        if self._has_path('O'):
            return 'O'
        return ""

    def _has_path(self, player):
        from collections import deque

        visited = set()
        queue = deque()

        if player == 'X':
            # Start from all 'X' on the left edge (column 0) if exists
            for y, row in enumerate(self.board):
                if len(row) > 0 and row[0] == 'X':
                    queue.append((y, 0))
                    visited.add((y, 0))
            while queue:
                y, x = queue.popleft()
                # Check if reached right edge of this row
                if x == len(self.board[y]) - 1:
                    return True
                for ny, nx in self._neighbors(y, x):
                    if (ny, nx) not in visited:
                        if 0 <= ny < len(self.board) and 0 <= nx < len(self.board[ny]):
                            if self.board[ny][nx] == 'X':
                                visited.add((ny, nx))
                                queue.append((ny, nx))
        else:  # player == 'O'
            # Start from all 'O' on the top edge (row 0)
            if self.board:
                for x in range(len(self.board[0])):
                    if self.board[0][x] == 'O':
                        queue.append((0, x))
                        visited.add((0, x))
            while queue:
                y, x = queue.popleft()
                # Check if reached bottom edge
                if y == len(self.board) - 1:
                    return True
                for ny, nx in self._neighbors(y, x):
                    if (ny, nx) not in visited:
                        if 0 <= ny < len(self.board) and 0 <= nx < len(self.board[ny]):
                            if self.board[ny][nx] == 'O':
                                visited.add((ny, nx))
                                queue.append((ny, nx))
        return False

    def _neighbors(self, y, x):
        # Hex grid neighbors: 6 directions
        directions = [(-1, 0), (-1, 1), (0, -1), (0, 1), (1, -1), (1, 0)]
        for dy, dx in directions:
            ny, nx = y + dy, x + dx
            if 0 <= ny < len(self.board) and 0 <= nx < len(self.board[ny]):
                yield ny, nx
