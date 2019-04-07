package gamelogic;
import java.awt.Color;
import java.awt.Graphics;

import data.PlayerColor;
import graphics.Geometry;
import lowlevel.*;

public class Piece {

    // colour consts
    public static int WHITE_PIECE_COLOUR = 0xe4e4d8;
    public static int BLACK_PIECE_COLOUR = 0x612d00;
    public static int WHITE_PIECE_INNER_COLOUR = 0xc0c0c0;
    public static int BLACK_PIECE_INNER_COLOUR = 0x452402;
    private static Color white_piece_color, black_piece_color;

    private Geometry geometry;
    private int centerX;
    private int centerY;

    private PlayerColor colour;
    private Utils utils = new Utils();
    private boolean stickToMouse;
    private int sourceSpikeId;

    Piece(Geometry geometry, Player father) {
        if (father == null) {
            Utils._E("Piece was made with a null father");
        }
        log("Piece made. father is " + father.getColour());
        this.geometry = geometry;
        colour = father.getColour();
        makeColourObjects();
    }

    public static void makeColourObjects() {
        white_piece_color = new Color(WHITE_PIECE_COLOUR);
        black_piece_color = new Color(BLACK_PIECE_COLOUR);
    }

    public PlayerColor getColour()
    {
        return colour;
    }

    public int getCenterX() {
        return centerX;
    }

    public int getCenterY() {
        return centerY;
    }

    public void drawPieceOnMouse(Graphics g) {
        paint( g, 0, 0);
    }

    public void paint(Graphics g, int x, int y) {
        int pieceDiameter = geometry.pieceDiameter();
        if (colour == PlayerColor.WHITE) {
            utils.setColor(g, white_piece_color);
        } else if (colour == PlayerColor.BLACK) {
            utils.setColor(g, black_piece_color);
        } else {
            Utils._E("Dave, Im a bit worried that this piece doesnt know what colour it is.");
        }

        if (stickToMouse) {
            x = Board.mouseHoverX;
            y = Board.mouseHoverY;
        }
        utils.fillCircle(g, x, y, pieceDiameter, pieceDiameter);
        utils.setColor(g, Color.BLACK);
        utils.drawCircle(g, x, y, pieceDiameter, pieceDiameter);

        centerX = x + geometry.pieceRadius();
        centerY = y + geometry.pieceRadius();
        if (CustomCanvas.showBoundaryBoxes) {
            utils.setColor(g,Color.RED);
            utils.drawRect(g, x, y, pieceDiameter, pieceDiameter);
        }
    }

    //this is called to tell the piece to use the x, y vals from the mouse
    //instead of its usual ones as the user is placing it and it needs to stick to the
    //mouse point until they do place it.
    public void stickToMouse(int spikeId) {
        stickToMouse = true;
        sourceSpikeId = spikeId;
    }

    public int sourceSpikeId() {
        return sourceSpikeId;
    }
    
    public void unstickFromMouse() {
        stickToMouse = false;
    }

    public boolean userClickedOnThis(int mouseX, int mouseY) {
       return Math.abs(mouseX - centerX) <= geometry.pieceRadius()
        && Math.abs(mouseY - centerY) <= geometry.pieceRadius();
    }

    private void log(String s) {
        Utils.log("Piece{}:" + s);
    }
}
