package team012;

import battlecode.common.*;

import java.util.Map;
import java.util.Random;

public class Soldier extends Global {

    static Direction movDir;

    private enum State {CAMP_ARCHON, EXPLORE}
    private static State state;

    public static void init() {

        movDir = randomDir();
        lastHp = rc.getHealth();
    }

    private static void turn() throws GameActionException{
        int zombieAdj = isZombieAdj();

        boolean didFire = false;
        if (rc.isWeaponReady()) {
            if (zombieAdj > -1)
                didFire = tryAttack(visibleZombies[zombieAdj].location);
            else
                didFire = tryAttack(null);
        }

        if(rc.isCoreReady()) {
            if (zombieAdj > -1)
                Path.moveTo(myLoc.add(myLoc.directionTo(visibleZombies[zombieAdj].location).opposite()));
            else if (healthLost > 0)
                Path.runFromEnemies();
            else if (myLoc.distanceSquaredTo(ourArchonSpawns[0]) > 20){
                if (!didFire)
                    Path.moveTo(ourArchonSpawns[0]);
            }
        }

        if (rc.isCoreReady())
            if (!didFire && visibleHostiles.length > 0) {
                Path.moveTo(visibleHostiles[0].location);
                tryAttack(null);
            }

        if(rc.isCoreReady()) {
            clearRubble();
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

    public static boolean tryAttack(MapLocation target) throws GameActionException{
        if (!(target == null) && rc.canAttackLocation(target)) {
            rc.attackLocation(target);
            return true;
        }

        RobotInfo[] attackableHostiles = rc.senseHostileRobots(myLoc, myAttackRange);
        if (attackableHostiles.length == 0) return false;

        for (RobotInfo robot : attackableHostiles)
            if (rc.canAttackLocation(robot.location)) {
                rc.attackLocation(robot.location);
                return true;
            }

        return false;
    }

    public static void getSignals(){
        for(Signal s : signals) {
            if (s.getTeam() == myTeam) {

            }
        }
    }

}


//
//        move++;
//        // on first move, determine which side of arcon
//        if (move == 1) {
//        RobotInfo[] info = rc.senseNearbyRobots(2, myTeam);
//        for (int i = 0; i < info.length; i++) {
//        if (info[i].type.equals(RobotType.ARCHON)) {
//        int roboStart = 1;
//        MapLocation aR = info[i].location;
//        MapLocation ours = rc.getLocation();
//        if (ours.x < aR.x) {
//        if (ours.y > aR.y) {
//        roboStart = 5;
//        } else {
//        roboStart = 7;
//        }
//        } else if (ours.y > aR.y) {
//        roboStart = 3;
//        }
//        d1 = directions[roboStart - 1];
//        d3 = directions[(roboStart + 1) % 8];
//        break;
//        }
//
//        }
//        }
//
//        // read the message to increase the bounds
//            /*Signal s = rc.readSignal();
//            Team a = s.getTeam();
//            if(a.equals(myTeam)) max++;*/
//        Signal[] signals = rc.emptySignalQueue();
//        for (int i = 0; i < signals.length; i++) {
//        if (signals[i].getTeam().equals(myTeam)) max++;
//        }
//
//            /*int fate = rand.nextInt(1000);
//
//            if (fate % 5 == 3) {
//                // Send a normal signal
//                rc.broadcastSignal(80);
//            }*/
//        int round = rc.getRoundNum();
//        boolean shouldAttack = false;
//
//        // If this robot type can attack, check for enemies within range and attack one
//        if (myAttackRange > 0) {
//        RobotInfo[] enemiesWithinRange = rc.senseNearbyRobots(myAttackRange, enemyTeam);
//        RobotInfo[] zombiesWithinRange = rc.senseNearbyRobots(myAttackRange, Team.ZOMBIE);
//        if (enemiesWithinRange.length > 0) {
//        shouldAttack = true;
//        // Check if weapon is ready
//        if (rc.isWeaponReady()) {
//        rc.attackLocation(enemiesWithinRange[rand.nextInt(enemiesWithinRange.length)].location);
//        }
//        } else if (zombiesWithinRange.length > 0) {
//        shouldAttack = true;
//        // Check if weapon is ready
//        if (rc.isWeaponReady()) {
//        rc.attackLocation(zombiesWithinRange[rand.nextInt(zombiesWithinRange.length)].location);
//        }
//        }
//        }
//
//        if (!shouldAttack) {
//        if (rc.isCoreReady()) {
//        if (max % 2 == 0 && rc.canMove(d3) && (south + west) < max) {
//        rc.move(d3);
//        south++;
//        } else if (rc.canMove(d1) && (south + west) < max) {
//        rc.move(d1);
//        west++;
//        } else if (rc.canMove(d3) && (south + west) < max) {
//        rc.move(d3);
//        south++;
//        }
//        }
//        }
//