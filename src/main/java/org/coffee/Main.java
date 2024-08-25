package org.coffee;

import org.coffee.component.IncorrectRoutesException;
import org.coffee.component.Intersection;

import static org.coffee.utils.IntersectionComponentsCreator.createIntersection;

public class Main {
    public static void main(String[] args) {

        Intersection intersection;
        try {
            intersection = createIntersection();
        } catch (IncorrectRoutesException e) {
            System.out.println(e.getMessage());
            return;
        }
        runIntersection(intersection);
    }

    public static void runIntersection(Intersection intersection) {
        while (true) {
            try {
                int greenTime = intersection.updateState();
                System.out.println("Intersection " + intersection.getName() + " will be green for " + greenTime + " seconds");
                Thread.sleep(greenTime * 1000);
            } catch (IllegalStateException e) {
                System.out.println("Intersection " + intersection.getName() + " has no more cycles");
                break;
            } catch (InterruptedException e) {
                System.out.println("Thread was interrupted, Failed to updateState on intersection:" + intersection.getName());
                Thread.currentThread().interrupt();
            } catch (Exception e) {
                System.out.println("Error running intersection: " + e.getMessage());
            }
        }
        System.out.println("Intersection " + intersection.getName() + " has finished, set to blinking yellow");
        intersection.defaultSignal();
    }
}