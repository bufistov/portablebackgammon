package gamelogic;

import data.PlayerColor;
import lowlevel.CustomCanvas;
import lowlevel.Main;
import org.aeonbits.owner.ConfigFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.swing.*;

import java.awt.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;


class TestableCanvas extends CustomCanvas {

    TestableCanvas(GameConfig config) {
        super(config);
    }

    @Override
    public int getWidth() {
        return 810;
    }

    @Override
    public int getHeight() {
        return 500;
    }
}

class BoardTest {

    @Test
    @DisplayName("Board object can be constructed")
    void test1() {
        ConfigFactory.setProperty(
            "configFileName", "somenonexistingconfig.config");
        GameConfig config = ConfigFactory.create(GameConfig.class);

        CustomCanvas canvas = new CustomCanvas(config);

        Board board = canvas.board;
        assertEquals(167, board.calculatePips(PlayerColor.WHITE));
        assertEquals(167, board.calculatePips(PlayerColor.BLACK));
        assertEquals(PlayerColor.WHITE, board.getCurrentPlayer().getColour());
    }

    @Test
    @DisplayName("Game screen changes to LOCAL_OR_NETWORK after 50 frames")
    void test2() {
        GameConfig config = Mockito.mock(GameConfig.class);
        Mockito.when(config.maxSplashCounter()).thenReturn(50);

        CustomCanvas canvas = new TestableCanvas(config);
        JFrame frame = new JFrame();
        frame.getContentPane().add(canvas);

        assertEquals(GuiState.SPLASH_SCREEN, canvas.getState());
        Graphics graphics = Mockito.mock(Graphics.class);
        for (int i = 0; i <= config.maxSplashCounter(); ++i) {
            canvas.paint(graphics);
        }
        assertEquals(GuiState.OPTIONS_SCREEN_LOCAL_OR_NETWORK, canvas.getState());
    }
}
