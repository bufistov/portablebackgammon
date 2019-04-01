package gamelogic;

import data.PlayerColor;
import lowlevel.CustomCanvas;
import org.aeonbits.owner.ConfigFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class BoardTest {

    @Test
    @DisplayName("Board object can be constructed")
    public void test1() {
        ConfigFactory.setProperty(
            "configFileName", "somenonexistingconfig.config");
        GameConfig config = ConfigFactory.create(GameConfig.class);

        CustomCanvas canvas = new CustomCanvas(config);
        Board board = new Board(canvas, config);

        assertEquals(167, board.calculatePips(PlayerColor.WHITE));
        assertEquals(167, board.calculatePips(PlayerColor.BLACK));
        assertEquals(PlayerColor.WHITE, board.getCurrentPlayer().getColour());
    }

    @Test
    @DisplayName("Board allows only correct moves")
    public void test2() {
        ConfigFactory.setProperty(
            "configFileName", "somenonexistingconfig.config");
        GameConfig config = ConfigFactory.create(GameConfig.class);

        CustomCanvas canvas = new CustomCanvas(config);
        // Given a board with all pieces at home spikes
        Board board = new Board(canvas, config);
        int[] whiteHome = {
            5, 5, 5, 0, 0, 0,
            0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0
        };
        int[] blackHome = {
            0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0,
            5, 5, 5, 0, 0, 0
        };
        board.initialiseBoardForNewGame(whiteHome, blackHome);

        board.calculatePotentialMoves();
    }
}
