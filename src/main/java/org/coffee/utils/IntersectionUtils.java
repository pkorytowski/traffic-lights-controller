package org.coffee.utils;

import org.coffee.component.lane.Lane;
import org.coffee.component.lane.LaneInbound;
import org.coffee.component.attribute.Location;
import org.coffee.component.lane.PedestrianLane;
import org.coffee.component.route.Route;

import java.util.List;
import java.util.Set;

import static org.coffee.component.attribute.Location.EAST;
import static org.coffee.component.attribute.Location.NORTH;
import static org.coffee.component.attribute.Location.SOUTH;
import static org.coffee.component.attribute.Location.WEST;
import static org.coffee.component.attribute.RouteType.GRADE_SEPARATED;
import static org.coffee.component.attribute.RouteType.NORMAL;

public class IntersectionUtils {

    public static boolean isAllowedToGo(Route route1, Route route2) {
        var type = route1.getType();
        return switch (type) {
            case NORMAL -> isAllowedToGoNormal(route1, route2);
            case GRADE_SEPARATED -> isAllowedToGoGradeSeparated(route1, route2);
            case CONDITIONAL -> isAllowedToGoConditionalTurn(route1, route2);
        };
    }

    private static boolean isAllowedToGoNormal(Route route1, Route route2) {
        if (route2.getType() == NORMAL) {
            return isTheSameDirection(route1.getInboundLane(), route2.getInboundLane());
        } else if (route2.getType() == GRADE_SEPARATED) {
            return notCrossingNormalWithGradeSeparatedRoute(route1, route2);
        }
        return true;
    }

    private static boolean notCrossingNormalWithGradeSeparatedRoute(Route route1, Route route2) {
        return lanesNotCrossing(route1, route2) &&
                pedestrianLanesNotCrossingInboundLane(route1, route2.getPedestrianLanes()) &&
                pedestrianLanesNotCrossingOutboundLanes(route1, route2.getPedestrianLanes()) &&
                pedestrianLanesNotCrossingInboundLane(route2, route1.getPedestrianLanes()) &&
                directionsNotCrossing(route1, route2);
    }

    private static boolean isAllowedToGoGradeSeparated(Route route1, Route route2) {
        return lanesNotCrossing(route1, route2) &&
                pedestrianLanesNotCrossingInboundLane(route1, route2.getPedestrianLanes()) &&
                pedestrianLanesNotCrossingOutboundLanes(route1, route2.getPedestrianLanes()) &&
                pedestrianLanesNotCrossingInboundLane(route2, route1.getPedestrianLanes()) &&
                pedestrianLanesNotCrossingOutboundLanes(route2, route1.getPedestrianLanes()) &&
                directionsNotCrossing(route1, route2);
    }

