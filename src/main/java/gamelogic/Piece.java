package gamelogic;

import data.PlayerColor;
import graphics.Geometry;
import lowlevel.Utils;

import java.awt.*;

class Piece {

    private int centerX;
    private int centerY;

    private final PlayerColor colour;
    private boolean stickToMouse;
    private int sourceSpikeId;

    Piece(PlayerColor playerColor) {
        log("Piece made. father is " + playerColor);
        colour = playerColor;
    }

    PlayerColor getColour() {
        return colour;
    }

    void paint(Graphics g, Color color, Geometry geometry, int upperLeftX, int upperLeftY) {
        int pieceDiameter = geometry.pieceDiameter();
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

    boolean userClickedOnThis(int mouseX, int mouseY, int pieceRadius) {
       return Math.abs(mouseX - centerX) <= pieceRadius
        && Math.abs(mouseY - centerY) <= pieceRadius;
    }

    private void log(String s) {
        Utils.log("Piece{}:" + s);
    }
}
