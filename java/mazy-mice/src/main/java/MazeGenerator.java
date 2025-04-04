import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Stack;

public class MazeGenerator {

    private static final char WALL_VERTICAL = '\u2502'; // │
    private static final char WALL_HORIZONTAL = '\u2500'; // ─
    private static final char CORNER_TL = '\u250C'; // ┌
    private static final char CORNER_TR = '\u2510'; // ┐
    private static final char CORNER_BL = '\u2514'; // └
    private static final char CORNER_BR = '\u2518'; // ┘
    private static final char T_UP = '\u2534'; // ��
    private static final char T_DOWN = '\u252C'; // ┬
    private static final char T_LEFT = '\u2524'; // ��
    private static final char T_RIGHT = '\u251C'; // ��
    private static final char CROSS = '\u253C'; // ┼
    private static final char PASSAGE = ' ';
    private static final char ENTRANCE = '\u21E8'; // ���
    private static final char EXIT = '\u21E8'; // ⇨

    private int rows;
    private int columns;
    private int gridRows;
    private int gridCols;
    private char[][] grid;
    private boolean[][] visited;
    private Random random;

    private static class Cell {
        int r, c;

        Cell(int r, int c) {
            this.r = r;
            this.c = c;
        }
    }

    // Public method without seed
    public char[][] generatePerfectMaze(int rows, int columns) {
        // Use a default random seed if none is provided
        return generatePerfectMaze(rows, columns, new Random().nextInt());
    }

    // Public method with seed
    public char[][] generatePerfectMaze(int rows, int columns, int seed) {
        if (rows < 5 || rows > 100 || columns < 5 || columns > 100) {
            throw new IllegalArgumentException("Maze dimensions must be between 5 and 100 cells.");
        }

        this.rows = rows;
        this.columns = columns;
        this.gridRows = 2 * rows + 1;
        this.gridCols = 2 * columns + 1;
        this.grid = new char[gridRows][gridCols];
        this.visited = new boolean[rows][columns];
        this.random = new Random(seed);

        // Initialize grid with walls
        for (int r = 0; r < gridRows; r++) {
            for (int c = 0; c < gridCols; c++) {
                // Default to wall - we'll carve passages later
                 if (r % 2 == 0 && c % 2 == 0) {
                    grid[r][c] = '+'; // Placeholder for intersections
                 } else if (r % 2 == 0) {
                    grid[r][c] = WALL_HORIZONTAL;
                 } else if (c % 2 == 0) {
                    grid[r][c] = WALL_VERTICAL;
                 } else {
                    grid[r][c] = PASSAGE; // Cell center
                 }
            }
        }


        // Perform Randomized DFS
        Stack<Cell> stack = new Stack<>();
        int startR = random.nextInt(rows);
        int startC = random.nextInt(columns);
        visited[startR][startC] = true;
        stack.push(new Cell(startR, startC));
        int visitedCount = 1;

        while (visitedCount < rows * columns) {
            Cell current = stack.peek();
            List<Cell> neighbors = getUnvisitedNeighbors(current);

            if (!neighbors.isEmpty()) {
                Cell next = neighbors.get(random.nextInt(neighbors.size()));
                removeWall(current, next);
                visited[next.r][next.c] = true;
                stack.push(next);
                visitedCount++;
            } else {
                stack.pop();
            }
        }

        // Refine grid characters (intersections)
        refineGridCharacters();

        // Add entrance and exit
        addEntranceAndExit();

        return grid;
    }

    private List<Cell> getUnvisitedNeighbors(Cell cell) {
        List<Cell> neighbors = new ArrayList<>();
        int r = cell.r;
        int c = cell.c;

        int[] dr = {-1, 1, 0, 0}; // N, S, E, W
        int[] dc = {0, 0, 1, -1};

        for (int i = 0; i < 4; i++) {
            int nr = r + dr[i];
            int nc = c + dc[i];

            if (nr >= 0 && nr < rows && nc >= 0 && nc < columns && !visited[nr][nc]) {
                neighbors.add(new Cell(nr, nc));
            }
        }
        return neighbors;
    }

