package gamelogic;
import java.awt.Color;
import java.awt.Graphics;

import graphics.Geometry;
import lowlevel.*;

public class Die {
    public static int DIE_COLOUR = 0xFFFFFF;
    public static int DOT_COLOUR = 0x000000;
    private static Color die_colour, dot_colour;

    private Geometry geometry;
    private Utils utils = new Utils();
    private int value = -1; // 1 to 6
    private int usesCounter;

    //Update, this does get called when the player is putting their pieces away and they roll a value too high leaving them with
    //no options, in this special case we lower the value of the die to allow the algorithm to handle that there is options
    // we DO NOT want the player to see this tho so we flag a boolean in here to stop the dice painting different.
    private int originalValue;

    public Die(Geometry geometry) {
        log("Die made");
        this.geometry = geometry;
        makeColourObjects();
    }

    public static void makeColourObjects() {
        die_colour = new Color(DIE_COLOUR);
        dot_colour = new Color(DOT_COLOUR);
    }

    public int roll() {
        value = Utils.getRand(1,6);
        originalValue = value;
        usesCounter = 1;
        return value;
    }

    public void doubleRoll() {
        this.usesCounter = 2;
    }

    public void setActualValue(int newValue) {
        assert newValue >= 0 && newValue <= 6;
        originalValue = value = newValue;
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
        log("WARNING, SETVALUE ON DICE CALLED");
        value = newValue;
        if (value < 0) {
            throw new RuntimeException("Value of die should not be negative, got: " + value);
        }
    }

    // disables a die so that the value is zero and this no logic will work out potential moves with this die now etc
    void disable() {
        if (usesCounter <= 0) {
            throw new RuntimeException("Disabling disabled die, value: " + value);
        }
        --usesCounter;
        if (usesCounter == 0)
            value = 0;
    }

    void noOptions() {
        value = 0;
    }

    boolean enabled() {
        return value > 0;
    }

    boolean disabled() {
        return value == 0;
    }

    void paint(Graphics g, int x, int y) {
        if (value >= 1) {
            int dotDiameter = geometry.dieDotDiameter();
            int halfDotDiameter = dotDiameter / 2;
            drawOutline(g, x, y, geometry.dieSize(), geometry.dieSize());
            drawDots(g, x, y, geometry.dieSize(), geometry.dieSize(), dotDiameter, halfDotDiameter);
        }
    }

