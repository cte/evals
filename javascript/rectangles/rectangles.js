// rectangles.js
export function count(lines) {
  if (!lines || lines.length === 0 || lines[0].length === 0) {
    return 0;
  }

  const rows = lines.length;
  const cols = lines[0].length;
  let rectangleCount = 0;

  for (let r1 = 0; r1 < rows; r1++) {
    for (let c1 = 0; c1 < cols; c1++) {
      // Check if top-left is a corner
      if (lines[r1][c1] !== '+') {
        continue;
      }

      // Iterate through potential bottom-right corners
      for (let r2 = r1 + 1; r2 < rows; r2++) {
        for (let c2 = c1 + 1; c2 < cols; c2++) {
          // Check if potential corners are '+'
          if (
            lines[r1][c2] !== '+' || // Top-right
            lines[r2][c1] !== '+' || // Bottom-left
            lines[r2][c2] !== '+'    // Bottom-right
          ) {
            continue;
          }

          // Check top edge
          let validTop = true;
          for (let c = c1 + 1; c < c2; c++) {
            if (lines[r1][c] !== '-' && lines[r1][c] !== '+') {
              validTop = false;
              break;
            }
          }
          if (!validTop) continue;

          // Check bottom edge
          let validBottom = true;
          for (let c = c1 + 1; c < c2; c++) {
            if (lines[r2][c] !== '-' && lines[r2][c] !== '+') {
              validBottom = false;
              break;
            }
          }
          if (!validBottom) continue;

          // Check left edge
          let validLeft = true;
          for (let r = r1 + 1; r < r2; r++) {
            if (lines[r][c1] !== '|' && lines[r][c1] !== '+') {
              validLeft = false;
              break;
            }
          }
          if (!validLeft) continue;

          // Check right edge
          let validRight = true;
          for (let r = r1 + 1; r < r2; r++) {
            if (lines[r][c2] !== '|' && lines[r][c2] !== '+') {
              validRight = false;
              break;
            }
          }
          if (!validRight) continue;

          // If all edges are valid, increment count
          rectangleCount++;
        }
      }
    }
  }

  return rectangleCount;
}
