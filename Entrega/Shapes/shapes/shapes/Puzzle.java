import java.awt.Graphics;

/**
 * Write a description of class Puzzle here.
 * 
 * @author Julian David Castiblanco Real y NICOLE DAYAN CALDERÓN ARÉVALO
 * @version (a version number or a date)
 */
public class Puzzle
{
  
    private int height;
    private int width;
    public char[][] puzzle;
    private char[][] ending;
    private boolean isEditable;
    private boolean isVisible;
    private boolean[][] glued;
    public boolean[][] holes;
    private Rectangle[][] rectangles;
    private Rectangle[][] endingRectangles;
    private char[][] previousState; // Variable para almacenar el estado anterior del tablero
    private boolean lastActionSuccessful;
    private String[][] originalColors;
    
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
        this.previousState = new char[h][w]; // Inicializar el estado anterior con el tamaño adecuado
        this.originalColors = new String[h][w];
        initializeRectangles();
        makeVisible();
    }
    
    public Puzzle(char[][] ending) {
        this.height = ending.length;
        this.width = ending[0].length;
        this.puzzle = new char[height][width];
        this.ending = ending;
        this.isEditable = false;
        this.isVisible = false;
        this.glued = new boolean[height][width];
        this.holes = new boolean[height][width];
        this.rectangles = new Rectangle[height][width];
        this.endingRectangles = new Rectangle[height][width];  // Inicializar matriz de ending
        this.originalColors = new String[height][width];
        initializeRectangles();
        fillPuzzleWithEnding();  // Llenar el tablero con el estado final
        makeVisible();  // Hacer visible el tablero
    }
    
    public Puzzle(char[][] starting, char[][] ending) {
        // Asegurar que ambos tableros tengan las mismas dimensiones
        if (starting.length != ending.length || starting[0].length != ending[0].length) {
            throw new IllegalArgumentException("Las dimensiones de starting y ending deben ser iguales.");
        }
        this.height = starting.length;
        this.width = starting[0].length;
        this.puzzle = starting;  // Este es el tablero editable
        this.ending = ending;  // Estado final del tablero
        this.isEditable = true;  // El tablero inicial es editable
        this.isVisible = false;
        this.glued = new boolean[height][width];
        this.holes = new boolean[height][width];
        this.rectangles = new Rectangle[height][width];
        this.endingRectangles = new Rectangle[height][width];  // Inicializar matriz para ending
        this.originalColors = new String[height][width];
        initializeRectangles();
        fillPuzzleWithStarting();  // Llenar el tablero con el estado inicial editable
        fillPuzzleWithEnding();    // Llenar también el tablero de referencia (ending)
        makeVisible();
    }
    
    public void addTile(int row, int column, String color) {
    if (!canEdit()) {
        System.out.println("No se puede editar el tablero.");
        return;
    }
    if (isPositionValid(row, column)) {
        if ((puzzle[row][column] == '\0')) { // Verifica que la posición está vacía
            puzzle[row][column] = convertColorToChar(color); // Asigna el nuevo valor a la ficha
            rectangles[row][column].changeColor(color); // Actualiza visualmente (cambia color, por ejemplo)
            checkCompletion();
        } else {
            System.out.println("La posición ya contiene una ficha.");
        }
    } else {
        System.out.println("Posición no válida.");
    }
    lastActionSuccessful = didStateChange(); // Verificar si la acción fue exitosa
}
    
    public void deleteTile(int row, int column) {
    if (!canEdit()) {
        System.out.println("No se puede editar el tablero.");
        return;
    }
    savePreviousState(); // Guardar el estado antes de la acción
    if (isPositionValid(row, column)) {
        if (puzzle[row][column] != '\0') {
            puzzle[row][column] = '\0';
            rectangles[row][column].changeColor("black");
            checkCompletion();
        } else {
            System.out.println("La posición ya está vacía.");
        }
    } else {
        System.out.println("Posición no válida.");
    }
    lastActionSuccessful = didStateChange(); // Verificar si la acción fue exitosa
}
   
