//
// This is only a SKELETON file for the 'Go Counting' exercise. It's been provided as a
// convenience to get you started writing code faster.
//

export class GoCounting {
  constructor(board) {
    this.board = board.map(row => row.split(''));
    this.height = this.board.length;
    this.width = this.board[0].length;
  }

  isValidCoord(x, y) {
    return x >= 0 && x < this.width && y >= 0 && y < this.height;
  }

  getTerritory(x, y) {
    if (!this.isValidCoord(x, y)) {
      return { error: 'Invalid coordinate' };
    }

    const point = this.board[y][x];
    if (point === 'B' || point === 'W') {
      return { owner: 'NONE', territory: [] };
    }

    const visited = new Set();
    const territory = [];
    const borderColors = new Set();

    const stack = [[x, y]];
    while (stack.length > 0) {
      const [cx, cy] = stack.pop();
      const key = `${cx},${cy}`;
      if (visited.has(key)) continue;
      visited.add(key);

      const cell = this.board[cy][cx];
      if (cell === ' ') {
        territory.push([cx, cy]);
        // Explore neighbors
        const neighbors = [
          [cx - 1, cy],
          [cx + 1, cy],
          [cx, cy - 1],
          [cx, cy + 1],
        ];
        for (const [nx, ny] of neighbors) {
          if (!this.isValidCoord(nx, ny)) continue;
          const neighborCell = this.board[ny][nx];
          if (neighborCell === ' ') {
            stack.push([nx, ny]);
          } else if (neighborCell === 'B' || neighborCell === 'W') {
            borderColors.add(neighborCell);
          }
        }
      }
    }

    let owner = 'NONE';
    if (borderColors.size === 1) {
      const color = borderColors.values().next().value;
      owner = color === 'B' ? 'BLACK' : 'WHITE';
    }

    territory.sort((a, b) => {
      if (a[0] !== b[0]) {
        return a[0] - b[0]; // sort by x
      }
      return a[1] - b[1]; // then by y
    });

    return { owner, territory };
  }

  getTerritories() {
    const visited = new Set();
    const territoryBlack = [];
    const territoryWhite = [];
    const territoryNone = [];

    for (let y = 0; y < this.height; y++) {
      for (let x = 0; x < this.width; x++) {
        const key = `${x},${y}`;
        if (visited.has(key)) continue;
        if (this.board[y][x] !== ' ') continue;

        const result = this.getTerritory(x, y);
        for (const [tx, ty] of result.territory) {
          visited.add(`${tx},${ty}`);
        }

        if (result.owner === 'BLACK') {
          territoryBlack.push(...result.territory);
        } else if (result.owner === 'WHITE') {
          territoryWhite.push(...result.territory);
        } else {
          territoryNone.push(...result.territory);
        }
      }
    }

    return {
      territoryBlack,
      territoryWhite,
      territoryNone,
    };
  }
}