    private static boolean isNotCrossingDirection(Location from1, Location to1, Location from2, Location to2) {
        if (from1 == SOUTH) {
            if (to1 == NORTH) {
                if (from2 == SOUTH) {
                    return Set.of(NORTH, WEST, EAST, SOUTH).contains(to2);
                } else if (from2 == NORTH) {
                    return Set.of(SOUTH, WEST).contains(to2);
                } else if (from2 == EAST) {
                    return to2 == EAST;
                } else if (from2 == WEST) {
                    return Set.of(SOUTH, WEST).contains(to2);
                }
            } else if (to1 == SOUTH) {
                if (from2 == SOUTH) {
                    return Set.of(NORTH, WEST, EAST, SOUTH).contains(to2);
                } else if (from2 == NORTH) {
                    return Set.of(EAST, WEST, NORTH).contains(to2);
                } else if (from2 == EAST) {
                    return Set.of(EAST, WEST, NORTH).contains(to2);
                } else if (from2 == WEST) {
                    return Set.of(EAST, WEST, NORTH).contains(to2);
                }
            } else if (to1 == EAST) {
                if (from2 == SOUTH) {
                    return Set.of(NORTH, WEST, EAST, SOUTH).contains(to2);
                } else if (from2 == NORTH) {
                    return Set.of(SOUTH, WEST, NORTH).contains(to2);
                } else if (from2 == EAST) {
                    return Set.of(NORTH, SOUTH, WEST).contains(to2);
                } else if (from2 == WEST) {
                    return Set.of(SOUTH, NORTH, WEST).contains(to2);
                }
            } else if (to1 == WEST) {
                if (from2 == SOUTH) {
                    return Set.of(NORTH, EAST, WEST, SOUTH).contains(to2);
                } else if (from2 == NORTH) {
                    return Set.of(NORTH, EAST).contains(to2);
                } else if (from2 == EAST) {
                    return Set.of(NORTH, EAST).contains(to2);
                } else if (from2 == WEST) {
                    return to2 == SOUTH;
                }
            }
        } else if (from1 == NORTH) {
            if (to1 == SOUTH) {
                if (from2 == SOUTH) {
                    return Set.of(NORTH, EAST).contains(to2);
                } else if (from2 == NORTH) {
                    return Set.of(SOUTH, WEST, EAST, NORTH).contains(to2);
                } else if (from2 == EAST) {
                    return Set.of(NORTH, EAST).contains(to2);
                } else if (from2 == WEST) {
                    return to2 == WEST;
                }
            } else if (to1 == NORTH) {
                if (from2 == SOUTH) {
                    return Set.of(EAST, WEST, SOUTH).contains(to2);
                } else if (from2 == NORTH) {
                    return Set.of(SOUTH, WEST, EAST, NORTH).contains(to2);
                } else if (from2 == EAST) {
                    return Set.of(EAST, WEST, SOUTH).contains(to2);
                } else if (from2 == WEST) {
                    return Set.of(EAST, WEST, SOUTH).contains(to2);
                }
            } else if (to1 == EAST) {
                if (from2 == SOUTH) {
                    return Set.of(SOUTH, WEST).contains(to2);
                } else if (from2 == NORTH) {
                    return Set.of(SOUTH, WEST, EAST, NORTH).contains(to2);
                } else if (from2 == EAST) {
                    return to2 == NORTH;
                } else if (from2 == WEST) {
                    return Set.of(SOUTH, WEST).contains(to2);
                }
            } else if (to1 == WEST) {
                if (from2 == SOUTH) {
                    return Set.of(NORTH, EAST, SOUTH).contains(to2);
                } else if (from2 == NORTH) {
                    return Set.of(NORTH, EAST, WEST, SOUTH).contains(to2);
                } else if (from2 == EAST) {
                    return Set.of(NORTH, SOUTH, EAST).contains(to2);
                } else if (from2 == WEST) {
                    return Set.of(EAST, NORTH, SOUTH).contains(to2);
                }
            }
        } else if (from1 == EAST) {
            if (to1 == SOUTH) {
                if (from2 == SOUTH) {
                    return to2 == EAST;
                } else if (from2 == NORTH) {
                    return Set.of(NORTH, WEST).contains(to2);
                } else if (from2 == EAST) {
                    return Set.of(NORTH, SOUTH, WEST, EAST).contains(to2);
                } else if (from2 == WEST) {
                    return Set.of(NORTH, WEST).contains(to2);
                }
            } else if (to1 == NORTH) {
                if (from2 == SOUTH) {
                    return Set.of(EAST, WEST, SOUTH).contains(to2);
                } else if (from2 == NORTH) {
                    return Set.of(SOUTH, WEST, EAST).contains(to2);
                } else if (from2 == EAST) {
                    return Set.of(NORTH, SOUTH, WEST, EAST).contains(to2);
                } else if (from2 == WEST) {
                    return Set.of(SOUTH, EAST, WEST).contains(to2);
                }
            } else if (to1 == EAST) {
                if (from2 == SOUTH) {
                    return Set.of(NORTH, WEST, SOUTH).contains(to2);
                } else if (from2 == NORTH) {
                    return Set.of(NORTH, WEST, SOUTH).contains(to2);
                } else if (from2 == EAST) {
                    return Set.of(NORTH, SOUTH, WEST, EAST).contains(to2);
                } else if (from2 == WEST) {
                    return Set.of(NORTH, WEST, SOUTH).contains(to2);
                }
            } else if (to1 == WEST) {
                if (from2 == SOUTH) {
                    return Set.of(SOUTH, EAST).contains(to2);
                } else if (from2 == NORTH) {
                    return to2 == NORTH;
                } else if (from2 == EAST) {
                    return Set.of(NORTH, SOUTH, WEST, EAST).contains(to2);
                } else if (from2 == WEST) {
                    return Set.of(EAST, SOUTH).contains(to2);
                }
            }
        } else if (from1 == WEST) {
            if (to1 == SOUTH) {
                if (from2 == SOUTH) {
                    return Set.of(WEST, NORTH, EAST).contains(to2);
                } else if (from2 == NORTH) {
                    return Set.of(WEST, NORTH, EAST).contains(to2);
                } else if (from2 == EAST) {
                    return Set.of(WEST, NORTH, EAST).contains(to2);
                } else if (from2 == WEST) {
                    return Set.of(NORTH, SOUTH, EAST, WEST).contains(to2);
                }
            } else if (to1 == NORTH) {
                if (from2 == SOUTH) {
                    return Set.of(EAST, WEST).contains(to2);
                } else if (from2 == NORTH) {
                    return to2 == WEST;
                } else if (from2 == EAST) {
                    return Set.of(SOUTH, WEST).contains(to2);
                } else if (from2 == WEST) {
                    return Set.of(NORTH, SOUTH, EAST, WEST).contains(to2);
                }
            } else if (to1 == EAST) {
                if (from2 == SOUTH) {
                    return to2 == SOUTH;
                } else if (from2 == NORTH) {
                    return Set.of(NORTH, WEST).contains(to2);
                } else if (from2 == EAST) {
                    return Set.of(NORTH, WEST).contains(to2);
                } else if (from2 == WEST) {
                    return Set.of(SOUTH, NORTH, EAST, WEST).contains(to2);
                }
            } else if (to1 == WEST) {
                if (from2 == SOUTH) {
                    return Set.of(NORTH, SOUTH, EAST).contains(to2);
                } else if (from2 == NORTH) {
                    return Set.of(NORTH, SOUTH, WEST).contains(to2);
                } else if (from2 == EAST) {
                    return Set.of(NORTH, SOUTH, WEST).contains(to2);
                } else if (from2 == WEST) {
                    return Set.of(NORTH, SOUTH, WEST, EAST).contains(to2);
                }
            }
        }
        return false;
    }

