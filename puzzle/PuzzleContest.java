package puzzle;

import shapes.*;
import java.util.List;
import javax.swing.JOptionPane;

/**
 * The PuzzleContest class is responsible for managing a puzzle game where users
 * can attempt to solve a tilting tiles puzzle. It uses a Puzzle instance to represent
 * the current state of the puzzle and a TiltingTilesSolver to determine if a solution
 * exists and to simulate the solution process.
 */
public class PuzzleContest {
    private Puzzle puzzle;
    private TiltingTilesSolver solver;
    char[][] boardClone; 
    Rectangle[][] boardCloneRectangle;

    /**
     * Attempts to solve the puzzle using the given starting and ending board configurations.
     */
    public boolean solve(char[][] startingBoard, char[][] endingBoard) {
        this.puzzle = new Puzzle(startingBoard, endingBoard);
        this.solver = new TiltingTilesSolver(startingBoard, endingBoard);
        
        // Check if the puzzle has a solution
        // String mensaje = (solver.canTransform()) ? "tiene solución" : "No tiene solución";
        // System.out.println(mensaje);        
        return solver.canTransform();
    }

    /**
     * Simulates the process of solving the puzzle by applying the necessary moves
     * to transform the starting board into the ending board.     */
    public void simulate(char[][] startingBoard, char[][] endingBoard) {
        this.puzzle = new Puzzle(startingBoard, endingBoard);
        TiltingTilesSolver solution = new TiltingTilesSolver(startingBoard, endingBoard);
        puzzle.makeVisible();

        if (solve(startingBoard, endingBoard)) {
            List<Character> movesPath = solution.getMovesPath();

            for (char d : movesPath) {
                puzzle.tilt(d);
                puzzle.makeVisible();
            }
            JOptionPane.showMessageDialog(null, "¡Simulación completada!");
        } else {
            JOptionPane.showMessageDialog(null, "No hay solución posible para alcanzar la configuración deseada.");
        }
    }
}

