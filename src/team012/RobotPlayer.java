package team012;

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
        System.out.println("Carl");
        switch (rc.getType()) {

            case ARCHON:
                System.out.println("Carl2");
                if(rc.getLocation().equals(rc.getInitialArchonLocations(rc.getTeam())[0])){
                    // Standing Archon
                    bot = new CampingArchon();
                    System.out.println("Carl3");
                } else{
                    bot = new RoamingArchon();
                    System.out.println("Carl4");
                }

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