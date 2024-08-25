package org.coffee.component;

import org.coffee.component.attribute.WorkingMode;

import org.coffee.component.cycle.Cycle;
import org.coffee.component.cycle.IncorrectRoutesException;
import org.coffee.component.intersection.Intersection;
import org.coffee.component.lane.LaneInbound;
import org.coffee.component.lane.LaneOutbound;
import org.coffee.component.light.RoadTrafficLight;
import org.coffee.component.light.TrafficLight;
import org.coffee.component.route.Route;
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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

class IntersectionTest {

    @Mock Camera camera1;
    @Mock Camera camera2;
    @Mock Camera camera3;
    @Mock Camera camera4;

    private Intersection sut;

    @BeforeEach
    void setUp() {
        openMocks(this);
    }

    @Test
    void shouldInitializeCorrectlyNormalRoutes() throws IncorrectRoutesException {
        Route route1 = getSimpleRoute();
        Route route2 = getSimpleRoute2();
        Cycle cycle1 = new Cycle("id", singletonList(route1), 3);
        Cycle cycle2 = new Cycle("id", singletonList(route2), 3);
        sut = new Intersection("id", "name", List.of(route1, route2), List.of(cycle1, cycle2), WorkingMode.CYCLES);
        assertEquals(sut.getCycleQueue().size(), 2);
    }

    @Test
    void shouldInitializeCorrectlyGradeSeparatedRoute() throws IncorrectRoutesException {
        Route route1 = getSimpleRoute();
        Route route2 = getGradeSeparatedRoute();
        Cycle cycle1 = new Cycle("id", singletonList(route1), 3);
        Cycle cycle2 = new Cycle("id", singletonList(route2), 3);
        sut = new Intersection("id", "name", List.of(route1, route2), List.of(cycle1, cycle2), WorkingMode.CYCLES);
        assertEquals(sut.getCycleQueue().size(), 2);
    }

    @Test
    void shouldInitializeCorrectlyConditionalRoute() throws IncorrectRoutesException {
        Route route1 = getSimpleRoute();
        Route route2 = getConditionalRoute();
        Cycle cycle1 = new Cycle("id", singletonList(route1), 3);
        Cycle cycle2 = new Cycle("id", singletonList(route2), 3);
        sut = new Intersection("id", "name", List.of(route1, route2), List.of(cycle1, cycle2), WorkingMode.CYCLES);
        assertEquals(sut.getCycleQueue().size(), 2);
    }

    @Test
    void shouldThrowIfRoutesCollide() {
        Route route1 = getSimpleRoute();
        Route route2 = getConditionalRoute();
        Cycle cycle1 = new Cycle("id", List.of(route1), 3);
        try {
            sut = new Intersection("id", "name", List.of(route1, route2), singletonList(cycle1), WorkingMode.CYCLES);
        } catch (IncorrectRoutesException e) {
            assertEquals("Cycle id contains routes that cannot be green at the same time", e.getMessage());
        }
    }

    @Test
    void shouldThrowIfOneRouteIsIncompatible() {
        Route route1 = getSimpleRoute();
        Route route2 = getSimpleRoute3();
        Route route3 = getSimpleRoute2();
        Cycle cycle1 = new Cycle("id", List.of(route1, route2, route3), 3);
        try {
            sut = new Intersection("id", "name", List.of(route1, route2, route3), singletonList(cycle1), WorkingMode.CYCLES);
        } catch (IncorrectRoutesException e) {
            assertEquals("Cycle id contains routes that cannot be green at the same time", e.getMessage());
        }
    }

    @Test
    void shouldUpdateCycle() throws IncorrectRoutesException, InterruptedException {
        Route route1 = getSimpleRoute();
        Route route2 = getConditionalRoute();
        Route route3 = getGradeSeparatedRoute();
        Cycle cycle1 = new Cycle("id", singletonList(route1), 3);
        Cycle cycle2 = new Cycle("id", singletonList(route2), 5);
        Cycle cycle3 = new Cycle("id", singletonList(route3), 6);

        sut = new Intersection("id", "name", List.of(route1, route2, route3), List.of(cycle1, cycle2, cycle3), WorkingMode.CYCLES);
        int time = sut.updateState();

        assertEquals(time, 3);
        assertEquals(sut.getCurrentCycle(), cycle1);
        assertEquals(sut.getCycleQueue().size(), 2);

        time = sut.updateState();
        assertEquals(time, 5);
        assertEquals(sut.getCurrentCycle(), cycle2);
        assertEquals(sut.getCycleQueue().size(), 2);

        time = sut.updateState();
        assertEquals(time, 6);
        assertEquals(sut.getCurrentCycle(), cycle3);
        assertEquals(sut.getCycleQueue().size(), 2);
    }

