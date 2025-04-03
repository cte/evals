//
// This is only a SKELETON file for the 'State of Tic Tac Toe' exercise. It's been provided as a
// convenience to get you started writing code faster.
//

export const gamestate = (board) => {
  const flat = board.join('');
  const xCount = [...flat].filter(c => c === 'X').length;
  const oCount = [...flat].filter(c => c === 'O').length;

  // Validate turn order
  if (oCount > xCount) {
    throw new Error('Wrong turn order: O started');
  }
  if (xCount > oCount + 1) {
    throw new Error('Wrong turn order: X went twice');
  }

  const lines = [
    // rows
    board[0], board[1], board[2],
    // columns
    board.map(row => row[0]).join(''),
    board.map(row => row[1]).join(''),
    board.map(row => row[2]).join(''),
    // diagonals
    [board[0][0], board[1][1], board[2][2]].join(''),
    [board[0][2], board[1][1], board[2][0]].join('')
  ];

  const xWins = lines.some(line => line === 'XXX');
  const oWins = lines.some(line => line === 'OOO');

  // Impossible if both players have winning lines
  if (xWins && oWins) {
    throw new Error('Impossible board: game should have ended after the game was won');
  }

  // Impossible if O won but X has more moves
  if (oWins && xCount !== oCount) {
    throw new Error('Impossible board: game should have ended after the game was won');
  }

  // Impossible if X won but O has equal moves
  if (xWins && xCount === oCount) {
    throw new Error('Impossible board: game should have ended after the game was won');
  }

  if (xWins || oWins) {
    return 'win';
  }

  if (flat.includes(' ')) {
    return 'ongoing';
  }

  return 'draw';
};
