import java.util.*;

class PuzzleSolver {

    public boolean solve(char[][] starting, char[][] ending) {
        Puzzle puzzle= new Puzzle(starting,ending);
        puzzle.intelligentTilt();
        if (puzzle.isPuzzleComplete()){
            return true;
        }else{
            return false;
        }
    }
    
    public void simulate(char[][] starting, char[][] ending) {
        }
}

    
