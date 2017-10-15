package team012;

import battlecode.common.*;

public class Scout extends Global {

    public static void turn() throws GameActionException{
        checkMessages();

        int msgsSent = 0;
        for(RobotInfo hostile: visibleHostiles) {
            if(msgsSent < 20) {
                Comm.sendRobot(ENEMY_HERE, hostile);
                msgsSent++;
            }
        }

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


