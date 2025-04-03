import java.util.*;

public class MazeGenerator {

    private static final char EMPTY = ' ';
    private static final char WALL_HORIZONTAL = '─';
    private static final char WALL_VERTICAL = '│';
    private static final char WALL_TOP_LEFT = '┌';
    private static final char WALL_TOP_RIGHT = '┐';
    private static final char WALL_BOTTOM_LEFT = '└';
    private static final char WALL_BOTTOM_RIGHT = '┘';
    private static final char WALL_T_UP = '┴';
    private static final char WALL_T_DOWN = '┬';
    private static final char WALL_T_LEFT = '┤';
    private static final char WALL_T_RIGHT = '├';
    private static final char WALL_CROSS = '┼';

    public char[][] generatePerfectMaze(int rows, int columns) {
        return generatePerfectMaze(rows, columns, new Random());
    }

    public char[][] generatePerfectMaze(int rows, int columns, int seed) {
        return generatePerfectMaze(rows, columns, new Random(seed));
    }

    private char[][] generatePerfectMaze(int rows, int columns, Random rand) {
        if (rows < 5 || rows > 100 || columns < 5 || columns > 100) {
            throw new IllegalArgumentException("Rows and columns must be between 5 and 100");
        }

        int height = rows * 2 + 1;
        int width = columns * 2 + 1;
        char[][] maze = new char[height][width];

        // Initialize maze with walls
        for (int y = 0; y < height; y++) {
            Arrays.fill(maze[y], WALL_HORIZONTAL);
        }

        // Carve initial grid
        for (int y = 1; y < height; y += 2) {
            for (int x = 1; x < width; x += 2) {
                maze[y][x] = EMPTY;
            }
        }

        boolean[][] visited = new boolean[rows][columns];
        dfs(0, 0, visited, maze, rand);

        // Fill left and right borders with walls
        for (int y = 0; y < height; y++) {
            maze[y][0] = WALL_VERTICAL;
            maze[y][width - 1] = WALL_VERTICAL;
        }

        // Create single entrance on the left border
        int entranceY = height / 2;
        maze[entranceY][0] = '⇨';
        maze[entranceY][1] = EMPTY; // ensure connected

        // Create single exit on the right border
        int exitY = height / 2;
        maze[exitY][width - 1] = '⇨';
        maze[exitY][width - 2] = EMPTY; // ensure connected

        // Replace wall placeholders with box-drawing characters
        updateWalls(maze);

        return maze;
    }

    private void dfs(int row, int col, boolean[][] visited, char[][] maze, Random rand) {
        visited[row][col] = true;
        int[] dirs = {0, 1, 2, 3}; // N, E, S, W
        shuffleArray(dirs, rand);

        int height = visited.length;
        int width = visited[0].length;

        for (int dir : dirs) {
            int newRow = row, newCol = col;
            switch (dir) {
                case 0: newRow = row - 1; break; // North
                case 1: newCol = col + 1; break; // East
                case 2: newRow = row + 1; break; // South
                case 3: newCol = col - 1; break; // West
            }

            if (newRow < 0 || newRow >= height || newCol < 0 || newCol >= width || visited[newRow][newCol]) {
                continue;
            }

            // Remove wall between cells
            int wallY = row * 2 + 1 + (newRow - row);
            int wallX = col * 2 + 1 + (newCol - col);
            maze[wallY][wallX] = EMPTY;

            dfs(newRow, newCol, visited, maze, rand);
        }
    }

    private void shuffleArray(int[] array, Random rand) {
        for (int i = array.length - 1; i > 0; i--) {
            int index = rand.nextInt(i + 1);
            int temp = array[index];
            array[index] = array[i];
            array[i] = temp;
        }
    }

    private void updateWalls(char[][] maze) {
        int height = maze.length;
        int width = maze[0].length;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (maze[y][x] == EMPTY || maze[y][x] == '⇨') continue;

                boolean up = y > 0 && maze[y - 1][x] != EMPTY && maze[y - 1][x] != '⇨';
                boolean down = y < height - 1 && maze[y + 1][x] != EMPTY && maze[y + 1][x] != '⇨';
                boolean left = x > 0 && maze[y][x - 1] != EMPTY && maze[y][x - 1] != '⇨';
                boolean right = x < width - 1 && maze[y][x + 1] != EMPTY && maze[y][x + 1] != '⇨';

                if (up && down && left && right) maze[y][x] = WALL_CROSS;
                else if (up && down && left) maze[y][x] = WALL_T_RIGHT;
                else if (up && down && right) maze[y][x] = WALL_T_LEFT;
                else if (up && left && right) maze[y][x] = WALL_T_UP;
                else if (down && left && right) maze[y][x] = WALL_T_DOWN;
                else if (up && down) maze[y][x] = WALL_VERTICAL;
                else if (left && right) maze[y][x] = WALL_HORIZONTAL;
                else if (down && right) maze[y][x] = WALL_TOP_LEFT;
                else if (down && left) maze[y][x] = WALL_TOP_RIGHT;
                else if (up && right) maze[y][x] = WALL_BOTTOM_LEFT;
                else if (up && left) maze[y][x] = WALL_BOTTOM_RIGHT;
                else maze[y][x] = WALL_HORIZONTAL;
            }
        }
    }
}
