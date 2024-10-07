

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * The test class PuzzleTest.
 *
 * @author  (your name)
 * @version (a version number or a date)
 */
public class PuzzleTest {

    public static void main(String[] args) {
        testAddTile();
        testMakeHole();
        testTilt();
    }
    
    @BeforeEach
    private static void testAddTile() {
        char[][] starting = {
            {'\0', '\0', '\0'},
            {'\0', '\0', '\0'},
            {'\0', '\0', '\0'}
        };
        char[][] ending = {
            {'r', '\0', 'b'},
            {'g', 'y', '\0'},
            {'\0', 's', 'w'}
        };
        Puzzle puzzle = new Puzzle(starting, ending);

        System.out.println("Test Add Tile:");
        puzzle.addTile(0, 0, "red");
        System.out.println(puzzle.puzzle[0][0] == 'r' ? "Pass" : "Fail");
    }

    @AfterEach
    public static void testMakeHole() {
        char[][] starting = {
            {'r', '\0', 'b'},
            {'g', 'y', '\0'},
            {'\0', 's', 'w'}
        };
        char[][] ending = {
            {'r', '\0', 'b'},
            {'g', 'y', '\0'},
            {'\0', 's', 'w'}
        };
        Puzzle puzzle = new Puzzle(starting, ending);

        System.out.println("Test Make Hole:");
        puzzle.makeHole(1, 1);
        System.out.println(puzzle.holes[1][1] ? "Pass" : "Fail");
    }

    private static void testTilt() {
        char[][] starting = {
            {'r', 'b', '\0'},
            {'\0', 'y', 'g'},
            {'s', 'w', '\0'}
        };
        char[][] ending = {
            {'r', 'b', '\0'},
            {'\0', 'y', 'g'},
            {'s', 'w', '\0'}
        };
        Puzzle puzzle = new Puzzle(starting, ending);

        System.out.println("Test Tilt:");
        puzzle.tilt('R'); // Example tilt to the right
        // Add assertions to verify the puzzle state after tilt
        // For simplicity, assuming the puzzle logic makes the board empty after tilt
        System.out.println(puzzle.puzzle[0][2] == 'b' ? "Pass" : "Fail");
    }

}
