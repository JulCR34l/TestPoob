import java.awt.Graphics;

/**
 * A class representing a customizable puzzle game where users can place, remove, or move tiles
 * to reach a target arrangement.
 * 
 * @author Julian David Castiblanco Real y  
 * @version (a version number or a date)
 */
public class Puzzle {
    private int height;
    private int width;
    private char[][] puzzle;
    private char[][] ending;
    private boolean isEditable;
    private boolean isVisible;
    private boolean[][] glued;
    private boolean[][] holes;
    private Rectangle[][] rectangles;
    private Rectangle[][] endingRectangles;
    private char[][] previousState;
    private boolean lastActionSuccessful;

    /**
     * Constructor to initialize a Puzzle with the given dimensions.
     *
     * @param h The height of the puzzle.
     * @param w The width of the puzzle.
     */
    public Puzzle(int h, int w) {
        this.height = h;
        this.width = w;
        this.puzzle = new char[h][w];
        this.ending = new char[h][w];
        this.isVisible = false;
        this.isEditable = true;
        this.glued = new boolean[h][w];
        this.holes = new boolean[h][w];
        this.rectangles = new Rectangle[h][w];
        this.endingRectangles = new Rectangle[h][w];
        this.previousState = new char[h][w];
        initializeRectangles();
        makeVisible();
    }

    /**
     * Constructor to initialize a Puzzle with the given ending state.
     *
     * @param ending A 2D char array representing the ending state of the puzzle.
     */
    public Puzzle(char[][] ending) {
        this(ending.length, ending[0].length);
        this.ending = ending;
        this.isEditable = false;
        fillPuzzleWithEnding();
    }

    /**
     * Constructor to initialize a Puzzle with both starting and ending states.
     *
     * @param starting A 2D char array representing the starting state of the puzzle.
     * @param ending A 2D char array representing the ending state of the puzzle.
     */
    public Puzzle(char[][] starting, char[][] ending) {
        this(starting.length, starting[0].length);
        if (starting.length != ending.length || starting[0].length != ending[0].length) {
            throw new IllegalArgumentException("Starting and ending dimensions must be the same.");
        }
        this.puzzle = starting;
        this.ending = ending;
        fillPuzzleWithStarting();
        fillPuzzleWithEnding();
    }

    /**
     * Adds a tile of a given color to the puzzle at a specified position.
     *
     * @param row The row position to add the tile.
     * @param column The column position to add the tile.
     * @param color The color of the tile.
     */
    public void addTile(int row, int column, String color) {
        if (!canEdit()) return;
        savePreviousState();

        if (isPositionValid(row, column) && puzzle[row][column] == '\0') {
            char charColor = convertColorToChar(color);
            if (charColor != '\0') {
                puzzle[row][column] = charColor;
                rectangles[row][column].changeColor(color);
                checkCompletion();
            }
        } else {
            System.out.println("Position already used or invalid");
        }
        lastActionSuccessful = didStateChange();
    }

    /**
     * Deletes a tile from the puzzle at a specified position.
     *
     * @param row The row position of the tile to delete.
     * @param column The column position of the tile to delete.
     */
    public void deleteTile(int row, int column) {
        if (!canEdit()) return;
        savePreviousState();

        if (isPositionValid(row, column) && puzzle[row][column] != '\0') {
            puzzle[row][column] = '\0';
            rectangles[row][column].changeColor("black");
            checkCompletion();
        } else {
            System.out.println("Invalid or empty position");
        }
        lastActionSuccessful = didStateChange();
    }

    /**
     * Relocates a tile from one position to another.
     *
     * @param from The source position of the tile.
     * @param to The destination position for the tile.
     */
    public void relocateTile(int[] from, int[] to) {
        if (!canEdit()) return;
        savePreviousState();

        if (isPositionValid(from[0], from[1]) && isPositionValid(to[0], to[1])
            && puzzle[from[0]][from[1]] != '\0' && puzzle[to[0]][to[1]] == '\0') {
            puzzle[to[0]][to[1]] = puzzle[from[0]][from[1]];
            puzzle[from[0]][from[1]] = '\0';
            checkCompletion();
        } else {
            System.out.println("Cannot relocate");
        }
        lastActionSuccessful = didStateChange();
    }

