//
// This is only a SKELETON file for the 'Rectangles' exercise. It's been provided as a
// convenience to get you started writing code faster.
//

export function count(grid) {
  if (!grid || grid.length === 0) return 0;

  const height = grid.length;
  const width = grid[0].length;
  let rectangles = 0;

  // Helper to check if all characters between (row, col1) and (row, col2) are '-' or '+'
  function isHorizontalEdge(row, col1, col2) {
    for (let c = col1 + 1; c < col2; c++) {
      const ch = grid[row][c];
      if (ch !== '-' && ch !== '+') return false;
    }
    return true;
  }

  // Helper to check if all characters between (row1, col) and (row2, col) are '|' or '+'
  function isVerticalEdge(col, row1, row2) {
    for (let r = row1 + 1; r < row2; r++) {
      const ch = grid[r][col];
      if (ch !== '|' && ch !== '+') return false;
    }
    return true;
  }

  // Iterate over all pairs of '+' in the same row (potential top edges)
  for (let r1 = 0; r1 < height; r1++) {
    for (let c1 = 0; c1 < width; c1++) {
      if (grid[r1][c1] !== '+') continue;
      for (let c2 = c1 + 1; c2 < width; c2++) {
        if (grid[r1][c2] !== '+') continue;
        if (!isHorizontalEdge(r1, c1, c2)) continue;

        // For each such pair, look for matching bottom corners
        for (let r2 = r1 + 1; r2 < height; r2++) {
          if (grid[r2][c1] !== '+' || grid[r2][c2] !== '+') continue;
          if (!isHorizontalEdge(r2, c1, c2)) continue;
          if (!isVerticalEdge(c1, r1, r2)) continue;
          if (!isVerticalEdge(c2, r1, r2)) continue;

          // Valid rectangle found
          rectangles++;
        }
      }
    }
  }

  return rectangles;
}
