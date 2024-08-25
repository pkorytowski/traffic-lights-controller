package org.coffee.component.light;

import lombok.Getter;
import org.coffee.component.attribute.Color;

import static java.lang.Thread.sleep;
import static org.coffee.component.attribute.Color.GREEN;
import static org.coffee.component.attribute.Color.RED;
import static org.coffee.component.attribute.Color.YELLOW;

@Getter
public class RoadTrafficLight implements TrafficLight {
    private final String id;
    private final String name;
    private Color color;
    private final Integer yellowDuration;

    public RoadTrafficLight(String id, String name) {
        this.id = id;
        this.name = name;
        this.color = RED;
        this.yellowDuration = 1500;
    }

    @Override
    public void allowTraffic() throws InterruptedException {
        color = YELLOW;
        System.out.println("Traffic light " + id + " switched to " + color);
        sleep(yellowDuration);
        color = GREEN;
        System.out.println("Traffic light " + id + " switched to " + color);
    }

    @Override
    public void stopTraffic() throws InterruptedException {
        color = YELLOW;
        System.out.println("Traffic light " + id + " switched to " + color);
        sleep(yellowDuration);
        color = RED;
        System.out.println("Traffic light " + id + " switched to " + color);
    }

    @Override
    public void defaultSignal() {
        color = YELLOW;
        System.out.println("Traffic light " + id + " switched to " + color);
    }

    @Override
    public boolean isAllowingTraffic() {
        return color == GREEN;
    }
}
