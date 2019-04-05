package gamelogic;

import data.PlayerColor;
import graphics.GameColour;
import graphics.Geometry;
import lowlevel.CustomCanvas;
import org.aeonbits.owner.ConfigFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.swing.*;

import static org.junit.jupiter.api.Assertions.*;

class TestableBoard extends Board {
    private final int rollValue;
    TestableBoard(GameColour gameColour, Geometry geometry, GameConfig config, int rollValue) {
        super(gameColour, geometry, config);
        this.rollValue = rollValue;
    }

    @Override
    public void rollDies(){
        die1.setActualValue(rollValue);
        die2.setActualValue(rollValue);
    }
}

class BoardTest {

    @Test
    @DisplayName("Board object can be constructed")
    void test1() {
        ConfigFactory.setProperty(
            "configFileName", "somenonexistingconfig.config");
        GameConfig config = ConfigFactory.create(GameConfig.class);

        GameColour colours = new GameColour();
        Geometry geometry = new Geometry(0, 0);
        Board board = new Board(colours, geometry, config);
        CustomCanvas canvas = new CustomCanvas(new JFrame(), colours, geometry, board, config);

        assertEquals(167, board.calculatePips(PlayerColor.WHITE));
        assertEquals(167, board.calculatePips(PlayerColor.BLACK));
        assertEquals(PlayerColor.WHITE, board.getCurrentPlayer().getColour());
    }
}
