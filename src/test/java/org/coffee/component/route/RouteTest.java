package org.coffee.component.route;

import org.coffee.component.attribute.RouteType;
import org.coffee.component.lane.LaneInbound;
import org.coffee.component.lane.LaneOutbound;
import org.coffee.component.light.ConditionalArrowTrafficLight;
import org.coffee.component.light.PedestrianTrafficLight;
import org.coffee.component.light.RoadTrafficLight;
import org.coffee.component.light.TrafficLight;
import org.coffee.component.sensor.Camera;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

class RouteTest {
    @Mock
    private LaneInbound inboundLane;

    @Mock
    private List<LaneOutbound> outboundLanes;

    @Mock
    private Camera camera;

    List<TrafficLight> trafficLights;

    private Route sut;

    @BeforeEach
    void setUp() {
        openMocks(this);
        trafficLights = List.of(
                mock(RoadTrafficLight.class),
                mock(ConditionalArrowTrafficLight.class),
                mock(PedestrianTrafficLight.class));

        when(trafficLights.get(0).getId()).thenReturn("1");
        when(trafficLights.get(1).getId()).thenReturn("2");
        when(trafficLights.get(2).getId()).thenReturn("3");
        sut = new Route("id", RouteType.NORMAL, inboundLane, outboundLanes, trafficLights);
    }

    @Test
    void shouldAllowTraffic() throws Exception {
        sut.allowTraffic();
        verify(trafficLights.get(0), times(1)).allowTraffic();
        verify(trafficLights.get(1), times(1)).allowTraffic();
        verify(trafficLights.get(2), times(1)).allowTraffic();
    }

    @Test
    void shouldStopTraffic() throws Exception {
        sut.stopTraffic();
        verify(trafficLights.get(0), times(1)).stopTraffic();
        verify(trafficLights.get(1), times(1)).stopTraffic();
        verify(trafficLights.get(2), times(1)).stopTraffic();
    }

    @Test
    void shouldSetDefaultSignal() {
        sut.defaultSignal();
        verify(trafficLights.get(0), times(1)).defaultSignal();
        verify(trafficLights.get(1), times(1)).defaultSignal();
        verify(trafficLights.get(2), times(1)).defaultSignal();
    }

    @Test
    void shouldShowAllowedTraffic() {
        when(trafficLights.get(0).isAllowingTraffic()).thenReturn(true);
        when(trafficLights.get(1).isAllowingTraffic()).thenReturn(true);
        when(trafficLights.get(2).isAllowingTraffic()).thenReturn(true);
        var result = sut.isAllowingTraffic();

        assertTrue(result);
        verify(trafficLights.get(0), times(1)).isAllowingTraffic();
        verify(trafficLights.get(1), times(1)).isAllowingTraffic();
        verify(trafficLights.get(2), times(1)).isAllowingTraffic();
    }

    @Test
    void shouldShowNotAllowedTraffic() {
        when(trafficLights.get(0).isAllowingTraffic()).thenReturn(true);
        when(trafficLights.get(1).isAllowingTraffic()).thenReturn(false);
        when(trafficLights.get(2).isAllowingTraffic()).thenReturn(true);
        var result = sut.isAllowingTraffic();

        assertFalse(result);
        verify(trafficLights.get(0), times(1)).isAllowingTraffic();
        verify(trafficLights.get(1), times(1)).isAllowingTraffic();
        verify(trafficLights.get(2), times(0)).isAllowingTraffic();
    }

    @Test
    void shouldReturnCurrentPressure() {
        when(inboundLane.getCamera()).thenReturn(camera);
        when(camera.getCars()).thenReturn(10);
        var result = sut.currentPressure();
        assertEquals(10, result);
    }

}