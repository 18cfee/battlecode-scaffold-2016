package team012;

import battlecode.common.Clock;
import battlecode.common.RobotController;

public class RobotPlayer {

    /**
     * run() is the method that is called when a robot is instantiated in the Battlecode world.
     * If this method returns, the robot dies!
     **/
    public static RobotController rc;

    public static void run(RobotController rc) {

        RobotPlayer.rc = rc;

        Global.init();
        switch (rc.getType()) {
            case ARCHON:
                Archon.init();
                Archon.loop();
                break;
            case SOLDIER:
                Soldier.init();
                Soldier.loop();
                break;
            case SCOUT:
                Scout.init();
                Scout.loop();
                break;
            case TURRET:
                Turret.init();
                Turret.loop();
                break;
            case GUARD:
                Guard.init();
                Guard.loop();
                break;
            default:
                DefaultBot.init();
                DefaultBot.loop();
                break;
        }

    }
}