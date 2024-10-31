package puzzle;
import shapes.*;
import java.util.HashMap;
import javax.swing.JOptionPane;
import java.util.*;
/**
 * Write a description of class Puzzle here.
 * 
 * @author Julián David Castiblanco Real 
 * @version (a version number or a date)
 */
public class Puzzle
{
    private int height;
    private int width;
    private char[][] starting;
    private char[][] ending;
    private char[][] startingClone;
    private char[] glued;
    private Rectangle[][] startingRectangles;
    private Rectangle[][] endingRectangles;
    private boolean isVisible;
    private boolean type;
    private boolean isEditable;
    public static final Map<String, String> COLORS;
    static {
        COLORS = new HashMap<>();
        COLORS.put("r", "red");
        COLORS.put("b", "blue");
        COLORS.put("g", "green");
        COLORS.put("y", "yellow");
    }
    private List<int[]> gluedGroup;
    
    /**
     * Creates a Puzzle object with specified dimensions, initializing the board and its visual rectangles.
     */
    public Puzzle(int h, int w)
    {
        this.height = h;
        this.width = w;
        this.isVisible = false;
        this.starting = new char[h][w];
        this.glued = new char[4];
        this.startingRectangles = new Rectangle[h][w];
        this.endingRectangles = new Rectangle[h][w];
        this.isVisible = false;
        this.type = false;
        this.isEditable = true;
        this.gluedGroup = new ArrayList<>();
        initializeRectangles(type);
    }
    
    /**
     * Creates a Puzzle object from a given final board layout, setting up the visual rectangles.
     */
    public Puzzle(char[][] ending)
    {
        this(ending.length, ending[0].length);
        this.ending = ending;
        this.type = true;
        initializeRectangles(type);
    }
    
    /**
     * Creates a Puzzle object from an initial and final board layout, setting up visual rectangles and ensuring both layouts have matching dimensions.
     */
    public Puzzle(char[][] starting, char[][] ending) {
        this(ending);
        this.starting = starting;
        this.startingClone = starting;
        this.isEditable = true;
        if(starting.length != ending.length && starting[0].length != ending[0].length){
            JOptionPane.showMessageDialog(null, "Las dimensiones de starting y ending tienen que ser iguales.");
        }
        initializeRectangles(type);     
    }
    
    /**
     * Adds a tile to a specific position on the board with the specified color, updating visibility if the board is visible.
     */
    public void addTile(int row, int column, String color)
    {   
        if (isPositionValid(row, column) && starting[row][column] == '\0'){
            if(color != null){
                char newColor = color.charAt(0);
                starting[row][column] = newColor;
                if(isVisible){
                    makeVisible();
                }
            }
        }
    }
    
    /**
     * Deletes a tile from a specific position on the board, updating its color and visibility if the board is visible.  
     */
    public void deleteTile(int row, int column){
        if (isPositionValid(row, column) && starting[row][column] != '\0'){
            starting[row][column] = '\0';
            if(isVisible){
                startingRectangles[row][column].changeColor("black");
                makeVisible();
            }
        }
    }
    
    /**
     * Moves a tile from one position to another on the board, removing it from the origin position and adding it at the destination position. 
     */
    public void relocateTile(int[] from, int[] to){
        if (starting[to[0]][to[1]] != 'H' && starting[from[0]][from[1]] != 'H') {
            String colorToChange = (String.valueOf(starting[from[0]][from[1]]));
            deleteTile(from[0], from[1]);
            addTile(to[0], to[1], colorToChange);
        } else if(starting[to[0]][to[1]] == 'H'){
            deleteTile(from[0], from[1]);
        }
    }
    
    /**
     * Adds glue to a specific position on the board, forming a group of glued tiles that includes adjacent valid positions.  
     */
    public void addGlue(int row, int column) {
        int[][] directions = {{1,0},{0,1},{-1,0},{0,-1}};
    
        if (isPositionValid(row, column) && starting[row][column] != '\0') {
            List<int[]> group = new ArrayList<>();
            group.add(new int[]{row, column}); // Agregar la posición inicial al grupo
    
            // Explorar adyacentes y añadir las posiciones válidas al grupo pegado
            for (int[] dir : directions) {
                int newRow = row + dir[0];
                int newCol = column + dir[1];
                if (isPositionValid(newRow, newCol) && starting[newRow][newCol] != '\0') {
                    group.add(new int[]{newRow, newCol});
                }
            }
    
            gluedGroup = group; // Almacenar el grupo pegado
        }
    }
    
