//
// This is only a SKELETON file for the 'Queen Attack' exercise. It's been provided as a
// convenience to get you started writing code faster.
//

export class QueenAttack {
  constructor({
    black: [blackRow, blackColumn] = [0, 3],
    white: [whiteRow, whiteColumn] = [7, 3],
  } = {}) {
    // Validate positions are on the board
    const positions = [
      { row: blackRow, col: blackColumn },
      { row: whiteRow, col: whiteColumn },
    ];
    for (const { row, col } of positions) {
      if (
        typeof row !== 'number' ||
        typeof col !== 'number' ||
        row < 0 ||
        row > 7 ||
        col < 0 ||
        col > 7
      ) {
        throw new Error('Queen must be placed on the board');
      }
    }

    // Check if queens share the same space
    if (blackRow === whiteRow && blackColumn === whiteColumn) {
      throw new Error('Queens cannot share the same space');
    }

    this.black = [blackRow, blackColumn];
    this.white = [whiteRow, whiteColumn];
  }

  get canAttack() {
    const [whiteRow, whiteCol] = this.white;
    const [blackRow, blackCol] = this.black;

    // Same row
    if (whiteRow === blackRow) return true;
    // Same column
    if (whiteCol === blackCol) return true;
    // Same diagonal
    if (Math.abs(whiteRow - blackRow) === Math.abs(whiteCol - blackCol)) return true;

    return false;
  }

  toString() {
    const size = 8;
    const board = Array.from({ length: size }, () => Array(size).fill('_'));

    const [whiteRow, whiteCol] = this.white;
    const [blackRow, blackCol] = this.black;

    board[whiteRow][whiteCol] = 'W';
    board[blackRow][blackCol] = 'B';

    return board.map(row => row.join(' ')).join('\n');
  }
}
