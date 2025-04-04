export class Board {
  constructor(boardLines) {
    // Remove spaces and store the board
    this.board = boardLines.map(line => line.replace(/\s/g, ''));
    this.rows = this.board.length;
    if (this.rows === 0) {
      this.cols = 0;
    } else {
      // Use the length of the first row as the reference column count
      this.cols = this.board[0].length;
    }
  }

  winner() {
    if (this.rows === 0 || this.cols === 0) {
      return ''; // No winner on an empty board
    }

    // Check for 'X' win (left to right)
    const xStarts = [];
    for (let r = 0; r < this.rows; r++) {
      // Ensure the cell exists before checking its value
      if (this.getCell(r, 0) === 'X') {
        xStarts.push([r, 0]);
      }
    }
    if (this.checkConnection(xStarts, 'X', (r, c) => c === this.cols - 1)) {
      return 'X';
    }

    // Check for 'O' win (top to bottom)
    const oStarts = [];
    for (let c = 0; c < this.cols; c++) {
       // Need to check the actual cell content for the first row
       if (this.getCell(0, c) === 'O') {
         oStarts.push([0, c]);
       }
    }
     if (this.checkConnection(oStarts, 'O', (r, c) => r === this.rows - 1)) {
      return 'O';
    }


    return ''; // No winner
  }

  getCell(r, c) {
    // Check row bounds first
    if (r < 0 || r >= this.rows) {
        return null;
    }
    // Then check column bounds for that specific row
    if (c < 0 || c >= this.board[r].length) {
      return null; // Out of bounds for this row
    }
    return this.board[r][c];
  }

  getNeighbors(r, c) {
    const neighbors = [
      [r, c - 1],     // left
      [r, c + 1],     // right
      [r - 1, c],     // top left
      [r - 1, c + 1], // top right
      [r + 1, c],     // bottom right
      [r + 1, c - 1], // bottom left
    ];
    // Filter neighbors that are within the board dimensions *and* exist
    return neighbors.filter(([nr, nc]) => this.getCell(nr, nc) !== null);
  }

  checkConnection(startNodes, player, isTarget) {
    const visited = new Set();
    const queue = []; // Initialize queue

    // Add valid start nodes to queue and visited set
    startNodes.forEach(node => {
        const [r, c] = node;
        // Double check the starting cell is actually the player's piece
        if (this.getCell(r, c) === player) {
            const nodeStr = node.toString();
            if (!visited.has(nodeStr)) {
                visited.add(nodeStr);
                queue.push(node);
            }
        }
    });


    while (queue.length > 0) {
      const [r, c] = queue.shift();

      // Check if the current node is on the target edge
      if (isTarget(r, c)) {
        return true; // Reached the target edge
      }

      const neighbors = this.getNeighbors(r, c);
      for (const [nr, nc] of neighbors) {
        const neighborStr = [nr, nc].toString();
        // Check if neighbor is valid, belongs to the player, and not visited
        if (!visited.has(neighborStr) && this.getCell(nr, nc) === player) {
          visited.add(neighborStr);
          queue.push([nr, nc]);
        }
      }
    }

    return false; // Did not reach the target edge
  }
}
