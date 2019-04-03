package gamelogic;

import data.PlayerColor;
import lowlevel.CustomCanvas;
import org.aeonbits.owner.ConfigFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.swing.*;

import static org.junit.jupiter.api.Assertions.*;


class BoardTest {

    @Test
    @DisplayName("Board object can be constructed")
    void test1() {
        ConfigFactory.setProperty(
            "configFileName", "somenonexistingconfig.config");
        GameConfig config = ConfigFactory.create(GameConfig.class);

        CustomCanvas canvas = new CustomCanvas(new JFrame(), config);

        Board board = canvas.board;
        assertEquals(167, board.calculatePips(PlayerColor.WHITE));
        assertEquals(167, board.calculatePips(PlayerColor.BLACK));
        assertEquals(PlayerColor.WHITE, board.getCurrentPlayer().getColour());
    }
}
