package team012;

import battlecode.common.*;

import java.util.ArrayList;
import java.util.Random;

public class RoamingArchon extends Bot{

    static RobotController rc = RobotPlayer.rc;
    static ArrayList<RobotType> buildList = new ArrayList<>();
    static Direction current = Direction.EAST;
    static int archonId = 0;
    static boolean isFirstA = false;
    static RobotType typeToBuild = RobotType.SCOUT;
    static MapLocation spot = rc.getInitialArchonLocations(myTeam)[0];
    static int lastTimeD = 0;
    static Random rand = new Random(rc.getID());

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

    /*private void roamAway(){
        if (rc.isCoreReady()) {
            //Outside of safe areas
            int minD = 50;

            MapLocation now = rc.getLocation();
            int xD = Math.abs(spot.x - now.x);
            int yD = Math.abs(spot.y - now.y);
            int distance = (int)Math.sqrt(xD*xD + yD*yD);
            minD = distance;

            boolean movingAway = (minD >= lastTimeD);
            boolean outOfCircles = (minD > 15);
            lastTimeD = minD;
            RobotInfo[] infoE = rc.senseNearbyRobots(10,enemyTeam);
            RobotInfo[] infoZ = rc.senseNearbyRobots(10,enemyTeam);

            if(rc.canMove(current) &&(outOfCircles || movingAway)) {
            } else {
                current = directions[(rand.nextInt(8))];
                for(int i = 0; i < 8;i++){
                    if(rc.canMove(current)){
                        break;
                    }
                    current.rotateLeft();
                }
            }
            // NearbyZombies
            if(infoZ.length != 0){
                //Nearby Enemy, then stop (never calls move command)
                if(infoE.length == 0){
                    boolean moved = false;
                    Direction curLeft = current.rotateLeft();
                    Direction current2 = current;
                    for(int i = 0; i < 4; i++){
                        boolean leftC = true;
                        int lX = curLeft.dx;
                        int lY = curLeft.dy;
                        boolean currentC = true;
                        int cX = current2.dx;
                        int cY = current2.dy;
                        // Check that extended line does not hit any zombies
                        // May want it to check different ranges at some point
                        for(int j = 0; j < infoZ.length; j++){
                            //if current is bad
                            RobotInfo z = infoZ[i];
                            int changeX = z.location.x - rc.getLocation().x;
                            int changeY = z.location.y - rc.getLocation().y;
                            if(cX == Math.abs(changeX)/changeX || cY == Math.abs(changeY)/changeY){
                                currentC = false;
                            }
                            if(lX == Math.abs(changeX)/changeX || lY == Math.abs(changeY)/changeY){
                                leftC = false;
                            }
                        }
                        if(currentC && rc.canMove(current2)){
                            rc.move(current2);
                            moved = true;
                            break;
                        } else if(leftC && rc.canMove(curLeft)){
                            rc.move(curLeft);
                            moved = true;
                            break;
                        }
                        current2.rotateRight();
                        curLeft.rotateLeft();
                    }
                    if(!moved){
                        rc.move(current);
                    }
                }
            } else{
                if(infoE.length == 0) current.opposite();
                rc.move(current);
            }

        }



    }*/

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
