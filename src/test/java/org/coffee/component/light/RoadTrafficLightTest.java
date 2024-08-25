package org.coffee.component.light;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.coffee.component.attribute.Color.GREEN;
import static org.coffee.component.attribute.Color.RED;
import static org.junit.jupiter.api.Assertions.*;

class RoadTrafficLightTest {

    private RoadTrafficLight sut;

    @BeforeEach
    void setUp() {
        sut = new RoadTrafficLight("1", "Traffic Light 1");
    }

    @Test
    void testAllowTraffic() throws InterruptedException {
        sut.allowTraffic();
        assertEquals(GREEN, sut.getColor());
        assertTrue(sut.isAllowingTraffic());
    }

    @Test
    void testStopTraffic() throws InterruptedException {
        sut.stopTraffic();
        assertEquals(RED, sut.getColor());
        assertFalse(sut.isAllowingTraffic());
    }

    @Test
    void testDefaultSignal() {
        sut.defaultSignal();
        assertEquals("YELLOW", sut.getColor().toString());
        assertFalse(sut.isAllowingTraffic());
    }



}