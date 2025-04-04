class WordSearch {
  constructor(grid) {
    this.grid = grid;
    this.rows = grid.length;
    this.cols = grid[0] ? grid[0].length : 0;
  }

  find(words) {
    const results = {};
    words.forEach(word => {
      results[word] = undefined; // Initialize with undefined
      for (let r = 0; r < this.rows; r++) {
        for (let c = 0; c < this.cols; c++) {
          if (this.grid[r][c] === word[0]) {
            const foundLocation = this.searchFromCell(r, c, word);
            if (foundLocation) {
              results[word] = foundLocation;
              // Break inner loops once word is found
              r = this.rows; // Force outer loop exit
              break;        // Exit inner loop
            }
          }
        }
      }
    });
    return results;
  }

  searchFromCell(startRow, startCol, word) {
    const directions = [
      { dr: 0, dc: 1 },  // Right
      { dr: 0, dc: -1 }, // Left
      { dr: 1, dc: 0 },  // Down
      { dr: -1, dc: 0 }, // Up
      { dr: 1, dc: 1 },  // Down-Right
      { dr: 1, dc: -1 }, // Down-Left
      { dr: -1, dc: 1 }, // Up-Right
      { dr: -1, dc: -1 } // Up-Left
    ];

    for (const { dr, dc } of directions) {
      let currentRow = startRow;
      let currentCol = startCol;
      let match = true;
      let endRow = startRow;
      let endCol = startCol;

      for (let i = 1; i < word.length; i++) {
        currentRow += dr;
        currentCol += dc;

        if (
          currentRow < 0 || currentRow >= this.rows ||
          currentCol < 0 || currentCol >= this.cols ||
          this.grid[currentRow][currentCol] !== word[i]
        ) {
          match = false;
          break;
        }
        endRow = currentRow;
        endCol = currentCol;
      }

      if (match) {
        // Convert 0-based index to 1-based coordinates
        return {
          start: [startRow + 1, startCol + 1],
          end: [endRow + 1, endCol + 1]
        };
      }
    }

    return undefined; // Word not found starting at this cell in any direction
  }
}

export default WordSearch;
