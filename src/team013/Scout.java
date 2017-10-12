package team013;

import battlecode.common.*;

public class Scout extends Global {

    public void runFrame() throws GameActionException{
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
        if(xShoot != 0 && yShoot != 0){
            rc.broadcastMessageSignal(xShoot,yShoot,80);
        }

    }

}