    /**
     * Adds glue to a tile at the specified position, making it immovable.
     *
     * @param row The row of the tile to glue.
     * @param column The column of the tile to glue.
     */
    public void addGlue(int row, int column) {
        savePreviousState();
        if (isPositionValid(row, column) && puzzle[row][column] != '\0' && !glued[row][column]) {
            glued[row][column] = true;
            rectangles[row][column].changeColor("gray");
        } else {
            System.out.println("Invalid position or already glued");
        }
        lastActionSuccessful = didStateChange();
    }

    /**
     * Removes glue from a tile at the specified position.
     *
     * @param row The row of the tile to unglue.
     * @param column The column of the tile to unglue.
     */
    public void deleteGlue(int row, int column) {
        savePreviousState();
        if (isPositionValid(row, column) && glued[row][column]) {
            glued[row][column] = false;
            rectangles[row][column].changeColor("black");
        } else {
            System.out.println("No glue at the specified position");
        }
        lastActionSuccessful = didStateChange();
    }

    /**
     * Creates a hole at the specified position in the puzzle.
     *
     * @param row The row of the tile to mark as a hole.
     * @param column The column of the tile to mark as a hole.
     */
    public void makeHole(int row, int column) {
        if (isPositionValid(row, column)) {
            holes[row][column] = true;
            rectangles[row][column].changeColor("white");
        } else {
            System.out.println("Invalid position for a hole.");
        }
    }

    /**
     * Tilts the entire puzzle in the specified direction.
     *
     * @param direction The direction to tilt the puzzle ('U' for up, 'D' for down, 'L' for left, 'R' for right).
     */
    public void tilt(char direction) {
        if (!canEdit()) return;
        savePreviousState();

        movePuzzle(direction);
        lastActionSuccessful = didStateChange();
    }
    
    /**
     * Tilts the puzzle in the optimal direction to bring it closer to the solution.
     */
    public void tilt() {
        if (!canEdit()) return;
        savePreviousState();

        char bestDirection = findBestDirection();
        tilt(bestDirection);

        lastActionSuccessful = didStateChange();
    }
    
    private void movePuzzle(char direction) {
        int dx = 0, dy = 0;
        switch (direction) {
            case 'U': dy = -1; break;
            case 'D': dy = 1; break;
            case 'L': dx = -1; break;
            case 'R': dx = 1; break;
        }

        for (int i = 0; i < (dx != 0 ? height : width); i++) {
            char[] newLine = new char[dx != 0 ? width : height];
            Rectangle[] newRectLine = new Rectangle[dx != 0 ? width : height];
            int index = (dx == 1 || dy == 1) ? (dx != 0 ? width : height) - 1 : 0;

            for (int j = (dx == 1 || dy == 1) ? (dx != 0 ? width : height) - 1 : 0;
                 (dx == 1 || dy == 1) ? j >= 0 : j < (dx != 0 ? width : height);
                 j += (dx == 1 || dy == 1) ? -1 : 1) {

                int x = dx != 0 ? i : j;
                int y = dx != 0 ? j : i;

                if (puzzle[x][y] != '\0' && !holes[x][y]) {
                    boolean passedHole = false;
                    for (int k = 1; k < (dx != 0 ? width : height); k++) {
                        int checkX = x + k * dx;
                        int checkY = y + k * dy;
                        if (checkX >= 0 && checkX < height && checkY >= 0 && checkY < width && holes[checkX][checkY]) {
                            passedHole = true;
                            break;
                        }
                    }

                    if (!passedHole) {
                        newLine[index] = puzzle[x][y];
                        newRectLine[index] = rectangles[x][y];
                        index += (dx == 1 || dy == 1) ? -1 : 1;
                    } else {
                        puzzle[x][y] = '\0';
                        rectangles[x][y].makeInvisible();
                    }
                }
            }

            for (int j = 0; j < (dx != 0 ? width : height); j++) {
                int x = dx != 0 ? i : j;
                int y = dx != 0 ? j : i;

                if ((dx == 1 || dy == 1) ? j > index : j < index) {
                    puzzle[x][y] = newLine[j];
                    rectangles[x][y] = newRectLine[j];
                    if (dx != 0) {
                        rectangles[x][y].setXPosition(y * 30 + 70);
                    } else {
                        rectangles[x][y].moveVertical((x - newRectLine[j].getYPosition() / 30) * 30);
                    }
                } else if (!holes[x][y]) {
                    puzzle[x][y] = '\0';
                    rectangles[x][y] = new Rectangle(30, 30, y * 30 + 70, x * 30 + 15, "black");
                    rectangles[x][y].makeVisible();
                }
            }
        }
    }
    
