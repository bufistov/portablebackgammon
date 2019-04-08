package gamelogic;

import data.DieType;
import data.PlayerColor;
import graphics.GameColour;
import graphics.Geometry;
import lowlevel.CustomCanvas;
import org.aeonbits.owner.ConfigFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
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

    private GameConfig config;
    private GameColour colours;
    private Geometry geometry;

    @BeforeEach
    void beforeEach() {
        ConfigFactory.setProperty("configFileName", "somenonexistingconfig.config");
        config = ConfigFactory.create(GameConfig.class);
        colours = new GameColour();
        geometry = new Geometry(810, 500);
    }
    @Test
    @DisplayName("Board object can be constructed")
    void test1() {
        Board board = new Board(colours, geometry, config);
        assertEquals(167, board.calculatePips(PlayerColor.WHITE));
        assertEquals(167, board.calculatePips(PlayerColor.BLACK));
        assertEquals(PlayerColor.WHITE, board.getCurrentPlayer().getColour());
    }

    @Test
    @DisplayName("Board calculates which die gets us to the container")
    void test2() throws Exception {
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
        assertEquals("Container cannot be reached from spike 20 die1: 1, die2: 2", error.getMessage());
    }

    @Test
    @DisplayName("Board calculates valid moves from the bar")
    void test3() {
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

    @Test
    @DisplayName("Board calculates which die gets us to the container")
    void test4() throws Exception {
        Board board = new TestableBoard(colours, geometry, config, 0, 5);

        int[] whiteHome = {
            0,15,0,0,0,0,
            0,0,0,0,0,0,
            0,0,0,0,0,0,
            0,0,0,0,0,0
        };

        int[] blackHome = {
            0,0,0,0,0,0,
            0,0,0,0,0,0,
            0,0,0,0,0,0,
            0,7,8,0,0,0
        };
        board.initialiseBoardForNewGame(whiteHome, blackHome);
        board.setCurrentPlayer(PlayerColor.BLACK);
        board.rollDies();
        assertEquals(DieType.DIE2, board.whichDieGetsUsToPieceContainer(board.getBlackPlayer(), 19));
    }

    @Test
    @DisplayName("Board everybodyAtHome")
    void test5() throws Exception {
        Board board = new Board(colours, geometry, config);
        Method allPiecesAreHome = Board.class.getDeclaredMethod("allPiecesAreHome", Player.class);
        allPiecesAreHome.setAccessible(true);
        assertFalse((Boolean)allPiecesAreHome.invoke(board, board.getWhitePlayer()));
        assertFalse((Boolean)allPiecesAreHome.invoke(board, board.getBlackPlayer()));

        // When all pieces are moved home, we realize this
        board.initialiseBoard(1);
        assertTrue((Boolean)allPiecesAreHome.invoke(board, board.getWhitePlayer()));
        assertTrue((Boolean)allPiecesAreHome.invoke(board, board.getBlackPlayer()));
    }

    @Test
    @DisplayName("Board everybodyAtHome, some at container")
    void test6() throws Exception {
        Board board = new Board(colours, geometry, config);
        int[] whiteHome = {
            0,14,0,0,0,0,
            0,0,0,0,0,0,
            0,0,0,0,0,0,
            0,0,0,0,0,0
        };
        int[] blackHome = {
            0,0,0,0,0,0,
            0,0,0,0,0,0,
            0,0,0,0,0,0,
            0,6,8,0,0,0
        };
        board.initialiseBoardForNewGame(whiteHome, blackHome);
        CustomCanvas.theBarWHITE.remove(0);
        CustomCanvas.whitePiecesSafelyInContainer.add(new Piece(geometry, board.getWhitePlayer()));
        board.checkConsistent();

        Method allPiecesAreHome = Board.class.getDeclaredMethod("allPiecesAreHome", Player.class);
        allPiecesAreHome.setAccessible(true);
        assertTrue((Boolean)allPiecesAreHome.invoke(board, board.getWhitePlayer()));
        assertFalse((Boolean)allPiecesAreHome.invoke(board, board.getBlackPlayer()));
    }
}
