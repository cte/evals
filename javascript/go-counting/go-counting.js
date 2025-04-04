// Define constants for players and empty space
const BLACK = 'B';
const WHITE = 'W';
const EMPTY = ' ';
const NONE = 'NONE'; // Represents territory owner when mixed or unenclosed

export class GoCounting {
  constructor(boardString) {
    this.board = boardString.map(row => row.split(''));
    this.height = this.board.length;
    this.width = this.height > 0 ? this.board[0].length : 0;
  }

  _isValid(x, y) {
    return x >= 0 && x < this.width && y >= 0 && y < this.height;
  }

  // Helper function using BFS to find a territory and its owner
  // visited: A Set passed in by the caller to track visited cells across multiple calls (for getTerritories)
  _findTerritory(startX, startY, visited) {
    // This function assumes the startX, startY is valid and EMPTY
    // It's called by getTerritory/getTerritories which should perform those checks.

    const territoryCoords = []; // Store as [x, y] pairs
    const q = [[startX, startY]];
    const currentVisited = new Set([`${startX},${startY}`]); // Visited for this specific BFS run
    let borderingColors = new Set(); // Track colors of bordering stones
    // let touchesEdge = false; // Removed: Edge touching doesn't directly determine ownership

    while (q.length > 0) {
      const [x, y] = q.shift();
      territoryCoords.push([x, y]); // Add coordinate pair
      if (visited) {
        visited.add(`${x},${y}`); // Mark as visited for the global search (getTerritories)
      }

      const neighbors = [
        [x + 1, y], [x - 1, y], [x, y + 1], [x, y - 1]
      ];

      for (const [nx, ny] of neighbors) {
        const coordStr = `${nx},${ny}`;

        if (this._isValid(nx, ny)) {
          const neighborContent = this.board[ny][nx];
          if (neighborContent === EMPTY) {
            if (!currentVisited.has(coordStr)) {
              currentVisited.add(coordStr);
              q.push([nx, ny]);
            }
          } else {
            // Found a bordering stone
            borderingColors.add(neighborContent);
          }
        } // else { /* Reaching the edge doesn't affect ownership directly */ }
        // The brace on the previous line was incorrectly closing the 'if' instead of the commented 'else'
      } // This brace closes the 'for' loop
    }

    // Determine owner based ONLY on bordering stone colors found
    let owner = NONE;
    const hasBlack = borderingColors.has(BLACK);
    const hasWhite = borderingColors.has(WHITE);

    if (hasBlack && !hasWhite) {
      owner = BLACK;
    } else if (hasWhite && !hasBlack) {
      owner = WHITE;
    } else { // Either mixed (hasBlack && hasWhite) or no bordering stones (empty set)
      owner = NONE;
    }
    // If borderingColors is empty and not touching edge (e.g., empty board), owner remains NONE

    // Map internal constants to external API strings
    const finalOwner = owner === BLACK ? 'BLACK' : owner === WHITE ? 'WHITE' : 'NONE';

    // Sort coordinates for consistent output
    territoryCoords.sort((a, b) => a[0] === b[0] ? a[1] - b[1] : a[0] - b[0]);

    return { owner: finalOwner, territory: territoryCoords };
  }


  getTerritory(x, y) {
    // Check for invalid coordinates first (as per tests)
    if (!this._isValid(x, y)) {
      return { error: 'Invalid coordinate' };
    }

    // Check if the coordinate is a stone (as per tests)
    if (this.board[y][x] !== EMPTY) {
        return { owner: 'NONE', territory: [] };
    }

    // Use _findTerritory without a global visited set
    const result = this._findTerritory(x, y, null);

    // Ensure the starting point is part of the found territory
    // (it should be, but as a safeguard)
    const found = result.territory.some(coord => coord[0] === x && coord[1] === y);
    if (!found) {
         // This case implies the starting point wasn't empty or something went wrong in BFS
         // Return based on initial checks
         return { owner: 'NONE', territory: [] };
    }


    return result;
  }

  getTerritories() {
    const territories = {
      territoryBlack: [],
      territoryWhite: [],
      territoryNone: [],
    };
    const visited = new Set(); // Keep track of visited empty cells across all searches

    for (let y = 0; y < this.height; y++) {
      for (let x = 0; x < this.width; x++) {
        if (this.board[y][x] === EMPTY && !visited.has(`${x},${y}`)) {
          // _findTerritory now returns sorted array directly
          const { owner, territory } = this._findTerritory(x, y, visited);

          if (territory.length > 0) { // Only add non-empty territories
              if (owner === 'BLACK') {
                territories.territoryBlack.push(...territory);
              } else if (owner === 'WHITE') {
                territories.territoryWhite.push(...territory);
              } else { // owner === 'NONE'
                territories.territoryNone.push(...territory);
              }
          }
        }
        // Also mark non-empty cells as 'visited' conceptually for this process
        else if (this.board[y][x] !== EMPTY) {
             visited.add(`${x},${y}`);
        }
      }
    }

     // Sort final territory arrays for consistent test results
     territories.territoryBlack.sort((a, b) => a[0] === b[0] ? a[1] - b[1] : a[0] - b[0]);
     territories.territoryWhite.sort((a, b) => a[0] === b[0] ? a[1] - b[1] : a[0] - b[0]);
     territories.territoryNone.sort((a, b) => a[0] === b[0] ? a[1] - b[1] : a[0] - b[0]);


    return territories;
  }
}
