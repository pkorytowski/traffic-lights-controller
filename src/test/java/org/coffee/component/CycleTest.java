package org.coffee.component;

import org.coffee.component.cycle.Cycle;
import org.coffee.component.route.Route;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.List;

import static org.coffee.component.attribute.RouteType.CONDITIONAL;
import static org.coffee.component.attribute.RouteType.NORMAL;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

class CycleTest {

    @Mock
    private List<Route> routes;

    @Mock
    private Route route1;

    @Mock
    private Route route2;

    private Cycle sut;

    @BeforeEach
    void setUp() {
        openMocks(this);
        routes = List.of(route1, route2);
        sut = new Cycle("id", routes, 10);
    }

    @Test
    void shouldAllowTraffic() {
        when(route1.getType()).thenReturn(NORMAL);
        when(route2.getType()).thenReturn(NORMAL);

        sut.allowTraffic();

        verify(route1, times(1)).allowTraffic();
        verify(route2, times(1)).allowTraffic();
    }

    @Test
    void shuoldAllowTrafficWhenConditionalRouteAllowsTraffic() {
        when(route1.getType()).thenReturn(NORMAL);
        when(route2.getType()).thenReturn(CONDITIONAL);
        when(route1.isAllowingTraffic()).thenReturn(false);
        when(route2.isAllowingTraffic()).thenReturn(true);

        sut.allowTraffic();

        verify(route1, times(1)).allowTraffic();
        verify(route2, times(1)).stopTraffic();
        verify(route2, times(0)).allowTraffic();
    }

    @Test
    void shouldStopTraffic() {
        sut.stopTraffic();

        verify(route1, times(1)).stopTraffic();
        verify(route2, times(1)).stopTraffic();
    }

    @Test
    void shouldDefaultSignal() {
        sut.defaultSignal();

        verify(route1, times(1)).defaultSignal();
        verify(route2, times(1)).defaultSignal();
    }

    @Test
    void shouldCurrentPressure() {
        when(route1.currentPressure()).thenReturn(10);
        when(route2.currentPressure()).thenReturn(20);

        int result = sut.currentPressure();

        assertEquals(20, result);
        verify(route1, times(1)).currentPressure();
        verify(route2, times(1)).currentPressure();
    }

    @Test
    void shouldAllowTrafficForConditionalRoutes() {
        when(route1.getType()).thenReturn(CONDITIONAL);
        when(route2.getType()).thenReturn(NORMAL);

        when(route1.isAllowingTraffic()).thenReturn(false);
        when(route2.isAllowingTraffic()).thenReturn(false);

        sut.allowTrafficForConditionalRoutes();

        verify(route1, times(1)).allowTraffic();
        verify(route2, times(0)).allowTraffic();
    }

    @Test
    void shouldStopTrafficForConditionalRoutes() {
        when(route1.getType()).thenReturn(CONDITIONAL);
        when(route2.getType()).thenReturn(NORMAL);

        when(route1.isAllowingTraffic()).thenReturn(true);
        when(route2.isAllowingTraffic()).thenReturn(false);

        sut.stopTrafficForConditionalRoutes();

        verify(route1, times(1)).stopTraffic();
        verify(route2, times(0)).stopTraffic();
    }

    @Test
    void shouldThrowIllegalStateExceptionWhenNormalTrafficIsAllowed() {
        when(route1.getType()).thenReturn(NORMAL);
        when(route2.getType()).thenReturn(CONDITIONAL);
        when(route1.isAllowingTraffic()).thenReturn(true);
        when(route2.isAllowingTraffic()).thenReturn(false);

        try {
            sut.allowTrafficForConditionalRoutes();
        } catch (IllegalStateException e) {
            assertEquals("Normal traffic is going, cannot allow conditional routes", e.getMessage());
        }
    }
}