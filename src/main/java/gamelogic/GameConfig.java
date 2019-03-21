package gamelogic;

import org.aeonbits.owner.Config;

@Config.LoadPolicy(Config.LoadType.MERGE)
@Config.Sources({ "file:${configFileName}",
    "classpath:gamelogic/backgammon.properties" })
public interface GameConfig extends Config {
    int boardSize();

    int totalCheckers();
}
