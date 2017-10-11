package rebuild;

import battlecode.common.*;

public class RobotPlayer {

    /**
     * run() is the method that is called when a robot is instantiated in the Battlecode world.
     * If this method returns, the robot dies!
     **/
    public static RobotController rc;

    public static void run(RobotController rc) {
        RobotPlayer.rc = rc;


        Bot bot;
        switch (rc.getType()) {

            case ARCHON:
                bot = new Archon();
                break;
            case SOLDIER:
                bot = new Soldier();
                break;
            case SCOUT:
                bot = new Scout();
                break;
            case TURRET:
                bot = new Turret();
                break;
            case GUARD:
                bot = new Guard();
                break;
            default:
                return;
        }

        bot.run();


    }
}