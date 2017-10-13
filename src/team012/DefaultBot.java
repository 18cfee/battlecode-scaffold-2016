package team012;

import battlecode.common.*;

// this class is for bots that dont have a specific class built for them at this time
public class DefaultBot extends Global{

    public static Direction lastDir;
    public static void init() {
        lastDir = myLoc.directionTo(theirArchonSpawns[0]);
    }

    public static void turn() throws GameActionException {
        lastDir = Path.moveSomewhereOrLeft(lastDir);
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

