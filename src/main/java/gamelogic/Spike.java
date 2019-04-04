package gamelogic;
import java.awt.*;

import data.PlayerColor;
import data.SpikeType;
import lowlevel.*;

import java.util.Enumeration;
import java.util.Vector;

import static data.SpikeType.STALECMITE;
import static data.SpikeType.STALECTITE;

/**
 * Board triangle that can store up to 15 checkers.
 */
public class Spike {

    public static int BLACK_SPIKE_COLOUR = 0x993802;
    public static int WHITE_SPIKE_COLOUR = 0xffcc7e;
    private Color black_spike_colour, white_spike_colour;
    private static final Color flashColor = new Color(255,225,0);

    public  Vector pieces = new Vector();
    private int position; // 1 to 24
    private final SpikeType type;
    private final String spikeName;
    private Utils utils = new Utils();

    private int TRIANGLE_WIDTH = 0;
    private int TRIANGLE_HEIGHT = 0;

    // these variables (along with TRIANGLE_WIDTH & TRIANGLE_HEIGHT) are used to work out if the player has clicked on the piece
    private int collision_x;
    private int collision_y;

    private static final int NOT_A_REAL_SPIKE_MINUS_99 = -99;
    private Die storedDie;
    int whichDiei = -1; // the die combination that brings piece at mouse to given spike
    private boolean flash = false;

    private int x1 = 0, y1 = 0; // left most point
    private int x2 = 0, y2 = 0; // middle point, can go up or down depending on type
    private int x3 = 0, y3 = 0; // right most point

    Spike(int position) {
        this.position = position;
        assert position != 0;
        if (position < 0) {
            log("Special spike made, this isnt a spike at all, its a piece container");
            this.position = NOT_A_REAL_SPIKE_MINUS_99;
            this.spikeName = "Container";
            this.type = SpikeType.CONTAINER;
        } else {
            spikeName = Integer.toString(position - 1);
            this.type = position <= 12 ? STALECTITE : STALECMITE;
        }
        log("Spike made "+ position);
        makeColourObjects();
    }

    String getName() {
        return spikeName;
    }

    @Override
    public String toString() {
        return spikeName;
    }

    public boolean userClickedOnThis(int mouseX, int mouseY) {
        return (mouseX >= collision_x && mouseX <= collision_x + width()) &&
            (mouseY >= collision_y && mouseY <= collision_y + height());
    }

    // add a piece to this spike
    public boolean addPiece(Piece p) {
        if (spikeName != null) {
            log("Spike "+ getSpikeNumber()+ " just has a piece added.");
        }
        pieces.add(p);
        return true;
    }

    // remove this piece from the spike, pass spike in to remove, or pass null and the
    // first one will be removed.
    public boolean removePiece(Piece p) {
        if (spikeName != null) {
            log("Spike "+getSpikeNumber()+" just has a piece removed.");
        } else {
            Utils._E("removePiece:spikeName is null");
        }
        pieces.remove(p);
        return true;
    }

    public int getAmountOfPieces(PlayerColor playerColor) {
        if (pieces.size() == 0 || ((Piece)pieces.get(0)).getColour() != playerColor)
            return 0;
        return pieces.size();
    }

    void paint(Graphics g, int boardWidth, int boardHeightWithBorder) {
        int boardHeight = boardHeightWithBorder - Board.BORDER * 2;
        TRIANGLE_WIDTH            = (boardWidth - ((Board.BORDER * 2) + Board.BAR) ) / 12;
        TRIANGLE_HEIGHT           = boardHeight / 2;

        workOutPositionsOfSpike(boardHeight, TRIANGLE_WIDTH);
        drawSpike(g);
        drawPieces(g, spikeName);
        drawPotentialDieMoves(g);
    }

    public int getSpikeNumber() {
        return position - 1;
    }

    public int getPosition() {
        return position;
    }

    private void drawPieces(Graphics g, String spikeName) {
        Enumeration e = pieces.elements();
        int yPosForPieces = y1 - Piece.PIECE_DIAMETER;

        //adjust the starting point of the pieces pending on type of spike:
        /////////////////////////////////////////////
        if(getType() == STALECTITE) {
           yPosForPieces = y1 - Piece.PIECE_DIAMETER;
        } else if(getType() == STALECMITE) {
           yPosForPieces = y1;
        } else {
            Utils._E(spikeName+">>>Cannot work out the Y value for a piece since the spike claims to have no type!");
        }
        ///////////////////////////////////////////////

        int overlapOnPieces = 0;
        if (pieces.size() <= 5) {
            overlapOnPieces = 0;
        }
        if (pieces.size() > 5) {
            overlapOnPieces = Piece.PIECE_DIAMETER / 3;
        }
        if (pieces.size() > 7) {
            overlapOnPieces = Piece.PIECE_DIAMETER / 2;
        }
        if (pieces.size() > 9) {
            overlapOnPieces = (Piece.PIECE_DIAMETER / 2) + pieces.size() / 3;
        }
        while (e.hasMoreElements()) {
            Piece p = (Piece) e.nextElement();
            int piecex = x2 - Piece.PIECE_DIAMETER / 2;
            int piecey = -1;

            //caters for overlappin pieces when manny are added.
            // we need a different y value for top and bottom spikes
            // so that on top spikes the pieces move down
            // and on bottom spikes the pieces move up
            if(getType() == STALECTITE) {
               piecey = yPosForPieces += (Piece.PIECE_DIAMETER-overlapOnPieces);
            } else if(getType() == STALECMITE) {
                piecey = yPosForPieces -= (Piece.PIECE_DIAMETER-overlapOnPieces);
            } else {
                Utils._E(spikeName+"---Cannot work out the Y value for a piece since the spike claims to have no type!");
            }
            if(getType() == STALECTITE) {  // overlap here just squares them up to the bottom/top of spike if there overlapping
                 p.paint(g, piecex,piecey + overlapOnPieces);
            } else {
                 p.paint(g, piecex,piecey - overlapOnPieces);
            }
        }
    }