    private void movePuzzle(char[][] tempPuzzle, char direction) {
        int dx = 0, dy = 0;
        switch (direction) {
            case 'U': dy = -1; break;
            case 'D': dy = 1; break;
            case 'L': dx = -1; break;
            case 'R': dx = 1; break;
        }

        for (int i = 0; i < (dx != 0 ? height : width); i++) {
            char[] newLine = new char[dx != 0 ? width : height];
            int index = (dx == 1 || dy == 1) ? (dx != 0 ? width : height) - 1 : 0;

            for (int j = (dx == 1 || dy == 1) ? (dx != 0 ? width : height) - 1 : 0;
                 (dx == 1 || dy == 1) ? j >= 0 : j < (dx != 0 ? width : height);
                 j += (dx == 1 || dy == 1) ? -1 : 1) {

                int x = dx != 0 ? i : j;
                int y = dx != 0 ? j : i;

                if (tempPuzzle[x][y] != '\0' && !holes[x][y]) {
                    boolean passedHole = false;
                    for (int k = 1; k < (dx != 0 ? width : height); k++) {
                        int checkX = x + k * dx;
                        int checkY = y + k * dy;
                        if (checkX >= 0 && checkX < height && checkY >= 0 && checkY < width && holes[checkX][checkY]) {
                            passedHole = true;
                            break;
                        }
                    }

                    if (!passedHole) {
                        newLine[index] = tempPuzzle[x][y];
                        index += (dx == 1 || dy == 1) ? -1 : 1;
                    } else {
                        tempPuzzle[x][y] = '\0';
                    }
                }
            }

            for (int j = 0; j < (dx != 0 ? width : height); j++) {
                int x = dx != 0 ? i : j;
                int y = dx != 0 ? j : i;

                if ((dx == 1 || dy == 1) ? j > index : j < index) {
                    tempPuzzle[x][y] = newLine[j];
                } else if (!holes[x][y]) {
                    tempPuzzle[x][y] = '\0';
                }
            }
        }
    }