    private static boolean isTheSameDirection(LaneInbound lane1, LaneInbound lane2) {
        var horizontal = Set.of(EAST, WEST);
        var vertical = Set.of(Location.NORTH, SOUTH);

        return (horizontal.contains(lane1.getLocation()) && horizontal.contains(lane2.getLocation())) ||
                (vertical.contains(lane1.getLocation()) && vertical.contains(lane2.getLocation()));
    }

    private static boolean lanesNotCrossing(Route route1, Route route2) {
        return route1.getOutboundLanes().stream()
                .noneMatch(lane1 -> route2.getOutboundLanes().stream()
                        .anyMatch(lane1::equals));
    }

    private static boolean pedestrianLaneNotCrossingInboundLane(Route route, PedestrianLane pedestrianLane) {
        return !pedestrianLane.getCrossingLanes().contains(route.getInboundLane());
    }

    private static boolean pedestrianLanesNotCrossingInboundLane(Route route, List<PedestrianLane> pedestrianLanes) {
        return pedestrianLanes.stream()
                .allMatch(pedestrianLane -> pedestrianLaneNotCrossingInboundLane(route, pedestrianLane));
    }

    private static boolean pedestrianLanesNotCrossingOutboundLanes(Route route, List<PedestrianLane> pedestrianLanes) {
        return pedestrianLanes.stream()
                .allMatch(pedestrianLane -> pedestrianLaneNotCrossingOutboundLane(route, pedestrianLane));
    }

    private static boolean pedestrianLaneNotCrossingOutboundLane(Route route, PedestrianLane pedestrianLane) {
        return route.getOutboundLanes().stream()
                .noneMatch(outboundLane -> pedestrianLane.getCrossingLanes().contains(outboundLane));
    }

    private static boolean directionsNotCrossing(Route route1, Route route2) {
        return route1.getOutboundLanes().stream()
                .map(Lane::getLocation)
                .allMatch(direction1 -> route2.getOutboundLanes().stream()
                        .map(Lane::getLocation)
                        .allMatch(direction2 -> isNotCrossingDirection(route1.getInboundLane().getLocation(), direction1, route2.getInboundLane().getLocation(), direction2)));
    }

    private static boolean isAllowedToGoConditionalTurn(Route route1, Route route2) {
        if (route2.getType() == GRADE_SEPARATED) {
            return lanesNotCrossing(route1, route2) &&
                    pedestrianLanesNotCrossingInboundLane(route1, route2.getPedestrianLanes()) &&
                    pedestrianLanesNotCrossingOutboundLanes(route1, route2.getPedestrianLanes()) &&
                    directionsNotCrossing(route1, route2);
        }
        return true;
    }
}
