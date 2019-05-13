package gamelogic;

import data.DieType;
import data.PlayerColor;
import data.SpikeType;
import graphics.GameColour;
import graphics.Geometry;
import lowlevel.Utils;

import java.awt.*;
import java.util.Enumeration;
import java.util.Vector;

import static data.DieType.*;
import static data.SpikeType.STALECMITE;
import static data.SpikeType.STALECTITE;

/**
 * Board triangle that can store up to 15 checkers.
 */
public class Spike {

    Vector pieces = new Vector();
    private final int position; // index in the array of spikes
    private final SpikeType type;
    private final String spikeName;
    private GameColour colours;

    // these variables (along with TRIANGLE_WIDTH & TRIANGLE_HEIGHT) are used to work out if the player has clicked on the piece
    private int collision_x;
    private int collision_y;

    private static final int NOT_A_REAL_SPIKE_MINUS_99 = -99;
    private Die die1;
    private Die die2;
    private DieType flashDieType; // the die combination that brings piece at mouse to given spike
    private boolean flash = false;

    private int x1 = 0, y1 = 0; // left most point
    private int x2 = 0, y2 = 0; // middle point, can go up or down depending on type
    private int x3 = 0, y3 = 0; // right most point

    Spike(GameColour colours, Geometry geometry, int position) {
        this.colours = colours;
        this.die1 = new Die(colours, geometry);
        this.die2 = new Die(colours, geometry);
        if (position < 0) {
            log("Container spike is made");
            this.position = NOT_A_REAL_SPIKE_MINUS_99;
            this.spikeName = "Container";
            this.type = SpikeType.CONTAINER;
        } else if (position > 24) {
            log("Bar spike is make");
            this.position = NOT_A_REAL_SPIKE_MINUS_99;
            this.spikeName = "Bar";
            this.type = SpikeType.BAR;
        } else {
            this.position = position;
            spikeName = Integer.toString(position - 1);
            this.type = position <= 12 ? STALECTITE : STALECMITE;
        }
        log("Spike made "+ position);
    }

    String getName() {
        return spikeName;
    }

    @Override
    public String toString() {
        return spikeName;
    }

    @Override
    public boolean equals(Object that) {
        if (! (that instanceof Spike)) {
            return false;
        }
        Spike thatSpike = (Spike)that;
        return position == thatSpike.getPosition();
    }

    Point firstPieceCenter(Geometry geometry) {
        return new Point(x2, y1 + ysign() * geometry.pieceRadius());
    }

    boolean isEmpty() {
        return pieces.isEmpty();
    }

    boolean userClickedOnThis(int mouseX, int mouseY, Geometry geometry) {
        return (mouseX >= collision_x && mouseX <= collision_x + geometry.spikeWidth()) &&
            (mouseY >= collision_y && mouseY <= collision_y + geometry.spikeHeight());
    }

    DieType flashDieType() {
        return flashDieType;
    }

    void addPiece(Piece p) {
        log("Spike "+ getSpikeNumber() + " just has a piece added.");
        pieces.add(p);
    }

    void removePiece(Piece p) {
        log("Spike " + getSpikeNumber() + " just has a piece removed.");
        pieces.remove(p);
    }

    int getAmountOfPieces(PlayerColor playerColor) {
        if (pieces.size() == 0 || ((Piece)pieces.get(0)).getColour() != playerColor)
            return 0;
        return pieces.size();
    }

    void paint(Graphics g, Geometry geometry, Color color, int numPieces) {
        workOutPositionsOfSpike(geometry,geometry.boardHeight() - 2 * geometry.borderWidth(),
            geometry.spikeWidth());
        drawSpike(g, color);
        drawPieces(g, geometry.pieceDiameter());
        if (flash) {
            drawPotentialDieMoves(g, geometry, flashDieType, die1, die2);
        }
        flash = false;
        die1.noOptions();
        die2.noOptions();
    }

    int getSpikeNumber() {
        return position - 1;
    }

    int getPosition() {
        return position;
    }

    SpikeType getType() {
        return type;
    }

    boolean isFlashed() {
        return flash;
    }

    // makes a colour that flashes, this gets called from board
    // when the spike needs to indicate its a potential move to the player.
    // whichDice tells us which die would be causing this move, so we can show player.
    void flash(DieType dieType, int die1Value, int die2Value) {
        flash = true;
        flashDieType = dieType;
        if (dieType != DieType.DIE2) {
            die1.setValue(die1Value);
        }
        if (dieType != DieType.DIE1) {
            die2.setValue(die2Value);
        }
    }

    boolean isContainer() {
        return type == SpikeType.CONTAINER;
    }

    boolean isBar() {
        return type == SpikeType.BAR;
    }

    Point getMiddlePoint(Geometry geometry) {
        return new Point(x2, y2 - ysign() * geometry.spikeHeight() / 2);
    }

