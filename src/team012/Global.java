package team012;

import battlecode.common.*;

public class Global {

    public static final int ZOMBIEDEN_LOCATION = 0xFF;
    public static final int ENEMY_ARCHON_LOCATION = 0xAB;
    public static final int HELP_ARCHON_AT_LOCATION = 0xAA;
    public static final int SCOUT_NEXT = 0xBB;
    public static final int TURRET_SHOOT_HERE = 0xAC;
    public static final int FORM_UNIT_ON_ME = 0xEE;
    public static final int EMPTY = 0x00;

    static final RobotController rc = RobotPlayer.rc;

    static Team myTeam;
    static Team enemyTeam;

    static RobotType myType;

    static final Direction[] directions = {Direction.NORTH, Direction.NORTH_EAST, Direction.EAST, Direction.SOUTH_EAST,
            Direction.SOUTH, Direction.SOUTH_WEST, Direction.WEST, Direction.NORTH_WEST};

    static MapLocation[] ourArchonSpawns;
    static MapLocation[] theirArchonSpawns;
    static MapLocation spawnLoc;
    static int spawnRound;

    static int myAttackRange;
    static int mySensorRange;
    static double myAttackPower;

    static RobotInfo[] visibleAllies;
    static RobotInfo[] visibleZombies;
    static RobotInfo[] visibleHostiles;
    static RobotInfo[] visibleTheirTeam;

    static MapLocation myLoc;
    static int roundNum;
    static double lastHp;
    static double enemyRatio;
    static double healthLost;
    static double myHp;

    static Signal[] signals;

    public static void init(){
        spawnRound = rc.getRoundNum();
        myLoc = rc.getLocation();
        myTeam = rc.getTeam();
        enemyTeam = myTeam.opponent();

        myType = rc.getType();

        ourArchonSpawns = rc.getInitialArchonLocations(myTeam);
        theirArchonSpawns = rc.getInitialArchonLocations(enemyTeam);

        myAttackRange = myType.attackRadiusSquared;
        mySensorRange = myType.sensorRadiusSquared;
        myAttackPower = myType.attackPower;
        spawnLoc = rc.getLocation();

        myHp = rc.getHealth();
        lastHp = myHp;

        Comm.init();
    }

    public static void update() {
        myLoc = rc.getLocation();
        roundNum = rc.getRoundNum();
        visibleZombies = rc.senseNearbyRobots(mySensorRange, Team.ZOMBIE);
        visibleHostiles = rc.senseHostileRobots(myLoc, mySensorRange);
        visibleAllies = rc.senseNearbyRobots(mySensorRange, myTeam);
        visibleTheirTeam = rc.senseNearbyRobots(mySensorRange, enemyTeam);
        signals = rc.emptySignalQueue();

        myHp = rc.getHealth();
        healthLost = lastHp-myHp;
        enemyRatio = getOutnumberFactor();
    }

    public static int getDangerFactor() {
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

    public static double getOutnumberFactor() {
        return ((double)visibleHostiles.length + 1) / (visibleAllies.length +1);
    }

    static Direction currentlyClearing = null;
    protected static void clearRubble() throws GameActionException{
        if(currentlyClearing != null && Path.senseRubbleFixBug(currentlyClearing) < 50){
            currentlyClearing = null;
        }
        if(currentlyClearing == null){
            //select lowest rubble in visinity
            Direction that = Direction.NORTH;
            Direction low = null;
            double min = 100000;
            for (int i = 0; i < 8; i++) {
                double rubAtSpot = Path.senseRubbleFixBug(that);
                if (rc.canBuild(that, RobotType.SCOUT) && min > rubAtSpot && rubAtSpot > 49) {
                    min = rubAtSpot;
                    low = that;
                }
                that = that.rotateLeft();
            }
            currentlyClearing = low;
        }
        if(currentlyClearing != null && rc.isCoreReady()) {
            rc.clearRubble(currentlyClearing);
        }
    }

    // UR CORE BETTER BE READY DUDDEEE
    protected static void clearRubble(Direction dir) throws GameActionException{
        MapLocation clearLocation = Path.getLocAt(dir, myLoc);
        if (rc.senseRubble(clearLocation) > 0) {
            rc.clearRubble(dir);
        }
    }

    public static Direction randomDir() {
        return directions[(int)(Math.random()*8)];
    }


    public static boolean canBuy(RobotType botType){
        return botType.partCost < rc.getTeamParts();
    }

    public static String mapLocationToString(MapLocation loc) {
        return "X: "+loc.x+"Y: "+loc.y;
    }

}
