package org.coffee.utils;

import org.coffee.component.intersection.IntersectionUtils;
import org.coffee.component.lane.Lane;
import org.coffee.component.lane.LaneInbound;
import org.coffee.component.lane.LaneOutbound;
import org.coffee.component.lane.PedestrianLane;
import org.coffee.component.light.RoadTrafficLight;
import org.coffee.component.light.TrafficLight;
import org.coffee.component.route.Route;
import org.coffee.component.sensor.Camera;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static java.util.Collections.singletonList;
import static org.coffee.component.attribute.Location.EAST;
import static org.coffee.component.attribute.Location.NORTH;
import static org.coffee.component.attribute.Location.SOUTH;
import static org.coffee.component.attribute.Location.WEST;
import static org.coffee.component.attribute.RouteType.CONDITIONAL;
import static org.coffee.component.attribute.RouteType.GRADE_SEPARATED;
import static org.coffee.component.attribute.RouteType.NORMAL;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.MockitoAnnotations.openMocks;

class IntersectionUtilsTest {

    @Mock
    Camera camera1;
    @Mock Camera camera2;
    @Mock Camera camera3;

    @BeforeEach
    void setUp() {
        openMocks(this);
    }

    @Test
    void shouldReturnTrueTwoNormalRoutes() {
        Route route = getSimpleRoute();
        Route route2 = getSimpleRoute2();

        assertTrue(IntersectionUtils.isAllowedToGo(route, route2));
    }

    @Test
    void shouldReturnFalseWhenTwoNormalRoutesAreNotInSameDirection() {
        Route route = getSimpleRoute();
        Route route2 = getSimpleRoute3();

        assertFalse(IntersectionUtils.isAllowedToGo(route, route2));
    }

    @Test
    void shouldReturnFalseCrossingWithGradeSeparatedRoute() {
        Route route = getSimpleRoute();
        Route route2 = getGradeSeparatedRoute();
        assertFalse(IntersectionUtils.isAllowedToGo(route, route2));
    }

    @Test
    void shouldReturnFalseWhenDirectionsCrossingInGradeSeparated() {
        Route route = getGradeSeparatedRoute();
        Route route2 = getGradeSeparatedRoute2();
        assertFalse(IntersectionUtils.isAllowedToGo(route, route2));
    }

    @Test
    void shouldReturnFalseWhenPedestrianLaneCrossingInboundLane() {
        Route route = getSimpleRoute2();
        Route route2 = getGradeSeparatedRouteWithPedestrianLane(route.getInboundLane());
        assertFalse(IntersectionUtils.isAllowedToGo(route, route2));
        assertFalse(IntersectionUtils.isAllowedToGo(route2, route));
    }

    @Test
    void shouldReturnFalseWhenPedestrianLaneCrossingOutboundLane() {
        Route route = getSimpleRoute();
        Route route2 = getGradeSeparatedRouteWithPedestrianLane(route.getOutboundLanes().getFirst());
        assertFalse(IntersectionUtils.isAllowedToGo(route, route2));
        assertFalse(IntersectionUtils.isAllowedToGo(route2, route));
    }

    @Test
    void shouldReturnFalseIfNormalAndConditionalRouteAreInSameDirection() {
        Route route = getSimpleRoute();
        Route route2 = getConditionalRoute();
        assertFalse(IntersectionUtils.isAllowedToGo(route, route2));
    }

    private Route getSimpleRoute() {
        LaneInbound inboundLane = new LaneInbound("id", "name", NORTH, camera1);
        LaneOutbound outboundLane = new LaneOutbound("id", "name", SOUTH, true);
        TrafficLight trafficLight = new RoadTrafficLight("id", "name");
        return new Route("id", NORMAL, inboundLane, singletonList(outboundLane), singletonList(trafficLight));
    }

    private Route getSimpleRoute2() {
        LaneInbound inboundLane = new LaneInbound("id", "name", SOUTH, camera3);
        LaneOutbound outboundLane = new LaneOutbound("id", "name", NORTH, true);
        TrafficLight trafficLight = new RoadTrafficLight("id", "name");
        return new Route("id2", NORMAL, inboundLane, singletonList(outboundLane), singletonList(trafficLight));
    }

    private Route getSimpleRoute3() {
        LaneInbound inboundLane = new LaneInbound("id", "name", EAST, camera3);
        LaneOutbound outboundLane = new LaneOutbound("id", "name", WEST, true);
        TrafficLight trafficLight = new RoadTrafficLight("id", "name");
        return new Route("id2", NORMAL, inboundLane, singletonList(outboundLane), singletonList(trafficLight));
    }

    private Route getConditionalRoute() {
        LaneInbound inboundLane = new LaneInbound("id", "name", NORTH, camera3);
        LaneOutbound outboundLane = new LaneOutbound("id", "name", WEST, true);
        TrafficLight trafficLight = new RoadTrafficLight("id", "name");
        return new Route("id2", CONDITIONAL, inboundLane, singletonList(outboundLane), singletonList(trafficLight));
    }

    private Route getGradeSeparatedRoute() {
        LaneInbound inboundLane = new LaneInbound("id", "name", SOUTH, camera2);
        LaneOutbound outboundLane = new LaneOutbound("id", "name", WEST, true);
        TrafficLight trafficLight = new RoadTrafficLight("id", "name");
        return new Route("id2", GRADE_SEPARATED, inboundLane, singletonList(outboundLane), singletonList(trafficLight));
    }

    private Route getGradeSeparatedRoute2() {
        LaneInbound inboundLane = new LaneInbound("id", "name", NORTH, camera2);
        LaneOutbound outboundLane = new LaneOutbound("id", "name", SOUTH, true);
        TrafficLight trafficLight = new RoadTrafficLight("id", "name");
        return new Route("id2", GRADE_SEPARATED, inboundLane, singletonList(outboundLane), singletonList(trafficLight));
    }

    private Route getGradeSeparatedRouteWithPedestrianLane(Lane crossingLane) {
        LaneInbound inboundLane = new LaneInbound("id", "name", SOUTH, camera2);
        LaneOutbound outboundLane = new LaneOutbound("id", "name", WEST, true);
        TrafficLight trafficLight = new RoadTrafficLight("id","name");
        PedestrianLane pedestrianLane = new PedestrianLane("id", "name", NORTH, singletonList(crossingLane));
        return new Route("id2", GRADE_SEPARATED, inboundLane, singletonList(outboundLane), singletonList(trafficLight), singletonList(pedestrianLane));
    }

}