//
// This is only a SKELETON file for the 'Tournament' exercise. It's been provided as a
// convenience to get you started writing code faster.
//

export const tournamentTally = (input) => {
  const teams = {};

  if (input.trim() !== '') {
    const matches = input.split('\n');
    for (const match of matches) {
      const [team1, team2, result] = match.split(';');

      if (!teams[team1]) {
        teams[team1] = { MP: 0, W: 0, D: 0, L: 0, P: 0 };
      }
      if (!teams[team2]) {
        teams[team2] = { MP: 0, W: 0, D: 0, L: 0, P: 0 };
      }

      teams[team1].MP += 1;
      teams[team2].MP += 1;

      if (result === 'win') {
        teams[team1].W += 1;
        teams[team1].P += 3;
        teams[team2].L += 1;
      } else if (result === 'loss') {
        teams[team2].W += 1;
        teams[team2].P += 3;
        teams[team1].L += 1;
      } else if (result === 'draw') {
        teams[team1].D += 1;
        teams[team1].P += 1;
        teams[team2].D += 1;
        teams[team2].P += 1;
      }
    }
  }

  const header = 'Team                           | MP |  W |  D |  L |  P';
  const sortedTeams = Object.keys(teams).sort((a, b) => {
    if (teams[b].P !== teams[a].P) {
      return teams[b].P - teams[a].P;
    }
    return a.localeCompare(b);
  });

  const lines = sortedTeams.map((team) => {
    const stats = teams[team];
    return `${team.padEnd(31)}|  ${stats.MP} |  ${stats.W} |  ${stats.D} |  ${stats.L} | ${String(stats.P).padStart(2)}`;
  });

  return [header, ...lines].join('\n');
};
