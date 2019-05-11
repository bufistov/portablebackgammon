package gamelogic;

import graphics.GameColour;
import graphics.Geometry;
import lowlevel.Utils;

import java.awt.*;

/**
 * A container to store pieces of the player. The game is over when all pieces are
 * in the container.
 */
public class PieceContainer extends Spike {

    private final boolean isWhite;

    PieceContainer(GameColour colours, Geometry geometry, boolean isWhite) {
        super(colours, geometry, -1);
        this.isWhite = isWhite;
    }

    @Override
    public boolean equals(Object that) {
        if (! (that instanceof PieceContainer)) {
            return false;
        }
        PieceContainer thatSpike = (PieceContainer) that;
        return super.equals(that) && isWhite == thatSpike.isWhite;
    }

    @Override
    void paint(Graphics g, Geometry geometry, Color color, int numPieces) {
        final int myX = geometry.containerX();
        int myY = isWhite ? geometry.whiteContainerY() : geometry.blackContainerY();
        for (int i = 0; i < 15; i++) {
            myY += geometry.containerSubSize();
            if (i < numPieces) {
                Utils.fillRect(g, Color.ORANGE,
                    myX, myY, geometry.containerWidth(), geometry.containerSubSize());
            }
            Utils.drawRect(g, color,
                myX, myY, geometry.containerWidth(), geometry.containerSubSize());
        }
    }

    @Override
    boolean userClickedOnThis(int mouseX, int mouseY, Geometry geometry) {
        final int myX = geometry.containerX() - geometry.tinyGap();
        final int myY = containerY(geometry) - geometry.tinyGap();
        return (mouseX >= myX && mouseY <= (myX + geometry.containerWidth() + 2 * geometry.tinyGap())) &&
            (mouseY >= myY && mouseY < (myY + geometry.containerHeight() + 2 * geometry.tinyGap()));
    }

    @Override
    Point firstPieceCenter(Geometry geometry) {
        return new Point(geometry.containerX() + geometry.containerWidth() / 2,
            containerY(geometry) + geometry.containerSubSize());
    }

    @Override
    Color getColor(GameColour colours, boolean flash, boolean allPiecesAtHome) {
        Color color = Color.WHITE;
        if (allPiecesAtHome) {
            color = Color.GREEN;
            if (flash) {
                color = Color.YELLOW;
            }
        }
        return color;
    }

    private int containerY(Geometry geometry) {
        return isWhite ? geometry.whiteContainerY() : geometry.blackContainerY();
    }
}
