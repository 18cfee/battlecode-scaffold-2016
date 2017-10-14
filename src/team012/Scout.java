package team012;

import battlecode.common.*;

import java.util.HashSet;

public class Scout extends Global {

    public static void turn() throws GameActionException{
        int minSD = 100;
        int xShoot = 0;
        int yShoot = 0;
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
        Signal[] signals = rc.emptySignalQueue();
        if(xShoot != 0 && yShoot != 0){
            rc.broadcastMessageSignal(xShoot,yShoot,7);
        } else {
            checkMessages(signals);
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
    public static void checkMessages(Signal[] signals) throws GameActionException{
        for(Signal message: signals){
            if(message.getTeam().equals(myTeam)){
                System.out.println("Got a message from my Team");
                int[] nums = message.getMessage();
                if(nums.length > 1){
                    int x = nums[0];
                    int y = nums[1];
                    int mx = myLoc.x;
                    int my = myLoc.y;
                    System.out.println("Message X: " + x + " Y: " + y);
                    System.out.println("My X: " + mx + " Y: " + my);
                    if(mx == -x && my == -y){
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


