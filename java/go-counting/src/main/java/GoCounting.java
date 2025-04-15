import java.awt.Point;
import java.util.ArrayDeque;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;

class GoCounting {

    private final char[][] board;
    private final int width;
    private final int height;

    private record TerritoryInfo(Set<Point> points, Set<Player> borderingPlayers) {}

    GoCounting(String boardString) {
        if (boardString == null || boardString.isEmpty()) {
            this.board = new char[0][0];
            this.width = 0;
            this.height = 0;
            return;
        }
        String[] lines = boardString.split("\n");
        this.height = lines.length;
        this.width = (this.height > 0) ? lines[0].length() : 0;
        this.board = new char[height][width];
        for (int y = 0; y < height; y++) {
             if (lines[y].length() != this.width) {
                 // Assume tests provide rectangular boards
             }
            for (int x = 0; x < Math.min(this.width, lines[y].length()); x++) {
                this.board[y][x] = lines[y].charAt(x);
            }
        }
    }

    // Centralized boundary check with corrected message
    private void checkBounds(int x, int y) {
         if (x < 0 || x >= width || y < 0 || y >= height) {
            // Match the exact exception message expected by the tests
            throw new IllegalArgumentException("Invalid coordinate");
        }
    }

    Player getTerritoryOwner(int x, int y) {
        checkBounds(x, y);
        if (board[y][x] != ' ') {
             return Player.NONE;
        }
        TerritoryInfo info = exploreTerritory(x, y, new HashSet<>());
        if (info.borderingPlayers().size() == 1) {
            return info.borderingPlayers().iterator().next();
        }
        return Player.NONE;
    }

    // Modified getTerritory to return points regardless of owner, if start is empty
    Set<Point> getTerritory(int x, int y) {
        checkBounds(x, y);
         if (board[y][x] != ' ') {
             return Collections.emptySet();
        }
        // Explore the connected empty area starting from (x, y)
        TerritoryInfo info = exploreTerritory(x, y, new HashSet<>());
        // Return all the points found in the connected area, regardless of bordering players
        return info.points();
    }

    Map<Player, Set<Point>> getTerritories() {
        Map<Player, Set<Point>> territories = new HashMap<>();
        territories.put(Player.NONE, new HashSet<>());
        territories.put(Player.BLACK, new HashSet<>());
        territories.put(Player.WHITE, new HashSet<>());

        Set<Point> visitedOverall = new HashSet<>();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Point currentPoint = new Point(x, y);
                if (board[y][x] == ' ' && !visitedOverall.contains(currentPoint)) {
                    TerritoryInfo info = exploreTerritory(x, y, visitedOverall);
                    visitedOverall.addAll(info.points());

                    if (info.borderingPlayers().size() == 1) {
                        Player owner = info.borderingPlayers().iterator().next();
                        territories.get(owner).addAll(info.points());
                    } else {
                        // Assign to NONE if contested (>1) or unenclosed (0)
                        territories.get(Player.NONE).addAll(info.points());
                    }
                }
            }
        }
        return territories;
    }

    private TerritoryInfo exploreTerritory(int startX, int startY, Set<Point> visitedOverall) {
        Point startPoint = new Point(startX, startY);
         // Check bounds and if the start point is actually empty space
        if (startX < 0 || startX >= width || startY < 0 || startY >= height || board[startY][startX] != ' ') {
             return new TerritoryInfo(Collections.emptySet(), Collections.emptySet());
        }
        // Check if already visited (relevant for getTerritories)
        if (visitedOverall.contains(startPoint)) {
            // Return empty info, but don't modify borderingPlayers status implicitly
             return new TerritoryInfo(Collections.emptySet(), Collections.emptySet());
        }


        Set<Point> territoryPoints = new HashSet<>();
        Set<Player> borderingPlayers = new HashSet<>();
        Queue<Point> queue = new ArrayDeque<>();
        Set<Point> visitedLocal = new HashSet<>();

        queue.offer(startPoint);
        visitedLocal.add(startPoint);

        int[] dx = {0, 0, 1, -1};
        int[] dy = {1, -1, 0, 0};

        while (!queue.isEmpty()) {
            Point current = queue.poll();
            territoryPoints.add(current);

            for (int i = 0; i < 4; i++) {
                int nx = current.x + dx[i];
                int ny = current.y + dy[i];
                Point neighborPoint = new Point(nx, ny);

                if (nx >= 0 && nx < width && ny >= 0 && ny < height) {
                    char neighborChar = board[ny][nx];
                    if (neighborChar == ' ') {
                        // Check visitedLocal first, then visitedOverall (only relevant for getTerritories call path)
                        if (!visitedLocal.contains(neighborPoint) && !visitedOverall.contains(neighborPoint)) {
                            visitedLocal.add(neighborPoint);
                            queue.offer(neighborPoint);
                        }
                    } else if (neighborChar == 'B') {
                        borderingPlayers.add(Player.BLACK);
                    } else if (neighborChar == 'W') {
                        borderingPlayers.add(Player.WHITE);
                    }
                }
                 // No action needed for out-of-bounds neighbors
            }
        }
        return new TerritoryInfo(territoryPoints, borderingPlayers);
    }
}