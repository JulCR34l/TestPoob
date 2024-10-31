package shapes;

import java.awt.*;

/**
 * A rectangle that can be manipulated and that draws itself on a canvas.
 * 
 * @author  Michael Kolling and David J. Barnes (Modified)
 * @version 1.0  (15 July 2000)()
 */


 
public class Rectangle extends Shape{
    public static int EDGES = 4;
    private int height;
    private int width;

    /**
     * Create a new rectangle at default position with default color.
     */
    public Rectangle(){
        height = 30;
        width = 40;
        xPosition = 70;
        yPosition = 15;
        color = "magenta";
        isVisible = false;
    }
    
    public Rectangle(int height, int width, int xPosition, int yPosition, String color) {
        this.height = height;
        this.width = width;
        this.xPosition = xPosition;
        this.yPosition = yPosition;
        this.color = color;
        this.isVisible = false;
    }
    
    public int getX() {
        return xPosition;
    }

    public int getY() {
        return yPosition;
    }

    public void setX(int x) {
        this.xPosition = x;
    }

    public void setY(int y) {
        this.yPosition = y;
    }
    
    public void setPosition(int x, int y) {
        this.xPosition = x;
        this.yPosition = y;
    }
  
    
    /**
     * Change the size to the new size
     * @param newHeight the new height in pixels. newHeight must be >=0.
     * @param newWidht the new width in pixels. newWidth must be >=0.
     */
    public void changeSize(int newHeight, int newWidth) {
        super.erase();
        height = newHeight;
        width = newWidth;
        draw();
    }
    
    /**
     * Draw the rectangle with current specifications on screen.
     */
    @Override
    public void draw() {
        if(isVisible) {
            Canvas canvas = Canvas.getCanvas();
            canvas.draw(this, color,new java.awt.Rectangle(xPosition, yPosition, width, height));
            canvas.wait(10);
        }
    }

}

