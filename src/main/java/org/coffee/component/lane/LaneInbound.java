package org.coffee.component.lane;

import lombok.Getter;
import org.coffee.component.sensor.Camera;
import org.coffee.component.attribute.Location;

@Getter
public class LaneInbound extends Lane {
    private final Camera camera;

    public LaneInbound(String id, String name, Location location, Camera camera) {
        super(id, name, location);
        this.camera = camera;
    }

}