    @Test
    void shouldIncreaseCycleTime() throws IncorrectRoutesException, InterruptedException {
        Route route1 = getSimpleRoute();
        Route route2 = getConditionalRoute();
        Route route3 = getGradeSeparatedRoute();
        Cycle cycle1 = new Cycle("id", singletonList(route1), 3);
        Cycle cycle2 = new Cycle("id", singletonList(route2), 5);
        Cycle cycle3 = new Cycle("id", singletonList(route3), 6);

        sut = new Intersection("id", "name", List.of(route1, route2, route3), List.of(cycle1, cycle2, cycle3), WorkingMode.CYCLES_VARIABLE_TIME);

        when(camera1.getCars()).thenReturn(10);
        int time = sut.updateState();

        assertEquals(time, 25);
    }

    @Test
    void shouldAvoidEmptyCycles() throws IncorrectRoutesException, InterruptedException {
        Route route1 = getSimpleRoute();
        Route route2 = getConditionalRoute();
        Route route3 = getGradeSeparatedRoute();
        Cycle cycle1 = new Cycle("id", singletonList(route1), 3);
        Cycle cycle2 = new Cycle("id", singletonList(route2), 5);
        Cycle cycle3 = new Cycle("id", singletonList(route3), 6);

        sut = new Intersection("id", "name", List.of(route1, route2, route3), List.of(cycle1, cycle2, cycle3),
                WorkingMode.CYCLES_VARIABLE_TIME_AVOID_EMPTY_CYCLE);

        when(camera1.getCars()).thenReturn(0);
        when(camera3.getCars()).thenReturn(0);
        when(camera2.getCars()).thenReturn(10);

        int time = sut.updateState();
        assertEquals(time, 25);
        assertEquals(sut.getCurrentCycle(), cycle3);
    }

    private Route getSimpleRoute() {
        LaneInbound inboundLane = new LaneInbound("id", "name", NORTH, camera1);
        LaneOutbound outboundLane = new LaneOutbound("id", "name", SOUTH, true);
        TrafficLight trafficLight = new RoadTrafficLight("id", "name");
        return new Route("id", NORMAL, inboundLane, singletonList(outboundLane), singletonList(trafficLight));
    }

    private Route getGradeSeparatedRoute() {
        LaneInbound inboundLane = new LaneInbound("id", "name", SOUTH, camera2);
        LaneOutbound outboundLane = new LaneOutbound("id", "name", WEST, true);
        TrafficLight trafficLight = new RoadTrafficLight("id", "name");
        return new Route("id2", GRADE_SEPARATED, inboundLane, singletonList(outboundLane), singletonList(trafficLight));
    }

    private Route getConditionalRoute() {
        LaneInbound inboundLane = new LaneInbound("id", "name", SOUTH, camera3);
        LaneOutbound outboundLane = new LaneOutbound("id", "name", EAST, true);
        TrafficLight trafficLight = new RoadTrafficLight("id", "name");
        return new Route("id2", CONDITIONAL, inboundLane, singletonList(outboundLane), singletonList(trafficLight));
    }

    private Route getSimpleRoute2() {
        LaneInbound inboundLane = new LaneInbound("id", "name", SOUTH, camera4);
        LaneOutbound outboundLane = new LaneOutbound("id", "name", NORTH, true);
        TrafficLight trafficLight = new RoadTrafficLight("id", "name");
        return new Route("id2", NORMAL, inboundLane, singletonList(outboundLane), singletonList(trafficLight));
    }

    private Route getSimpleRoute3() {
        LaneInbound inboundLane = new LaneInbound("id", "name", SOUTH, camera3);
        LaneOutbound outboundLane = new LaneOutbound("id", "name", EAST, true);
        TrafficLight trafficLight = new RoadTrafficLight("id", "name");
        return new Route("id3", NORMAL, inboundLane, singletonList(outboundLane), singletonList(trafficLight));
    }
}