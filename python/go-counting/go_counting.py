# Define constants for player representation
BLACK = 'B'
WHITE = 'W'
NONE = ''

class Board:
    """Count territories of each player in a Go game

    Args:
        board (list[str]): A two-dimensional Go board
    """

    def __init__(self, board):
        if not board:
            self.board = []
            self.height = 0
            self.width = 0
        else:
            self.board = board
            self.height = len(board)
            self.width = len(board[0])
        self._validate_board()

    def _validate_board(self):
        if self.height == 0 and self.width == 0:
            return # Empty board is valid
        if self.height == 0 or self.width == 0:
             raise ValueError("Invalid board dimensions.")
        expected_width = self.width
        for y, row in enumerate(self.board):
            if len(row) != expected_width:
                raise ValueError("Board must be rectangular.")
            for x, char in enumerate(row):
                if char not in ' BW':
                    raise ValueError("Invalid character on board.")

    def _is_valid(self, x, y):
        return 0 <= y < self.height and 0 <= x < self.width

    def _find_territory(self, x, y, visited):
        """Helper function to find a single territory using BFS."""
        if not self._is_valid(x, y) or self.board[y][x] != ' ' or (x, y) in visited:
            return None, set()

        q = [(x, y)]
        current_territory = set()
        bordering_players = set()
        visited.add((x, y))
        current_territory.add((x, y))
        # Removed touches_edge logic

        while q:
            cx, cy = q.pop(0)

            for dx, dy in [(0, 1), (0, -1), (1, 0), (-1, 0)]:
                nx, ny = cx + dx, cy + dy

                if not self._is_valid(nx, ny):
                    # If a neighbor is outside the board, it doesn't affect territory ownership directly
                    # based on the problem description (only bordering stones matter).
                    continue

                neighbor_char = self.board[ny][nx]
                neighbor_coord = (nx, ny)

                if neighbor_char == ' ':
                    if neighbor_coord not in visited:
                        visited.add(neighbor_coord)
                        current_territory.add(neighbor_coord)
                        q.append(neighbor_coord)
                else: # 'B' or 'W'
                    bordering_players.add(neighbor_char)

        # Determine owner based solely on bordering players
        if len(bordering_players) == 1:
            owner = bordering_players.pop()
        else: # 0 or >1 bordering players means neutral territory
            owner = NONE

        return owner, current_territory


    def territory(self, x, y):
        """Find the owner and the territories given a coordinate on
           the board

        Args:
            x (int): Column on the board
            y (int): Row on the board

        Returns:
            (str, set): A tuple, the first element being the owner
                        of that area.  One of "W", "B", "".  The
                        second being a set of coordinates, representing
                        the owner's territories.
        """
        if not self._is_valid(x, y):
            # Corrected error message
            raise ValueError("Invalid coordinate")

        char_at_coord = self.board[y][x]
        if char_at_coord != ' ':
            # Coordinate is a stone, not territory
            return NONE, set()

        # Use a local visited set for single territory check
        owner, territory_set = self._find_territory(x, y, set())
        # Ensure owner is one of the defined constants
        if owner not in [BLACK, WHITE]:
            owner = NONE
        return owner, territory_set


    def territories(self):
        """Find the owners and the territories of the whole board

        Args:
            none

        Returns:
            dict(str, set): A dictionary whose key being the owner
                        , i.e. "W", "B", "".  The value being a set
                        of coordinates owned by the owner.
        """
        result = {BLACK: set(), WHITE: set(), NONE: set()}
        visited = set() # Global visited set for the whole board scan

        for y in range(self.height):
            for x in range(self.width):
                if self.board[y][x] == ' ' and (x, y) not in visited:
                    owner, territory_set = self._find_territory(x, y, visited)
                    if owner is not None: # Check if _find_territory actually ran
                         # Ensure owner is one of the defined constants before updating result
                         if owner not in [BLACK, WHITE]:
                             owner = NONE
                         result[owner].update(territory_set)
                elif self.board[y][x] != ' ':
                    # Mark stones as visited too, so we don't try to start search from them
                    visited.add((x,y))


        return result
