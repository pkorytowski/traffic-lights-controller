package org.coffee.utils;

import org.coffee.component.cycle.IncorrectRoutesException;
import org.coffee.component.attribute.WorkingMode;
import org.coffee.component.lane.PedestrianLane;
import org.coffee.component.light.ConditionalArrowTrafficLight;
import org.coffee.component.sensor.Camera;
import org.coffee.component.cycle.Cycle;
import org.coffee.component.intersection.Intersection;
import org.coffee.component.lane.LaneInbound;
import org.coffee.component.lane.LaneOutbound;
import org.coffee.component.route.Route;
import org.coffee.component.light.RoadTrafficLight;

import java.util.List;

import static java.util.Collections.singletonList;
import static org.coffee.component.attribute.Location.EAST;
import static org.coffee.component.attribute.Location.NORTH;
import static org.coffee.component.attribute.Location.SOUTH;
import static org.coffee.component.attribute.Location.WEST;
import static org.coffee.component.attribute.RouteType.CONDITIONAL;
import static org.coffee.component.attribute.RouteType.ISOLATED;
import static org.coffee.component.attribute.RouteType.NORMAL;

public class IntersectionComponentsCreator {

    public static Intersection createSimpleIntersection() throws IncorrectRoutesException {

        var cam1 = new Camera("Cam1", "Cam1");
        var cam2 = new Camera("Cam2", "Cam2");
        var cam3 = new Camera("Cam3", "Cam3");
        var cam4 = new Camera("Cam4", "Cam4");

        var li1 = new LaneInbound("L1", "L1", NORTH, cam1);
        var li2 = new LaneInbound("L3", "L3", SOUTH, cam2);
        var li3 = new LaneInbound("L5", "L5", EAST, cam3);
        var li4 = new LaneInbound("L7", "L7", WEST, cam4);

        var lo1 = new LaneOutbound("L2", "L2", NORTH, false);
        var lo2 = new LaneOutbound("L4", "L4", SOUTH, false);
        var lo3 = new LaneOutbound("L6", "L6", EAST, false);
        var lo4 = new LaneOutbound("L8", "L8", WEST, false);

        var tl1 = new RoadTrafficLight("TL1", "TL1");
        var tl2 = new RoadTrafficLight("TL2", "TL2");
        var tl3 = new RoadTrafficLight("TL3", "TL3");
        var tl4 = new RoadTrafficLight("TL4", "TL4");

        var r1 = new Route("R1", NORMAL, li1, List.of(lo2, lo3, lo4), List.of(tl1));
        var r2 = new Route("R2", NORMAL, li2, List.of(lo1, lo3, lo4), List.of(tl2));
        var r3 = new Route("R3", NORMAL, li3, List.of(lo1, lo2, lo4), List.of(tl3));
        var r4 = new Route("R4", NORMAL, li4, List.of(lo1, lo2, lo3), List.of(tl4));

        var c1 = new Cycle("C1", List.of(r1, r2), 3);
        var c2 = new Cycle("C2", List.of(r3, r4), 5);

        return new Intersection(
                "I1",
                "I1",
                List.of(r1, r2, r3, r4),
                List.of(c1, c2),
                WorkingMode.CYCLES_VARIABLE_TIME_AVOID_EMPTY_CYCLE
        );
    }

