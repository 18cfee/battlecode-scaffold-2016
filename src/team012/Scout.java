package team012;

import battlecode.common.*;

import java.util.HashSet;

public class Scout extends Global {

    public static void turn() throws GameActionException{
        int minSD = 100;
        int xShoot = -100;
        int yShoot = -100;
        int myX = rc.getLocation().x;
        int myY = rc.getLocation().y;
        for(RobotInfo enemy : visibleHostiles) {
            int x = enemy.location.x;
            int y = enemy.location.y;
            int dx = Math.abs(x - myX);
            int dy = Math.abs(y - myY);
            int cur = dx*dx + dy*dy;
            if(cur < minSD){
                minSD = cur;
                xShoot = x;
                yShoot = y;
            }
        }

        for(RobotInfo enemy : visibleZombies) {
            int x = enemy.location.x;
            int y = enemy.location.y;
            int dx = Math.abs(x - myX);
            int dy = Math.abs(y - myY);
            int cur = dx*dx + dy*dy;
            if(cur < minSD){
                minSD = cur;
                xShoot = x;
                yShoot = y;
            }
        }

        if(xShoot != -100 && yShoot != -100) {
            Comm.sendMsgXY(TURRET_SHOOT_HERE, xShoot, yShoot);
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

    private static void move() throws GameActionException{
        Path.moveSomewhereOrLeft(Direction.NORTH);
        iNeedToMove = false;
    }

    private static boolean iNeedToMove = false;
    public static void checkMessages() throws GameActionException{
        for(Signal message: signals) {
            if (Comm.readSig(message)) {
                if (Comm.channel == SCOUT_NEXT) {
                    if (Comm.loc.equals(myLoc) ) {
                        iNeedToMove = true;
                        break;
                    }
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


