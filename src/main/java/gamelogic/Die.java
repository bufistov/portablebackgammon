package gamelogic;

import graphics.GameColour;
import graphics.Geometry;
import lowlevel.Utils;

import java.awt.*;

class Die {

    private GameColour colours;
    private Geometry geometry;
    private int value;
    private int usesCounter;

    Die(GameColour colours, Geometry geometry) {
        Utils.log("Die{}: Die made");
        this.colours = colours;
        this.geometry = geometry;
    }

    void roll() {
        value = Utils.getRand(1,6);
        usesCounter = 1;
    }

    void doubleRoll() {
        this.usesCounter = 2;
    }

    boolean isDoubleRolled() {
        return usesCounter == 2;
    }

    void setValue(int newValue) {
        assert newValue >= 0 && newValue <= 6;
        usesCounter = 1;
        value = newValue;
    }

    int getValue() {
        return value;
    }

    // disables a die so that the value is zero and this no logic will work out potential moves with this die
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
        int dotDiameter = geometry.dieDotDiameter(geometry.dieSize());
        drawOutline(g, x, y, geometry.dieSize(), geometry.dieSize());
        drawDots(g, x, y, geometry.dieSize(), geometry.dieSize(), dotDiameter);
    }

    void drawMiniDie(Graphics g, int x, int y) {
        int miniDotDiameter = geometry.dieDotDiameter(geometry.miniDieSize());
        drawOutline(g,x, y, geometry.miniDieSize(), geometry.miniDieSize());
        drawDots(g, x, y, geometry.miniDieSize(), geometry.miniDieSize(), miniDotDiameter);
    }

    private void drawOutline(Graphics g, int x, int y, int DIE_WIDTH, int DIE_HEIGHT) {
        Utils.fillRoundRect(g, colours.getDie(), x, y, DIE_WIDTH, DIE_HEIGHT);
        Utils.drawRoundRect(g, Color.black, x, y, DIE_WIDTH, DIE_HEIGHT);
    }

    private void drawDots(Graphics g, int x, int y, int DIE_WIDTH, int DIE_HEIGHT,
                          int DOT_DIAMETER) {
        final int dots = value;
        final int HALF_DOT_DIAMETER = DOT_DIAMETER / 2;
        final int tinyGap = geometry.tinyGap();
        switch(dots) {
            case 1:
                //draw single dot in middle
                Utils.fillCircle(g, colours.getDieDot(),
                    (x + DIE_WIDTH / 2) - HALF_DOT_DIAMETER,
                    (y + DIE_HEIGHT / 2)-HALF_DOT_DIAMETER, DOT_DIAMETER, DOT_DIAMETER);
                break;
            case 2:
                //draw 2 dots in opposing corners
                Utils.fillCircle(g, colours.getDieDot(), (x+DIE_WIDTH)-(DOT_DIAMETER+tinyGap), y+DOT_DIAMETER, DOT_DIAMETER, DOT_DIAMETER);
                Utils.fillCircle(g, x + tinyGap, y+(DIE_HEIGHT-DOT_DIAMETER-tinyGap), DOT_DIAMETER, DOT_DIAMETER);
                break;
            case 3:
                //draw 3 dots diagnally
                Utils.fillCircle(g, colours.getDieDot(), (x+DIE_WIDTH)-(DOT_DIAMETER+tinyGap), y+DOT_DIAMETER, DOT_DIAMETER, DOT_DIAMETER);
                Utils.fillCircle(g, (x+tinyGap), y+(DIE_HEIGHT-DOT_DIAMETER-tinyGap), DOT_DIAMETER, DOT_DIAMETER);
                Utils.fillCircle(g, (x+DIE_WIDTH/2)-HALF_DOT_DIAMETER, (y+DIE_HEIGHT/2)-HALF_DOT_DIAMETER, DOT_DIAMETER, DOT_DIAMETER);
                break;
            case 4:
                //draw 4 dots 2 at top and 2 at bottom
                Utils.fillCircle(g, colours.getDieDot(),  (x+DIE_WIDTH)-(DOT_DIAMETER+tinyGap), y+DOT_DIAMETER, DOT_DIAMETER, DOT_DIAMETER);
                Utils.fillCircle(g, (x+tinyGap), y+DOT_DIAMETER, DOT_DIAMETER, DOT_DIAMETER);
                Utils.fillCircle(g, (x+DIE_WIDTH)-(DOT_DIAMETER+tinyGap), y+(DIE_HEIGHT-DOT_DIAMETER-tinyGap), DOT_DIAMETER, DOT_DIAMETER);
                Utils.fillCircle(g, (x+tinyGap), y+(DIE_HEIGHT-DOT_DIAMETER-tinyGap), DOT_DIAMETER, DOT_DIAMETER);
                break;
            case 5:
                //draw 4 dots 2 at top and 2 at bottom
                Utils.fillCircle(g, colours.getDieDot(), (x+DIE_WIDTH)-(DOT_DIAMETER+tinyGap), y+DOT_DIAMETER, DOT_DIAMETER, DOT_DIAMETER);
                Utils.fillCircle(g, (x+tinyGap), y+DOT_DIAMETER, DOT_DIAMETER, DOT_DIAMETER);
                Utils.fillCircle(g, (x+DIE_WIDTH)-(DOT_DIAMETER+tinyGap), y+(DIE_HEIGHT-DOT_DIAMETER-tinyGap), DOT_DIAMETER, DOT_DIAMETER);
                Utils.fillCircle(g, (x+tinyGap), y+(DIE_HEIGHT-DOT_DIAMETER-tinyGap), DOT_DIAMETER, DOT_DIAMETER);
                //and one in the middle
                Utils.fillCircle(g, (x+DIE_WIDTH/2)-HALF_DOT_DIAMETER, (y+DIE_HEIGHT/2)-HALF_DOT_DIAMETER, DOT_DIAMETER, DOT_DIAMETER);
                break;
            case 6:
                //draw 4 dots 2 at top and 2 at bottom
                Utils.fillCircle(g, colours.getDieDot(), (x+DIE_WIDTH)-(DOT_DIAMETER+tinyGap), y+DOT_DIAMETER, DOT_DIAMETER, DOT_DIAMETER);
                Utils.fillCircle(g, (x+tinyGap), y+DOT_DIAMETER, DOT_DIAMETER, DOT_DIAMETER);
                Utils.fillCircle(g, (x+DIE_WIDTH)-(DOT_DIAMETER+tinyGap), y+(DIE_HEIGHT-DOT_DIAMETER-tinyGap), DOT_DIAMETER, DOT_DIAMETER);
                Utils.fillCircle(g, (x+tinyGap), y+(DIE_HEIGHT-DOT_DIAMETER-tinyGap), DOT_DIAMETER, DOT_DIAMETER);
                //and 2 in middle top and bottom
                Utils.fillCircle(g, (x+DIE_WIDTH/2)-HALF_DOT_DIAMETER, y+DOT_DIAMETER, DOT_DIAMETER, DOT_DIAMETER);
                Utils.fillCircle(g, (x+DIE_WIDTH/2)-HALF_DOT_DIAMETER, y+(DIE_HEIGHT-DOT_DIAMETER-tinyGap), DOT_DIAMETER, DOT_DIAMETER);
                break;
            default:
                break;
        }
    }
}
