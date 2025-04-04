// Helper function to check if a player has won
const checkWin = (board, player) => {
  const n = 3;
  // Check rows
  for (let i = 0; i < n; i++) {
    if (board[i] === player.repeat(n)) {
      return true;
    }
  }
  // Check columns
  for (let j = 0; j < n; j++) {
    if (board[0][j] === player && board[1][j] === player && board[2][j] === player) {
      return true;
    }
  }
  // Check diagonals
  if (board[0][0] === player && board[1][1] === player && board[2][2] === player) {
    return true;
  }
  if (board[0][2] === player && board[1][1] === player && board[2][0] === player) {
    return true;
  }
  return false;
};

// Helper function to count marks
const countMarks = (board) => {
  let xCount = 0;
  let oCount = 0;
  for (const row of board) {
    for (const cell of row) {
      if (cell === 'X') {
        xCount++;
      } else if (cell === 'O') {
        oCount++;
      }
    }
  }
  return { xCount, oCount };
};

// Helper function to check if the board is full
const isFull = (board) => {
  for (const row of board) {
    if (row.includes(' ')) {
      return false;
    }
  }
  return true;
};

export const gamestate = (board) => {
  const { xCount, oCount } = countMarks(board);

  // Validate turn order
  if (oCount > xCount) {
    throw new Error('Wrong turn order: O started');
  }
  if (xCount > oCount + 1) {
    throw new Error('Wrong turn order: X went twice');
  }

  const xWins = checkWin(board, 'X');
  const oWins = checkWin(board, 'O');

  // Validate impossible scenarios
  if (xWins && oWins) {
     // This case should technically not happen if turn order is correct,
     // but handles edge cases like the test 'Invalid board: X won and O kept playing'
     // where both might appear to win simultaneously due to continued play.
     throw new Error('Impossible board: game should have ended after the game was won');
  }
  if (xWins && xCount === oCount) {
    // If X won, X must have made the last move, so xCount must be > oCount
    throw new Error('Impossible board: game should have ended after the game was won');
  }
  if (oWins && xCount > oCount) {
    // If O won, O must have made the last move, so xCount must be === oCount
    throw new Error('Impossible board: game should have ended after the game was won');
  }


  // Determine game state
  if (xWins || oWins) {
    return 'win';
  }

  if (isFull(board)) {
    return 'draw';
  }

  return 'ongoing';
};
