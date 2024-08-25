
# Traffic lights controller

## Aim of the project
The aim of the project was to create traffic lights controller that can handle traffic on a real-life intersection and provide safe way to navigate through it.
System should be able to handle various types of intersections including those with pedestrian crossings and multi-lane setup.

## Project structure
Project is divided into several components:
- **Traffic light** - represents a single traffic light. In real-life system it can be a connector to a traffic light controller in separate service/device. For now, it is only a simulation of changing lights.
- **Sensor** - represents a sensor that indicates traffic on a lane. It can be a camera, a pressure sensor or any other device that can detect presence of a vehicle or a pedestrian.
- **Lane** - represents a single lane of the intersection. It can be either for cars or pedestrians. Cars lanes are divided into inbound and outbound lanes. This allows to create multi-lanes intersections.
- **Route** - represents a route that a vehicle or a pedestrian can take. It indicates which outbound lanes are available from given inbound lane. This is the main component that creates the intersection. 
There are multiple rules applied to determine if two or more routes can serve traffic at the same time. There are three types of routes: 
  - Normal - represents a route indicated by traffic light S1
  - Conditional - represents a route indicated by traffic light S2, it allows to make conditional turn (e.g. right turn on red light aka "zielona strzałka")
  - Isolated - represents a route with undisturbed flow of traffic (traffic light S3)
- **Cycle** - a group of routes that can be served at the same time. For now, cycles are created manually, but validation of the cycle is done automatically.
- **Intersection** - a container for all components. It is responsible for validating routes and cycles and for serving them in the correct order.

All rules were defined based on Polish road traffic law.

## Available working modes:
- **Normal mode** - a default mode, cycles are served sequentially with fixed duration defined in cycle
- **Cycle with variable time** - cycles are served sequentially but duration of each is determined by traffic pressure on each cycle.
- **Cycle with variable time and avoid empty cycle** - modification of the previous mode. If there is no traffic on a cycle, it is skipped and next cycle is served.
- **Cycle-less** - to be implemented.

## Things to be done:
- Implement cycle-less mode
- Add more tests
- Add verification if lights changed correctly
- Emergency mode (stop traffic for all but one route to allow emergency vehicles to pass)
- Intersection refactor (there is a little mess)
- Refactor of isNotCrossingDirection method (maybe change from enum to azimuth in degrees)
- Add new features (e.g. trams, buses, bicycles)



