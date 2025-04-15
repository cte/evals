const HEADER = 'Team                           | MP |  W |  D |  L |  P';

const defaultStats = () => ({ mp: 0, w: 0, d: 0, l: 0, p: 0 });

const updateStats = (stats, teamA, teamB, result) => {
  if (!stats[teamA]) {
    stats[teamA] = defaultStats();
  }
  if (!stats[teamB]) {
    stats[teamB] = defaultStats();
  }

  stats[teamA].mp += 1;
  stats[teamB].mp += 1;

  switch (result) {
    case 'win':
      stats[teamA].w += 1;
      stats[teamA].p += 3;
      stats[teamB].l += 1;
      break;
    case 'loss':
      stats[teamA].l += 1;
      stats[teamB].w += 1;
      stats[teamB].p += 3;
      break;
    case 'draw':
      stats[teamA].d += 1;
      stats[teamA].p += 1;
      stats[teamB].d += 1;
      stats[teamB].p += 1;
      break;
    default:
      // Should not happen with valid input
      break;
  }
};

const formatRow = (team, { mp, w, d, l, p }) => {
  const teamPadded = team.padEnd(30);
  const mpPadded = String(mp).padStart(2);
  const wPadded = String(w).padStart(2);
  const dPadded = String(d).padStart(2);
  const lPadded = String(l).padStart(2);
  const pPadded = String(p).padStart(2);
  return `${teamPadded} | ${mpPadded} | ${wPadded} | ${dPadded} | ${lPadded} | ${pPadded}`;
};

export const tournamentTally = (input) => {
  if (!input) {
    return HEADER;
  }

  const stats = {};
  const lines = input.split('\n').filter(line => line.trim() !== '');

  lines.forEach(line => {
    const [teamA, teamB, result] = line.split(';');
    if (teamA && teamB && result) {
      updateStats(stats, teamA, teamB, result);
    }
  });

  const sortedTeams = Object.entries(stats).sort(([, statsA], [, statsB]) => {
    if (statsB.p !== statsA.p) {
      return statsB.p - statsA.p; // Sort by points descending
    }
    // If points are equal, sort alphabetically by team name
    const teamA = Object.keys(stats).find(key => stats[key] === statsA);
    const teamB = Object.keys(stats).find(key => stats[key] === statsB);
    return teamA.localeCompare(teamB);
  });

  const rows = sortedTeams.map(([team, teamStats]) => formatRow(team, teamStats));

  return [HEADER, ...rows].join('\n');
};
