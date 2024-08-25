package org.coffee.component.light;

import lombok.Getter;
import org.coffee.component.attribute.Color;

import static java.lang.Thread.sleep;
import static org.coffee.component.attribute.Color.BLACK;
import static org.coffee.component.attribute.Color.GREEN;
import static org.coffee.component.attribute.Color.RED;

@Getter
public class PedestrianTrafficLight implements TrafficLight {

    private final String id;
    private final String name;
    private Color color;
    private static final int BLINK_DURATION = 1000;

    public PedestrianTrafficLight(String id, String name) {
        this.id = id;
        this.name = name;
        this.color = RED;
    }

    @Override
    public void allowTraffic() throws InterruptedException {
        color = GREEN;
        System.out.println("Pedestrian traffic light " + id + " switched to " + color);
    }

    @Override
    public void stopTraffic() throws InterruptedException {
        if (color == RED) {
            return;
        }
        if (color == GREEN) {
            System.out.println("Pedestrian traffic light " + id + " blinking " + color);
            sleep(BLINK_DURATION);
        }
        color = RED;
        System.out.println("Pedestrian traffic light " + id + " switched to " + color);
    }

    @Override
    public void defaultSignal() {
        color = BLACK;
        System.out.println("Pedestrian traffic light " + id + " switched to " + color);
    }

    @Override
    public boolean isAllowingTraffic() {
        return color == GREEN;
    }
}
