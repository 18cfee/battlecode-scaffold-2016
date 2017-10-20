package team012;

import battlecode.common.*;

// this class is for bots that dont have a specific class built for them at this time
public class Viper extends Global{

    public static void init() {
    }

    public static void turn() throws GameActionException {
        rc.setIndicatorString(0, "I'm a viper!");
        if (rc.isCoreReady()) {
            if (!Soldier.tryAttack(null))
                Path.moveSomewhereOrLeft(Path.lastDirMoved);

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