public void relocateTile(int[] from, int[] to) {
    if (!canEdit()) return;
    savePreviousState(); // Guardar el estado antes de la acción
    // Verificar si las posiciones son válidas y si la ficha se puede mover
    if (isPositionValid(from[0], from[1]) && isPositionValid(to[0], to[1])
        && puzzle[from[0]][from[1]] != '\0' && puzzle[to[0]][to[1]] == '\0'){
        if (!glued[from[0]][from[1]]) {
            String color = rectangles[from[0]][from[1]].getColor();
            puzzle[to[0]][to[1]] = puzzle[from[0]][from[1]];
            puzzle[from[0]][from[1]] = '\0'; // Vaciar la posición original
            rectangles[to[0]][to[1]].changeColor(color); // Aplicar el color de la ficha movida
            rectangles[from[0]][from[1]].changeColor("black"); // Vaciar la celda original
            checkCompletion(); // Comprobar si se ha completado el puzzle
        }else{  System.out.println("La ficha está pegada y no se puede mover.");}
    } else { System.out.println("No se puede reubicar");}
    lastActionSuccessful = didStateChange(); // Verificar si la acción fue exitosa
}

    /**
    * This method adds glue to the tiles and changes 
    * their color to make visible that they have glue
    *
    */
public void addGlue(int row, int column) {
    if (isPositionValid(row, column) && puzzle[row][column] != '\0' && !glued[row][column]) {
        glued[row][column] = true;
        if (originalColors[row][column] == null) {
            originalColors[row][column] = rectangles[row][column].getColor(); // Debes implementar getColor() en Rectangle
        }
        rectangles[row][column].changeColor("grey");
    } else {
        System.out.println("Posición no válida o ya está pegada");
    }
}
    
    /**
    * This method removes glue from the tiles and changes 
    * their color to make visible that they have no glue
    *
    */
    public void deleteGlue(int row, int column) {
    if (isPositionValid(row, column) && glued[row][column]) {
        glued[row][column] = false;
        if (puzzle[row][column] != '\0' && originalColors[row][column] != null) {
            rectangles[row][column].changeColor(originalColors[row][column]);
        } else {
            rectangles[row][column].changeColor("black"); // Si no hay color original, vuelve a negro
        }
    } else {
        System.out.println("No hay pegamento en la posición especificada");
    }
}

    
public void makeHole(int row, int column) {
    if (isPositionValid(row, column)) {
            holes[row][column] = true; // Se marca la posición como hueco
            rectangles[row][column].changeColor("white"); // Cambia el color visual del hueco
    } else {
            System.out.println("Posición no válida para hacer un hueco.");
    }
}
    
    public void tilt(char direction) {
        movePuzzle(direction);
    }

    public void intelligentTilt() {
        boolean moved = false;
        
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (puzzle[i][j] != ending[i][j] && puzzle[i][j] != '\0') {
                    moved = moveTileTowardsEnding(i, j);
                    if (moved) return; // Realizar sólo un movimiento inteligente
                }
            }
        }
    }
    
    private boolean moveTileTowardsEnding(int row, int col) {
        int targetRow = -1, targetCol = -1;
    
        // Encontrar la posición objetivo en el estado final
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (ending[i][j] == puzzle[row][col] && puzzle[i][j] == '\0') {
                    targetRow = i;
                    targetCol = j;
                    break;
                }
            }
        }
    
        if (targetRow != -1 && targetCol != -1) {
            // Mover la ficha en la dirección correcta
            if (targetRow > row) {
                tilt('D');  // Mover hacia abajo
                return true;
            } else if (targetRow < row) {
                tilt('U');  // Mover hacia arriba
                return true;
            } else if (targetCol > col) {
                tilt('R');  // Mover hacia la derecha
                return true;
            } else if (targetCol < col) {
                tilt('L');  // Mover hacia la izquierda
                return true;
            }
        }
        
        return false; // No se pudo mover la ficha
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
                        rectangles[x][y].setXPosition(y * 60 + 70);
                    } else {
                        rectangles[x][y].moveVertical((x - newRectLine[j].getYPosition() / 60) * 60);
                    }
                } else if (!holes[x][y]) {
                    puzzle[x][y] = '\0';
                    rectangles[x][y] = new Rectangle(60, 60, y * 60 + 70, x * 60 + 15, "black");
                    rectangles[x][y].makeVisible();
                }
            }
        }
    }

private boolean isPositionValid(int row, int col) {
    return row >= 0 && row < height && col >= 0 && col < width && !(holes[row][col]);
}

