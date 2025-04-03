BLACK = "B"
WHITE = "W"
NONE = ""

class Board:
    """Count territories of each player in a Go game

    Args:
        board (list[str]): A two-dimensional Go board
    """

    def __init__(self, board):
        self.board = board
        self.height = len(board)
        self.width = len(board[0]) if self.height > 0 else 0

    def territory(self, x, y):
        """Find the owner and the territories given a coordinate on
           the board
        """
        if x < 0 or x >= self.width or y < 0 or y >= self.height:
            raise ValueError("Invalid coordinate")

        if self.board[y][x] != ' ':
            return NONE, set()

        visited = set()
        border_colors = set()
        stack = [(x, y)]

        while stack:
            cx, cy = stack.pop()
            if (cx, cy) in visited:
                continue
            visited.add((cx, cy))

            for dx, dy in [(-1,0), (1,0), (0,-1), (0,1)]:
                nx, ny = cx + dx, cy + dy
                if not (0 <= nx < self.width and 0 <= ny < self.height):
                    continue
                neighbor = self.board[ny][nx]
                if neighbor == ' ' and (nx, ny) not in visited:
                    stack.append((nx, ny))
                elif neighbor in (BLACK, WHITE):
                    border_colors.add(neighbor)

        if len(border_colors) == 1:
            owner = border_colors.pop()
        else:
            owner = NONE

        return owner, visited

    def territories(self):
        """Find the owners and the territories of the whole board
        """
        result = {BLACK: set(), WHITE: set(), NONE: set()}
        seen = set()

        for y in range(self.height):
            for x in range(self.width):
                if self.board[y][x] == ' ' and (x, y) not in seen:
                    owner, territory = self.territory(x, y)
                    result[owner].update(territory)
                    seen.update(territory)

        return result
        pass
