package team012;

import battlecode.common.*;

public class Turret extends Global {

    public static void init(){};

    public static void turn() throws GameActionException{


        // Disingrate Stuff below here
        for(Signal sig: signals) {
            if (Comm.readSig(sig) && Comm.channel == DISINEGRATE) {
                rc.disintegrate();
            }
        }
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
            if(!shot){
                for(Signal sig: signals){
                    // Shooting from here
                    if (Comm.readSig(sig) && Comm.channel == TURRET_SHOOT_HERE) {
                        rc.setIndicatorLine(myLoc,Comm.loc, 255,0,0);
                        MapLocation shoot = Comm.loc;
                        if (rc.canAttackLocation(shoot)) {
                            rc.attackLocation(shoot);
                            break;
                        }
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
