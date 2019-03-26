/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gamelogic;
import java.awt.Color;
import java.awt.Graphics;
import lowlevel.*;
/**
 *
 * @author Gaz
 */
public class Die {
     //Colour constants
    public static int DIE_COLOUR=0xFFFFFF;
    public static int DOT_COLOUR=0x000000;
    public static Color die_colour, dot_colour;

    Utils utils = new Utils();
    private int value = -1; //1 to 6
    public Die() {
        log("Die made");
        makeColourObjects(false);
    }

    public static void makeColourObjects(boolean forceRecreation) {
        if (die_colour==null || forceRecreation) {
            die_colour= new Color(DIE_COLOUR);
        }
        if (dot_colour==null || forceRecreation) {
            dot_colour= new Color(DOT_COLOUR);
        }
    }

    //returns a random int between 1 and 6 to simulate a dice roll
    public int roll() {
        showOriginalValue = false;
        value = Utils.getRand(1,6);
        return value;
    }

    //returns the current value (ie what the die is showing now)
    public int getValue()
    {
        return value;
    }

    //this is never called apart from for debubbing where we want to trick the dice for a test
    //Update, this does get called when the player is putting their pieces away and they roll a value too high leaving them with
    //no options, in this special case we lower the value of the die to allow the algorithm to handle that there is options
    // we DO NOT want the player to see this tho so we flag a boolean in here to stop the dice painting different.
    boolean showOriginalValue;
    int originalValue;

    public void setValue(int newValue) {
        if (showOriginalValue)//if its already true we dont want to update the oriignal val
        {
        } else {
            showOriginalValue=true;
            originalValue=value;
        }
        
        log("WARNING, SETVALUE ON DICE CALLED");
        value = newValue;
    }

    //disables a die so that the value is zero and this no logic will work out potential moves with this die now etc
    public void disable()
    {
        value=0;
    }
    
    private void log(String s)
    {
        Utils.log("Die{}:" + s);
    }

    public static int DIE_WIDTH=0;
    public static int DIE_HEIGHT=0;
    public static String rollString = "Roll";
    int DOT_DIAMETER=0;
    int HALF_DOT_DIAMETER=0;
    int TINY_GAP=5;

    public void paint(Graphics g, int x, int y) {
        if (value >= 1) {
            //die size based on piece diameter
            DIE_WIDTH = Piece.PIECE_DIAMETER;
            DIE_HEIGHT = Piece.PIECE_DIAMETER;
            DOT_DIAMETER = Piece.PIECE_DIAMETER / 5;
            HALF_DOT_DIAMETER = DOT_DIAMETER / 2;
            drawOutline(g, x, y, DIE_WIDTH, DIE_HEIGHT, DOT_DIAMETER, HALF_DOT_DIAMETER);
            drawDots(g, x, y, DIE_WIDTH, DIE_HEIGHT, DOT_DIAMETER, HALF_DOT_DIAMETER);
        }
    }

    public static int MINI_DIE_WIDTH;
    public static int MINI_DIE_HEIGHT;
    public static int MINI_DOT_DIAMETER;
    public static int MINI_HALF_DOT_DIAMETER;
    ////////int MINI_DIE_DIVIDE_BY=2; // to make die smaller we divide by this
    //draws a minni version of the die anywhere you specifiy, the purpose is so that a small die
    //can be drawn when potential moves are so they know which die theyre going to use up
    public void drawMiniDie(Graphics g, int x, int y)
    {
        //die size based on piece diameter
        MINI_DIE_WIDTH  = Piece.PIECE_DIAMETER-6;
        MINI_DIE_HEIGHT = Piece.PIECE_DIAMETER-6;
        MINI_DOT_DIAMETER=(Piece.PIECE_DIAMETER-6)/5;
        MINI_HALF_DOT_DIAMETER=DOT_DIAMETER/2;

        //draw outline of the die
        drawOutline(g,x,y,MINI_DIE_WIDTH,MINI_DIE_HEIGHT,MINI_DOT_DIAMETER,MINI_HALF_DOT_DIAMETER);

        //draw dots//
        drawDots(g,x,y,MINI_DIE_WIDTH,MINI_DIE_HEIGHT,MINI_DOT_DIAMETER,MINI_HALF_DOT_DIAMETER);
    }

    private void drawOutline(Graphics g, int x, int y,int DIE_WIDTH,int DIE_HEIGHT,int DOT_DIAMETER,int HALF_DOT_DIAMETER) {
        utils.setColor(g, die_colour);
        utils.fillRoundRect(g, x, y, DIE_WIDTH, DIE_WIDTH);
        utils.setColor(g, Color.black);
        utils.drawRoundRect(g, x, y, DIE_WIDTH, DIE_WIDTH);
        
        if (CustomCanvas.showBoundaryBoxes) {
            utils.setColor(g,Color.RED);
            utils.drawRect(g,x, y,DIE_WIDTH, DIE_WIDTH);
        }
    }

