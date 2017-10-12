package team013;

import battlecode.common.*;

import java.util.Random;

public class Soldier extends Global {

    static int dir;
    static int move;
    static int max; // Corner of box to fill with guys
    static int west;
    static int south;

    static Random rand;


    public static void init() {

        dir = 0;
        move = 0;
        max = 1;
        west = 0;
        south = 0;

        rand = new Random();
    }

    static Direction d1 = Direction.WEST;
    static Direction d3 = Direction.NORTH;

    public void runFrame() throws GameActionException{

        move++;
        // on first move, determine which side of arcon
        if (move == 1) {
            RobotInfo[] info = rc.senseNearbyRobots(2, myTeam);
            for (int i = 0; i < info.length; i++) {
                if (info[i].type.equals(RobotType.ARCHON)) {
                    int roboStart = 1;
                    MapLocation aR = info[i].location;
                    MapLocation ours = rc.getLocation();
                    if (ours.x < aR.x) {
                        if (ours.y > aR.y) {
                            roboStart = 5;
                        } else {
                            roboStart = 7;
                        }
                    } else if (ours.y > aR.y) {
                        roboStart = 3;
                    }
                    d1 = directions[roboStart - 1];
                    d3 = directions[(roboStart + 1) % 8];
                    break;
                }

            }
        }

        // read the message to increase the bounds
            /*Signal s = rc.readSignal();
            Team a = s.getTeam();
            if(a.equals(myTeam)) max++;*/
        Signal[] signals = rc.emptySignalQueue();
        for (int i = 0; i < signals.length; i++) {
            if (signals[i].getTeam().equals(myTeam)) max++;
        }

            /*int fate = rand.nextInt(1000);

            if (fate % 5 == 3) {
                // Send a normal signal
                rc.broadcastSignal(80);
            }*/
        boolean shouldAttack = false;

        // If this robot type can attack, check for enemies within range and attack one
        if (myAttackRange > 0) {
            RobotInfo[] enemiesWithinRange = rc.senseNearbyRobots(myAttackRange, enemyTeam);
            RobotInfo[] zombiesWithinRange = rc.senseNearbyRobots(myAttackRange, Team.ZOMBIE);
            if (enemiesWithinRange.length > 0) {
                shouldAttack = true;
                // Check if weapon is ready
                if (rc.isWeaponReady()) {
                    rc.attackLocation(enemiesWithinRange[rand.nextInt(enemiesWithinRange.length)].location);
                }
            } else if (zombiesWithinRange.length > 0) {
                shouldAttack = true;
                // Check if weapon is ready
                if (rc.isWeaponReady()) {
                    rc.attackLocation(zombiesWithinRange[rand.nextInt(zombiesWithinRange.length)].location);
                }
            }
        }

        if (!shouldAttack) {
            if (rc.isCoreReady()) {
                if (max % 2 == 0 && rc.canMove(d3) && (south + west) < max) {
                    rc.move(d3);
                    south++;
                } else if (rc.canMove(d1) && (south + west) < max) {
                    rc.move(d1);
                    west++;
                } else if (rc.canMove(d3) && (south + west) < max) {
                    rc.move(d3);
                    south++;
                }
            }
        }
    }

}
