package gamelogic;

import data.DieType;
import data.PlayerColor;
import graphics.GameColour;
import graphics.Geometry;
import lowlevel.CustomCanvas;
import org.aeonbits.owner.ConfigFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.swing.*;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class TestableBoard extends Board {
    private final int roll1Value;
    private final int roll2Value;
    TestableBoard(GameColour gameColour, Geometry geometry, GameConfig config, int roll1Value,
                  int roll2Value) {
        super(gameColour, geometry, config);
        this.roll1Value = roll1Value;
        this.roll2Value = roll2Value;
    }

    TestableBoard(GameColour gameColour, Geometry geometry, GameConfig config, int rollValue) {
        this(gameColour, geometry, config, rollValue, rollValue);
    }

    @Override
    public void rollDies(){
        die1.setActualValue(roll1Value);
        die2.setActualValue(roll2Value);
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

    @Test
    @DisplayName("Board calculates which die gets us to the container")
    void test2() throws Exception {
        ConfigFactory.setProperty("configFileName", "somenonexistingconfig.config");
        GameConfig config = ConfigFactory.create(GameConfig.class);
        GameColour colours = new GameColour();
        Geometry geometry = new Geometry(810, 500);
        Board board = new TestableBoard(colours, geometry, config, 1, 2);

        int[] whiteHome = {
            0,15,0,0,0,0,
            0,0,0,0,0,0,
            0,0,0,0,0,0,
            0,0,0,0,0,0
        };

        int[] blackHome = {
            2,0,0,0,0,0,
            0,0,0,0,0,0,
            0,0,0,0,0,0,
            0,0,13,0,0,0
        };
        board.initialiseBoardForNewGame(whiteHome, blackHome);
        board.setCurrentPlayer(PlayerColor.WHITE);
        board.rollDies();
        assertEquals(DieType.DIE2, board.whichDieGetsUsToPieceContainer(board.getWhitePlayer(), 1));
        Exception error = assertThrows(Exception.class, () -> board.whichDieGetsUsToPieceContainer(board.getWhitePlayer(), 0));
        assertEquals("Source spike does not belongs to WHITE player", error.getMessage());

        error = assertThrows(Exception.class, () -> board.whichDieGetsUsToPieceContainer(board.getBlackPlayer(), 20));
        assertEquals("Container cannot be reached from spike 20", error.getMessage());
    }

    @Test
    @DisplayName("Board calculates valid moves from the bar")
    void test3() throws Exception {
        ConfigFactory.setProperty("configFileName", "somenonexistingconfig.config");
        GameConfig config = ConfigFactory.create(GameConfig.class);
        GameColour colours = new GameColour();
        Geometry geometry = new Geometry(810, 500);
        Board board = new TestableBoard(colours, geometry, config, 1, 2);

        int[] whiteHome = {
            0, 0, 0, 0, 0, 14,
            0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0
        };
        int[] blackHome = {
            0, 0, 0, 0, 2, 0,
            0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0,
            0, 0, 11, 2, 0, 0
        };
        board.initialiseBoardForNewGame(whiteHome, blackHome);
        CustomCanvas.theBarWHITE.add(new Piece(geometry, board.getWhitePlayer()));
        board.setCurrentPlayer(PlayerColor.WHITE);
        board.rollDies();
        ArrayList<Spike> spikes = board.spikesToMoveToFromBar(PlayerColor.WHITE);
        assertEquals(2, spikes.size());
        assertEquals(24, spikes.get(0).getPosition());
        assertEquals(23 , spikes.get(1).getPosition());
    }
}
