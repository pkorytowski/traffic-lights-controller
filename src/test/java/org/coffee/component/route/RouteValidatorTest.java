package org.coffee.component.route;

import org.coffee.component.lane.LaneInbound;
import org.coffee.component.lane.LaneOutbound;
import org.coffee.component.lane.PedestrianLane;
import org.coffee.component.light.RoadTrafficLight;
import org.coffee.component.light.TrafficLight;
import org.coffee.component.sensor.Camera;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.List;

import static java.util.Collections.singletonList;
import static org.coffee.component.attribute.Location.EAST;
import static org.coffee.component.attribute.Location.NORTH;
import static org.coffee.component.attribute.Location.SOUTH;
import static org.coffee.component.attribute.Location.WEST;
import static org.coffee.component.attribute.RouteType.CONDITIONAL;
import static org.coffee.component.attribute.RouteType.GRADE_SEPARATED;
import static org.coffee.component.attribute.RouteType.NORMAL;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.MockitoAnnotations.openMocks;

class RouteValidatorTest {

    @Mock
    Camera camera;

    @BeforeEach
    void setUp() {
        openMocks(this);
    }

    @Test
    void shouldValidateNormalRoute() {
        LaneInbound inboundLane = new LaneInbound("id", "name", NORTH, camera);
        LaneOutbound outboundLane = new LaneOutbound("id", "name", SOUTH, true);
        LaneOutbound outboundLane2 = new LaneOutbound("id", "name", EAST, true);
        PedestrianLane pedestrianLane = new PedestrianLane("id", "name", EAST, singletonList(outboundLane2));
        List<TrafficLight> trafficLights = List.of(new RoadTrafficLight("id", "name"));
        Route route = new Route("id", NORMAL, inboundLane, List.of(outboundLane, outboundLane2), trafficLights, singletonList(pedestrianLane));
        assertDoesNotThrow(() -> RouteValidator.validateRoute(route));
    }

    @Test
    void shouldValidateGradeSeparatedRoute() {
        LaneInbound inboundLane = new LaneInbound("id", "name", NORTH, camera);
        LaneOutbound outboundLane = new LaneOutbound("id", "name", SOUTH, true);
        LaneOutbound outboundLane2 = new LaneOutbound("id", "name", EAST, true);
        LaneOutbound outboundLane3 = new LaneOutbound("id", "name", WEST, true);
        PedestrianLane pedestrianLane = new PedestrianLane("id", "name", WEST, singletonList(outboundLane3));
        List<TrafficLight> trafficLights = List.of(new RoadTrafficLight("id", "name"));
        Route route = new Route("id", GRADE_SEPARATED, inboundLane, List.of(outboundLane, outboundLane2), trafficLights, singletonList(pedestrianLane));
        assertDoesNotThrow(() -> RouteValidator.validateRoute(route));
    }

    @Test
    void shouldValidateConditionalRoute() {
        LaneInbound inboundLane = new LaneInbound("id", "name", NORTH, camera);
        LaneOutbound outboundLane = new LaneOutbound("id", "name", WEST, true);
        List<TrafficLight> trafficLights = List.of(new RoadTrafficLight("id", "name"));
        Route route = new Route("id", GRADE_SEPARATED, inboundLane, singletonList(outboundLane), trafficLights);
        assertDoesNotThrow(() -> RouteValidator.validateRoute(route));
    }

    @Test
    void shouldThrowIfPedestrianLaneCrossesInboundLane() {
        LaneInbound inboundLane = new LaneInbound("id", "name", NORTH, camera);
        LaneOutbound outboundLane = new LaneOutbound("id", "name", WEST, true);
        PedestrianLane pedestrianLane = new PedestrianLane("id", "name", NORTH, singletonList(inboundLane));
        List<TrafficLight> trafficLights = List.of(new RoadTrafficLight("id", "name"));
        Route route = new Route("id", NORMAL, inboundLane, singletonList(outboundLane), trafficLights, singletonList(pedestrianLane));
        InvalidRouteException exception = assertThrows(InvalidRouteException.class, () -> RouteValidator.validateRoute(route));
        assertEquals("Pedestrian lane cannot cross the inbound lane", exception.getMessage());
    }

    @Test
    void shouldThrowIfConditionalRouteHasMoreThanOneOutboundLane() {
        LaneInbound inboundLane = new LaneInbound("id", "name", NORTH, camera);
        LaneOutbound outboundLane = new LaneOutbound("id", "name", WEST, true);
        LaneOutbound outboundLane2 = new LaneOutbound("id", "name", EAST, true);
        List<TrafficLight> trafficLights = List.of(new RoadTrafficLight("id", "name"));
        Route route = new Route("id", CONDITIONAL, inboundLane, List.of(outboundLane, outboundLane2), trafficLights);
        InvalidRouteException exception = assertThrows(InvalidRouteException.class, () -> RouteValidator.validateRoute(route));
        assertEquals("Conditional route must have exactly one outbound lane", exception.getMessage());
    }

    @Test
    void shouldThrowIfConditionalRouteDoesNotHaveOutboundLaneAllowingConditionalTurn() {
        LaneInbound inboundLane = new LaneInbound("id", "name", NORTH, camera);
        LaneOutbound outboundLane = new LaneOutbound("id", "name", WEST, false);
        List<TrafficLight> trafficLights = List.of(new RoadTrafficLight("id", "name"));
        Route route = new Route("id", CONDITIONAL, inboundLane, singletonList(outboundLane), trafficLights);
        InvalidRouteException exception = assertThrows(InvalidRouteException.class, () -> RouteValidator.validateRoute(route));
        assertEquals("Conditional route must have an outbound lane that allows conditional turn", exception.getMessage());
    }

