package team013;

import battlecode.common.*;

public class Guard extends Global {

    public static void init() {}

    public static void turn() {

    }

    public static void loop() {
        while (true) {
            try {
                // BEGIN TURN
                update();
                turn();
                // END OF TURN
            } catch(Exception e) {
                e.printStackTrace();
            }
            Clock.yield();
        }
    }
}
