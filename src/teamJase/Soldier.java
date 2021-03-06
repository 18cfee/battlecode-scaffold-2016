package teamJase;

import battlecode.common.*;

import java.util.Map;
import java.util.Random;

public class Soldier extends Global {

    static Direction movDir;

    public static void init() {
        movDir = randomDir();
    }

    private static void turn() throws GameActionException{

        double potentialDmg = Path.enemyDmgAtLocation(myLoc);

        if(potentialDmg < 1) {
            if (rc.isCoreReady()) {
                if (!tryAttack()) {
                    // IF WE CANT ATTACK ANYTHING WE KEEP MOVING
                    movDir = Path.moveSomewhereOrLeft(movDir);
                }
            }
        } else if(potentialDmg < rc.getHealth()){
            if (rc.isCoreReady()) {
                if (!tryAttack()) {
                    movDir = randomDir();
                    if(!Path.tryMoveDir(movDir))
                        movDir = Path.moveSomewhereOrLeft(movDir);
                }
            }
        } else {            //if (potentialDmg > rc.getHealth()) {
            if (rc.isCoreReady()) {
                //move to safer spot
                Direction awayFromHostile = myLoc.directionTo(visibleHostiles[0].location).opposite();
                if (!Path.moveSafeTo(Path.getLocAt(awayFromHostile, myLoc))) ; // VISIBLE HOSTILES SHOULD NOT BE EMPTY
                    if (!Path.tryMoveDir(awayFromHostile))
                        tryAttack();

            }
        }

        if(rc.isCoreReady())
            clearRubble();
    }

    public static void loop() {
        while (true) {
            try {
                // BEGIN TURN
                update();
                //turn();
                // END OF TURN
            } catch(Exception e) {
                e.printStackTrace();
            }
            Clock.yield();
        }
    }

    public static boolean tryAttack() throws GameActionException{
        RobotInfo[] attackableHostiles = rc.senseHostileRobots(myLoc, myAttackRange);
        if (attackableHostiles.length == 0) return false;

        RobotInfo bestTarget = chooseBestTarget(attackableHostiles);
        if (rc.canAttackLocation(bestTarget.location)) {
            rc.attackLocation(bestTarget.location);
            return true;
        }
        return false;
    }

    // DOES NOT CHECK IF ARR IS EMPTY
    public static RobotInfo chooseBestTarget(RobotInfo[] robots) {

        int bestIdx = 0;
        double bestWeight = 0;
        for(int i = 0; i < Math.min(robots.length, 15); i++){
            if (getTargetWeight(robots[i]) > bestWeight)
                bestIdx = i;
        }
        return robots[bestIdx];
    }

    public static double getTargetWeight(RobotInfo robot) {
        switch (robot.type) {
            case ARCHON:
                return robot.health < myAttackPower ? 1000f : 0.2f;
            case SOLDIER:
                if (robot.zombieInfectedTurns > 0)
                    return robot.health < myAttackPower ? 0.7f : 0.6f;
                else
                    return robot.health < myAttackPower ? 0.6f : 0.5f;
            case FASTZOMBIE:
                return robot.health < myAttackPower ? 0.8f : 0.6f;
            case RANGEDZOMBIE:
                return robot.health < myAttackPower ? 0.7f : 1f;
            case BIGZOMBIE:
                return robot.health < myAttackPower ? 3f : 0.4f;
            case STANDARDZOMBIE:
                return robot.health < myAttackPower ? 0.65f : 0.55f;
            case ZOMBIEDEN:
                return robot.health < myAttackPower ? 2f : 0.2f;
            case VIPER:
                return robot.health < myAttackPower ? 3f : 1.2f;
            case GUARD:
                return robot.health < myAttackPower ? 0.33f : 0.05f;
            case SCOUT:
                if (robot.zombieInfectedTurns > 0)
                    return robot.health < myAttackPower ? 0.35f : 0.3f;
                else
                    return robot.health < myAttackPower ? 0.25f : 0.1f;
            case TURRET:
                return robot.health < myAttackPower ? 1.1f : 1f;
            case TTM:
                return robot.health < myAttackPower ? 0.6f : 0.5f;
            default:
                return .01;
        }
    }

    public static void getSignals(){
        for(Signal s : rc.emptySignalQueue()) {
            if (s.getTeam() == myTeam) {
                int[] msg = s.getMessage();
                MapLocation msgPoint = new MapLocation(msg[0], msg[1]);
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