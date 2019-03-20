package gamelogic;

import org.aeonbits.owner.Config;

@Config.LoadPolicy(Config.LoadType.MERGE)
@Config.Sources({ "file:backgammon.config",
    "file:/etc/backgammon.config",
    "classpath:gamelogic/backgammon.properties" })
public interface GameConfig extends Config {
    int boardSize();
}
