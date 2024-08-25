package org.coffee.component.lane;

import lombok.Getter;
import org.coffee.component.attribute.Location;

import java.util.List;

@Getter
public class PedestrianLane extends Lane {
    private final List<Lane> crossingLanes;

    public PedestrianLane(String id, String name, Location location, List<Lane> crossingLanes) {
        super(id, name, location);
        this.crossingLanes = crossingLanes;
    }
}
