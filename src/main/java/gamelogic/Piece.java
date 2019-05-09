package gamelogic;

import data.PlayerColor;
import graphics.GameColour;
import graphics.Geometry;
import lowlevel.Utils;

import java.awt.*;

class Piece {

    private Geometry geometry;
    private int centerX;
    private int centerY;

    private final PlayerColor colour;
    private boolean stickToMouse;
    private int sourceSpikeId;

    Piece(Geometry geometry, PlayerColor playerColor) {
        log("Piece made. father is " + playerColor);
        this.geometry = geometry;
        colour = playerColor;
    }

    PlayerColor getColour() {
        return colour;
    }

    void paint(Graphics g, GameColour colours, int upperLeftX, int upperLeftY) {
        int pieceDiameter = geometry.pieceDiameter();
        Color color = (colour == PlayerColor.WHITE) ? colours.getWhitePiece() : colours.getBlackPiece();
        Utils.fillCircle(g, color, upperLeftX, upperLeftY, pieceDiameter, pieceDiameter);
        Utils.drawCircle(g, Color.BLACK, upperLeftX, upperLeftY, pieceDiameter, pieceDiameter);

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
