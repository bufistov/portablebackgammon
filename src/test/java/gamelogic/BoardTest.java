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
            0,
            0,15,0,0,0,0,
            0,0,0,0,0,0,
            0,0,0,0,0,0,
            0,0,0,0,0,0,
            0
        };

        int[] blackHome = {
            0,
            0,0,0,13,0,0,
            0,0,0,0,0,0,
            0,0,0,0,0,0,
            0,0,0,0,0,2,
            0
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
            0,
            0, 0, 0, 0, 0, 14,
            0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0,
            1
        };
        int[] blackHome = {
            0,
            0, 0, 2, 11, 0, 0,
            0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0,
            0, 2, 0, 0, 0, 0,
            0
        };
        board.initialiseBoardForNewGame(whiteHome, blackHome);
        Field theBarWHITE = Board.class.getDeclaredField("theBarWHITE");
        theBarWHITE.setAccessible(true);
        ((Spike)theBarWHITE.get(board)).addPiece(new Piece(geometry, PlayerColor.WHITE));
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
            0,
            0,15,0,0,0,0,
            0,0,0,0,0,0,
            0,0,0,0,0,0,
            0,0,0,0,0,0,
            0
        };

        int[] blackHome = {
            0,
            0,0,0,8,7,0,
            0,0,0,0,0,0,
            0,0,0,0,0,0,
            0,0,0,0,0,0,
            0
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
            0,
            0,14,0,0,0,0,
            0,0,0,0,0,0,
            0,0,0,0,0,0,
            0,0,0,0,0,0,
            1
        };
        int[] blackHome = {
            0,
            0,0,0,8,6,0,
            0,0,0,0,0,0,
            0,0,0,0,0,0,
            0,0,0,0,0,0,
            1
        };
        board.initialiseBoardForNewGame(whiteHome, blackHome);
        Field theBarWHITE = Board.class.getDeclaredField("theBarWHITE");
        theBarWHITE.setAccessible(true);
        ((Spike)theBarWHITE.get(board)).pieces.remove(0);
        Field whitePiecesSafelyInContainer = Board.class.getDeclaredField("whitePiecesSafelyInContainer");
        whitePiecesSafelyInContainer.setAccessible(true);
        ((Vector) whitePiecesSafelyInContainer.get(board)).add(new Piece(geometry, PlayerColor.WHITE));
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
            0,
            0,0,0,0,14,1,
            0,0,0,0,0,0,
            0,0,0,0,0,0,
            0,0,0,0,0,0,
            0
        };
        int[] blackHome = {
            0,
            0,0,0,9,6,0,
            0,0,0,0,0,0,
            0,0,0,0,0,0,
            0,0,0,0,0,0,
            0
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
            0,
            0,0,0,0,14,0,
            0,0,0,0,0,0,
            0,0,0,0,0,0,
            0,0,0,0,0,0,
            1
        };
        int[] blackHome = {
            0,
            0,0,0,8,6,0,
            0,0,0,0,0,0,
            0,0,0,0,0,0,
            0,0,0,0,0,0,
            1
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
            11,
            0,0,0,0,4,0,
            0,0,0,0,0,0,
            0,0,0,0,0,0,
            0,0,0,0,0,0,
            0
        };
        int[] blackHome = {
            0,
            0,0,0,5,6,0,
            0,0,0,0,0,0,
            0,0,0,0,0,0,
            0,0,0,2,2,0,
            0
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
            11,
            0,0,0,4,0,0,
            0,0,0,0,0,0,
            0,0,0,0,0,0,
            0,0,0,0,0,0,
            0
        };
        int[] blackHome = {
            0,
            0,0,0,5,6,0,
            0,0,0,0,0,0,
            0,0,0,0,0,0,
            0,0,0,2,0,2,
            0
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

    @Test
    @DisplayName("Pulsate not-container spikes, init configuration")
    void test11() {
        Board board = new TestableBoard(colours, geometry, config, 5, 2);
        Graphics graphics = Mockito.mock(Graphics.class);

        // No rolls, no flashing
        board.paint(graphics, geometry.boardHeight(), geometry.boardWidth(), true, 0, 0);
        assertTrue(board.showRollButton());
        for (Spike spike: board.getSpikes()) {
            assertFalse(spike.isFlashed());
        }

        // When dies are rolled
        board.rollDies();
        // And mouse is hovering over spike 23, which belongs to white player and have two pieces at start
        Point spikeMiddle = board.getSpikes().get(23).getMiddlePoint();
        board.paint(graphics, geometry.boardHeight(), geometry.boardWidth(),
            true, spikeMiddle.x, spikeMiddle.y);

        ArrayList<Integer> flashed = new ArrayList<>();
        flashed.add(21);
        for (Spike spike: board.getSpikes()) {
            assertEquals(flashed.contains(spike.getSpikeNumber()), spike.isFlashed());
        }

        // TODO: check containers are not flushed
        board.setCurrentPlayer(board.getBlackPlayer());
        board.paint(graphics, geometry.boardHeight(), geometry.boardWidth(),
            true, spikeMiddle.x, spikeMiddle.y);

        for (Spike spike: board.getSpikes()) {
            assertFalse(spike.isFlashed());
        }

        spikeMiddle = board.getSpikes().get(0).getMiddlePoint();
        board.paint(graphics, geometry.boardHeight(), geometry.boardWidth(),
            true, spikeMiddle.x, spikeMiddle.y);

        flashed.clear();
        flashed.add(2);
        for (Spike spike: board.getSpikes()) {
            assertEquals(flashed.contains(spike.getSpikeNumber()), spike.isFlashed());
        }

        spikeMiddle = board.getSpikes().get(1).getMiddlePoint();
        board.paint(graphics, geometry.boardHeight(), geometry.boardWidth(), true, spikeMiddle.x, spikeMiddle.y);
        for (Spike spike: board.getSpikes()) {
            assertFalse(spike.isFlashed());
        }

        spikeMiddle = board.getSpikes().get(11).getMiddlePoint();
        board.paint(graphics, geometry.boardHeight(), geometry.boardWidth(), true, spikeMiddle.x, spikeMiddle.y);

        flashed.clear();
        flashed.add(13);
        flashed.add(16);
        flashed.add(18);
        for (Spike spike: board.getSpikes()) {
            assertEquals(flashed.contains(spike.getSpikeNumber()), spike.isFlashed());
        }

        spikeMiddle = board.getSpikes().get(18).getMiddlePoint();
        board.paint(graphics, geometry.boardHeight(), geometry.boardWidth(), true, spikeMiddle.x, spikeMiddle.y);
        flashed.clear();
        flashed.add(20);
        for (Spike spike: board.getSpikes()) {
            assertEquals(flashed.contains(spike.getSpikeNumber()), spike.isFlashed());
        }
    }

    @Test
    @DisplayName("Pulsate not-container spikes, init configuration")
    void test12() {
        Board board = new TestableBoard(colours, geometry, config, 5, 3);
        Graphics graphics = Mockito.mock(Graphics.class);

        // No rolls, no flashing
        board.paint(graphics, geometry.boardHeight(), geometry.boardWidth(), true, 0, 0);
        assertTrue(board.showRollButton());
        for (Spike spike: board.getSpikes()) {
            assertFalse(spike.isFlashed());
        }

        // When dies are rolled
        board.rollDies();
        // And mouse is hovering over spike 23, which belongs to white player and have two pieces at start
        Point spikeMiddle = board.getSpikes().get(23).getMiddlePoint();
        board.paint(graphics, geometry.boardHeight(), geometry.boardWidth(),true, spikeMiddle.x, spikeMiddle.y);

        ArrayList<Integer> flashed = new ArrayList<>();
        flashed.add(20);
        flashed.add(15);
        for (Spike spike: board.getSpikes()) {
            assertEquals(flashed.contains(spike.getSpikeNumber()), spike.isFlashed());
        }

        spikeMiddle = board.getSpikes().get(12).getMiddlePoint();
        board.paint(graphics, geometry.boardHeight(), geometry.boardWidth(),true, spikeMiddle.x, spikeMiddle.y);
        flashed.clear();
        flashed.add(9);
        flashed.add(7);
        flashed.add(4);
        for (Spike spike: board.getSpikes()) {
            assertEquals(flashed.contains(spike.getSpikeNumber()), spike.isFlashed());
        }

        spikeMiddle = board.getSpikes().get(11).getMiddlePoint();
        board.paint(graphics, geometry.boardHeight(), geometry.boardWidth(),true, spikeMiddle.x, spikeMiddle.y);

        for (Spike spike: board.getSpikes()) {
            assertFalse(spike.isFlashed());
        }

        spikeMiddle = board.getSpikes().get(7).getMiddlePoint();
        board.paint(graphics, geometry.boardHeight(), geometry.boardWidth(),true, spikeMiddle.x, spikeMiddle.y);
        flashed.clear();
        flashed.add(4);
        flashed.add(2);

        for (Spike spike: board.getSpikes()) {
            assertEquals(flashed.contains(spike.getSpikeNumber()), spike.isFlashed());
        }

        spikeMiddle = board.getSpikes().get(6).getMiddlePoint();
        board.paint(graphics, geometry.boardHeight(), geometry.boardWidth(),true, spikeMiddle.x, spikeMiddle.y);

        for (Spike spike: board.getSpikes()) {
            assertFalse(spike.isFlashed());
        }

        spikeMiddle = board.getSpikes().get(5).getMiddlePoint();
        board.paint(graphics, geometry.boardHeight(), geometry.boardWidth(),true, spikeMiddle.x, spikeMiddle.y);
        flashed.clear();
        flashed.add(2);
        for (Spike spike: board.getSpikes()) {
            assertEquals(flashed.contains(spike.getSpikeNumber()), spike.isFlashed());
        }
    }

    @Test
    @DisplayName("Pulsate not-container spikes, piece on bar")
    void test13() {
        Board board = new TestableBoard(colours, geometry, config, 5, 2);
        int[] whiteHome = {
            0,
            0,0,0,4,0,0,
            0,10,0,0,0,0,
            0,0,0,0,0,0,
            0,0,0,0,0,0,
            1
        };
        int[] blackHome = {
            0,
            0,0,0,5,6,0,
            0,0,0,0,0,0,
            0,0,0,0,0,0,
            0,0,0,2,0,2,
            0
        };
        board.initialiseBoardForNewGame(whiteHome, blackHome);
        Graphics graphics = Mockito.mock(Graphics.class);

        // No rolls, no flashing
        board.paint(graphics, geometry.boardHeight(), geometry.boardWidth(), true, 0, 0);
        assertTrue(board.showRollButton());
        for (Spike spike : board.getSpikes()) {
            assertFalse(spike.isFlashed());
        }

        // When dies are rolled
        board.rollDies();
        // And mouse is hovering over any spike, spike 22 must be only pulsated
        ArrayList<Integer> flashed = new ArrayList<>();
        flashed.add(22);
        for (Spike spike: board.getSpikes()) {
            Point spikeMiddle = spike.getMiddlePoint();
            board.paint(graphics, geometry.boardHeight(), geometry.boardWidth(), true, spikeMiddle.x, spikeMiddle.y);
            for (Spike spike1 : board.getSpikes()) {
                assertEquals(flashed.contains(spike1.getSpikeNumber()), spike1.isFlashed());
            }
        }

        board.setCurrentPlayer(board.getBlackPlayer());
        Point spikeMiddle = board.getSpikes().get(20).getMiddlePoint();
        board.paint(graphics, geometry.boardHeight(), geometry.boardWidth(), true, spikeMiddle.x, spikeMiddle.y);

        flashed.clear();
        flashed.add(22);
        for (Spike spike1 : board.getSpikes()) {
            assertEquals(flashed.contains(spike1.getSpikeNumber()), spike1.isFlashed());
        }

        spikeMiddle = board.getSpikes().get(0).getMiddlePoint();
        board.paint(graphics, geometry.boardHeight(), geometry.boardWidth(), true, spikeMiddle.x, spikeMiddle.y);

        flashed.clear();
        flashed.add(2);
        flashed.add(5);
        for (Spike spike1 : board.getSpikes()) {
            assertEquals(flashed.contains(spike1.getSpikeNumber()), spike1.isFlashed());
        }

        spikeMiddle = board.getSpikes().get(2).getMiddlePoint();
        board.paint(graphics, geometry.boardHeight(), geometry.boardWidth(), true, spikeMiddle.x, spikeMiddle.y);

        flashed.clear();
        flashed.add(4);
        flashed.add(9);
        for (Spike spike1 : board.getSpikes()) {
            assertEquals(flashed.contains(spike1.getSpikeNumber()), spike1.isFlashed());
        }
    }

    @Test
    @DisplayName("Pulsate not-container spikes, piece on bar, double, one move")
    void test14() {
        Board board = new TestableBoard(colours, geometry, config, 3);
        int[] whiteHome = {
            0,
            14,0,0,0,0,0,
            0,0,0,0,0,0,
            0,0,0,0,0,0,
            0,0,0,0,0,0,
            1
        };
        int[] blackHome = {
            0,
            0,0,0,5,6,2,
            0,0,0,0,0,0,
            0,0,0,0,0,0,
            0,0,0,2,0,0,
            0
        };
        board.initialiseBoardForNewGame(whiteHome, blackHome);
        Graphics graphics = Mockito.mock(Graphics.class);

        // No rolls, no flashing
        board.paint(graphics, geometry.boardHeight(), geometry.boardWidth(), true, 0, 0);
        assertTrue(board.showRollButton());

        board.rollDies();

        board.paint(graphics, geometry.boardHeight(), geometry.boardWidth(), true, 0, 0);
        board.drawBarPieces(graphics);
        assertTrue(board.getSpikes().get(21).isFlashed());

        // When piece on bar is clicked
        Point pieceOnBar = new Point(geometry.boardWidth() / 2,
            geometry.boardHeight() / 2 - geometry.pieceDiameter() - geometry.pieceRadius());
        board.checkIfPieceClickedOn(pieceOnBar.x, pieceOnBar.y);

        board.paint(graphics, geometry.boardHeight(), geometry.boardWidth(), true, 0, 0);
        // Destination spike is still flashed
        ArrayList<Integer> flashed = new ArrayList<>();
        flashed.add(21);
        for (Spike spike: board.getSpikes())
            assertEquals(flashed.contains(spike.getSpikeNumber()), spike.isFlashed());

        // When destination spike is clicked
        Point destinatioinSpike = board.getSpikes().get(21).getMiddlePoint();
        board.checkIfSpikeClickedOn(destinatioinSpike.x, destinatioinSpike.y);
        board.paint(graphics, geometry.boardHeight(), geometry.boardWidth(), true, 0, 0);

        // No spikes are flashed
        for (Spike spike: board.getSpikes()) {
            assertFalse(spike.isFlashed());
        }
    }

    @Test
    @DisplayName("Pulsate not-container spikes, no options from bar")
    void test15() {
        Board board = new TestableBoard(colours, geometry, config, 4);
        int[] whiteHome = {
            0,
            14, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0,
            1
        };
        int[] blackHome = {
            0,
            0, 0, 0, 5, 6, 2,
            0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0,
            0, 0, 0, 2, 0, 0,
            0
        };
        board.initialiseBoardForNewGame(whiteHome, blackHome);
        Graphics graphics = Mockito.mock(Graphics.class);

        // No rolls, no flashing
        board.paint(graphics, geometry.boardHeight(), geometry.boardWidth(), true, 0, 0);
        assertTrue(board.showRollButton());

        board.rollDies();

        board.paint(graphics, geometry.boardHeight(), geometry.boardWidth(), true, 0, 0);
        board.drawBarPieces(graphics);

        for (Spike spike: board.getSpikes()) {
            assertFalse(spike.isFlashed());
        }
        assertTrue(board.turnOver());
    }

    @Test
    @DisplayName("Pulsate not-container spikes, piece on bar, two moves")
    void test16() {
        Board board = new TestableBoard(colours, geometry, config, 5, 3);
        int[] whiteHome = {
            0,
            13,0,0,0,0,0,
            0,0,0,0,0,0,
            0,0,0,0,0,0,
            0,0,0,0,0,0,
            2
        };
        int[] blackHome = {
            0,
            0,0,0,5,0,2,
            0,0,0,0,6,0,
            0,0,0,0,0,0,
            0,0,0,2,0,0,
            0
        };
        board.initialiseBoardForNewGame(whiteHome, blackHome);
        Graphics graphics = Mockito.mock(Graphics.class);

        // No rolls, no flashing
        board.paint(graphics, geometry.boardHeight(), geometry.boardWidth(), true, 0, 0);
        assertTrue(board.showRollButton());

        board.rollDies();

        board.paint(graphics, geometry.boardHeight(), geometry.boardWidth(), true, 0, 0);
        board.drawBarPieces(graphics);
        ArrayList<Integer> flashed = new ArrayList<>();
        flashed.add(21);
        flashed.add(19);
        for (Spike spike: board.getSpikes())
            assertEquals(flashed.contains(spike.getSpikeNumber()), spike.isFlashed());

        // When first piece on bar is clicked
        Point pieceOnBar = new Point(geometry.boardWidth() / 2,
            geometry.boardHeight() / 2 - geometry.pieceDiameter() - geometry.pieceRadius());
        board.checkIfPieceClickedOn(pieceOnBar.x, pieceOnBar.y);

        board.paint(graphics, geometry.boardHeight(), geometry.boardWidth(), true, 0, 0);
        // Destination spike are still flashed
        for (Spike spike: board.getSpikes())
            assertEquals(flashed.contains(spike.getSpikeNumber()), spike.isFlashed());

        // When destination spike is clicked
        Point destinatioinSpike = board.getSpikes().get(21).getMiddlePoint();
        board.checkIfSpikeClickedOn(destinatioinSpike.x, destinatioinSpike.y);
        board.paint(graphics, geometry.boardHeight(), geometry.boardWidth(), true, 0, 0);

        // Only one spike is flashed now
        flashed.remove(new Integer(21));
        for (Spike spike: board.getSpikes()) {
            assertEquals(flashed.contains(spike.getSpikeNumber()), spike.isFlashed());
        }

        // Second piece is clicked
        board.drawBarPieces(graphics);
        board.checkIfPieceClickedOn(pieceOnBar.x, pieceOnBar.y);
        for (Spike spike: board.getSpikes()) {
            assertEquals(flashed.contains(spike.getSpikeNumber()), spike.isFlashed());
        }

        // And placed to the possible spike
        destinatioinSpike = board.getSpikes().get(19).getMiddlePoint();
        board.checkIfSpikeClickedOn(destinatioinSpike.x, destinatioinSpike.y);
        board.paint(graphics, geometry.boardHeight(), geometry.boardWidth(), true, 0, 0);

        assertTrue(board.turnOver());
    }

    @Test
    @DisplayName("Pulsate not-container spikes, piece on bar, double, two moves")
    void test17() {
        Board board = new TestableBoard(colours, geometry, config, 3);
        int[] whiteHome = {
            0,
            0,0,13,0,0,0,
            0,0,0,0,0,0,
            0,0,0,0,0,0,
            0,0,0,0,0,0,
            2
        };
        int[] blackHome = {
            0,
            0,0,0,5,0,2,
            0,0,0,0,6,0,
            0,0,0,0,0,0,
            0,0,2,0,0,0,
            0
        };
        board.initialiseBoardForNewGame(whiteHome, blackHome);
        board.checkConsistent();
        Graphics graphics = Mockito.mock(Graphics.class);

        // No rolls, no flashing
        board.paint(graphics, geometry.boardHeight(), geometry.boardWidth(), true, 0, 0);
        assertTrue(board.showRollButton());

        board.rollDies();

        board.paint(graphics, geometry.boardHeight(), geometry.boardWidth(), true, 0, 0);
        ArrayList<Integer> flashed = new ArrayList<>();
        flashed.add(21);
        for (Spike spike: board.getSpikes())
            assertEquals(flashed.contains(spike.getSpikeNumber()), spike.isFlashed());

        // When first piece on bar is clicked
        Point pieceOnBar = new Point(geometry.boardWidth() / 2,
            geometry.boardHeight() / 2 - geometry.pieceDiameter() - geometry.pieceRadius());
        board.drawBarPieces(graphics);
        board.checkIfPieceClickedOn(pieceOnBar.x, pieceOnBar.y);

        board.paint(graphics, geometry.boardHeight(), geometry.boardWidth(), true, 0, 0);
        // Destination spike are still flashed
        for (Spike spike: board.getSpikes())
            assertEquals(flashed.contains(spike.getSpikeNumber()), spike.isFlashed());

        // When destination spike is clicked
        Point destinatioinSpike = board.getSpikes().get(21).getMiddlePoint();
        board.checkIfSpikeClickedOn(destinatioinSpike.x, destinatioinSpike.y);
        board.paint(graphics, geometry.boardHeight(), geometry.boardWidth(), true, 0, 0);

        // Same spike is still flashed
        for (Spike spike: board.getSpikes()) {
            assertEquals(flashed.contains(spike.getSpikeNumber()), spike.isFlashed());
        }

        // Second piece is clicked
        board.drawBarPieces(graphics);
        board.checkIfPieceClickedOn(pieceOnBar.x, pieceOnBar.y);
        for (Spike spike: board.getSpikes()) {
            assertEquals(flashed.contains(spike.getSpikeNumber()), spike.isFlashed());
        }

        // And placed to the possible spike
        board.checkIfSpikeClickedOn(destinatioinSpike.x, destinatioinSpike.y);

        // Two paints to disable both dies
        board.paint(graphics, geometry.boardHeight(), geometry.boardWidth(), true, 0, 0);
        for (Spike spike: board.getSpikes()) {
            assertFalse(spike.isFlashed());
        }
        board.paint(graphics, geometry.boardHeight(), geometry.boardWidth(), true, 0, 0);

        assertTrue(board.turnOver());
        assertEquals(2, board.getSpikes().get(21).getAmountOfPieces(PlayerColor.WHITE));
    }

    @Test
    @DisplayName("Pulsate not-container spikes, piece on bar, double, four moves")
    void test18() {
        Board board = new TestableBoard(colours, geometry, config, 3);
        int[] whiteHome = {
            0,
            0,0,0,0,0,0,
            13,0,0,0,0,0,
            0,0,0,0,0,0,
            0,0,0,0,0,0,
            2
        };
        int[] blackHome = {
            0,
            0,0,0,5,0,2,
            0,0,0,0,6,0,
            0,0,0,0,0,0,
            0,0,0,2,0,0,
            0
        };
        board.initialiseBoardForNewGame(whiteHome, blackHome);
        board.checkConsistent();
        Graphics graphics = Mockito.mock(Graphics.class);

        // No rolls, no flashing
        board.paint(graphics, geometry.boardHeight(), geometry.boardWidth(), true, 0, 0);
        assertTrue(board.showRollButton());

        board.rollDies();

        board.paint(graphics, geometry.boardHeight(), geometry.boardWidth(), true, 0, 0);
        ArrayList<Integer> flashed = new ArrayList<>();
        flashed.add(21);
        for (Spike spike: board.getSpikes())
            assertEquals(flashed.contains(spike.getSpikeNumber()), spike.isFlashed());

        // When first piece on bar is clicked
        Point pieceOnBar = new Point(geometry.boardWidth() / 2,
            geometry.boardHeight() / 2 - geometry.pieceDiameter() - geometry.pieceRadius());
        board.drawBarPieces(graphics);
        board.checkIfPieceClickedOn(pieceOnBar.x, pieceOnBar.y);

        board.paint(graphics, geometry.boardHeight(), geometry.boardWidth(), true, 0, 0);
        // Destination spike are still flashed
        for (Spike spike: board.getSpikes())
            assertEquals(flashed.contains(spike.getSpikeNumber()), spike.isFlashed());

        // When destination spike is clicked
        Point destinatioinSpike = board.getSpikes().get(21).getMiddlePoint();
        board.checkIfSpikeClickedOn(destinatioinSpike.x, destinatioinSpike.y);
        board.paint(graphics, geometry.boardHeight(), geometry.boardWidth(), true, 0, 0);

        // Same spike is still flashed
        for (Spike spike: board.getSpikes()) {
            assertEquals(flashed.contains(spike.getSpikeNumber()), spike.isFlashed());
        }

        // Second piece is clicked
        board.drawBarPieces(graphics);
        board.checkIfPieceClickedOn(pieceOnBar.x, pieceOnBar.y);
        for (Spike spike: board.getSpikes()) {
            assertEquals(flashed.contains(spike.getSpikeNumber()), spike.isFlashed());
        }

        // And placed to the possible spike
        board.checkIfSpikeClickedOn(destinatioinSpike.x, destinatioinSpike.y);

        board.paint(graphics, geometry.boardHeight(), geometry.boardWidth(), true, 0, 0);
        for (Spike spike: board.getSpikes()) {
            assertFalse(spike.isFlashed());
        }
        board.paint(graphics, geometry.boardHeight(), geometry.boardWidth(), true, 0, 0);

        assertFalse(board.turnOver());
        assertEquals(2, board.getSpikes().get(21).getAmountOfPieces(PlayerColor.WHITE));

        // When the mouse points spike with white  pieces
        Point sourceSpike = board.getSpikes().get(6).getMiddlePoint();
        board.paint(graphics, geometry.boardHeight(), geometry.boardWidth(), true, sourceSpike.x,sourceSpike.y);

        flashed.clear();
        flashed.add(3);
        flashed.add(0);
        for (Spike spike: board.getSpikes()) {
            assertEquals(flashed.contains(spike.getSpikeNumber()), spike.isFlashed());
        }
        assertEquals(DieType.DIE1AND2, board.getSpikes().get(0).flashDieType());
        assertEquals(DieType.DIE1, board.getSpikes().get(3).flashDieType());

        board.checkIfPieceClickedOn(sourceSpike.x, sourceSpike.y);
        board.paint(graphics, geometry.boardHeight(), geometry.boardWidth(), true, 0, 0);
        for (Spike spike: board.getSpikes()) {
            assertEquals(flashed.contains(spike.getSpikeNumber()), spike.isFlashed());
        }

        destinatioinSpike = board.getSpikes().get(3).getMiddlePoint();
        board.checkIfSpikeClickedOn(destinatioinSpike.x, destinatioinSpike.y);
        board.paint(graphics, geometry.boardHeight(), geometry.boardWidth(), true, sourceSpike.x, sourceSpike.y);
        // Now only one spike is flashed
        flashed.remove(new Integer(0));
        for (Spike spike: board.getSpikes()) {
            assertEquals(flashed.contains(spike.getSpikeNumber()), spike.isFlashed());
        }

        // When spike "6" is clicked again
        board.checkIfPieceClickedOn(sourceSpike.x, sourceSpike.y);
        // And piece placed on spike "3"
        board.checkIfSpikeClickedOn(destinatioinSpike.x, destinatioinSpike.y);
        board.paint(graphics, geometry.boardHeight(), geometry.boardWidth(), true, sourceSpike.x, sourceSpike.y);
        assertEquals(2, board.getSpikes().get(3).getAmountOfPieces(PlayerColor.WHITE));
        assertTrue(board.turnOver());
    }
}
