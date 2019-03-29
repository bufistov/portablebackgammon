package gamelogic;
import java.awt.Color;
import java.awt.Graphics;
import lowlevel.*;

public class Die {
    public static int DIE_COLOUR = 0xFFFFFF;
    public static int DOT_COLOUR = 0x000000;
    public static String rollString = "Roll";

    private static Color die_colour, dot_colour;

    private static int dieWidth = Piece.PIECE_DIAMETER;
    private static int dieHeight = Piece.PIECE_DIAMETER;

    private int dotDiameter = Piece.PIECE_DIAMETER / 5;
    private final int TINY_GAP = 5;

    private Utils utils = new Utils();
    public int value = -1; // 1 to 6

    //Update, this does get called when the player is putting their pieces away and they roll a value too high leaving them with
    //no options, in this special case we lower the value of the die to allow the algorithm to handle that there is options
    // we DO NOT want the player to see this tho so we flag a boolean in here to stop the dice painting different.
    private boolean showOriginalValue;
    private int originalValue;

    public Die() {
        log("Die made");
        makeColourObjects();
    }

    public static void makeColourObjects() {
        die_colour = new Color(DIE_COLOUR);
        dot_colour = new Color(DOT_COLOUR);
    }

    public static int getWidth() {
        return dieWidth;
    }

    public static int getHeight() {
        return dieHeight;
    }

    // returns a random int between 1 and 6 to simulate a dice roll
    public int roll() {
        showOriginalValue = false;
        value = Utils.getRand(1,6);
        return value;
    }

    //returns the current value (ie what the die is showing now)
    public int getValue() {
        return value;
    }

    /**
     * Currently this is a hack, that reduces value of a dies to make a perfect
     * match with home spike. The original value is saved to draw correct value on canvas.
     * @param newValue smaller value to make a perfect match
     */
    void setValue(int newValue) {
        if (!showOriginalValue) {//if its already true we dont want to update the oriignal val
            showOriginalValue = true;
            originalValue = value;
        }
        log("WARNING, SETVALUE ON DICE CALLED");
        value = newValue;
        if (value < 0) {
            throw new RuntimeException("Value of die should not be negative, got: " + value);
        }
    }

    // disables a die so that the value is zero and this no logic will work out potential moves with this die now etc
    void disable() {
        value = 0;
    }

    int miniDieWidth() {
        return Piece.PIECE_DIAMETER-6;
    }

    int miniDieHeight() {
        return Piece.PIECE_DIAMETER-6;
    }

    void paint(Graphics g, int x, int y) {
        if (value >= 1) {
            //die size based on piece diameter
            dieWidth = Piece.PIECE_DIAMETER;
            dieHeight = Piece.PIECE_DIAMETER;
            dotDiameter = Piece.PIECE_DIAMETER / 5;
            int halfDotDiameter = dotDiameter / 2;
            drawOutline(g, x, y, dieWidth, dieHeight);
            drawDots(g, x, y, dieWidth, dieHeight, dotDiameter, halfDotDiameter);
        }
    }

    // to make die smaller we divide by this
    // draws a minni version of the die anywhere you specifiy, the purpose is so that a small die
    // can be drawn when potential moves are so they know which die theyre going to use up
    void drawMiniDie(Graphics g, int x, int y) {
        int miniDotDiameter = miniDieWidth() / 5;
        int miniHalfDotDiameter = dotDiameter / 2;
        drawOutline(g,x,y,miniDieWidth(), miniDieHeight());
        drawDots(g, x, y, miniDieWidth(), miniDieHeight(), miniDotDiameter,miniHalfDotDiameter);
    }

    private void drawOutline(Graphics g, int x, int y, int DIE_WIDTH, int DIE_HEIGHT) {
        utils.setColor(g, die_colour);
        utils.fillRoundRect(g, x, y, DIE_WIDTH, DIE_HEIGHT);
        utils.setColor(g, Color.black);
        utils.drawRoundRect(g, x, y, DIE_WIDTH, DIE_HEIGHT);
        
        if (CustomCanvas.showBoundaryBoxes) {
            utils.setColor(g,Color.RED);
            utils.drawRect(g,x, y,DIE_WIDTH, DIE_HEIGHT);
        }
    }

    private void drawDots(Graphics g, int x, int y, int DIE_WIDTH, int DIE_HEIGHT,
                          int DOT_DIAMETER, int HALF_DOT_DIAMETER) {
        utils.setColor(g, dot_colour);
        int dots = value;
        if (showOriginalValue) {
            utils.setColor(g, Color.RED);
            dots = originalValue; // rarely we hide the real value form player, see above for why
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
                break;
        }
    }

    private void log(String s) {
        Utils.log("Die{}:" + s);
    }
}
