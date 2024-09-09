/**
 * Write a description of class Puzzle here.
 * 
 * @author Julian David Castiblanco Real y Nicole Dayan Calderón Arévalo 
 * @version (a version number or a date)
 */
public class Puzzle
{
    private char[][] puzzle;
    private char[][] ending;
    private int height;
    private int width;
    private boolean isVisible;
    private Rectangle[][] rectangles;
    
    public Puzzle(int h, int w)
    {
        this.height = h;
        this.width = w;
        this.puzzle = new char[h][w];
        this.isVisible = false;
        this.rectangles = new Rectangle[h][w];
        initializeRectangles();
    }
    
    public Puzzle(char[][] ending)
    {
        this.ending = ending;
        this.height = ending.length;
        this.width = ending[0].length;
        this.puzzle = new char[height][width];
        this.isVisible = false;
        initializeRectangles();
    }
    
    public Puzzle(char[][] starting, char[][] ending)
    {
        this.ending = ending;
        this.height = starting.length;
        this.width = starting[0].length;
        this.puzzle = new char[height][width];
        this.isVisible = false;
        initializeRectangles();
    }
    
    private void initializeRectangles() {
        int rectHeight = 30; // Tamaño de cada rectángulo
        int rectWidth = 40;
        int xOffset = 70; // Posición inicial
        int yOffset = 15;

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                rectangles[i][j] = new Rectangle(rectHeight, rectWidth, 
                                                 xOffset + j * rectWidth, 
                                                 yOffset + i * rectHeight, 
                                                 "black"); // Color inicial negro
            }
        }
    }
    
    public void addtile(int row, int column, String color)
    {
        if (isPositionValid(row,column) && puzzle[row][column] == '\0')
        {   
            char charColor = convertColorToChar(color);
            if (charColor != '\0'){
                puzzle[row][column] = charColor; 
                rectangles[row][column].changeColor(color);
            }
        } else
        {
            System.out.println("Posición ya usada o no valida");   
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
    
    public void deleteTile(int row, int column){
        if (isPositionValid(row, column) && puzzle[row][column] != '\0'){
            puzzle[row][column] = '\0';
            rectangles[row][column].changeColor("black");
        } else 
        {
            System.out.println("Posición no valida o vacía");  
        }
    }
    
    public void relocateTile(int[] from, int[] to)
    {
        if (isPositionValid(from[0], from[1]) && isPositionValid(to[0], to[1]) && puzzle[from[0]][from[1]] != '\0' && 
        puzzle[to[2]][to[1]] == '\0'){
            puzzle[to[0]][to[1]] = puzzle[from[0]][from[1]];
            puzzle[from[0]][from[1]] = '\0';
        } else 
        {
         System.out.println("Cannot relocate");    
        }
    }
    
    public void addGlue(int row, int column)
    {
           
    }
    
    public void tilt(char direction)
    {   
        switch (direction) {
            case 'U': // Up - Arriba
                moveUpPuzzle();
                break;
            case 'D': // Down - Abajo
                moveDownPuzzle();
                break;
            case 'L': // Left - Izquierda
                moveLeftPuzzle();
                break;
            case 'R': // Right - Derecha
                moveRightPuzzle();
                break;
            default:
                System.out.println("Dirección no válida");
        }
    }
    
    private void moveUpPuzzle() {
    for (int col = 0; col < width; col++) {
        char[] newCol = new char[height];
        Rectangle[] newRectCol = new Rectangle[height];
        int index = 0;

        for (int row = 0; row < height; row++) {
            if (puzzle[row][col] != '\0') {
                newCol[index] = puzzle[row][col];
                newRectCol[index] = rectangles[row][col];
                index++;
            }
        }

        // Rellenar los espacios restantes con '\0' y mover los rectángulos
        for (int row = 0; row < height; row++) {
            if (row < index) {
                puzzle[row][col] = newCol[row];
                rectangles[row][col] = newRectCol[row];
                rectangles[row][col].moveVertical((row - newRectCol[row].getYPosition() / 30) * 30); // Ajustar gráfico
            } else {
                puzzle[row][col] = '\0';
                rectangles[row][col] = new Rectangle(30, 40, col * 40 + 70, row * 30 + 15, "black");
                rectangles[row][col].makeVisible();
            }
        }
    }
    }
    
    private void moveDownPuzzle()
    {
        for (int col = 0; col < width; col++) {
        char[] newCol = new char[height];
        Rectangle[] newRectCol = new Rectangle[height];
        int index = height - 1;

        for (int row = height - 1; row >= 0; row--) {
            if (puzzle[row][col] != '\0') {
                newCol[index] = puzzle[row][col];
                newRectCol[index] = rectangles[row][col];
                index--;
            }
        }

        // Rellenar los espacios restantes con '\0' y mover los rectángulos
        for (int row = height - 1; row >= 0; row--) {
            if (row > index) {
                puzzle[row][col] = newCol[row];
                rectangles[row][col] = newRectCol[row];
                rectangles[row][col].moveVertical((row - newRectCol[row].getYPosition() / 30) * 30); // Ajustar gráfico
            } else {
                puzzle[row][col] = '\0';
                rectangles[row][col] = new Rectangle(30, 40, col * 40 + 70, row * 30 + 15, "black");
                rectangles[row][col].makeVisible();
            }
        }
    }
    }
    
    private void moveRightPuzzle()
    {
    for (int row = 0; row < height; row++) {
        char[] newRow = new char[width];
        Rectangle[] newRectRow = new Rectangle[width];
        int index = width - 1;

        for (int col = width - 1; col >= 0; col--) {
            if (puzzle[row][col] != '\0') {
                newRow[index] = puzzle[row][col];
                newRectRow[index] = rectangles[row][col];
                index--;
            }
        }

        for (int col = width - 1; col >= 0; col--) {
            if (col > index) {
                puzzle[row][col] = newRow[col];
                rectangles[row][col] = newRectRow[col];
                rectangles[row][col].setXPosition(col * 40 + 70); // Ajustar directamente la posición
            } else {
                puzzle[row][col] = '\0';
                rectangles[row][col] = new Rectangle(30, 40, col * 40 + 70, row * 30 + 15, "black");
                rectangles[row][col].makeVisible();
            }
        }
    }
    }

    
    private void moveLeftPuzzle()
    {
    for (int row = 0; row < height; row++) {
        char[] newRow = new char[width];
        Rectangle[] newRectRow = new Rectangle[width];
        int index = 0;

        for (int col = 0; col < width; col++) {
            if (puzzle[row][col] != '\0') {
                newRow[index] = puzzle[row][col];
                newRectRow[index] = rectangles[row][col];
                index++;
            }
        }

        for (int col = 0; col < width; col++) {
            if (col < index) {
                puzzle[row][col] = newRow[col];
                rectangles[row][col] = newRectRow[col];
                rectangles[row][col].setXPosition(col * 40 + 70); // Ajustar directamente la posición
            } else {
                puzzle[row][col] = '\0';
                rectangles[row][col] = new Rectangle(30, 40, col * 40 + 70, row * 30 + 15, "black");
                rectangles[row][col].makeVisible();
            }
        }
    }
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
                    rectangles[i][j].makeVisible(); // Hace visibles todos los rectángulos
                }
            }
            isVisible = true;
        }
    }
    
    private boolean isPositionValid(int row, int column)
    {
        return row >= 0 && row < height && column >= 0 && column < width;    
    }
    
}
