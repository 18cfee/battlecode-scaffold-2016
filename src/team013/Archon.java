package team013;

import battlecode.common.*;

import java.util.ArrayList;

public class Archon extends Global {

    static ArrayList<RobotType> buildList;
    static Direction current;
    static boolean placedScout;
    static RobotType typeToBuild = RobotType.SCOUT;

    static int archonId;
    // Just for testing out healing
    //static MapLocation b = new MapLocation(spawnLoc.x+1,spawnLoc.y);

    public static void init() {
        for (int i = 0; i < ourArchonSpawns.length; i++)
            if (spawnLoc.compareTo(ourArchonSpawns[i]) == 0)
                archonId = i;

        if (archonId == 0) {
            // BASE ARCHON INIT HERE

            current = Direction.NONE;
            placedScout = false;

            buildList = new ArrayList<>();
            buildList.add(RobotType.SCOUT);
            buildList.add(RobotType.TURRET);
            buildList.add(RobotType.SOLDIER);
            buildList.add(RobotType.SOLDIER);
            buildList.add(RobotType.SOLDIER);
            buildList.add(RobotType.SOLDIER);
        } else {
            // ROAMING ARCHON INIT HERE
            goal = rc.getInitialArchonLocations(enemyTeam)[0];


        }
        rc.setIndicatorString(2, String.valueOf(archonId));
    }

    public void runFrame() throws GameActionException{
        if (archonId > 0) {
            roam();
            return;
        } else if (archonId == 0) {
            if (rc.isCoreReady() && (rc.hasBuildRequirements(typeToBuild))) {
            /*if(rc.senseRobotAtLocation(b) != null && rc.senseRobotAtLocation(b).team.equals(myTeam)){
                rc.repair(b);
            }*/
                for (int i = 0; i < 8; i++) {
                    if (rc.canBuild(current, typeToBuild)) {
                        rc.build(current, typeToBuild);
                        if (!placedScout) {
                            typeToBuild = RobotType.TURRET;
                            placedScout = true;
                        }
                        current = current.rotateLeft();
                        break;
                    } else {
                        current = current.rotateLeft();
                    }
                }
            }
        }
    }

    private static void roam() throws GameActionException {

        rc.setIndicatorLine(goal,rc.getLocation(), 255,0,0);

        int b = Clock.getBytecodeNum();
        if (rc.isCoreReady()) {
            if (!Path.moveSafeTo(goal))
                if (!Path.moveTo(goal))
                    Path.moveSomewhereOrLeft(myLoc.directionTo(goal));
        }
        rc.setIndicatorString(0,String.valueOf(Clock.getBytecodeNum()-b));

    }

    public void tryConvertNeutral() {
        if (!rc.isCoreReady())
            return;
        RobotInfo[] adjNeutral = rc.senseNearbyRobots(2, Team.NEUTRAL);

    }

    public boolean buildFromList(Direction dir) throws GameActionException {
        if (buildList.isEmpty()) {
            if (tryBuild(dir, buildList.get(0))) {
                buildList.remove(0);
                return true;
            }
        }
        return false;
    }

    public boolean tryBuild(Direction dir, RobotType rType) throws GameActionException {
        if (rc.isCoreReady() && rc.canBuild(dir, rType)) {
            rc.build(dir, rType);
            return true;
        }
        return false;
    }

}