    private void removeWall(Cell current, Cell next) {
        int r1 = current.r;
        int c1 = current.c;
        int r2 = next.r;
        int c2 = next.c;

        // Grid coordinates for the wall to remove
        int wallR, wallC;

        if (r1 == r2) { // Horizontal move
            wallR = 2 * r1 + 1;
            wallC = 2 * Math.min(c1, c2) + 1 + 1; // Wall between cells
        } else { // Vertical move
            wallR = 2 * Math.min(r1, r2) + 1 + 1; // Wall between cells
            wallC = 2 * c1 + 1;
        }
        grid[wallR][wallC] = PASSAGE;
    }

    private void refineGridCharacters() {
        for (int r = 0; r < gridRows; r += 2) {
            for (int c = 0; c < gridCols; c += 2) {
                // Check passages around the intersection (r, c)
                boolean up = r > 0 && grid[r - 1][c] == PASSAGE;
                boolean down = r < gridRows - 1 && grid[r + 1][c] == PASSAGE;
                boolean left = c > 0 && grid[r][c - 1] == PASSAGE;
                boolean right = c < gridCols - 1 && grid[r][c + 1] == PASSAGE;

                // Set corners first
                if (r == 0 && c == 0) grid[r][c] = CORNER_TL;
                else if (r == 0 && c == gridCols - 1) grid[r][c] = CORNER_TR;
                else if (r == gridRows - 1 && c == 0) grid[r][c] = CORNER_BL;
                else if (r == gridRows - 1 && c == gridCols - 1) grid[r][c] = CORNER_BR;
                // Set edges based on connections
                else if (r == 0) grid[r][c] = (down ? T_DOWN : WALL_HORIZONTAL); // Top edge
                else if (r == gridRows - 1) grid[r][c] = (up ? T_UP : WALL_HORIZONTAL); // Bottom edge
                else if (c == 0) grid[r][c] = (right ? T_RIGHT : WALL_VERTICAL); // Left edge
                else if (c == gridCols - 1) grid[r][c] = (left ? T_LEFT : WALL_VERTICAL); // Right edge
                // Set internal intersections based on connections
                else {
                    if (up && down && left && right) grid[r][c] = CROSS;
                    else if (up && down && left) grid[r][c] = T_LEFT;    // No right connection
                    else if (up && down && right) grid[r][c] = T_RIGHT;   // No left connection
                    else if (up && left && right) grid[r][c] = T_DOWN;    // No down connection
                    else if (down && left && right) grid[r][c] = T_UP;      // No up connection
                    else if (up && down) grid[r][c] = WALL_VERTICAL;
                    else if (left && right) grid[r][c] = WALL_HORIZONTAL;
                    else if (down && right) grid[r][c] = CORNER_TL; // Connects down and right
                    else if (down && left) grid[r][c] = CORNER_TR;  // Connects down and left
                    else if (up && right) grid[r][c] = CORNER_BL;   // Connects up and right
                    else if (up && left) grid[r][c] = CORNER_BR;    // Connects up and left
                    // These cases should ideally not be hit for internal points in a perfect maze
                    // but handle potential dead ends terminating internally
                    else if (up || down) grid[r][c] = WALL_VERTICAL;
                    else if (left || right) grid[r][c] = WALL_HORIZONTAL;
                    else grid[r][c] = '?'; // Should not happen
                }
            }
        }
    }


    private void addEntranceAndExit() {
        // Find a random passage row for entrance (must be odd index)
        int entranceRowIndex;
        List<Integer> possibleEntryRows = new ArrayList<>();
        for(int r = 1; r < gridRows; r += 2) {
            possibleEntryRows.add(r);
        }
        entranceRowIndex = possibleEntryRows.get(random.nextInt(possibleEntryRows.size()));
        grid[entranceRowIndex][0] = ENTRANCE;


        // Find a random passage row for exit (must be odd index)
         int exitRowIndex;
         List<Integer> possibleExitRows = new ArrayList<>();
         for(int r = 1; r < gridRows; r += 2) {
             possibleExitRows.add(r);
         }
         exitRowIndex = possibleExitRows.get(random.nextInt(possibleExitRows.size()));
        grid[exitRowIndex][gridCols - 1] = EXIT;
    }
}
