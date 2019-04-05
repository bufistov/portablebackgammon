package graphics;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GeometryTest {

    @Test
    @DisplayName("Geometry default values")
    void test1() {
        Geometry geometry = new Geometry(810, 478);
        assertEquals(648, geometry.boardWidth());
        assertEquals(478, geometry.boardHeight());
        assertEquals(5, geometry.tinyGap());
        assertEquals(5, geometry.panelSizeFraction());
        assertEquals(152, geometry.panelWidth());
        assertEquals(10, geometry.borderWidth());
        assertEquals(20, geometry.centralBarWidth());
        assertEquals(50, geometry.spikeWidth());
        assertEquals(207, geometry.spikeHeight());
        assertEquals(32, geometry.pieceDiameter());
        assertEquals(32, geometry.dieSize());
        assertEquals(6, geometry.dieDotDiameter());
        assertEquals(26, geometry.miniDieSize());
    }
}
