package gamelogic;

import graphics.GameColour;
import graphics.Geometry;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.awt.*;

import static org.junit.jupiter.api.Assertions.*;

public class PieceBarTest {
    @Test
    @DisplayName("PieceBar basic functionality")
    void test1() {
        int canvasWidth = 810;
        int canvasHeight = 500;
        Geometry geometry = new Geometry(canvasWidth, canvasHeight);
        GameColour colours = new GameColour();
        PieceBar whitePieceBar = new PieceBar(colours, geometry,true);
        assertFalse(whitePieceBar.isContainer());
        assertTrue(whitePieceBar.isBar());
        assertTrue(whitePieceBar.isEmpty());

        Point whiteCenter = new Point(320, 216);
        assertEquals(whiteCenter, whitePieceBar.firstPieceCenter(geometry));

        PieceBar blackPieceBar = new PieceBar(colours, geometry, false);
        Point blackCenter = new Point(320, 216 + 2 * geometry.pieceDiameter());
        assertEquals(blackCenter, blackPieceBar.firstPieceCenter(geometry));

        assertFalse(whitePieceBar.userClickedOnThis(whiteCenter.x, whiteCenter.y, geometry));
        assertFalse(blackPieceBar.userClickedOnThis(blackCenter.x, blackCenter.y, geometry));

        PieceBar whiteBar2 = new PieceBar(colours, geometry, true);
        assertTrue(whitePieceBar.equals(whiteBar2));
        assertFalse(whitePieceBar.equals(blackPieceBar));

        assertDoesNotThrow(
            () -> whitePieceBar.paint(Mockito.mock(Graphics.class), geometry, Color.WHITE, 1)
        );
        assertDoesNotThrow(
            () -> blackPieceBar.paint(Mockito.mock(Graphics.class), geometry, Color.BLACK, 2)
        );
    }
}
