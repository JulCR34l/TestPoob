package puzzle;

import java.util.*;

/**
 * The TiltingTilesSolver class is responsible for determining if one board configuration
 * can be transformed into another through a series of tilting moves. It uses a breadth-first
 * search algorithm to explore possible board states and tracks the moves taken to reach the goal.
 */
class TiltingTilesSolver {
    char[][] initialBoard;
    char[][] finalBoard;
    int rows, cols;
    List<Character> movesPath = new ArrayList<>();
    
    /**
     * Constructor for the TiltingTilesSolver class.
     */
    public TiltingTilesSolver(char[][] initialBoard, char[][] finalBoard) {
        this.initialBoard = initialBoard;
        this.finalBoard = finalBoard;
        this.rows = initialBoard.length;
        this.cols = initialBoard[0].length;
    }

    /**
     * The main method to determine if the initial board can be transformed into the final board.
     * Utilizes breadth-first search to explore all possible states.
    */
    public boolean canTransform() {
        Set<String> visited = new HashSet<>();
        Queue<Node> queue = new LinkedList<>();

        queue.add(new Node(initialBoard, new ArrayList<>()));
        visited.add(boardToString(initialBoard));

        while (!queue.isEmpty()) {
            Node currentNode = queue.poll();
            char[][] currentBoard = currentNode.board;

            // Verifica si el estado actual coincide con la disposición final
            if (boardsAreEqual(currentBoard, finalBoard)) {
                movesPath = currentNode.moves;
                return true;
            }

            // Realiza movimientos en las 4 direcciones y añade nuevos estados a la cola
            for (int i = 0; i < 4; i++) {
                char[][] nextBoard = getMove(currentBoard, i);
                String nextState = boardToString(nextBoard);
                if (!visited.contains(nextState)) {
                    visited.add(nextState);

                    List<Character> newMoves = new ArrayList<>(currentNode.moves);
                    newMoves.add(getDirection(i));
                    queue.add(new Node(nextBoard, newMoves));
                }
            }
        }

        return false;
    }

    /**
     * Gets the resulting board after performing a move in the specified direction.
     */
    private char[][] getMove(char[][] board, int direction) {
        switch (direction) {
            case 0: return tiltUp(board);
            case 1: return tiltDown(board);
            case 2: return tiltLeft(board);
            case 3: return tiltRight(board);
            default: return board;
        }
    }

    /**
     * Performs a left tilt on the board.
     */
    private char[][] tiltLeft(char[][] board) {
        char[][] newBoard = copyBoard(board);
        for (int r = 0; r < rows; r++) {
            int col = 0;
            for (int c = 0; c < cols; c++) {
                if (newBoard[r][c] != '\0') {
                    newBoard[r][col++] = newBoard[r][c];
                }
            }
            while (col < cols) newBoard[r][col++] = '\0';
        }
        return newBoard;
    }

    /**
     * Performs a right tilt on the board.
     */
    private char[][] tiltRight(char[][] board) {
        char[][] newBoard = copyBoard(board);
        for (int r = 0; r < rows; r++) {
            int col = cols - 1;
            for (int c = cols - 1; c >= 0; c--) {
                if (newBoard[r][c] != '\0') {
                    newBoard[r][col--] = newBoard[r][c];
                }
            }
            while (col >= 0) newBoard[r][col--] = '\0';
        }
        return newBoard;
    }

    /**
     * Performs an upward tilt on the board.
     */
    private char[][] tiltUp(char[][] board) {
        char[][] newBoard = copyBoard(board);
        for (int c = 0; c < cols; c++) {
            int row = 0;
            for (int r = 0; r < rows; r++) {
                if (newBoard[r][c] != '\0') {
                    newBoard[row++][c] = newBoard[r][c];
                }
            }
            while (row < rows) newBoard[row++][c] = '\0';
        }
        return newBoard;
    }

    /**
     * Performs a downward tilt on the board.
     */
    private char[][] tiltDown(char[][] board) {
        char[][] newBoard = copyBoard(board);
        for (int c = 0; c < cols; c++) {
            int row = rows - 1;
            for (int r = rows - 1; r >= 0; r--) {
                if (newBoard[r][c] != '\0') {
                    newBoard[row--][c] = newBoard[r][c];
                }
            }
            while (row >= 0) newBoard[row--][c] = '\0';
        }
        return newBoard;
    }

    /**
     * Compares two board configurations for equality.
     */
    private boolean boardsAreEqual(char[][] board1, char[][] board2) {
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (board1[r][c] != board2[r][c]) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Creates a deep copy of the given board.
     */
    private char[][] copyBoard(char[][] board) {
        char[][] newBoard = new char[rows][cols];
        for (int i = 0; i < rows; i++) {
            newBoard[i] = board[i].clone();
        }
        return newBoard;
    }

    /**
     * Converts the board to a string representation for easier comparison.
     */
    private String boardToString(char[][] board) {
        StringBuilder sb = new StringBuilder();
        for (char[] row : board) {
            for (char cell : row) {
                sb.append(cell == '\0' ? '.' : cell); // para facilitar la lectura
            }
        }
        return sb.toString();
    }

    /**
     * Gets the character representation of the direction based on an index.
     */
    private char getDirection(int dir) {
        return switch (dir) {
            case 0 -> 'U';
            case 1 -> 'D';
            case 2 -> 'L';
            case 3 -> 'R';
            default -> ' ';
        };
    }

    /**
     * Inner class representing a node in the search space.
     */
    private static class Node {
        char[][] board;
        List<Character> moves;
        
        /**
         * Constructor for the Node class.
         */
        Node(char[][] board, List<Character> moves) {
            this.board = board;
            this.moves = moves;
        }
    }

    /**
     * Retrieves the list of moves that led to the final board configuration.
     */
    public List<Character> getMovesPath() {
        return movesPath;
    }
}


