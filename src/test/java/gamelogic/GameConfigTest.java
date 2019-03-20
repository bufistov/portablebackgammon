package gamelogic;

import org.aeonbits.owner.ConfigFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GameConfigTest {

    @Test
    @DisplayName("Default values are loaded from classpath props")
    public void test1() {
        ConfigFactory.setProperty(
            "configFileName", "backgammon.config");
        GameConfig config = ConfigFactory.create(GameConfig.class);
        assertEquals(24, config.boardSize());
    }

    @Test
    @DisplayName("Config file takes precedence over classpath file")
    public void test2() throws Exception {
        File tempFile = File.createTempFile("testbackgammon-", ".properties");
        Files.write(tempFile.toPath(), "boardSize=101".getBytes());
        ConfigFactory.setProperty("configFileName", tempFile.getAbsolutePath());
        GameConfig config = ConfigFactory.create(GameConfig.class);
        assertEquals(101, config.boardSize());
    }
}