    @Test
    void shouldThrowIfConditionalRouteHasOutboundLaneInSameDirectionAsInboundLane() {
        LaneInbound inboundLane = new LaneInbound("id", "name", NORTH, camera);
        LaneOutbound outboundLane = new LaneOutbound("id", "name", SOUTH, true);
        List<TrafficLight> trafficLights = List.of(new RoadTrafficLight("id", "name"));
        Route route = new Route("id", CONDITIONAL, inboundLane, singletonList(outboundLane), trafficLights);
        InvalidRouteException exception = assertThrows(InvalidRouteException.class, () -> RouteValidator.validateRoute(route));
        assertEquals("Conditional route must have an outbound lane that is not in the same direction as the inbound lane", exception.getMessage());
    }

    @Test
    void shouldThrowIfPedestrianLaneCrossesStraightOutboundLane() {
        LaneInbound inboundLane = new LaneInbound("id", "name", NORTH, camera);
        LaneOutbound outboundLane = new LaneOutbound("id", "name", SOUTH, true);
        LaneOutbound outboundLane2 = new LaneOutbound("id", "name", EAST, true);
        PedestrianLane pedestrianLane = new PedestrianLane("id", "name", SOUTH, singletonList(outboundLane));
        List<TrafficLight> trafficLights = List.of(new RoadTrafficLight("id", "name"));
        Route route = new Route("id", NORMAL, inboundLane, List.of(outboundLane, outboundLane2), trafficLights, singletonList(pedestrianLane));
        InvalidRouteException exception = assertThrows(InvalidRouteException.class, () -> RouteValidator.validateRoute(route));
        assertEquals("Pedestrian lane cannot cross the straight outbound lane", exception.getMessage());
    }

    @Test
    void shouldThrowIfPedestrianLaneCrossesGradeSeparatedRoute() {
        LaneInbound inboundLane = new LaneInbound("id", "name", NORTH, camera);
        LaneOutbound outboundLane = new LaneOutbound("id", "name", SOUTH, true);
        LaneOutbound outboundLane2 = new LaneOutbound("id", "name", EAST, true);
        PedestrianLane pedestrianLane = new PedestrianLane("id", "name", EAST, singletonList(outboundLane2));
        List<TrafficLight> trafficLights = List.of(new RoadTrafficLight("id", "name"));
        Route route = new Route("id", GRADE_SEPARATED, inboundLane, List.of(outboundLane, outboundLane2), trafficLights, singletonList(pedestrianLane));
        InvalidRouteException exception = assertThrows(InvalidRouteException.class, () -> RouteValidator.validateRoute(route));
        assertEquals("Pedestrian lane cannot cross the outbound lane", exception.getMessage());
    }

    @Test
    void shouldThrowIfConditionalRouteIsStraight() {
        LaneInbound inboundLane = new LaneInbound("id", "name", NORTH, camera);
        LaneOutbound outboundLane = new LaneOutbound("id", "name", SOUTH, true);
        List<TrafficLight> trafficLights = List.of(new RoadTrafficLight("id", "name"));
        Route route = new Route("id", CONDITIONAL, inboundLane, singletonList(outboundLane), trafficLights);
        InvalidRouteException exception = assertThrows(InvalidRouteException.class, () -> RouteValidator.validateRoute(route));
        assertEquals("Conditional route must have an outbound lane that is not in the same direction as the inbound lane", exception.getMessage());
    }

    @Test
    void shouldThrowIfRouteHasNoOutboundLane() {
        LaneInbound inboundLane = new LaneInbound("id", "name", NORTH, camera);
        List<TrafficLight> trafficLights = List.of(new RoadTrafficLight("id", "name"));
        Route route = new Route("id", NORMAL, inboundLane, List.of(), trafficLights);
        InvalidRouteException exception = assertThrows(InvalidRouteException.class, () -> RouteValidator.validateRoute(route));
        assertEquals("Route must have at least one outbound lane", exception.getMessage());
    }

    @Test
    void shouldThrowIfRouteHasNoInboundLane() {
        LaneOutbound outboundLane = new LaneOutbound("id", "name", SOUTH, true);
        List<TrafficLight> trafficLights = List.of(new RoadTrafficLight("id", "name"));
        Route route = new Route("id", NORMAL, null, singletonList(outboundLane), trafficLights);
        InvalidRouteException exception = assertThrows(InvalidRouteException.class, () -> RouteValidator.validateRoute(route));
        assertEquals("Route must have at least one inbound lane", exception.getMessage());
    }

    @Test
    void shouldThrowIfRouteHasNoTrafficLight() {
        LaneInbound inboundLane = new LaneInbound("id", "name", NORTH, camera);
        LaneOutbound outboundLane = new LaneOutbound("id", "name", SOUTH, true);
        Route route = new Route("id", NORMAL, inboundLane, singletonList(outboundLane), List.of());
        InvalidRouteException exception = assertThrows(InvalidRouteException.class, () -> RouteValidator.validateRoute(route));
        assertEquals("Route must have at least one traffic light", exception.getMessage());
    }
}