    public static Intersection createComplexIntersection() throws IncorrectRoutesException {
        var cam1 = new Camera("Cam1", "Cam1");
        var cam2 = new Camera("Cam2", "Cam2");
        var cam3 = new Camera("Cam3", "Cam3");
        var cam4 = new Camera("Cam4", "Cam4");
        var cam5 = new Camera("Cam5", "Cam5");
        var cam6 = new Camera("Cam6", "Cam6");

        var li1 = new LaneInbound("Li1", "Li1", EAST, cam1);
        var li2 = new LaneInbound("Li2", "Li2", WEST, cam2);
        var li3 = new LaneInbound("Li3", "Li3", NORTH, cam3);
        var li4 = new LaneInbound("Li4", "Li4", NORTH, cam4);
        var li5 = new LaneInbound("Li5", "Li5", SOUTH, cam5);
        var li6 = new LaneInbound("Li6", "Li6", SOUTH, cam6);

        var lo1 = new LaneOutbound("Lo3", "Lo3", WEST, false);
        var lo2 = new LaneOutbound("Lo6", "Lo6", EAST, false);
        var lo3 = new LaneOutbound("Lo4", "Lo4", NORTH, true);
        var lo4 = new LaneOutbound("Lo7", "Lo7", NORTH, false);
        var lo5 = new LaneOutbound("Lo5", "Lo5", SOUTH, false);
        var lo6 = new LaneOutbound("Lo8", "Lo8", SOUTH, true);

        var tl1 = new RoadTrafficLight("TL1", "TL1");
        var tl2 = new RoadTrafficLight("TL2", "TL2");
        var tl3 = new RoadTrafficLight("TL3", "TL3");
        var tl4 = new RoadTrafficLight("TL4", "TL4");
        var tl6 = new RoadTrafficLight("TL6", "TL6");
        var tl5 = new ConditionalArrowTrafficLight("TL5", "TL5");
        var tl7 = new RoadTrafficLight("TL7", "TL7");
        var tl8 = new ConditionalArrowTrafficLight("TL8", "TL8");

        var pl1 = new PedestrianLane("PL1", "PL1", SOUTH, List.of(lo5, lo6));
        var pl2 = new PedestrianLane("PL2", "PL2", SOUTH, List.of(li5, li6));
        var pl3 = new PedestrianLane("PL3", "PL3", EAST, singletonList(lo2));
        var pl4 = new PedestrianLane("PL4", "PL4", EAST, singletonList(li1));
        var pl5 = new PedestrianLane("PL5", "PL5", NORTH, List.of(lo3, lo4));
        var pl6 = new PedestrianLane("PL6", "PL6", NORTH, List.of(li3, li4));
        var pl7 = new PedestrianLane("PL7", "PL7", WEST, singletonList(lo1));
        var pl8 = new PedestrianLane("PL8", "PL8", WEST, singletonList(li2));

        var r1 = new Route("R1", NORMAL, li1, List.of(lo1, lo3, lo5), singletonList(tl1), List.of(pl5, pl6));
        var r2 = new Route("R2", NORMAL, li2, List.of(lo2, lo4, lo6), singletonList(tl2), List.of(pl1, pl2));
        var r3 = new Route("R3", NORMAL, li3, List.of(lo1, lo6), singletonList(tl3), List.of(pl7, pl8));
        var r4 = new Route("R4", ISOLATED, li4, List.of(lo2, lo5), singletonList(tl4), singletonList(pl2));
        var r5 = new Route("R5", CONDITIONAL, li1, singletonList(lo3), singletonList(tl5), List.of(pl4, pl5));
        var r6 = new Route("R6", NORMAL, li5, List.of(lo2, lo3), singletonList(tl6), List.of(pl3, pl4));
        var r7 = new Route("R7", ISOLATED, li6, List.of(lo4, lo1), singletonList(tl7), singletonList(pl6));
        var r8 = new Route("R8", CONDITIONAL, li2, singletonList(lo6), singletonList(tl8), List.of(pl8, pl1));

        var c1 = new Cycle("C1", List.of(r1, r2), 3);
        var c2 = new Cycle("C2", List.of(r3, r4, r5), 4);
        var c3 = new Cycle("C3", List.of(r6, r7, r8), 4);

        return new Intersection(
                "I1",
                "I1",
                List.of(r1, r2, r3, r4, r5, r6, r7, r8),
                List.of(c1, c2, c3),
                WorkingMode.CYCLES_VARIABLE_TIME_AVOID_EMPTY_CYCLE
        );
    }
}
