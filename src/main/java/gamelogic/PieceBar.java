package gamelogic;

import graphics.GameColour;
import graphics.Geometry;

import java.awt.*;
import java.util.Enumeration;

public class PieceBar extends Spike {
    private final boolean isWhite;

    PieceBar(GameColour colours, Geometry geometry, boolean isWhite) {
        super(colours, geometry, 25);
        this.isWhite = isWhite;
    }

    @Override
    public boolean equals(Object that) {
        if (! (that instanceof PieceBar)) {
            return false;
        }
        PieceBar thatBar = (PieceBar) that;
        return super.equals(that) && isWhite == thatBar.isWhite;
    }

    @Override
    void paint(Graphics g, Geometry geometry, Color color, int numPieces) {
        int pieceOnBarY = isWhite ? geometry.whitePieceOnBarY() : geometry.blackPieceOnBarY();
        Enumeration eW = pieces.elements();
        final int pieceX = geometry.pieceOnBarX();
        while (eW.hasMoreElements()) {
            Piece p = (Piece) eW.nextElement();
            if (!p.stickToMouse()) {
                p.paint(g, color, geometry.pieceDiameter(), pieceX, pieceOnBarY);
                pieceOnBarY -= geometry.pieceDiameter();
            }
        }
    }

    @Override
    boolean userClickedOnThis(int mouseX, int mouseY, Geometry geometry) {
        return false;
    }

    @Override
    Point firstPieceCenter(Geometry geometry) {
        return new Point(geometry.pieceOnBarX() + geometry.pieceRadius(), barY(geometry));
    }


    private int barY(Geometry geometry) {
        return geometry.pieceRadius() + (isWhite ? geometry.whitePieceOnBarY() : geometry.blackPieceOnBarY());
    }
}
