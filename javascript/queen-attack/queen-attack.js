export class QueenAttack {
  constructor(args = {}) {
    // Define correct default positions based on tests
    const defaultWhite = [7, 3];
    const defaultBlack = [0, 3];

    // Assign positions, using defaults if not provided in args
    // Use nullish coalescing (??) to handle cases where white/black might be explicitly null/undefined in args
    const [whiteRow, whiteColumn] = args.white ?? defaultWhite;
    const [blackRow, blackColumn] = args.black ?? defaultBlack;

    // Helper function to validate position
    const isValidPosition = (row, col) =>
      typeof row === 'number' && typeof col === 'number' &&
      row >= 0 && row < 8 && col >= 0 && col < 8;

    // Validate the final positions (either provided or default)
    if (!isValidPosition(whiteRow, whiteColumn)) {
      throw new Error('Queen must be placed on the board');
    }
    if (!isValidPosition(blackRow, blackColumn)) {
      throw new Error('Queen must be placed on the board');
    }

    // Check if queens occupy the same space
    if (blackRow === whiteRow && blackColumn === whiteColumn) {
      throw new Error('Queens cannot share the same space');
    }

    // Store valid positions
    this.white = [whiteRow, whiteColumn];
    this.black = [blackRow, blackColumn];
  }

  toString() {
    // Create an 8x8 board initialized with underscores
    const board = Array(8)
      .fill(null)
      .map(() => Array(8).fill('_'));

    // Place the queens on the board
    const [whiteRow, whiteColumn] = this.white;
    const [blackRow, blackColumn] = this.black;

    board[whiteRow][whiteColumn] = 'W';
    board[blackRow][blackColumn] = 'B';

    // Format the board as a string
    return board.map(row => row.join(' ')).join('\n');
  }

  get canAttack() {
    const [whiteRow, whiteColumn] = this.white;
    const [blackRow, blackColumn] = this.black;

    // Check for attack on the same row
    if (whiteRow === blackRow) {
      return true;
    }

    // Check for attack on the same column
    if (whiteColumn === blackColumn) {
      return true;
    }

    // Check for attack on the same diagonal
    // The absolute difference in rows equals the absolute difference in columns
    if (Math.abs(whiteRow - blackRow) === Math.abs(whiteColumn - blackColumn)) {
      return true;
    }

    // If none of the above conditions are met, queens cannot attack each other
    return false;
  }
}
