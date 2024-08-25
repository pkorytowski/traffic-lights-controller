package org.coffee.component.light;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.coffee.component.attribute.Color.BLACK;
import static org.coffee.component.attribute.Color.GREEN;
import static org.junit.jupiter.api.Assertions.*;

class ConditionalArrowTrafficLightTest {
    ConditionalArrowTrafficLight sut;

    @BeforeEach
    void setUp() {
        sut = new ConditionalArrowTrafficLight("1", "Test");
    }

    @Test
    void shouldChangeColorToGreenWhenAllowTraffic() {
        sut.allowTraffic();
        assertEquals(GREEN, sut.getColor());
    }

    @Test
    void shouldChangeColorToBlackWhenStopTraffic() {
        sut.stopTraffic();
        assertEquals(BLACK, sut.getColor());
    }

    @Test
    void shouldChangeColorToBlackWhenDefaultSignal() {
        sut.defaultSignal();
        assertEquals(BLACK, sut.getColor());
    }

    @Test
    void shouldReturnTrueWhenIsAllowingTraffic() {
        sut.allowTraffic();
        assertTrue(sut.isAllowingTraffic());
    }

    @Test
    void shouldReturnFalseWhenIsNotAllowingTraffic() {
        sut.stopTraffic();
        assertFalse(sut.isAllowingTraffic());
    }

    @Test
    void shouldReturnFalseWhenIsDefaultSignal() {
        sut.defaultSignal();
        assertFalse(sut.isAllowingTraffic());
    }

    @Test
    void shouldReturnFalseAfterAllowAndStopTraffic() {
        sut.allowTraffic();
        sut.stopTraffic();
        assertFalse(sut.isAllowingTraffic());
    }

}