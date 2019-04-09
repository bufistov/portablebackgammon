package gamelogic;

import graphics.GameColour;
import graphics.Geometry;

public class TestableBoard extends Board {
    private final int roll1Value;
    private final int roll2Value;
    public TestableBoard(GameColour gameColour, Geometry geometry, GameConfig config, int roll1Value,
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
        die1.setValue(roll1Value);
        die2.setValue(roll2Value);
    }
}
