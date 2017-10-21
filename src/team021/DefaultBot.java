package team021;

import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.GameActionException;

// this class is for bots that dont have a specific class built for them at this time
public class DefaultBot extends Global {

    public static Direction lastDir;
    public static void init() {
        lastDir = myLoc.directionTo(theirArchonSpawns[0]);
    }

    public static void turn() throws GameActionException {
        if (healthLost < 1)
            Path.moveRandom();
        else
            Path.runFromEnemies();
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

