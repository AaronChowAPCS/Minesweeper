import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import de.bezier.guido.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class Minesweeper extends PApplet {


public final static int NUM_ROWS = 20;
public final static int NUM_COLS = 20;
private MSButton[][] buttons; //2D array of minesweeper buttons
private ArrayList <MSButton> bombs; //ArrayList of just the minesweeper buttons that are bombs
private MSButton restartButton;
public boolean test = false; 

public void setup ()
{
    size(400, 450);
    textAlign(CENTER,CENTER);
    // make the manager
    Interactive.make( this );
    bombs = new ArrayList<MSButton>();

    //Declare and Initialize buttons
    buttons = new MSButton[NUM_ROWS][NUM_COLS];
    for(int i = 0; i < 20; i ++){
        for(int j = 0; j < 20; j++){
            buttons[i][j] = new MSButton(i,j);
        }
    }
    restartButton = new MSButton(21, 21);
    restartButton.x = 320;
    restartButton.y = 415;
    restartButton.width = 70;
    restartButton.height = 15;
    restartButton.setLabel("RESTART");
    for(int i = 0; i < 35; i++)
        setBombs();
}

public void setBombs()
{
    //Sets a specific number of bombs in random locations
    int row = (int)(Math.random()*20);
    int column = (int)(Math.random()*20);
    if(!bombs.contains(buttons[row][column])){
        bombs.add(buttons[row][column]);
    }
}

public void keyPressed()
{
    //For test use, displays the Winning Message
    if(key == 'w')
        test = !test;
}

public boolean isWon()
{
    //Determines whether all spaces have been clicked without activating any bombs, or whether all bombs have been marked
    int spaceCount = 0;
    int markedCount = 0;
    for(int r = 0; r < NUM_ROWS; r++){
        for(int c = 0; c < NUM_COLS; c++){
            if(bombs.contains(buttons[r][c])){
                if(buttons[r][c].isClicked() == true)
                    return false;
            }
            else if(!bombs.contains(buttons[r][c])){
                if(buttons[r][c].isClicked() == true)
                    spaceCount++;
            }
        }
    }
    
    for(int r = 0; r < NUM_ROWS; r++){
        for(int c = 0; c < NUM_COLS; c++){
            if(bombs.contains(buttons[r][c])){
                if(buttons[r][c].isMarked() == true)
                    markedCount++;
            }
        }
    }
    if(markedCount == bombs.size())
        return true;
    if(spaceCount == ((NUM_ROWS*NUM_COLS) - bombs.size()))
        return true;
    else
        return false;
}

public void draw ()
{
    background( 0 );
    restartButton.setLabel("RESTART");
    if(isWon())
        displayWinningMessage();
    if(test)
        displayWinningMessage();
    for(int r = 0; r < 20; r++){
        for(int c = 0; c < 20; c++){
            if(buttons[r][c].isClicked() == true){
                if(bombs.contains(buttons[r][c]))
                    displayLosingMessage();
            }
        }
    }
}

public void displayLosingMessage()
{
    //Flips all buttons, displaying all bomb locations
    for(int r = 0; r < 20; r++){
        for(int c = 0; c < 20; c++){
            if(bombs.contains(buttons[r][c]))
                buttons[r][c].setClicked(true);
            else
            {
                if(buttons[r][c].countBombs(r,c) > 0){
                    buttons[r][c].setLabel("" + buttons[r][c].countBombs(r,c));
                }
                buttons[r][c].setClicked(true);
            }
        }
    }

    textSize(35);
    textAlign(CENTER,CENTER);
    fill(243,59,59);
    text("GAME OVER", width/2,418);
    textSize(12);
    /*
    String losingMessage = "YOU DIED :(";
    int space = 4;
    for(int c = 0; c < losingMessage.length(); c++){
        buttons[NUM_ROWS/2][c + space].setLabel("" + losingMessage.substring(c,c+1));
    }
    */

}

public void displayWinningMessage()
{
    textSize(35);
    textAlign(CENTER,CENTER);
    fill(135,206,235);
    text("YOU WON!", width/2,418);
    textSize(12);
}

public class MSButton
{
    private int r, c;
    private float x,y, width, height;
    private boolean clicked; //Boolean for whether a bomb has been activated
    private boolean marked; //Boolean for whether a bomb has been marked by player
    private String label;
    
    public MSButton ( int rr, int cc )
    {
        width = 400/NUM_COLS;
        height = 400/NUM_ROWS;
        r = rr;
        c = cc; 
        x = c*width;
        y = r*height;
        label = "";
        clicked = false;
        marked = false;
        Interactive.add( this ); // register it with the manager
    }
    public boolean isClicked()
    {
        return clicked;
    }
    public boolean isMarked()
    {
       return marked;
    }
    public void setClicked(boolean variable)
    {
        clicked = variable;
    }
    public void setMarked(boolean variable)
    {
        marked = variable;
    }
    public void setLabel(String newLabel)
    {
        label = newLabel;
    }
    public void draw () 
    {    

        if( clicked && bombs.contains(this) ) 
            fill(255,0,0);
        else if(clicked)
            fill( 200 );
        else if(marked)
            fill(0,0,0);
        else 
            fill( 100 );

        rect(x, y, width, height);
        fill(0);
        text(label,x+width/2,y+height/2);

    }
    public void mousePressed () 
    {
        if(mouseButton == LEFT){
            if(restartButton == this){
                for(int r = 0; r < 20; r++){
                    for(int c = 0; c < 20; c++){
                        if(bombs.contains(buttons[r][c]))
                            bombs.remove(buttons[r][c]);
                    }
                }
                for(int i = 0; i < 35; i++)
                    setBombs();
                for(int r = 0; r < 20; r++){
                    for(int c = 0; c < 20; c++){
                        buttons[r][c].setClicked(false);
                        buttons[r][c].setMarked(false);
                        buttons[r][c].setLabel("");
                    }
                }
                
            }
            clicked = true;
            if(bombs.contains(this)){
                displayLosingMessage();
            }
            else if(countBombs(r,c) > 0){
                setLabel("" + countBombs(r,c));
            }
            else {
                if(isValid(r, c - 1) && buttons[r][c-1].isClicked() != true){
                    buttons[r][c-1].mousePressed();
                }
                if(isValid(r-1, c-1) && buttons[r-1][c-1].isClicked() != true){
                    buttons[r-1][c-1].mousePressed();
                }
                if(isValid(r - 1, c) && buttons[r-1][c].isClicked() != true){
                    buttons[r-1][c].mousePressed();
                }  
                if(isValid(r-1, c+1) && buttons[r-1][c+1].isClicked() != true){
                    buttons[r-1][c+1].mousePressed();
                }
                if(isValid(r, c + 1) && buttons[r][c+1].isClicked() != true){
                    buttons[r][c+1].mousePressed();
                }
                if(isValid(r+1, c+1) && buttons[r+1][c+1].isClicked() != true){
                    buttons[r+1][c+1].mousePressed();
                }
                if(isValid(r + 1, c) && buttons[r+1][c].isClicked() != true){
                    buttons[r+1][c].mousePressed();
                }
                if(isValid(r+1, c-1) && buttons[r+1][c-1].isClicked() != true){
                    buttons[r+1][c-1].mousePressed();
                }       
            }
        }
        else if(mouseButton == RIGHT){
            marked = true;
        }
    }
    public boolean isValid(int r, int c)
    {
        //Determines whether a button is on the grid or not
        if(r >= 20 || c >= 20){
            return false;
        }
        if(r < 0 || c < 0){
            return false;
        }
        return true;
    }
    public int countBombs(int row, int col)
    {
        //Counts the bombs surrounding a space 
        int numBombs = 0;
        if(isValid(row, col - 1)){
            if(bombs.contains(buttons[row][col-1]))
                numBombs++;
        }
        if(isValid(row, col + 1)){
            if(bombs.contains(buttons[row][col+1]))
                numBombs++;
        }
        if(isValid(row + 1, col + 1)){
            if(bombs.contains(buttons[row+1][col+1]))
                numBombs++;
        }
        if(isValid(row + 1, col - 1)){
            if(bombs.contains(buttons[row+1][col-1]))
                numBombs++;
        }
        if(isValid(row - 1, col + 1)){
            if(bombs.contains(buttons[row-1][col+1]))
                numBombs++;
        }
        if(isValid(row - 1, col - 1)){
            if(bombs.contains(buttons[row-1][col-1]))
                numBombs++;
        }
        if(isValid(row + 1, col)){
            if(bombs.contains(buttons[row+1][col]))
                numBombs++;
        }
        if(isValid(row - 1, col)){
            if(bombs.contains(buttons[row-1][col]))
                numBombs++;
        }
        return numBombs;
    }
}
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "Minesweeper" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
