package org.coffee.component.route;

import lombok.Getter;
import org.coffee.component.attribute.RouteType;
import org.coffee.component.lane.LaneInbound;
import org.coffee.component.lane.LaneOutbound;
import org.coffee.component.lane.PedestrianLane;
import org.coffee.component.light.TrafficLight;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static java.util.Collections.emptyList;

@Getter
public class Route {

    private final String id;
    private final LaneInbound inboundLane;
    private final RouteType type;
    private final List<LaneOutbound> outboundLanes;
    private final List<PedestrianLane> pedestrianLanes;
    private final List<TrafficLight> trafficLights;
    private final int greenTime;

    public Route(String id, RouteType type, LaneInbound inboundLane, List<LaneOutbound> outboundLanes, List<TrafficLight> trafficLights) {
        this.id = id;
        this.type = type;
        this.inboundLane = inboundLane;
        this.outboundLanes = outboundLanes;
        this.trafficLights = trafficLights;
        this.greenTime = 10;
        this.pedestrianLanes = emptyList();
    }

    public Route(String id, RouteType type, LaneInbound inboundLane, List<LaneOutbound> outboundLanes, List<TrafficLight> trafficLights, List<PedestrianLane> pedestrianLanes) {
        this.id = id;
        this.type = type;
        this.inboundLane = inboundLane;
        this.outboundLanes = outboundLanes;
        this.trafficLights = trafficLights;
        this.pedestrianLanes = pedestrianLanes;
        this.greenTime = 10;
    }

    public Route(String id, RouteType type, LaneInbound inboundLane, List<LaneOutbound> outboundLanes, List<TrafficLight> trafficLights, int greenTime) {
        this.id = id;
        this.type = type;
        this.inboundLane = inboundLane;
        this.outboundLanes = outboundLanes;
        this.trafficLights = trafficLights;
        this.greenTime = greenTime;
        this.pedestrianLanes = emptyList();
    }

    public void allowTraffic() {
        List<CompletableFuture<Void>> trafficFutures = trafficLights.stream()
                .map(trafficLight -> CompletableFuture.runAsync(() -> {
                    try {
                        trafficLight.allowTraffic();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        System.out.println("Thread was interrupted, Failed to doGreen on trafficLight:" + trafficLight.getId());
                    } catch (Exception e) {
                        System.out.println("Failed to doGreen on trafficLight:" + trafficLight.getId());
                    }
                }))
                .toList();
        CompletableFuture.allOf(trafficFutures.toArray(new CompletableFuture[0])).join();
    }

    public void stopTraffic() {
        List<CompletableFuture<Void>> futures = trafficLights.stream()
            .map(trafficLight -> CompletableFuture.runAsync(() -> {
                try {
                    trafficLight.stopTraffic();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    System.out.println("Thread was interrupted, Failed to doRed on trafficLight:" + trafficLight.getId());
                } catch (Exception e) {
                    System.out.println("Failed to doRed on trafficLight:" + trafficLight.getId());
                }
            })).toList();
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
    }

    public void defaultSignal() {
        trafficLights.forEach(roadTrafficLight -> CompletableFuture.runAsync(roadTrafficLight::defaultSignal));
    }

    public boolean isAllowingTraffic() {
        return trafficLights.stream().allMatch(TrafficLight::isAllowingTraffic);
    }

    public int currentPressure() {
        return inboundLane.getCamera().getCars();
    }
}
