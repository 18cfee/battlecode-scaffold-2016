package team012;

import battlecode.common.*;

public class Scout extends Bot {

    static final int myAttackRange = rc.getType().sensorRadiusSquared;

    static Direction current = Direction.NORTH;
    static MapLocation aCenter = archonLocs[0];
    static int lastTimeD = 0;
    static RobotInfo[] enemiesInRange;
    static RobotInfo[] zombiesInRange;

    public void runFrame() throws GameActionException{
        enemiesInRange = rc.senseNearbyRobots(myAttackRange, enemyTeam);
        zombiesInRange = rc.senseNearbyRobots(myAttackRange, Team.ZOMBIE);
        int minSD = 100;
        int xShoot = 0;
        int yShoot = 0;
        int myX = rc.getLocation().x;
        int myY = rc.getLocation().y;
        for(RobotInfo enemy : enemiesInRange) {
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

        for(RobotInfo enemy : zombiesInRange) {
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


