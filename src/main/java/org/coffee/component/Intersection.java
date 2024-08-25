package org.coffee.component;

import lombok.Getter;
import org.coffee.component.attribute.WorkingMode;
import org.coffee.component.route.Route;
import org.coffee.component.route.RouteValidator;
import org.coffee.utils.IntersectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

@Getter
public class Intersection {
    private final int PAUSE_TIME = 1500;
    private final int MINIMUM_CYCLE_TIME = 5;
    private final int CYCLE_TIME_PER_CAR = 2;
    private final int MAX_AGE = 5;
    private final int PAUSE_TIME_BETWEEN_TRAFFIC_CHECKS = 1000;
    private final String id;
    private final String name;
    private final List<Route> routes;
    private final List<Cycle> cycles;

    private Map<Route, List<Route>> collisionMatrix;
    private List<Route> greenRoutes;
    private Queue<Cycle> cycleQueue;
    private Cycle currentCycle;
    private WorkingMode workingMode;

    public Intersection(String id, String name, List<Route> routes, List<Cycle> cycles, WorkingMode workingMode) throws IncorrectRoutesException {
        this.id = id;
        this.name = name;
        this.routes = routes;
        this.cycles = cycles;
        this.workingMode = workingMode;
        initialize();
    }

    private void fillCollisionMatrix() {
        collisionMatrix = new HashMap<>();
        for (var route : routes) {
            List<Route> collisionRoutes = routes.stream()
                    .filter(r -> IntersectionUtils.isAllowedToGo(route, r))
                    .toList();
            collisionMatrix.put(route, collisionRoutes);
        }
    }

    private void initialize() throws IncorrectRoutesException {
        validateRoutes();
        fillCollisionMatrix();
        try {
            validateCycles();
        } catch (IllegalStateException e) {
            throw new IncorrectRoutesException(e.getMessage());
        }
        cycleQueue = new LinkedList<>(cycles);
        System.out.println("Intersection " + name + " initialized");
        System.out.println("Numbers of routes in queue: " + cycleQueue.size());
        greenRoutes = new ArrayList<>();
    }

    private void validateRoutes() {
        routes.forEach(RouteValidator::validateRoute);
    }

    private void validateCycles() throws IllegalStateException {
       cycles.forEach(cycle -> {
           var firstRoute = cycle.getRoutes().stream()
                   .findFirst()
                   .orElseThrow(() -> new IllegalArgumentException("Cycle " + cycle.getId() + " does not contain any routes"));
           var goodRoutes = collisionMatrix.get(firstRoute);
           var routesNotCollides = cycle.getRoutes().stream()
                   .filter(route -> route != firstRoute)
                   .allMatch(goodRoutes::contains);
           if (!routesNotCollides) {
               throw new IllegalStateException("Cycle " + cycle.getId() + " contains routes that cannot be green at the same time");
           }
       });
    }

    public int updateState() throws InterruptedException {
        return switch (workingMode) {
            case CYCLES -> updateStateCycles();
            case CYCLES_VARIABLE_TIME -> updateStateCyclesWithVariableTime();
            case CYCLES_VARIABLE_TIME_AVOID_EMPTY_CYCLE -> updateStateCyclesWithVariableTimeAvoidEmptyCycles();
            default -> -1;
        };
    }

    public int updateStateCyclesWithVariableTime() throws InterruptedException {
        stopCurrentCycle();

        currentCycle = cycleQueue.poll();
        if (currentCycle == null) {
            System.out.println("Intersection " + name + " has no more cycles");
            throw new IllegalStateException("Intersection " + name + " has no more cycles");
        } else {
            currentCycle.allowTraffic();
            System.out.println("Intersection " + name + " started cycle " + currentCycle.getId());
            return countTrafficTime(currentCycle.currentPressure());
        }
    }

    public int updateStateCyclesWithVariableTimeAvoidEmptyCycles() throws InterruptedException  {
        int pressure;
        Cycle nextCycle = cycleQueue.poll();
        if (nextCycle != null) {
            pressure = nextCycle.currentPressure();
        } else {
            System.out.println("Intersection " + name + " has no more cycles");
            stopCurrentCycle();
            throw new IllegalStateException("Intersection " + name + " has no more cycles");
        }
        while (pressure == 0 || nextCycle == currentCycle) {
            cycleQueue.add(nextCycle);
            nextCycle = cycleQueue.poll();
            if (nextCycle != null) {
                pressure = nextCycle.currentPressure();
                if (nextCycle == currentCycle) {
                    Thread.sleep(PAUSE_TIME_BETWEEN_TRAFFIC_CHECKS); //no need to check traffic every millisecond
                }
            } else {
                System.out.println("Intersection " + name + " has no more cycles");
                stopCurrentCycle();
                throw new IllegalStateException("Intersection " + name + " has no more cycles");
            }
        }

        stopCurrentCycle();

        currentCycle = nextCycle;
        currentCycle.allowTraffic();

        System.out.println("Intersection " + name + " started cycle " + currentCycle.getId());
        return countTrafficTime(currentCycle.currentPressure());
    }

    private int countTrafficTime(int pressure) {
        return pressure > 3 ? MINIMUM_CYCLE_TIME + pressure * CYCLE_TIME_PER_CAR : MINIMUM_CYCLE_TIME;
    }

    private int updateStateCycles() throws InterruptedException {
        stopCurrentCycle();

        currentCycle = cycleQueue.poll();
        if (currentCycle == null) {
            System.out.println("Intersection " + name + " has no more cycles");
            throw new IllegalStateException("Intersection " + name + " has no more cycles");
        } else {
            currentCycle.allowTraffic();
            System.out.println("Intersection " + name + " started cycle " + currentCycle.getId());
            return currentCycle.getGreenTime();
        }
    }

    private void stopCurrentCycle() throws InterruptedException {
        if (currentCycle != null) {
            currentCycle.stopTraffic();
            System.out.println("Intersection " + name + " ended cycle " + currentCycle.getId());
            cycleQueue.add(currentCycle);
            Thread.sleep(PAUSE_TIME);
        }
    }

    public void defaultSignal() {
        cycles.forEach(Cycle::defaultSignal);
    }
}
