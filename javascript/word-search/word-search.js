//
// This is only a SKELETON file for the 'Word Search' exercise. It's been provided as a
// convenience to get you started writing code faster.
//

class WordSearch {
  constructor(grid) {
    this.grid = grid;
    this.rows = grid.length;
    this.cols = grid[0].length;
  }

  find(words) {
    const results = {};
    for (const word of words) {
      results[word] = this.searchWord(word);
    }
    return results;
  }

  searchWord(word) {
    const directions = [
      [0, 1],   // right
      [0, -1],  // left
      [1, 0],   // down
      [-1, 0],  // up
      [1, 1],   // down-right
      [-1, -1], // up-left
      [1, -1],  // down-left
      [-1, 1],  // up-right
    ];

    for (let row = 0; row < this.rows; row++) {
      for (let col = 0; col < this.cols; col++) {
        for (const [dr, dc] of directions) {
          if (this.checkDirection(row, col, dr, dc, word)) {
            const start = [row + 1, col + 1];
            const end = [
              row + dr * (word.length - 1) + 1,
              col + dc * (word.length - 1) + 1,
            ];
            return { start, end };
          }
        }
      }
    }
    return undefined;
  }

  checkDirection(row, col, dr, dc, word) {
    for (let i = 0; i < word.length; i++) {
      const r = row + dr * i;
      const c = col + dc * i;
      if (
        r < 0 ||
        r >= this.rows ||
        c < 0 ||
        c >= this.cols ||
        this.grid[r][c] !== word[i]
      ) {
        return false;
      }
    }
    return true;
  }
}

export default WordSearch;
