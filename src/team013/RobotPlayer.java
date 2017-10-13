package team013;

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
        Global bot;
        switch (rc.getType()) {
            case ARCHON:
                bot = new Archon();
                Archon.init();
                break;
            case SOLDIER:
                bot = new Soldier();
                Soldier.init();
                break;
            case SCOUT:
                bot = new Scout();
                Scout.init();
                break;
            case TURRET:
                bot = new Turret();
                Turret.init();
                break;
            case GUARD:
                bot = new Guard();
                Guard.init();
                break;
            default:
                bot = new CapturedBot();
                CapturedBot.init();
                break;
        }
        bot.run();


    }
}