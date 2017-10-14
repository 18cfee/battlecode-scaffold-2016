package team012;

import battlecode.common.*;

public class Guard extends Global {

    public static void init() {

    }

    private static Direction moving = Direction.NORTH;
    public static void turn() throws GameActionException {
        if(rc.isCoreReady()){
            moving = Path.moveSomewhereOrLeft(moving);
            if(rc.canMove(moving)){
                rc.move(moving);
            }
        }
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