    /**
     * Removes glue from a specific position on the board (currently not implemented).
     */
    public void deleteGlue(int row, int column){
        
    }
    
    /**
     * Creates a hole at a specific position on the board, visually represented in white.
     */
    public void makeHole(int row, int column){
        if(isPositionValid(row,column) && starting[row][column] == '\0'){
            starting[row][column] = 'H';
            startingRectangles[row][column].changeColor("white");
        }
    }
    
    /**
     * Tilts the board in a specific direction (up, down, left, or right), moving tiles in that direction and applying group movement if tiles are glued.
     */
    public void tilt(char direction) {
        int row = starting.length;
        int column = starting[0].length;
        
        switch (direction) {
            case 'U':
                moveGame(column, row, column, -1, 0, 'Y');
                break;
            case 'D':
                moveGame(column, row, column, 1, 0, 'Y');
                break;
            case 'R':
                moveGame(row, row, column, 0, 1, 'X');
                break;
            case 'L':
                moveGame(row, row, column, 0, -1, 'X');
                break;
            default:
                JOptionPane.showMessageDialog(null, "Valor inválido");
        }
    }
    
    /**
     * Solves the board layout from the initial to final state through a sequence of moves, applying transformations step-by-step.
     */
    public void tilt() {
        TiltingTilesSolver solver = new TiltingTilesSolver(starting, ending);
    
        if (solver.canTransform()) {
            List<Character> movesPath = solver.getMovesPath();
            for(char c: movesPath){
                tilt(c);
                movesPath.remove(0);
                break;
            }
        } else {
            JOptionPane.showMessageDialog(null, "No es posible alcanzar la disposición final.");
        }
    }
    
    /**
     * Swaps the initial and final board layouts, along with their visual rectangle positions, while maintaining visibility.
     */
    public void exchange() {
        makeInvisible();
        // Intercambiar las referencias de starting y ending
        char[][] temp = starting;
        starting = ending;
        ending = temp;
        // También intercambiar las referencias de los rectángulos visuales
        Rectangle[][] tempRectangles = startingRectangles;
        startingRectangles = endingRectangles;
        endingRectangles = tempRectangles;
    
        for (int i = 0; i < startingRectangles.length; i++) {
            for (int j = 0; j < startingRectangles[0].length; j++) {
                if (startingRectangles[i][j] != null && endingRectangles[i][j] != null) {
                    // Obtener la posición del rectangulo que se puede editar
                    int tempX = startingRectangles[i][j].getX();
                    int tempY = startingRectangles[i][j].getY();
                    
                    // Intercambiar las posiciones de los rectángulos que se esta ditando al de referencia 
                    startingRectangles[i][j].setPosition(endingRectangles[i][j].getX(), endingRectangles[i][j].getY());
                    endingRectangles[i][j].setPosition(tempX, tempY);
                }
            }
        }
        
        makeVisible();
    }
    
    /**
     * Makes the board and its tiles visible, updating the colors of visual rectangles according to the current tile state. 
     */
    public void makeVisible()
    {
        for(int i = 0; i < height; i++){
            for(int j = 0; j < width; j++){
                confirmRectangle(starting, startingRectangles, i, j);
                confirmRectangle(ending, endingRectangles, i, j);
            }
        }
        isVisible = true;
    }
    
    /**
     * Hides the board, removing the visibility of all visual rectangles on both the initial and final boards.  
     */
    public void makeInvisible()
    {
        for(int i = 0; i < height; i++){
            for(int j = 0; j < width; j++){
                startingRectangles[i][j].makeInvisible();
                if (endingRectangles[i][j] != null) {
                    endingRectangles[i][j].makeInvisible();
                }
            }
        }
        isVisible = false;
    }
    
    /**
     * Ends the simulation, making the board invisible and clearing its references, which closes the program.
     */
    public void finish() {
        makeInvisible();
    
        starting = null;
        ending = null;
        startingRectangles = null;
        endingRectangles = null;
    
        JOptionPane.showMessageDialog(null, "El simulador ha terminado.");
        System.exit(0);
    }
    
