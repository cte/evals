//
// This is only a SKELETON file for the 'Connect' exercise. It's been provided as a
// convenience to get you started writing code faster.
//

export class Board {
  constructor(input) {
    this.size = input.length;
    this.board = input.map(row => row.trim().split(' '));
  }

  winner() {
    if (this.hasPath('X')) return 'X';
    if (this.hasPath('O')) return 'O';
    return '';
  }

  hasPath(player) {
    const visited = new Set();
    const stack = [];

    if (player === 'X') {
      // Start from all 'X' on the left edge
      for (let row = 0; row < this.size; row++) {
        if (this.board[row][0] === 'X') {
          stack.push([row, 0]);
          visited.add(`${row},0`);
        }
      }
    } else {
      // Start from all 'O' on the top edge
      for (let col = 0; col < this.board[0].length; col++) {
        if (this.board[0][col] === 'O') {
          stack.push([0, col]);
          visited.add(`0,${col}`);
        }
      }
    }

    const directions = [
      [-1, 0],  // up
      [-1, 1],  // up-right
      [0, -1],  // left
      [0, 1],   // right
      [1, -1],  // down-left
      [1, 0],   // down
    ];

    while (stack.length > 0) {
      const [r, c] = stack.pop();

      if (player === 'X' && c === this.board[r].length - 1) {
        return true;
      }
      if (player === 'O' && r === this.size - 1) {
        return true;
      }

      for (const [dr, dc] of directions) {
        const nr = r + dr;
        const nc = c + dc;
        if (
          nr >= 0 && nr < this.size &&
          nc >= 0 && nc < this.board[nr].length &&
          this.board[nr][nc] === player &&
          !visited.has(`${nr},${nc}`)
        ) {
          visited.add(`${nr},${nc}`);
          stack.push([nr, nc]);
        }
      }
    }

    return false;
  }
}
