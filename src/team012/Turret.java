package team012;

import battlecode.common.*;

public class Turret extends Global {

    public static void init(){};

    public static void turn() throws GameActionException{

        // If this robot type can attack, check for enemies within range and attack one
        if (rc.isWeaponReady()) {
            boolean shot = false;
            RobotInfo[] enemiesWithinRange = rc.senseNearbyRobots(myAttackRange, enemyTeam);
            RobotInfo[] zombiesWithinRange = rc.senseNearbyRobots(myAttackRange, Team.ZOMBIE);
            if (enemiesWithinRange.length > 0) {
                for (RobotInfo enemy : enemiesWithinRange) {
                    // Check whether the enemy is in a valid attack range (turrets have a minimum range)
                    if (rc.canAttackLocation(enemy.location)) {
                        rc.attackLocation(enemy.location);
                        shot = true;
                        break;
                    }
                }
            } else if (zombiesWithinRange.length > 0) {
                for (RobotInfo zombie : zombiesWithinRange) {
                    if (rc.canAttackLocation(zombie.location)) {
                        rc.attackLocation(zombie.location);
                        shot = true;
                        break;

                    }
                }
            }
            Signal[] signals = rc.emptySignalQueue();
            if(!shot && signals.length > 0){
                for(Signal sig: signals){
                    if(sig.getTeam().equals(myTeam)){
                        int x = sig.getMessage()[0];
                        int y = sig.getMessage()[1];
                        MapLocation shoot = new MapLocation(x,y);
                        if (rc.canAttackLocation(shoot)) {
                            rc.attackLocation(shoot);
                        }
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