    /**
     * Moves tiles on the board based on specified parameters, checking for glued tiles and applying movement accordingly. 
     */
    private void moveGame(int orientation, int row, int column, int yWay, int xWay, char axis) {
        int count = 0;
        while (count != orientation) {
            for (int i = 0; i < row; i++) {
                for (int j = 0; j < column; j++) {
                    if (axis == 'Y' 
                        && (starting[i][j] == '\0'
                        || starting[i][j] == 'H')
                        && i - yWay >= 0
                        && i - yWay < row
                        && starting[i - yWay][j] != '\0') {
                        int[] from = {i - yWay, j};
                        int[] to = {i, j};
                        relocateTile(from, to);
                    } else if (axis == 'X' 
                        && (starting[i][j] == '\0'
                        || starting[i][j] == 'H')
                        && j - xWay >= 0
                        && j - xWay < column
                        && starting[i][j - xWay] != '\0') {
                        int[] from = {i, j - xWay};
                        int[] to = {i, j};
                        relocateTile(from, to);
                    }
                }
            }
            count++;
        }
    }
    
    /**
     * Confirms and updates the color of a visual rectangle based on the current state of the board tile at the specified position.
     */
    private void confirmRectangle(char[][] matriz, Rectangle[][] matrizRectangles, int i, int j){
        if(matrizRectangles[i][j] != null){
            String color = COLORS.get(String.valueOf(matriz[i][j]));
            if (color != null){
                matrizRectangles[i][j].changeColor(color);
            }
            matrizRectangles[i][j].makeVisible();
        }
    }
    
    private void correctMatriz(char[][] matriz){
        for(int i = 0; i < height; i++){
            for(int j = 0; j < width; j++){
                if(matriz[i][j] == '.'){
                    matriz[i][j] = '\0';
                    }
            }
        }
    }
    
    private void correctCharMatriz(char[][] matriz){
        for(int i = 0; i < height; i++){
            for(int j = 0; j < width; j++){
                if(matriz[i][j] == '\0'){
                    matriz[i][j] = '.';
                    }
            }
        }
    }

    /**
     * Initializes the visual rectangles on the board according to its type, setting colors and positions for tiles based on their states.
     */
    private void initializeRectangles(boolean type){ //true = start/ending     false = starting
        int rectHeight = 40;
        int rectWidth = 40;
        int xOffset = 160;
        int yOffset = 360;
        int spacing = 3;
        if(!type){
            creatingRectangles(starting, startingRectangles, rectHeight, rectWidth, xOffset, yOffset, spacing);
        } else {
            creatingRectangles(ending, endingRectangles, rectHeight, rectWidth, xOffset * 3, yOffset, spacing);
            creatingRectangles(starting, startingRectangles, rectHeight, rectWidth, xOffset, yOffset, spacing);
        }
    }
    
    public void creatingRectangles(char[][] matriz, Rectangle[][] rectangles,int rectHeight, int rectWidth, int xOffset, int yOffset, int spacing){
        for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    int xPosition = xOffset + j * (rectWidth + spacing); // Calcular la posición X
                    int yPosition = yOffset + i * (rectHeight + spacing); // Calcular la posición Y
                    correctMatriz(matriz);
                    if(matriz[i][j] !=  '.'){
                        rectangles[i][j]=new Rectangle(rectHeight,rectWidth,xPosition,yPosition,
                        String.valueOf(matriz[i][j]));
                    }
                }
            }
    }
    
    /**
     * Verifies whether a position is valid within the boundaries of the board dimensions. 
     */
    private boolean isPositionValid(int row, int column){
        return  row >=0 && row < width && column >= 0 && column < height;
    }
    
    private boolean canMoveGroup(List<int[]> group, int yOffset, int xOffset) {
        for (int[] pos : group) {
            int newRow = pos[0] + yOffset;
            int newCol = pos[1] + xOffset;
    
            // Verificar si la posición nueva es válida y no está ocupada o es un hueco
            if (!isPositionValid(newRow, newCol) || starting[newRow][newCol] != '\0') {
                return false; // Si alguno no puede moverse, retornar false
            }
        }
        return true; // Todos pueden moverse
    }
    
    private boolean canMoveGluedGroup(char direction) {
        int yOffset = 0, xOffset = 0;
    
        switch (direction) {
            case 'U': yOffset = -1; break;
            case 'D': yOffset = 1; break;
            case 'L': xOffset = -1; break;
            case 'R': xOffset = 1; break;
            default: System.out.println("Dirección inválida"); return false;
        }
    
        // Verificar si todas las posiciones del grupo pueden moverse en la dirección dada
        for (int[] pos : gluedGroup) {
            int newRow = pos[0] + yOffset;
            int newCol = pos[1] + xOffset;
            if (!isPositionValid(newRow, newCol) || starting[newRow][newCol] != '\0') {
                return false; // Al menos una posición del grupo no puede moverse
            }
        }
        return true; // Todas las posiciones del grupo pueden moverse
    }
    
}
