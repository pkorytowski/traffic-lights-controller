package org.coffee.utils;

import org.coffee.component.IncorrectRoutesException;
import org.coffee.component.attribute.WorkingMode;
import org.coffee.component.sensor.Camera;
import org.coffee.component.Cycle;
import org.coffee.component.Intersection;
import org.coffee.component.lane.LaneInbound;
import org.coffee.component.lane.LaneOutbound;
import org.coffee.component.route.Route;
import org.coffee.component.light.RoadTrafficLight;

import java.util.List;

import static org.coffee.component.attribute.Location.EAST;
import static org.coffee.component.attribute.Location.NORTH;
import static org.coffee.component.attribute.Location.SOUTH;
import static org.coffee.component.attribute.Location.WEST;
import static org.coffee.component.attribute.RouteType.NORMAL;

public class IntersectionComponentsCreator {

    public static Intersection createIntersection() throws IncorrectRoutesException {

        var Cam1 = new Camera("Cam1", "Cam1");
        var Cam2 = new Camera("Cam2", "Cam2");
        var Cam3 = new Camera("Cam3", "Cam3");
        var Cam4 = new Camera("Cam4", "Cam4");

        var Li1 = new LaneInbound("L1", "L1", NORTH, Cam1);
        var Li2 = new LaneInbound("L3", "L3", SOUTH, Cam2);
        var Li3 = new LaneInbound("L5", "L5", EAST, Cam3);
        var Li4 = new LaneInbound("L7", "L7", WEST, Cam4);

        var Lo1 = new LaneOutbound("L2", "L2", NORTH, false);
        var Lo2 = new LaneOutbound("L4", "L4", SOUTH, false);
        var Lo3 = new LaneOutbound("L6", "L6", EAST, false);
        var Lo4 = new LaneOutbound("L8", "L8", WEST, false);

        var TL1 = new RoadTrafficLight("TL1", "TL1");
        var TL2 = new RoadTrafficLight("TL2", "TL2");
        var TL3 = new RoadTrafficLight("TL3", "TL3");
        var TL4 = new RoadTrafficLight("TL4", "TL4");

        var R1 = new Route("R1", NORMAL, Li1, List.of(Lo2, Lo3, Lo4), List.of(TL1));
        var R2 = new Route("R2", NORMAL, Li2, List.of(Lo1, Lo3, Lo4), List.of(TL2));
        var R3 = new Route("R3", NORMAL, Li3, List.of(Lo1, Lo2, Lo4), List.of(TL3));
        var R4 = new Route("R4", NORMAL, Li4, List.of(Lo1, Lo2, Lo3), List.of(TL4));

        var C1 = new Cycle("C1", List.of(R1, R2), 3);
        var C2 = new Cycle("C2", List.of(R3, R4), 5);

        return new Intersection(
                "I1",
                "I1",
                List.of(R1, R2, R3, R4),
                List.of(C1, C2),
                WorkingMode.CYCLES_VARIABLE_TIME_AVOID_EMPTY_CYCLE
        );
    }
}
