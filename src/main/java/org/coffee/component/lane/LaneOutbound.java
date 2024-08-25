package org.coffee.component.lane;

import lombok.Getter;
import org.coffee.component.attribute.Location;

@Getter
public class LaneOutbound extends Lane {
    private final boolean allowConditionalTurn;

    public LaneOutbound(String id, String name, Location location, boolean allowConditionalTurn) {
        super(id, name, location);
        this.allowConditionalTurn = allowConditionalTurn;
    }
}
