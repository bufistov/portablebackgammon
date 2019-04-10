package gamelogic;

import data.DieType;
import data.PlayerColor;
import graphics.GameColour;
import graphics.Geometry;
import org.aeonbits.owner.ConfigFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.awt.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Vector;

import static org.junit.jupiter.api.Assertions.*;

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
        board.setCurrentPlayer(board.getWhitePlayer());
        board.rollDies();

        assertEquals(DieType.DIE2, board.whichDieGetsUsToPieceContainer(board.getWhitePlayer(), 1));

        Exception error = assertThrows(Exception.class, () -> board.whichDieGetsUsToPieceContainer(board.getWhitePlayer(), 0));
        assertEquals("Source spike does not belongs to WHITE player", error.getMessage());

        error = assertThrows(Exception.class, () -> board.whichDieGetsUsToPieceContainer(board.getBlackPlayer(), 20));
        assertEquals("Container cannot be reached from spike 20 die1: 1, die2: 2", error.getMessage());
    }

    @Test
    @DisplayName("Board calculates valid moves from the bar")
    void test3() throws Exception {
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
        Field theBarWHITE = Board.class.getDeclaredField("theBarWHITE");
        theBarWHITE.setAccessible(true);
        ((Spike)theBarWHITE.get(board)).addPiece(new Piece(geometry, board.getWhitePlayer()));
        board.setCurrentPlayer(board.getWhitePlayer());
        board.rollDies();
        ArrayList<Spike> spikes = board.spikesToMoveToFromBar();
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
        board.setCurrentPlayer(board.getBlackPlayer());
        board.rollDies();
        assertEquals(DieType.DIE2, board.whichDieGetsUsToPieceContainer(board.getBlackPlayer(), 19));
        assertThrows(Exception.class, ()->board.whichDieGetsUsToPieceContainer(board.getBlackPlayer(), 20));
    }

    @Test
    @DisplayName("Board allPiecesAreHome")
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
    @DisplayName("Board allPiecesAreHome, some at container")
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
        Field theBarWHITE = Board.class.getDeclaredField("theBarWHITE");
        theBarWHITE.setAccessible(true);
        ((Spike)theBarWHITE.get(board)).pieces.remove(0);
        Field whitePiecesSafelyInContainer = Board.class.getDeclaredField("whitePiecesSafelyInContainer");
        whitePiecesSafelyInContainer.setAccessible(true);
        ((Vector) whitePiecesSafelyInContainer.get(board)).add(new Piece(geometry, board.getWhitePlayer()));
        board.checkConsistent();

        Method allPiecesAreHome = Board.class.getDeclaredMethod("allPiecesAreHome", Player.class);
        allPiecesAreHome.setAccessible(true);
        assertTrue((Boolean)allPiecesAreHome.invoke(board, board.getWhitePlayer()));
        assertFalse((Boolean)allPiecesAreHome.invoke(board, board.getBlackPlayer()));
    }

    @Test
    @DisplayName("Board pulsateContainer")
    void test7() throws Exception {
        Board board = new TestableBoard(colours, geometry, config, 3);
        Method pulsateContainer = Board.class.getDeclaredMethod("pulsateContainer",
            Player.class, int.class);
        pulsateContainer.setAccessible(true);
        for (int spikeId = 0; spikeId < board.getSpikes().size(); ++spikeId) {
            assertFalse((Boolean) pulsateContainer.invoke(board, board.getWhitePlayer(), spikeId));
            assertFalse((Boolean) pulsateContainer.invoke(board, board.getBlackPlayer(), spikeId));
        }

        int[] whiteHome = {
            0,0,0,0,14,1,
            0,0,0,0,0,0,
            0,0,0,0,0,0,
            0,0,0,0,0,0
        };
        int[] blackHome = {
            0,0,0,0,0,0,
            0,0,0,0,0,0,
            0,0,0,0,0,0,
            0,6,9,0,0,0
        };
        board.initialiseBoardForNewGame(whiteHome, blackHome);
        board.checkConsistent();
        board.rollDies();
        board.setCurrentPlayer(board.getWhitePlayer());
        assertTrue((Boolean) pulsateContainer.invoke(board, board.getWhitePlayer(), 5));
        assertFalse((Boolean) pulsateContainer.invoke(board, board.getWhitePlayer(), 4));
        assertFalse((Boolean) pulsateContainer.invoke(board, board.getWhitePlayer(), 2));

        assertTrue((Boolean) pulsateContainer.invoke(board, board.getBlackPlayer(), 19));
        board.setCurrentPlayer(board.getBlackPlayer());
        assertEquals(PlayerColor.BLACK, board.getCurrentPlayer().getColour());
        assertFalse((Boolean) pulsateContainer.invoke(board, board.getBlackPlayer(), 20));
        assertFalse((Boolean) pulsateContainer.invoke(board, board.getBlackPlayer(), 21));
        assertFalse((Boolean) pulsateContainer.invoke(board, board.getBlackPlayer(), 22));
        assertFalse((Boolean) pulsateContainer.invoke(board, board.getBlackPlayer(), 18));
    }

    @Test
    @DisplayName("can we get of the bar with this dies")
    void test8() throws Exception {
        Board board = new Board(colours, geometry, config);
        Method canWeGetOffTheBarWithThisDie = Board.class.getDeclaredMethod("canWeGetOffTheBarWithThisDie",
            Die.class, DieType.class, ArrayList.class);
        canWeGetOffTheBarWithThisDie.setAccessible(true);
        board.checkConsistent();
        board.rollDies();
        board.setCurrentPlayer(board.getWhitePlayer());
        Die die = new Die(geometry);
        die.setValue(3);
        assertFalse((Boolean) canWeGetOffTheBarWithThisDie.invoke(board, die, DieType.DIE1, null));

        int[] whiteHome = {
            0,0,0,0,14,0,
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
        board.checkConsistent();
        assertTrue((Boolean) canWeGetOffTheBarWithThisDie.invoke(board, die, DieType.DIE1, null));
        assertTrue((Boolean) canWeGetOffTheBarWithThisDie.invoke(board, die, DieType.DIE2, null));
        die.setValue(4);
        assertFalse((Boolean) canWeGetOffTheBarWithThisDie.invoke(board, die, DieType.DIE1, null));
        die.setValue(5);
        assertFalse((Boolean) canWeGetOffTheBarWithThisDie.invoke(board, die, DieType.DIE1, null));
        die.roll();
        die.setValue(6);
        assertTrue((Boolean) canWeGetOffTheBarWithThisDie.invoke(board, die, DieType.DIE1, null));
        die.roll();
        die.disable();
        assertFalse((Boolean) canWeGetOffTheBarWithThisDie.invoke(board, die, DieType.DIE1, null));
    }

    @Test
    @DisplayName("4 moves with double to container")
    void test9() throws Exception {
        Board board = new TestableBoard(colours, geometry, config, 5);
        int[] whiteHome = {
            0,0,0,0,4,0,
            0,0,0,0,0,0,
            0,0,0,0,0,0,
            0,0,0,0,0,0,11
        };
        int[] blackHome = {
            0,2,2,0,0,0,
            0,0,0,0,0,0,
            0,0,0,0,0,0,
            0,6,5,0,0,0
        };
        board.initialiseBoardForNewGame(whiteHome, blackHome);
        board.checkConsistent();
        board.rollDies();
        assertTrue(board.getCurrentPlayer().isWhite());
        Graphics graphics = Mockito.mock(Graphics.class);
        board.paint(graphics, geometry.boardWidth(), geometry.boardHeight(), true, 0, 0);
        for (int i = 0; i < 4; ++i) {
            assertFalse(board.turnOver());
            Point spike = board.getSpikes().get(4).leftMostPoint();
            board.checkIfPieceClickedOn(spike.x + geometry.spikeWidth() / 2, spike.y + geometry.pieceRadius());
            Point containerMiddle = new Point(geometry.containerX(), geometry.whiteContainerY() + geometry.containerHeight() / 2);
            board.checkIfPieceContainerClickedOn(containerMiddle.x, containerMiddle.y);
            // repaint to update coordinates of pieces on spike
            board.paint(graphics, geometry.boardWidth(), geometry.boardHeight(), true, 0, 0);
        }
        assertTrue(board.turnOver());
        assertTrue(board.gameIsOver());
    }

    @Test
    @DisplayName("4 moves with double to container, double move")
    void test10() throws Exception {
        Board board = new TestableBoard(colours, geometry, config, 2);
        int[] whiteHome = {
            0,0,0,4,0,0,
            0,0,0,0,0,0,
            0,0,0,0,0,0,
            0,0,0,0,0,0,11
        };
        int[] blackHome = {
            2,0,2,0,0,0,
            0,0,0,0,0,0,
            0,0,0,0,0,0,
            0,6,5,0,0,0
        };
        board.initialiseBoardForNewGame(whiteHome, blackHome);

        for (int i = 0; i < 2; ++i) {
            board.checkConsistent();
            board.setCurrentPlayer(board.getWhitePlayer());
            board.rollDies();
            assertTrue(board.getCurrentPlayer().isWhite());
            Graphics graphics = Mockito.mock(Graphics.class);
            board.paint(graphics, geometry.boardWidth(), geometry.boardHeight(), true, 0, 0);

            Point sourceSpike = board.getSpikes().get(3).leftMostPoint();
            board.checkIfPieceClickedOn(sourceSpike.x + geometry.spikeWidth() / 2, sourceSpike.y + geometry.pieceRadius());
            Point destinationSpike = board.getSpikes().get(1).leftMostPoint();
            destinationSpike = new Point(destinationSpike.x + geometry.spikeWidth() / 2,
                destinationSpike.y + geometry.pieceRadius());
            board.checkIfSpikeClickedOn(destinationSpike.x, destinationSpike.y);
            board.paint(graphics, geometry.boardWidth(), geometry.boardHeight(), true, 0, 0);

            board.checkIfPieceClickedOn(destinationSpike.x, destinationSpike.y);
            Point containerMiddle = new Point(geometry.containerX(), geometry.whiteContainerY() + geometry.containerHeight() / 2);
            board.checkIfPieceContainerClickedOn(containerMiddle.x, containerMiddle.y);
            board.paint(graphics, geometry.boardWidth(), geometry.boardHeight(), true, 0, 0);

            // Container still have to be reachable from source spike
            assertTrue(board.die1.enabled());
            assertTrue(board.die2.enabled());

            board.checkIfPieceClickedOn(sourceSpike.x + geometry.spikeWidth() / 2, sourceSpike.y + geometry.pieceRadius());
            board.checkIfSpikeClickedOn(destinationSpike.x, destinationSpike.y);
            board.paint(graphics, geometry.boardWidth(), geometry.boardHeight(), true, 0, 0);
            board.checkIfPieceClickedOn(destinationSpike.x, destinationSpike.y);
            board.checkIfPieceContainerClickedOn(containerMiddle.x, containerMiddle.y);
            assertTrue(board.turnOver());
            if (!board.gameIsOver())
                board.nextTurn();
        }
        assertTrue(board.turnOver());
        assertTrue(board.gameIsOver());
    }
}
