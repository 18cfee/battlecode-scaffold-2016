package team012;

import battlecode.common.*;

import java.util.ArrayList;

public class Archon extends Bot{

    static RobotController rc = RobotPlayer.rc;
    static ArrayList<RobotType> buildList = new ArrayList<>();
    static Direction buildDir;
    static int archonId = 0;

    public Archon(){

        buildList.add(RobotType.SCOUT);
        buildList.add(RobotType.TURRET);
        buildList.add(RobotType.SOLDIER);
        buildList.add(RobotType.SOLDIER);
        buildList.add(RobotType.SOLDIER);
        buildList.add(RobotType.SOLDIER);

        buildDir = randomDir();

        for(int i = 0; i < archonLocs.length; i++)
            if (archonLocs[i] == spawnLoc)
                archonId = i+1;

    }

    public void runFrame() throws GameActionException{

//        if (archonId == 1)
//            buildFromList(randomDir());
        tryBuild(buildDir, RobotType.SOLDIER);
        buildDir = buildDir.rotateLeft();

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
