package org.coffee.component;

import lombok.Getter;
import org.coffee.component.route.Route;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.coffee.component.attribute.RouteType.CONDITIONAL;

@Getter
public class Cycle {
    private final String id;
    private final List<Route> routes;
    private final int greenTime;

    public Cycle(String id, List<Route> routes, Integer greenTime) {
        this.id = id;
        this.routes = routes;
        this.greenTime = greenTime;
    }

    public void allowTraffic() {
        if (isAllowingConditionalTraffic()) {
            stopTrafficForConditionalRoutes();
        }
        List<CompletableFuture<Void>> futures = routes.stream()
                .filter(route -> !route.getType().equals(CONDITIONAL))
                .map(route -> CompletableFuture.runAsync(route::allowTraffic))
                .toList();
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
    }

    public void stopTraffic() {
        List<CompletableFuture<Void>> futures = routes.stream()
                .map(route -> CompletableFuture.runAsync(route::stopTraffic))
                .toList();
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
    }

    public void defaultSignal() {
        routes.forEach(Route::defaultSignal);
    }

    public int currentPressure() {
        return routes.stream()
                .map(Route::currentPressure)
                .max(Integer::compareTo)
                .orElse(0);
    }

    public void allowTrafficForConditionalRoutes() {
        if (isAllowingNormalTraffic()) {
            throw new IllegalStateException("Normal traffic is going, cannot allow conditional routes");
        }
        List<CompletableFuture<Void>> futures = routes.stream()
                .filter(route -> route.getType().equals(CONDITIONAL))
                .map(route -> CompletableFuture.runAsync(route::allowTraffic))
                .toList();
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
    }

    public void stopTrafficForConditionalRoutes() {
        List<CompletableFuture<Void>> futures = routes.stream()
                .filter(route -> route.getType().equals(CONDITIONAL))
                .map(route -> CompletableFuture.runAsync(route::stopTraffic))
                .toList();
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
    }

    private boolean isAllowingNormalTraffic() {
        return routes.stream()
                .filter(route -> !route.getType().equals(CONDITIONAL))
                .anyMatch(Route::isAllowingTraffic);
    }

    private boolean isAllowingConditionalTraffic() {
        return routes.stream()
                .filter(route -> route.getType().equals(CONDITIONAL))
                .anyMatch(Route::isAllowingTraffic);
    }
}
