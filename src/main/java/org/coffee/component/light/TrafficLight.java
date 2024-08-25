package org.coffee.component.light;

public interface TrafficLight {
    String getId();
    String getName();
    void allowTraffic() throws Exception;
    void stopTraffic() throws Exception;
    void defaultSignal();
    boolean isAllowingTraffic();
}
