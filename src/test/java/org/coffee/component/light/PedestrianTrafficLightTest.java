package org.coffee.component.light;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.coffee.component.attribute.Color.GREEN;
import static org.coffee.component.attribute.Color.RED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PedestrianTrafficLightTest {

    private PedestrianTrafficLight sut;

    @BeforeEach
    void setUp() {
        sut = new PedestrianTrafficLight("1", "Test");
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
        assertEquals("BLACK", sut.getColor().toString());
        assertFalse(sut.isAllowingTraffic());
    }
}