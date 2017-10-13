package team10;

import battlecode.common.*;

import java.util.ArrayList;

public class CampingArchon extends Bot{

    static RobotController rc = RobotPlayer.rc;
    static ArrayList<RobotType> buildList = new ArrayList<>();
    static Direction current = Direction.EAST;
    static boolean placedScout = false;
    static RobotType typeToBuild = RobotType.SCOUT;
    // Just for testing out healing
    //static MapLocation b = new MapLocation(spawnLoc.x+1,spawnLoc.y);

    public CampingArchon(){

        buildList.add(RobotType.SCOUT);
        buildList.add(RobotType.TURRET);
        buildList.add(RobotType.SOLDIER);
        buildList.add(RobotType.SOLDIER);
        buildList.add(RobotType.SOLDIER);
        buildList.add(RobotType.SOLDIER);
    }

    public void runFrame() throws GameActionException{

        if(rc.isCoreReady() && (rc.hasBuildRequirements(typeToBuild))) {
            /*if(rc.senseRobotAtLocation(b) != null && rc.senseRobotAtLocation(b).team.equals(myTeam)){
                rc.repair(b);
            }*/
            for(int i = 0; i < 8; i++){
                if(rc.canBuild(current,typeToBuild)){
                    rc.build(current,typeToBuild);
                    if(!placedScout){
                        typeToBuild = RobotType.TURRET;
                        placedScout = true;
                    }
                    current = current.rotateLeft();
                    break;
                }else{
                    current = current.rotateLeft();
                }
            }
        }



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