    //makes a colour that flashes, this gets called from board
    //when the spike needs to indicate its a potential move to the player.
    //whichDice tells us which die would be causing this move, so we can show player.
    void flash(int whichDice) {
        flash = true;
        whichDiei = whichDice;
    }

    private void drawSpike(Graphics g) {
        if (isContainer()) {
            assert false;
            return;
        }
        if (paintBlackColour(g)) {
            utils.setColor(g, black_spike_colour);
        } else {
            utils.setColor(g, white_spike_colour);
        }
        if (flash) {
            utils.setColor(g, flashColor);
            flash = false;
        }
        utils.fillTriangle(g, x1, y1, x2, y2, x3, y3);

        //draw outline after otherwise it gets distored by the filled shape
        utils.setColor(g, Color.BLACK);
        utils.drawTriangle(g, x1, y1, x2, y2, x3, y3);

        if (CustomCanvas.showBoundaryBoxes) {
            utils.setColor(g, Color.RED);
            utils.drawRect(g, collision_x, collision_y, width(), height());
        }
    }

    private void drawPotentialDieMoves(Graphics g) {
        final int miniDieX = x2 - Die.miniDieWidth() / 2;
        final int miniDieHeight = Die.miniDieHeight();
        if (whichDiei == Board.DIE1) {
            if (getType() == STALECMITE) {
                Board.die1.drawMiniDie(g, miniDieX, y1 - miniDieHeight);
            } else {
                Board.die1.drawMiniDie(g, miniDieX, y1);
            }

        } else if (whichDiei == Board.DIE2) {
            if (getType() == STALECMITE) {
                Board.die2.drawMiniDie(g, miniDieX, y1 - miniDieHeight);
            } else {
                Board.die2.drawMiniDie(g, miniDieX, y1);
            }
        } else if (whichDiei == Board.DIE1AND2) {
            if (getType() == STALECMITE) {
                Board.die1.drawMiniDie(g, miniDieX, y1 - miniDieHeight * 2);
                Board.die2.drawMiniDie(g, miniDieX, y1 - miniDieHeight);
            } else {
                Board.die1.drawMiniDie(g, miniDieX, y1);
                Board.die2.drawMiniDie(g, miniDieX, y1 + miniDieHeight);
            }
        } else if (whichDiei == -1) {
            //not an error now just means dont draw s it must be -1
        } else {
            Utils._E("whichDiei type is unknown");
        }
    }

    //sets the colour based on odd and even to alternative spike colours
    private boolean paintBlackColour(Graphics g) {
        if (getSpikeNumber() % 2 == 0) {
            g.setColor(Color.BLACK);
            return true;
        } else {
            g.setColor(Color.WHITE);
            return false;
        }
    }

    SpikeType getType() {
        return type;
    }

    // this calculates the 3 points for this spike, each with x,y value
    // and boundaries for mouse click event
    private void workOutPositionsOfSpike(int boardHeight, int TRIANGLE_WIDTH) {
        int widthMinusBorderAndPieceComponent = CustomCanvas.WIDTH - Board.BORDER;
        y1 = Board.BORDER;
        if (position <= 6) {
            //TOP RIGHT SEGMENT OF BOARD (6 spikes_
            x1 = widthMinusBorderAndPieceComponent - TRIANGLE_WIDTH * position;
        } else if (position <= 12) {
            // TOP LEFT
            x1 = widthMinusBorderAndPieceComponent - TRIANGLE_WIDTH * position - Board.BAR;
        } else {
            // BOTTOM
            x1 = widthMinusBorderAndPieceComponent - (TRIANGLE_WIDTH * (25 - position));
            y1 = Board.BORDER + boardHeight;
            if (position <= 18) {
                // BOTTOM LEFT
                 x1 -= Board.BAR;
            }
        }

        x3 = x1 + TRIANGLE_WIDTH;
        y3 = y1;
        collision_x = x1;
        if (type == STALECTITE) {
            x2 = x1 + TRIANGLE_WIDTH / 2;
            y2 = y1 + height();
            collision_y = y1;
        } else {
            x2 = x1 + TRIANGLE_WIDTH / 2;
            y2 = y1 - height();
            collision_y = y1 - height();
        }
    }

    private void log(String s) {
        Utils.log("Spike{}:" + s);
    }

    void makeColourObjects() {
        black_spike_colour = new Color(BLACK_SPIKE_COLOUR);
        white_spike_colour = new Color(WHITE_SPIKE_COLOUR);
    }

    /*
     * when a piece is on the bar,and we are checking which spikes it can go to, and this spike is a valid option
     * for ease we store the die which would get it there in this spike, and grab it later, this is the only instance in which this
     * method is used.
     */
    void store_this_die(Die die) {
        storedDie=die;
    }

    public Die get_stored_die() {
        return storedDie;
    }

    boolean isContainer() {
        return type == SpikeType.CONTAINER;
    }

    Point getMiddlePoint() {
        int dy = type == STALECTITE ? -height() / 2 : height() / 2;
        return new Point(x2, y2 + dy);
    }

    private int height() {
        assert TRIANGLE_HEIGHT > 0;
        return TRIANGLE_HEIGHT - TRIANGLE_HEIGHT / 10;
    }

    private int width() {
        return TRIANGLE_WIDTH;
    }
}
