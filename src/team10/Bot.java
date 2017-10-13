package team10;

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

    public static Direction moveSomewhere(Direction dir) throws GameActionException{
        for (int i = 0; i < 8; i++) {
            if (rc.canMove(dir)) {
                rc.move(dir);
                return dir;
            }
            dir = dir.rotateLeft();
        }
        return dir;
    }

    public boolean canBuy(RobotType botType){
        return botType.partCost < rc.getTeamParts();
    }

    public static MapLocation getLocAt(Direction dir, MapLocation loc){
        switch (dir) {
            case EAST:
                return new MapLocation(loc.x+1,loc.y);
            case WEST:
                return new MapLocation(loc.x-1, loc.y);
            case NORTH:
                return new MapLocation(loc.x, loc.y+1);
            case SOUTH:
                return new MapLocation(loc.x, loc.y-1);
            case SOUTH_EAST:
                return new MapLocation(loc.x+1, loc.y-1);
            case SOUTH_WEST:
                return new MapLocation(loc.x-1, loc.y-1);
            case NORTH_WEST:
                return new MapLocation(loc.x-1, loc.y+1);
            case NORTH_EAST:
                return new MapLocation(loc.x+1, loc.y+1);
            default: return new MapLocation(loc.x, loc.y);
        }
    }

}