    private void drawDots(Graphics g, int x, int y,int DIE_WIDTH,int DIE_HEIGHT,int DOT_DIAMETER,int HALF_DOT_DIAMETER) {
        utils.setColor(g, dot_colour);
        int dots;
        if (showOriginalValue) {
            utils.setColor(g, Color.RED);
            dots=originalValue; //rarely we hide the real value form player, see above for why
        } else {
            dots = getValue();
        }

        switch(dots) {
            case 1:
                //draw single dot in middle
                utils.fillCircle(g, (x+DIE_WIDTH/2)-HALF_DOT_DIAMETER, (y+DIE_HEIGHT/2)-HALF_DOT_DIAMETER, DOT_DIAMETER, DOT_DIAMETER);
                break;
            case 2:
                //draw 2 dots in opposing corners
                utils.fillCircle(g, (x+DIE_WIDTH)-(DOT_DIAMETER+TINY_GAP), y+DOT_DIAMETER, DOT_DIAMETER, DOT_DIAMETER);
                utils.fillCircle(g, (x+TINY_GAP), y+(DIE_HEIGHT-DOT_DIAMETER-TINY_GAP), DOT_DIAMETER, DOT_DIAMETER);
                break;
            case 3:
                //draw 3 dots diagnally
                utils.fillCircle(g, (x+DIE_WIDTH)-(DOT_DIAMETER+TINY_GAP), y+DOT_DIAMETER, DOT_DIAMETER, DOT_DIAMETER);
                utils.fillCircle(g, (x+TINY_GAP), y+(DIE_HEIGHT-DOT_DIAMETER-TINY_GAP), DOT_DIAMETER, DOT_DIAMETER);
                utils.fillCircle(g, (x+DIE_WIDTH/2)-HALF_DOT_DIAMETER, (y+DIE_HEIGHT/2)-HALF_DOT_DIAMETER, DOT_DIAMETER, DOT_DIAMETER);
                break;
            case 4:
                //draw 4 dots 2 at top and 2 at bottom
                utils.fillCircle(g, (x+DIE_WIDTH)-(DOT_DIAMETER+TINY_GAP), y+DOT_DIAMETER, DOT_DIAMETER, DOT_DIAMETER);
                utils.fillCircle(g, (x+TINY_GAP), y+DOT_DIAMETER, DOT_DIAMETER, DOT_DIAMETER);
                utils.fillCircle(g, (x+DIE_WIDTH)-(DOT_DIAMETER+TINY_GAP), y+(DIE_HEIGHT-DOT_DIAMETER-TINY_GAP), DOT_DIAMETER, DOT_DIAMETER);
                utils.fillCircle(g, (x+TINY_GAP), y+(DIE_HEIGHT-DOT_DIAMETER-TINY_GAP), DOT_DIAMETER, DOT_DIAMETER);
                break;
            case 5:
                //draw 4 dots 2 at top and 2 at bottom
                utils.fillCircle(g, (x+DIE_WIDTH)-(DOT_DIAMETER+TINY_GAP), y+DOT_DIAMETER, DOT_DIAMETER, DOT_DIAMETER);
                utils.fillCircle(g, (x+TINY_GAP), y+DOT_DIAMETER, DOT_DIAMETER, DOT_DIAMETER);
                utils.fillCircle(g, (x+DIE_WIDTH)-(DOT_DIAMETER+TINY_GAP), y+(DIE_HEIGHT-DOT_DIAMETER-TINY_GAP), DOT_DIAMETER, DOT_DIAMETER);
                utils.fillCircle(g, (x+TINY_GAP), y+(DIE_HEIGHT-DOT_DIAMETER-TINY_GAP), DOT_DIAMETER, DOT_DIAMETER);
                //and one in the middle
                utils.fillCircle(g, (x+DIE_WIDTH/2)-HALF_DOT_DIAMETER, (y+DIE_HEIGHT/2)-HALF_DOT_DIAMETER, DOT_DIAMETER, DOT_DIAMETER);
                break;
            case 6:
                //draw 4 dots 2 at top and 2 at bottom
                utils.fillCircle(g, (x+DIE_WIDTH)-(DOT_DIAMETER+TINY_GAP), y+DOT_DIAMETER, DOT_DIAMETER, DOT_DIAMETER);
                utils.fillCircle(g, (x+TINY_GAP), y+DOT_DIAMETER, DOT_DIAMETER, DOT_DIAMETER);
                utils.fillCircle(g, (x+DIE_WIDTH)-(DOT_DIAMETER+TINY_GAP), y+(DIE_HEIGHT-DOT_DIAMETER-TINY_GAP), DOT_DIAMETER, DOT_DIAMETER);
                utils.fillCircle(g, (x+TINY_GAP), y+(DIE_HEIGHT-DOT_DIAMETER-TINY_GAP), DOT_DIAMETER, DOT_DIAMETER);
                //and 2 in middle top and bottom
                utils.fillCircle(g, (x+DIE_WIDTH/2)-HALF_DOT_DIAMETER, y+DOT_DIAMETER, DOT_DIAMETER, DOT_DIAMETER);
                utils.fillCircle(g, (x+DIE_WIDTH/2)-HALF_DOT_DIAMETER, y+(DIE_HEIGHT-DOT_DIAMETER-TINY_GAP), DOT_DIAMETER, DOT_DIAMETER);
                break;
            default:
                /////Utils._E("Dave, I'm a bit confused: how can a dice have a value that isnt between 1 and 6??");
                break;
        }
    }
}
