import java.awt.Point;
import java.util.Map;
import java.util.Set;

class GoCounting {

    private final char[][] grid;
    private final int rows;
    private final int cols;

    GoCounting(String board) {
        String[] lines = board.split("\n");
        rows = lines.length;
        cols = lines[0].length();
        grid = new char[rows][cols];
        for (int y = 0; y < rows; y++) {
            grid[y] = lines[y].toCharArray();
        }
    }

    private void validateCoordinate(int x, int y) {
        if (x < 0 || y < 0 || y >= rows || x >= cols) {
            throw new IllegalArgumentException("Invalid coordinate");
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (char[] row : grid) {
            sb.append(new String(row)).append("\n");
        }
        return sb.toString();
    }

    Player getTerritoryOwner(int x, int y) {
        validateCoordinate(x, y);
        if (grid[y][x] == 'B' || grid[y][x] == 'W') {
            return Player.NONE;
        }
        TerritoryResult result = floodFill(x, y, new boolean[rows][cols]);
        return result.owner;
    }

    Set<Point> getTerritory(int x, int y) {
        validateCoordinate(x, y);
        if (grid[y][x] == 'B' || grid[y][x] == 'W') {
            return Set.of();
        }
        TerritoryResult result = floodFill(x, y, new boolean[rows][cols]);
        return result.points;
    }

    Map<Player, Set<Point>> getTerritories() {
        Map<Player, Set<Point>> territories = new java.util.HashMap<>();
        territories.put(Player.BLACK, new java.util.HashSet<>());
        territories.put(Player.WHITE, new java.util.HashSet<>());
        territories.put(Player.NONE, new java.util.HashSet<>());

        boolean[][] globalVisited = new boolean[rows][cols];

        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {
                if (!globalVisited[y][x] && grid[y][x] == ' ') {
                    boolean[][] localVisited = new boolean[rows][cols];
                    TerritoryResult result = floodFill(x, y, localVisited);
                    // mark all points as visited globally
                    for (Point p : result.points) {
                        globalVisited[p.y][p.x] = true;
                    }
                    territories.get(result.owner).addAll(result.points);
                }
            }
        }
        return territories;
    }

    private TerritoryResult floodFill(int startX, int startY, boolean[][] visited) {
        java.util.Set<Point> territory = new java.util.HashSet<>();
        java.util.Set<Character> borderingColors = new java.util.HashSet<>();
        java.util.Queue<Point> queue = new java.util.LinkedList<>();
        queue.add(new Point(startX, startY));

        while (!queue.isEmpty()) {
            Point p = queue.poll();
            int x = p.x;
            int y = p.y;

            if (x < 0 || y < 0 || y >= rows || x >= cols) continue;
            if (visited[y][x]) continue;
            visited[y][x] = true;

            char cell = grid[y][x];
            if (cell == ' ') {
                territory.add(p);
                queue.add(new Point(x + 1, y));
                queue.add(new Point(x - 1, y));
                queue.add(new Point(x, y + 1));
                queue.add(new Point(x, y - 1));
            } else if (cell == 'B' || cell == 'W') {
                borderingColors.add(cell);
            }
        }

        Player owner;
        if (borderingColors.size() == 1) {
            char c = borderingColors.iterator().next();
            owner = (c == 'B') ? Player.BLACK : Player.WHITE;
        } else {
            owner = Player.NONE;
        }

        return new TerritoryResult(owner, territory);
    }

    private static class TerritoryResult {
        final Player owner;
        final Set<Point> points;

        TerritoryResult(Player owner, Set<Point> points) {
            this.owner = owner;
            this.points = points;
        }
    }
}