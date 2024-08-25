package org.coffee.component.lane;

import lombok.Getter;
import org.coffee.component.attribute.Location;

@Getter
public abstract class Lane {
    private final String id;
    private final String name;
    private final Location location;

    public Lane(String id, String name, Location location) {
        this.id = id;
        this.name = name;
        this.location = location;
    }
}
