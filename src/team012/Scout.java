package team012;

import battlecode.common.*;

public class Scout extends Global {

    public static void turn() throws GameActionException{

        MapLocation closest = null;
        int closestDist = 1000;
        for(RobotInfo enemy : visibleHostiles) {
            int enemyDist = myLoc.distanceSquaredTo(enemy.location);
            if(enemyDist < closestDist) {
                closest = enemy.location;
                closestDist = enemyDist;
            }
        }
        if(!(closest == null)) {
            Comm.sendMap(TURRET_SHOOT_HERE, closest);
        }

        checkMessages();

        if(rc.isCoreReady()){
            if(49 < rc.senseRubble(rc.getLocation())){
                rc.clearRubble(Direction.NONE);
            } else if(iNeedToMove){
                move();
            }

        }
    }

    static Direction lastMoved = Direction.NORTH;
    private static void move() throws GameActionException{
        lastMoved = Path.moveSomewhereOrLeft(lastMoved).rotateLeft();
        iNeedToMove = false;
        rc.setIndicatorString(0,"Set iNeedToMove to false");
    }

    private static boolean iNeedToMove = false;
    public static void checkMessages() throws GameActionException{
        for(Signal message: signals) {
            if (Comm.readSig(message)) {
                switch (Comm.channel) {
                    case SCOUT_NEXT:
                        if (Comm.loc.equals(myLoc)) {
                            iNeedToMove = true;
                            rc.setIndicatorString(0, "Set iNeedToMove to true");
                        }
                        break;
                    case DISINTEGRATE:
                        if (myLoc.equals(Comm.loc) && !rc.isInfected())
                            rc.disintegrate();
                        break;
                }
            }
        }
    }

    public static void loop() {
        while (true) {
            try {
                // BEGIN TURN
                update();
                turn();
                // END OF TURN
            } catch(Exception e) {
                e.printStackTrace();
            }
            Clock.yield();
        }
    }

}


