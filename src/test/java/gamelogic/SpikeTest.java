package gamelogic;

import data.PlayerColor;
import data.SpikeType;
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
        Spike spike = new Spike(geometry,2);
        assertEquals(2, spike.getPosition());
        assertEquals(1, spike.getSpikeNumber());
        assertEquals(0, spike.getAmountOfPieces(PlayerColor.WHITE));
        assertEquals(0, spike.getAmountOfPieces(PlayerColor.BLACK));
        assertEquals("1", spike.getName());
        assertEquals(SpikeType.STALECTITE, spike.getType());

        Graphics graphics = Mockito.mock(Graphics.class);
        spike.paint(graphics);
        int middleX = geometry.boardWidth() - 2 * geometry.spikeWidth() -
            geometry.borderWidth() + geometry.spikeWidth() / 2;
        int middleY = geometry.borderWidth() + geometry.spikeHeight() / 2;
        assertEquals(middleX, spike.getMiddlePoint().x);
        assertEquals(middleY, spike.getMiddlePoint().y);
        assertTrue(spike.userClickedOnThis(middleX, middleY));
    }

    @Test
    @DisplayName("Spike calculates center of the first piece")
    void test2() {
        int canvasWidth = 810;
        int canvasHeight = 500;
        Geometry geometry = new Geometry(canvasWidth, canvasHeight);
        Spike spike = new Spike(geometry, 2);
        spike.paint(Mockito.mock(Graphics.class));
        Point actual = spike.firstPieceCenter();
        Point expected = spike.getMiddlePoint();
        expected.y -= geometry.spikeHeight() / 2;
        expected.y += geometry.pieceRadius();
        assertEquals(expected, actual);

        Spike spike2 = new Spike(geometry, 14);
        spike2.paint(Mockito.mock(Graphics.class));

        actual = spike2.firstPieceCenter();
        expected.x = geometry.borderWidth() + geometry.spikeWidth() * 3 / 2;
        expected.y = geometry.boardHeight() - geometry.borderWidth() - geometry.pieceRadius();
        assertEquals(expected, actual);
    }
}
