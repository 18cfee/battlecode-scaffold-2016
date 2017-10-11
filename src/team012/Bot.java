package team012;

import battlecode.common.*;

public abstract class Bot {

    static final RobotController rc = RobotPlayer.rc;

    static final Team myTeam = rc.getTeam();
    static final Team enemyTeam = myTeam.opponent();

    static final Direction[] directions = {Direction.NORTH, Direction.NORTH_EAST, Direction.EAST, Direction.SOUTH_EAST,
            Direction.SOUTH, Direction.SOUTH_WEST, Direction.WEST, Direction.NORTH_WEST};

    static final RobotType[] robotTypes = {RobotType.SCOUT, RobotType.SOLDIER, RobotType.SOLDIER, RobotType.SOLDIER,
            RobotType.GUARD, RobotType.GUARD, RobotType.VIPER, RobotType.TURRET};

    static final MapLocation[] archonLocs = rc.getInitialArchonLocations(myTeam);

    static MapLocation spawnLoc;

    public Bot () {
            spawnLoc = rc.getLocation();

    }


    public abstract void runFrame() throws GameActionException;

    public void run() {

        while (true) {

            try {
                runFrame();
            } catch(Exception e) {
                e.printStackTrace();
            }

            Clock.yield();
        }

    }

    public static Direction randomDir() {
        return directions[(int)(Math.random()*8)];
    }

}