    // to make die smaller we divide by this
    // draws a minni version of the die anywhere you specifiy, the purpose is so that a small die
    // can be drawn when potential moves are so they know which die theyre going to use up
    void drawMiniDie(Graphics g, int x, int y) {
        int miniDotDiameter = geometry.miniDieSize() / 5;
        int miniHalfDotDiameter = geometry.dieDotDiameter() / 2;
        drawOutline(g,x, y, geometry.miniDieSize(), geometry.miniDieSize());
        drawDots(g, x, y, geometry.miniDieSize(), geometry.miniDieSize(),
            miniDotDiameter, miniHalfDotDiameter);
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
        int dots = originalValue;
        if (value != originalValue) {
            utils.setColor(g, Color.RED);
        }
        int tinyGap = geometry.tinyGap();
        switch(dots) {
            case 1:
                //draw single dot in middle
                utils.fillCircle(g, (x+DIE_WIDTH/2)-HALF_DOT_DIAMETER, (y+DIE_HEIGHT/2)-HALF_DOT_DIAMETER, DOT_DIAMETER, DOT_DIAMETER);
                break;
            case 2:
                //draw 2 dots in opposing corners
                utils.fillCircle(g, (x+DIE_WIDTH)-(DOT_DIAMETER+tinyGap), y+DOT_DIAMETER, DOT_DIAMETER, DOT_DIAMETER);
                utils.fillCircle(g, (x+tinyGap), y+(DIE_HEIGHT-DOT_DIAMETER-tinyGap), DOT_DIAMETER, DOT_DIAMETER);
                break;
            case 3:
                //draw 3 dots diagnally
                utils.fillCircle(g, (x+DIE_WIDTH)-(DOT_DIAMETER+tinyGap), y+DOT_DIAMETER, DOT_DIAMETER, DOT_DIAMETER);
                utils.fillCircle(g, (x+tinyGap), y+(DIE_HEIGHT-DOT_DIAMETER-tinyGap), DOT_DIAMETER, DOT_DIAMETER);
                utils.fillCircle(g, (x+DIE_WIDTH/2)-HALF_DOT_DIAMETER, (y+DIE_HEIGHT/2)-HALF_DOT_DIAMETER, DOT_DIAMETER, DOT_DIAMETER);
                break;
            case 4:
                //draw 4 dots 2 at top and 2 at bottom
                utils.fillCircle(g, (x+DIE_WIDTH)-(DOT_DIAMETER+tinyGap), y+DOT_DIAMETER, DOT_DIAMETER, DOT_DIAMETER);
                utils.fillCircle(g, (x+tinyGap), y+DOT_DIAMETER, DOT_DIAMETER, DOT_DIAMETER);
                utils.fillCircle(g, (x+DIE_WIDTH)-(DOT_DIAMETER+tinyGap), y+(DIE_HEIGHT-DOT_DIAMETER-tinyGap), DOT_DIAMETER, DOT_DIAMETER);
                utils.fillCircle(g, (x+tinyGap), y+(DIE_HEIGHT-DOT_DIAMETER-tinyGap), DOT_DIAMETER, DOT_DIAMETER);
                break;
            case 5:
                //draw 4 dots 2 at top and 2 at bottom
                utils.fillCircle(g, (x+DIE_WIDTH)-(DOT_DIAMETER+tinyGap), y+DOT_DIAMETER, DOT_DIAMETER, DOT_DIAMETER);
                utils.fillCircle(g, (x+tinyGap), y+DOT_DIAMETER, DOT_DIAMETER, DOT_DIAMETER);
                utils.fillCircle(g, (x+DIE_WIDTH)-(DOT_DIAMETER+tinyGap), y+(DIE_HEIGHT-DOT_DIAMETER-tinyGap), DOT_DIAMETER, DOT_DIAMETER);
                utils.fillCircle(g, (x+tinyGap), y+(DIE_HEIGHT-DOT_DIAMETER-tinyGap), DOT_DIAMETER, DOT_DIAMETER);
                //and one in the middle
                utils.fillCircle(g, (x+DIE_WIDTH/2)-HALF_DOT_DIAMETER, (y+DIE_HEIGHT/2)-HALF_DOT_DIAMETER, DOT_DIAMETER, DOT_DIAMETER);
                break;
            case 6:
                //draw 4 dots 2 at top and 2 at bottom
                utils.fillCircle(g, (x+DIE_WIDTH)-(DOT_DIAMETER+tinyGap), y+DOT_DIAMETER, DOT_DIAMETER, DOT_DIAMETER);
                utils.fillCircle(g, (x+tinyGap), y+DOT_DIAMETER, DOT_DIAMETER, DOT_DIAMETER);
                utils.fillCircle(g, (x+DIE_WIDTH)-(DOT_DIAMETER+tinyGap), y+(DIE_HEIGHT-DOT_DIAMETER-tinyGap), DOT_DIAMETER, DOT_DIAMETER);
                utils.fillCircle(g, (x+tinyGap), y+(DIE_HEIGHT-DOT_DIAMETER-tinyGap), DOT_DIAMETER, DOT_DIAMETER);
                //and 2 in middle top and bottom
                utils.fillCircle(g, (x+DIE_WIDTH/2)-HALF_DOT_DIAMETER, y+DOT_DIAMETER, DOT_DIAMETER, DOT_DIAMETER);
                utils.fillCircle(g, (x+DIE_WIDTH/2)-HALF_DOT_DIAMETER, y+(DIE_HEIGHT-DOT_DIAMETER-tinyGap), DOT_DIAMETER, DOT_DIAMETER);
                break;
            default:
                break;
        }
    }

    private void log(String s) {
        Utils.log("Die{}:" + s);
    }
}
