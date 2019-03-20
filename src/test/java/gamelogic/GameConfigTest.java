package gamelogic;

import org.aeonbits.owner.ConfigFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import gamelogic.GameConfig;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GameConfigTest {

    @Test
    @DisplayName("Default values are loaded from classpath props")
    public void test1() {
        GameConfig config = ConfigFactory.create(GameConfig.class);
        assertEquals(24, config.boardSize());
    }
}
