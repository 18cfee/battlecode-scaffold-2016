package team012;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import battlecode.common.RobotType;

import java.util.ArrayList;

public class RoamingArchon extends Bot{

    static RobotController rc = RobotPlayer.rc;
    static ArrayList<RobotType> buildList = new ArrayList<>();
    static Direction current = Direction.EAST;
    static int archonId = 0;
    static boolean isFirstA = false;
    static RobotType typeToBuild = RobotType.SCOUT;

    public RoamingArchon(){

        buildList.add(RobotType.SCOUT);
        buildList.add(RobotType.TURRET);
        buildList.add(RobotType.SOLDIER);
        buildList.add(RobotType.SOLDIER);
        buildList.add(RobotType.SOLDIER);
        buildList.add(RobotType.SOLDIER);
        if(spawnLoc == rc.getInitialArchonLocations(myTeam)[0]) isFirstA =true;
    }

    public void runFrame() throws GameActionException{




    }

    public boolean buildFromList(Direction dir) throws GameActionException {
        if (buildList.isEmpty() == false) {
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
