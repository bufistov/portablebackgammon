package gamelogic;

import data.PlayerColor;
import graphics.Geometry;
import lowlevel.Utils;

import java.awt.*;

public class Piece {

    // colour consts
    private static int WHITE_PIECE_COLOUR = 0xe4e4d8;
    private static int BLACK_PIECE_COLOUR = 0x612d00;
    private static Color white_piece_color, black_piece_color;

    private Geometry geometry;
    private int centerX;
    private int centerY;

    private final PlayerColor colour;
    private Utils utils = new Utils();
    private boolean stickToMouse;
    private int sourceSpikeId;

    Piece(Geometry geometry, PlayerColor playerColor) {
        log("Piece made. father is " + playerColor);
        this.geometry = geometry;
        colour = playerColor;
        makeColourObjects(WHITE_PIECE_COLOUR, BLACK_PIECE_COLOUR);
    }

    public static void makeColourObjects(int WHITE_PIECE_COLOUR, int BLACK_PIECE_COLOUR) {
        white_piece_color = new Color(WHITE_PIECE_COLOUR);
        black_piece_color = new Color(BLACK_PIECE_COLOUR);
    }

    PlayerColor getColour() {
        return colour;
    }

    void paint(Graphics g, int upperLeftX, int upperLeftY) {
        int pieceDiameter = geometry.pieceDiameter();
        if (colour == PlayerColor.WHITE) {
            utils.setColor(g, white_piece_color);
        } else if (colour == PlayerColor.BLACK) {
            utils.setColor(g, black_piece_color);
        }

        utils.fillCircle(g, upperLeftX, upperLeftY, pieceDiameter, pieceDiameter);
        utils.setColor(g, Color.BLACK);
        utils.drawCircle(g, upperLeftX, upperLeftY, pieceDiameter, pieceDiameter);

        centerX = upperLeftX + geometry.pieceRadius();
        centerY = upperLeftY + geometry.pieceRadius();
    }

    void stickToMouse(int spikeId) {
        stickToMouse = true;
        sourceSpikeId = spikeId;
    }

    int sourceSpikeId() {
        return sourceSpikeId;
    }

    boolean stickToMouse() {
        return stickToMouse;
    }
    
    void unstickFromMouse() {
        stickToMouse = false;
    }

    boolean userClickedOnThis(int mouseX, int mouseY) {
       return Math.abs(mouseX - centerX) <= geometry.pieceRadius()
        && Math.abs(mouseY - centerY) <= geometry.pieceRadius();
    }

    private void log(String s) {
        Utils.log("Piece{}:" + s);
    }
}