    Point leftMostPoint() {
        return new Point(x1, y1);
    }

    Color getColor(GameColour colours, boolean flash, boolean allPiecesAtHome) {
        if (flash) {
            return colours.flash();
        }
        return paintBlackColour() ? colours.getBlackSpike() : colours.getWhiteSpike();
    }

    private void drawPieces(Graphics g, int pieceDiameter) {
        Enumeration e = pieces.elements();
        int piecey = y1 - pieceDiameter;
        if (getType() == STALECMITE) {
           piecey = y1;
        }

        int overlapOnPieces = 0;
        if (pieces.size() > 5) {
            overlapOnPieces = pieceDiameter / 3;
        }
        if (pieces.size() > 7) {
            overlapOnPieces = pieceDiameter / 2;
        }
        if (pieces.size() > 9) {
            overlapOnPieces = pieceDiameter / 2 + pieces.size() / 3;
        }
        final int piecex = x2 - pieceDiameter / 2;
        while (e.hasMoreElements()) {
            Piece p = (Piece) e.nextElement();
            if (!p.stickToMouse()) {
                piecey += ysign() * (pieceDiameter - overlapOnPieces);
                p.paint(g, p.getColour() == PlayerColor.WHITE ? colours.getWhitePiece() : colours.getBlackPiece(),
                    pieceDiameter, piecex, piecey + ysign() * overlapOnPieces);
            }
        }
    }

    private void drawSpike(Graphics g, Color triangleColor) {
        if (isContainer()) {
            throw new RuntimeException("Wrong method to draw container");
        }
        Utils.fillTriangle(g, triangleColor, x1, y1, x2, y2, x3, y3);
        Utils.drawTriangle(g, Color.BLACK, x1, y1, x2, y2, x3, y3);
    }

    private void drawPotentialDieMoves(Graphics g, Geometry geometry, DieType dieType, Die die1, Die die2) {
        final int miniDieX = x2 - geometry.miniDieSize() / 2;
        final int miniDieHeight = geometry.miniDieSize();
        if (dieType == DIE1) {
            if (getType() == STALECMITE) {
                die1.drawMiniDie(g, miniDieX, y1 - miniDieHeight);
            } else {
                die1.drawMiniDie(g, miniDieX, y1);
            }
        } else if (dieType == DIE2) {
            if (getType() == STALECMITE) {
                die2.drawMiniDie(g, miniDieX, y1 - miniDieHeight);
            } else {
                die2.drawMiniDie(g, miniDieX, y1);
            }
        } else if (dieType == DIE1AND2) {
            if (getType() == STALECMITE) {
                die1.drawMiniDie(g, miniDieX, y1 - miniDieHeight * 2);
                die2.drawMiniDie(g, miniDieX, y1 - miniDieHeight);
            } else {
                die1.drawMiniDie(g, miniDieX, y1);
                die2.drawMiniDie(g, miniDieX, y1 + miniDieHeight);
            }
        }
    }

    private boolean paintBlackColour() {
        return  getSpikeNumber() % 2 == 0;
    }

    // this calculates the 3 points for this spike, each with x,y value
    // and boundaries for mouse click event
    private void workOutPositionsOfSpike(Geometry geometry, int boardHeight, int TRIANGLE_WIDTH) {
        int widthMinusBorderAndPieceComponent = geometry.boardWidth() - geometry.borderWidth();
        int total = geometry.spikeWidth() * 12 + geometry.centralBarWidth() + 2 * geometry.borderWidth();
        assert total == geometry.boardWidth();
        y1 = geometry.borderWidth();
        if (position <= 6) {
            //TOP RIGHT SEGMENT OF BOARD (6 spikes_
            x1 = widthMinusBorderAndPieceComponent - TRIANGLE_WIDTH * position;
        } else if (position <= 12) {
            // TOP LEFT
            x1 = widthMinusBorderAndPieceComponent - TRIANGLE_WIDTH * position - geometry.centralBarWidth();
        } else {
            // BOTTOM
            x1 = widthMinusBorderAndPieceComponent - (TRIANGLE_WIDTH * (25 - position));
            y1 = geometry.borderWidth() + boardHeight;
            if (position <= 18) {
                // BOTTOM LEFT
                 x1 -= geometry.centralBarWidth();
            }
        }

        x3 = x1 + TRIANGLE_WIDTH;
        y3 = y1;

        x2 = x1 + TRIANGLE_WIDTH / 2;
        y2 = y1 + ysign() * geometry.spikeHeight();

        collision_x = x1;
        collision_y = (type == STALECTITE) ? y1 : y1 - geometry.spikeHeight();
    }

    private void log(String s) {
        Utils.log("Spike{}:" + s);
    }

    private int ysign() {
        return type == STALECTITE ? 1 : -1;
    }
}