public void makeVisible() {
        if (!isVisible) {
            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    rectangles[i][j].makeVisible();  // Hacer visible el tablero editable
                    endingRectangles[i][j].makeVisible();  // Hacer visible el tablero de referencia
                }
            }
            isVisible = true;
        }
    }
    
    public void makeInVisible() {
        if (isVisible) {
            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    rectangles[i][j].makeInvisible();
                }
            }
            isVisible = true;
        }
    }
    
    public void exchange() {
    // Intercambiar los contenidos de los tableros (starting y ending)
    char[][] tempPuzzle = puzzle;
    puzzle = ending;
    ending = tempPuzzle;

    // Intercambiar visualmente los rectángulos asociados
    for (int row = 0; row < height; row++) {
        for (int col = 0; col < width; col++) {
            if (puzzle[row][col] == '\0') {
                rectangles[row][col].changeColor("black");  // Espacios vacíos
            } else {
                // Asignar el color basado en la ficha actual
                switch (puzzle[row][col]) {
                    case 'r': rectangles[row][col].changeColor("red"); break;
                    case 'g': rectangles[row][col].changeColor("green"); break;
                    case 'b': rectangles[row][col].changeColor("blue"); break;
                    case 'y': rectangles[row][col].changeColor("yellow"); break;
                }
            }
        }
    }
}

    
    private void finish() {
        if (isPuzzleComplete()) {
            // Lógica para finalizar la simulación
            System.out.println("¡Felicidades, Puzzle resuelto!");
            isEditable = false;
            makeInVisible();
    }
    }

    public boolean ok() {
        return lastActionSuccessful;  // Devolver si la última acción cambió el estado
    }
    
    public void savePreviousState() {
        if (this.previousState == null) {
            System.out.println("Error, no fue posible realizar esta acción");
            this.previousState = new char[this.height][this.width];  // Inicializar si es null
        }
        
        // Guardar el estado actual en previousState
        for (int i = 0; i < this.height; i++) {
            for (int j = 0; j < this.width; j++) {
                this.previousState[i][j] = this.puzzle[i][j];
            }
        }
        System.out.println("Movimiento realizado");
    }

    // Método que compara el estado actual con el anterior para verificar si hubo cambios
    private boolean didStateChange() {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (puzzle[i][j] != previousState[i][j]) {
                    return true;  // Si hay una diferencia, la acción fue exitosa
                }
            }
        }
        return false; // Si no hubo diferencias, la acción no fue exitosa
    }
    
    private void checkCompletion() {
        if (isPuzzleComplete()) {
            finish();  // Llamar a finish cuando se completa el puzzle
        }
    }
    
    public boolean isPuzzleComplete() {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (puzzle[i][j] != ending[i][j]) {
                    return false;  // Si hay al menos una diferencia, el puzzle no está completo
                }
            }
        }
        return true;
    }
    
    private boolean canEdit() {
        if (!isEditable) {
            System.out.println("Este tablero no se puede editar.");
        }
        return isEditable;
    }
    
    private void fillPuzzleWithEnding() {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                endingRectangles[i][j].changeColor(convertCharToColor(ending[i][j]));  // Asignar color a los rectángulos de ending
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
            case 's':
                return "grey";
            case 'w':
                return "white";
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
            case "grey":
                return 's';
            case "white":
                return 'w';
            default:
                return '\0';
        }
    }
    
    private void fillPuzzleWithStarting() {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                rectangles[i][j].changeColor(convertCharToColor(puzzle[i][j]));  // Asignar color a los rectángulos
            }
        }
        this.isEditable = true;  // Hacer el tablero editable
    }
    
    private void initializeRectangles() {
        int rectHeight = 60;
        int rectWidth = 60;
        int xOffset = 70;
        int yOffset = 15;
        int separation = width * rectWidth + 70;  // Separación entre los dos tableros

        // Inicializar rectángulos para el tablero editable (starting)
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                // Crear los rectángulos del tablero editable
                rectangles[i][j] = new Rectangle(
                    rectHeight, 
                    rectWidth, 
                    xOffset + j * rectWidth,   // Posición en X
                    yOffset + i * rectHeight,  // Posición en Y
                    convertCharToColor(puzzle[i][j]) // Color inicial según el contenido del tablero
                );
                // Crear los rectángulos para el tablero de referencia (ending)
                endingRectangles[i][j] = new Rectangle(
                    rectHeight, 
                    rectWidth, 
                    xOffset + j * rectWidth + separation,  // Posicionar al lado del tablero editable
                    yOffset + i * rectHeight,  // Misma posición en Y
                    convertCharToColor(ending[i][j]) // Color según el estado final
                );
            }
        }
    }
    
}
