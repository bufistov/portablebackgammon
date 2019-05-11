package gamelogic;

import data.PlayerColor;
import data.SpikeType;
import graphics.GameColour;
import graphics.Geometry;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.awt.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SpikeTest {

    @Test
    @DisplayName("Spike can be constructed")
    void test1() {
        int canvasWidth = 810;
        int canvasHeight = 500;
        Geometry geometry = new Geometry(canvasWidth, canvasHeight);
        GameColour colours = new GameColour();
        Spike spike = new Spike(colours, geometry,2);
        assertEquals(2, spike.getPosition());
        assertEquals(1, spike.getSpikeNumber());
        assertEquals(0, spike.getAmountOfPieces(PlayerColor.WHITE));
        assertEquals(0, spike.getAmountOfPieces(PlayerColor.BLACK));
        assertEquals("1", spike.getName());
        assertEquals(SpikeType.STALECTITE, spike.getType());

        Graphics graphics = Mockito.mock(Graphics.class);
        spike.paint(graphics, geometry, Color.WHITE, 0);
        int middleX = geometry.boardWidth() - 2 * geometry.spikeWidth() -
            geometry.borderWidth() + geometry.spikeWidth() / 2;
        int middleY = geometry.borderWidth() + geometry.spikeHeight() / 2;
        assertEquals(middleX, spike.getMiddlePoint(geometry).x);
        assertEquals(middleY, spike.getMiddlePoint(geometry).y);
        assertTrue(spike.userClickedOnThis(middleX, middleY, geometry));
    }

    @Test
    @DisplayName("Spike calculates center of the first piece")
    void test2() {
        int canvasWidth = 810;
        int canvasHeight = 500;
        Geometry geometry = new Geometry(canvasWidth, canvasHeight);
        Spike spike = new Spike(new GameColour(), geometry, 2);
        spike.paint(Mockito.mock(Graphics.class), geometry, Color.WHITE, spike.pieces.size());
        Point actual = spike.firstPieceCenter(geometry);
        Point expected = spike.getMiddlePoint(geometry);
        expected.y -= geometry.spikeHeight() / 2;
        expected.y += geometry.pieceRadius();
        assertEquals(expected, actual);

        Spike spike2 = new Spike(new GameColour(), geometry, 14);
        spike2.paint(Mockito.mock(Graphics.class), geometry, Color.WHITE, 0);

        actual = spike2.firstPieceCenter(geometry);
        expected.x = geometry.borderWidth() + geometry.spikeWidth() * 3 / 2;
        expected.y = geometry.boardHeight() - geometry.borderWidth() - geometry.pieceRadius();
        assertEquals(expected, actual);
    }
}
