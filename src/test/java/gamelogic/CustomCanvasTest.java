package gamelogic;

import data.PlayerColor;
import lowlevel.CustomCanvas;
import org.aeonbits.owner.ConfigFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TestableCanvas extends CustomCanvas {

    TestableCanvas(JFrame frame, GameConfig config) {
        super(frame, config);
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

class CustomCanvasTest {

    private Field makeCanvasFieldPublic(String fieldName) throws Exception {
        Field field = CustomCanvas.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        return field;
    }

    @Test
    @DisplayName("Transit to start game state")
    void test1() throws Exception {
        GameConfig config = Mockito.mock(GameConfig.class);
        Mockito.when(config.maxSplashCounter()).thenReturn(50);

        JFrame frame = new JFrame();
        CustomCanvas canvas = new TestableCanvas(frame, config);

        assertEquals(GuiState.SPLASH_SCREEN, canvas.getState());
        Graphics graphics = Mockito.mock(Graphics.class);
        for (int i = 0; i <= config.maxSplashCounter(); ++i) {
            canvas.paint(graphics);
        }
        assertEquals(GuiState.OPTIONS_SCREEN_LOCAL_OR_NETWORK, canvas.getState());
        canvas.paint(graphics);

        Field buttonxA = makeCanvasFieldPublic("buttonxA");
        Field buttonyA = makeCanvasFieldPublic("buttonyA");
        Field buttonwA = makeCanvasFieldPublic("buttonwA");
        Field buttonhA = makeCanvasFieldPublic("buttonhA");

        int buttonX = (int)buttonxA.get(canvas) + (int)buttonwA.get(canvas) / 2;
        int buttonY = (int)buttonyA.get(canvas) + (int)buttonhA.get(canvas) / 2;
        canvas.mouseClicked(new MouseEvent(canvas, 0, System.nanoTime(), 0, buttonX, buttonY, 1, false));
        assertEquals(GuiState.OPTIONS_SCREEN_LOCAL_COMPUTER_OR_HUMAN, canvas.getState());
        canvas.paint(graphics);

        canvas.mouseClicked(new MouseEvent(canvas, 0, System.nanoTime(), 0, buttonX, buttonY, 1, false));
        assertEquals(GuiState.GAME_IN_PROGRESS, canvas.getState());
        canvas.paint(graphics);
    }
}
