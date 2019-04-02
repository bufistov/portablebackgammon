package gamelogic;

import data.PlayerColor;
import lowlevel.CustomCanvas;
import lowlevel.Main;
import org.aeonbits.owner.ConfigFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.swing.*;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

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
    void test2() throws Exception {
        ConfigFactory.setProperty(
            "configFileName", "somenonexistingconfig.config");
        GameConfig config = ConfigFactory.create(GameConfig.class);

        CustomCanvas canvas = new CustomCanvas(config);
        JFrame frame = new JFrame();
        frame.getContentPane().add(canvas);
        Main.initMainWindow(frame, true);
        canvas.init();
        // Given a board with all pieces at home spikes
        Board board = canvas.board;
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

        assertEquals(810, frame.getWidth());
        assertEquals(500, frame.getHeight());

        assertEquals(GuiState.SPLASH_SCREEN, canvas.getState());
        for (int i = 0; i <= config.maxSplashCounter(); ++i) {
            canvas.paint(null);
        }
        assertEquals(GuiState.OPTIONS_SCREEN_LOCAL_OR_NETWORK, canvas.getState());
    }
}
