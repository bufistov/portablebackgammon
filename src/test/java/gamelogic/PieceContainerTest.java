package gamelogic;

import graphics.GameColour;
import graphics.Geometry;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.awt.*;

import static org.junit.jupiter.api.Assertions.*;

class PieceContainerTest {

    @Test
    @DisplayName("PieceContainer basic functionality")
    void test1() {
        int canvasWidth = 810;
        int canvasHeight = 500;
        Geometry geometry = new Geometry(canvasWidth, canvasHeight);
        GameColour colours = new GameColour();
        PieceContainer whitePieceContainer = new PieceContainer(colours, geometry,true);
        assertTrue(whitePieceContainer.isContainer());
        assertFalse(whitePieceContainer.isBar());
        assertTrue(whitePieceContainer.isEmpty());

        Point whiteCenter = new Point(680, 92);
        assertEquals(whiteCenter, whitePieceContainer.firstPieceCenter(geometry));

        PieceContainer blackPieceContainer = new PieceContainer(colours, geometry, false);
        Point blackCenter = new Point(680, 317);
        assertEquals(blackCenter, blackPieceContainer.firstPieceCenter(geometry));

        assertTrue(whitePieceContainer.userClickedOnThis(whiteCenter.x, whiteCenter.y, geometry));
        assertFalse(whitePieceContainer.userClickedOnThis(blackCenter.x, blackCenter.y, geometry));

        assertTrue(blackPieceContainer.userClickedOnThis(blackCenter.x, blackCenter.y, geometry));
        assertFalse(blackPieceContainer.userClickedOnThis(whiteCenter.x, whiteCenter.y, geometry));

        PieceContainer whiteContainer2 = new PieceContainer(colours, geometry, true);
        assertTrue(whitePieceContainer.equals(whiteContainer2));
        assertFalse(whitePieceContainer.equals(blackPieceContainer));

        assertEquals(Color.WHITE, whitePieceContainer.getColor(colours, false, false));
        assertEquals(Color.WHITE, whitePieceContainer.getColor(colours, true, false));
        assertEquals(Color.GREEN, whitePieceContainer.getColor(colours, false, true));
        assertEquals(Color.YELLOW, whitePieceContainer.getColor(colours, true, true));

        assertDoesNotThrow(
            () -> whitePieceContainer.paint(Mockito.mock(Graphics.class), geometry, Color.WHITE, 1)
        );
        assertDoesNotThrow(
            () -> blackPieceContainer.paint(Mockito.mock(Graphics.class), geometry, Color.BLACK, 2)
        );
    }
}
