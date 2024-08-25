package org.coffee.component.light;

import lombok.Getter;
import org.coffee.component.attribute.Color;

import static org.coffee.component.attribute.Color.GREEN;

@Getter
public class ConditionalArrowTrafficLight implements TrafficLight {
    private final String id;
    private final String name;
    private Color color;

    public ConditionalArrowTrafficLight(String id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public void allowTraffic() {
        color = GREEN;
        System.out.println("Conditional traffic light " + id + " switched to " + color);
    }

    @Override
    public void stopTraffic() {
        color = Color.BLACK;
        System.out.println("Conditional traffic light " + id + " switched to " + color);
    }

    @Override
    public void defaultSignal() {
        color = Color.BLACK;
        System.out.println("Conditional traffic light " + id + " switched to " + color);
    }

    @Override
    public boolean isAllowingTraffic() {
        return color == GREEN;
    }
}
