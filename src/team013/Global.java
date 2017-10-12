package team013;

import battlecode.common.*;

public class Global {

    static final RobotController rc = RobotPlayer.rc;

    static Team myTeam;
    static Team enemyTeam;

    static final Direction[] directions = {Direction.NORTH, Direction.NORTH_EAST, Direction.EAST, Direction.SOUTH_EAST,
            Direction.SOUTH, Direction.SOUTH_WEST, Direction.WEST, Direction.NORTH_WEST};

    static MapLocation[] ourArchonSpawns;
    static MapLocation spawnLoc;

    static int myAttackRange;
    static int mySensorRange;

    static RobotInfo[] visibleAllies;
    static RobotInfo[] visibleZombies;
    static RobotInfo[] visibleHostiles;

    static MapLocation myLoc;
    static int roundNum;

    static enum Goals {ENEMY_ARCHON, NEUTRAL_ARCHON, SHIT_IF_I_KNOW}
    static MapLocation goal;

    public static void init(){
        myTeam = rc.getTeam();
        enemyTeam = myTeam.opponent();

        ourArchonSpawns = rc.getInitialArchonLocations(myTeam);
        myAttackRange = rc.getType().attackRadiusSquared;
        mySensorRange = rc.getType().sensorRadiusSquared;
        spawnLoc = rc.getLocation();

    }


    public void runFrame() throws GameActionException {

    }

    public void run() {

        while (true) {
            try {
                myLoc = rc.getLocation();
                roundNum = rc.getRoundNum();
                updateNearby();

                runFrame();
            } catch(Exception e) {
                e.printStackTrace();
            }
            Clock.yield();
        }
    }

    public static void updateNearby(){
        visibleZombies = rc.senseNearbyRobots(mySensorRange, Team.ZOMBIE);
        visibleHostiles = rc.senseHostileRobots(myLoc, mySensorRange);
        visibleAllies = rc.senseNearbyRobots(mySensorRange, myTeam);
    }

    public static int getDangerLevel() {
        int danger = 0;
        for (RobotInfo robot : visibleZombies) {
            if(robot.type == RobotType.BIGZOMBIE) {
                danger += 50;
                continue;
            }
            if(robot.type == RobotType.FASTZOMBIE) {
                danger += 25;
                continue;
            }
            if(robot.type == RobotType.STANDARDZOMBIE) {
                danger += 20;
            }
        }
        return danger;
    }

    public static Direction randomDir() {
        return directions[(int)(Math.random()*8)];
    }


    public boolean canBuy(RobotType botType){
        return botType.partCost < rc.getTeamParts();
    }

}