    /**
     * Calculates the distance between the current puzzle state and the reference board.
     *
     * @param tempPuzzle The puzzle state to compare.
     * @return The distance score.
     */
    private int calculateDistance(char[][] tempPuzzle) {
        int distance = 0;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (tempPuzzle[i][j] != ending[i][j]) {
                    distance++;
                }
            }
        }
        return distance;
    }
    
    /**
     * An example of a method - replace this comment with your own
     * 
     * @param  y   a sample parameter for a method
     * @return     the sum of x and y 
     */
    public void makeVisible() {
        if (!isVisible) {
            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    rectangles[i][j].makeVisible();
                    endingRectangles[i][j].makeVisible();
                }
            }
            isVisible = true;
        }
    }
    
    /**
     * Makes the puzzle invisible by hiding both the puzzle and ending rectangles.
     * This method works in conjunction with makeVisible() to toggle visibility.
     */
    public void makeInVisible() {
        if (isVisible) {
            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    rectangles[i][j].makeInvisible();
                    endingRectangles[i][j].makeInvisible();
                }
            }
            isVisible = false;
        }
    }
    
    public void finish() {
        System.out.println("¡Felicidades! Has completado el puzzle.");
        isEditable = false;
    }
    
    public boolean ok() {
        return lastActionSuccessful;
    }
    
    /**
     * Saves the current state of the puzzle to allow for undo or comparison later.
     */
    public void savePreviousState() {
        if (this.previousState == null) {
            System.out.println("Error, unable to perform this action.");
            this.previousState = new char[this.height][this.width];
        }

        for (int i = 0; i < this.height; i++) {
            for (int j = 0; j < this.width; j++) {
                this.previousState[i][j] = this.puzzle[i][j];
            }
        }
        System.out.println("Move completed");
    }

    /**
     * Compares the current state with the previous state to check if any changes were made.
     *
     * @return True if the puzzle state changed, false otherwise.
     */
    private boolean didStateChange() {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (puzzle[i][j] != previousState[i][j]) {
                    return true;
                }
            }
        }
        return false;
    }
    
    /**
     * Checks if the puzzle is complete by comparing the current state with the ending state.
     */
    private void checkCompletion() {
        if (isPuzzleComplete()) {
            finish();  // Call finish when the puzzle is completed
        }
    }
    
    /**
     * Checks if the current state matches the ending state, indicating the puzzle is complete.
     *
     * @return True if the puzzle is complete, false otherwise.
     */
    public boolean isPuzzleComplete() {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (puzzle[i][j] != ending[i][j]) {
                    return false;
                }
            }
        }
        return true;
    }
    
    /**
     * Checks if the puzzle is editable.
     *
     * @return True if the puzzle is editable, false otherwise.
     */
    private boolean canEdit() {
        if (!isEditable) {
            System.out.println("This board cannot be edited.");
        }
        return isEditable;
    }
    
    /**
     * Checks if a given position in the puzzle is valid.
     *
     * @param row The row of the position to check.
     * @param col The column of the position to check.
     * @return True if the position is valid, false otherwise.
     */
    private boolean isPositionValid(int row, int col) {
        return row >= 0 && row < height && col >= 0 && col < width;
    }
    
    /**
     * Fills the puzzle with the ending state by assigning colors to the ending rectangles.
     */
    private void fillPuzzleWithEnding() {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                endingRectangles[i][j].changeColor(convertCharToColor(ending[i][j]));
            }
        }
    }
    
    private String convertCharToColor(char colorChar) {
        switch (colorChar) {
            case 'r':
                return "red";
            case 'b':
                return "blue";
            case 'y':
                return "yellow";
            case 'g':
                return "green";
            default:
                return "black";
        }
    }
    
    private char convertColorToChar(String color)
    {
        switch(color.toLowerCase()){
            case "red":
                return 'r';
            case "blue":
                return 'b';
            case "yellow":
                return 'y';
            case "green":
                return 'g';
            default:
                return '\0';
        }
    }
    
    /**
     * Fills the puzzle with the starting state by assigning colors to the puzzle rectangles.
     */
    private void fillPuzzleWithStarting() {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                rectangles[i][j].changeColor(convertCharToColor(puzzle[i][j]));
            }
        }
        this.isEditable = true;
    }
    
    private void initializeRectangles() {
        int rectHeight = 30;
        int rectWidth = 30;
        int xOffset = 70;
        int yOffset = 15;
        int separation = width * rectWidth + 50;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                rectangles[i][j] = new Rectangle(
                    rectHeight, 
                    rectWidth, 
                    xOffset + j * rectWidth,
                    yOffset + i * rectHeight,
                    convertCharToColor(puzzle[i][j])
                );
                endingRectangles[i][j] = new Rectangle(
                    rectHeight, 
                    rectWidth, 
                    xOffset + j * rectWidth + separation,
                    yOffset + i * rectHeight,
                    convertCharToColor(ending[i][j])
                );
            }
        }
    }
    
    /**
     * Finds the best direction to tilt the puzzle to get closer to the reference board.
     *
     * @return The best direction to tilt ('U', 'D', 'L', 'R').
     */
    private char findBestDirection() {
        int bestScore = Integer.MAX_VALUE;
        char bestDirection = 'U';
        char[] directions = {'U', 'D', 'L', 'R'};

        for (char direction : directions) {
            int score = simulateTilt(direction);
            if (score < bestScore) {
                bestScore = score;
                bestDirection = direction;
            }
        }

        return bestDirection;
    }
    
    /**
     * Simulates a tilt in the given direction and returns a score based on how close it gets to the reference board.
     *
     * @param direction The direction to simulate the tilt ('U', 'D', 'L', 'R').
     * @return The score representing the distance to the reference board after the tilt.
     */
    private int simulateTilt(char direction) {
        char[][] tempPuzzle = new char[height][width];
        for (int i = 0; i < height; i++) {
            System.arraycopy(puzzle[i], 0, tempPuzzle[i], 0, width);
        }

        movePuzzle(tempPuzzle, direction);

        return calculateDistance(tempPuzzle);
    }
}

