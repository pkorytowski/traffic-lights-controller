package org.coffee.component.route;

import org.coffee.component.lane.LaneOutbound;
import org.coffee.component.lane.PedestrianLane;

import java.util.Set;

import static org.coffee.component.attribute.Location.EAST;
import static org.coffee.component.attribute.Location.NORTH;
import static org.coffee.component.attribute.Location.SOUTH;
import static org.coffee.component.attribute.Location.WEST;

public class RouteValidator {

    public static void validateRoute(Route route) {
        validateHasAtLeastOneInboundLane(route);
        validateHasAtLeastOneOutboundLane(route);
        validateHasAtLeastOneTrafficLight(route);

        switch (route.getType()) {
            case NORMAL -> validateNormalRoute(route);
            case ISOLATED -> validateGradeSeparatedRoute(route);
            case CONDITIONAL -> validateConditionalRoute(route);
            default -> { }
        }
    }

    private static void validateHasAtLeastOneOutboundLane(Route route) {
        if (route.getOutboundLanes().isEmpty()) {
            throw new InvalidRouteException("Route must have at least one outbound lane");
        }
    }

    private static void validateHasAtLeastOneTrafficLight(Route route) {
        if (route.getTrafficLights().isEmpty()) {
            throw new InvalidRouteException("Route must have at least one traffic light");
        }
    }

    private static void validateHasAtLeastOneInboundLane(Route route) {
        if (route.getInboundLane() == null) {
            throw new InvalidRouteException("Route must have at least one inbound lane");
        }
    }

    private static void validateConditionalRoute(Route route) {
        if (route.getOutboundLanes().size() != 1) {
            throw new InvalidRouteException("Conditional route must have exactly one outbound lane");
        }
        if (!route.getOutboundLanes().getFirst().isAllowConditionalTurn()) {
            throw new InvalidRouteException("Conditional route must have an outbound lane that allows conditional turn");
        }
        var horizontal = Set.of(EAST, WEST);
        var vertical = Set.of(NORTH, SOUTH);
        if ((horizontal.contains(route.getInboundLane().getLocation()) && horizontal.contains(route.getOutboundLanes().getFirst().getLocation()))
            || (vertical.contains(route.getInboundLane().getLocation()) && vertical.contains(route.getOutboundLanes().getFirst().getLocation()))) {
            throw new InvalidRouteException("Conditional route must have an outbound lane that is not in the same direction as the inbound lane");
        }
    }

    private static void validateNormalRoute(Route route) {
        route.getPedestrianLanes().forEach(pedestrianLane -> validatePedestrianLaneNotCrossInboundLane(route, pedestrianLane));
        LaneOutbound straightLane = findStraightLane(route);
        if (straightLane != null) {
            route.getPedestrianLanes().forEach(pedestrianLane -> {
                if (pedestrianLane.getCrossingLanes().contains(straightLane)) {
                    throw new InvalidRouteException("Pedestrian lane cannot cross the straight outbound lane");
                }
            });
        }
    }

    private static LaneOutbound findStraightLane(Route route) {
        if (route.getInboundLane().getLocation() == NORTH) {
            return route.getOutboundLanes().stream()
                    .filter(lane -> lane.getLocation() == SOUTH)
                    .findFirst()
                    .orElse(null);
        } else if (route.getInboundLane().getLocation() == SOUTH) {
            return route.getOutboundLanes().stream()
                    .filter(lane -> lane.getLocation() == NORTH)
                    .findFirst()
                    .orElse(null);
        } else if (route.getInboundLane().getLocation() == EAST) {
            return route.getOutboundLanes().stream()
                    .filter(lane -> lane.getLocation() == WEST)
                    .findFirst()
                    .orElse(null);
        } else {
            return route.getOutboundLanes().stream()
                    .filter(lane -> lane.getLocation() == EAST)
                    .findFirst()
                    .orElse(null);
        }
    }

    private static void validatePedestrianLaneNotCrossInboundLane(Route route, PedestrianLane pedestrianLane) {
        if (pedestrianLane.getCrossingLanes().contains(route.getInboundLane())) {
            throw new InvalidRouteException("Pedestrian lane cannot cross the inbound lane");
        }
    }

    private static void validatePedestrianLaneNotCrossOutboundLane(Route route, PedestrianLane pedestrianLane) {
        route.getOutboundLanes().forEach(outboundLane -> {
            if (pedestrianLane.getCrossingLanes().contains(outboundLane)) {
                throw new InvalidRouteException("Pedestrian lane cannot cross the outbound lane");
            }
        });
    }

    private static void validateGradeSeparatedRoute(Route route) {
        route.getPedestrianLanes().forEach(pedestrianLane -> validatePedestrianLaneNotCrossInboundLane(route, pedestrianLane));
        route.getPedestrianLanes().forEach(pedestrianLane -> validatePedestrianLaneNotCrossOutboundLane(route, pedestrianLane));
    }
}
