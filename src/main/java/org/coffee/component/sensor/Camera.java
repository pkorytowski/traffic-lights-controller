package org.coffee.component.sensor;

import lombok.Getter;

import java.util.Random;

@Getter
public class Camera {
    private final String id;
    private final String name;
    private final Random random;

    public Camera(String id, String name) {
        this.id = id;
        this.name = name;
        this.random = new Random();
    }

    //simulation of camera detecting cars on the lane
    public int getCars() {
        boolean shouldReturnZero = random.nextBoolean();
        return shouldReturnZero ? 0 : random.nextInt(11);
    }
}
