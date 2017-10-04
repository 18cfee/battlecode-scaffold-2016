package team012;

import battlecode.common.*;

import java.util.Random;

public class RobotPlayer {

    /**
     * run() is the method that is called when a robot is instantiated in the Battlecode world.
     * If this method returns, the robot dies!
     **/
    @SuppressWarnings("unused")
    public static void run(RobotController rc) {
        // You can instantiate variables here.
        Direction[] directions = {Direction.NORTH, Direction.NORTH_EAST, Direction.EAST, Direction.SOUTH_EAST,
                Direction.SOUTH, Direction.SOUTH_WEST, Direction.WEST, Direction.NORTH_WEST};
        RobotType[] robotTypes = {RobotType.SCOUT, RobotType.SOLDIER, RobotType.SOLDIER, RobotType.SOLDIER,
                RobotType.GUARD, RobotType.GUARD, RobotType.VIPER, RobotType.TURRET};
        Random rand = new Random(rc.getID());
        int myAttackRange = 0;
        Team myTeam = rc.getTeam();
        Team enemyTeam = myTeam.opponent();

        if (rc.getType() == RobotType.ARCHON) {
            try {
                // Any code here gets executed exactly once at the beginning of the game.
            } catch (Exception e) {
                // Throwing an uncaught exception makes the robot die, so we need to catch exceptions.
                // Caught exceptions will result in a bytecode penalty.
                System.out.println(e.getMessage());
                e.printStackTrace();
            }
            boolean placed = false;
            while (true) {
                // This is a loop to prevent the run() method from returning. Because of the Clock.yield()
                // at the end of it, the loop will iterate once per game round.
                try {
                    Signal[] signals = rc.emptySignalQueue();
                    if (signals.length > 0) {
                        // Set an indicator string that can be viewed in the client
                        rc.setIndicatorString(0, "I received a signal this turn!");
                    } else {
                        rc.setIndicatorString(0, "I don't any signal buddies");
                    }
                    if (rc.isCoreReady()) {
                        // Choose a random unit to build // now soldier
                        RobotType typeToBuild = robotTypes[1];
                        // Check for sufficient parts
                        if (rc.hasBuildRequirements(typeToBuild)) {
                            // Choose a random direction to try to build in
                            Direction dirToBuild = directions[(rand.nextInt(4)*2 + 1)];
                            placed = false;
                            for (int i = 0; i < 4; i++) {
                                // If possible, build in this direction
                                if (rc.canBuild(dirToBuild, typeToBuild)) {
                                    rc.build(dirToBuild, typeToBuild);
                                    placed = true;
                                    break;
                                } else {
                                    // Rotate the direction to try
                                    dirToBuild = dirToBuild.rotateLeft().rotateLeft();
                                }
                            }
                            if(!placed){
                                rc.broadcastSignal(80);
                            }
                        }
                    }

                    Clock.yield();
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    e.printStackTrace();
                }
            }
        } else if (rc.getType() != RobotType.TURRET) {
            try {
                // Any code here gets executed exactly once at the beginning of the game.
                myAttackRange = rc.getType().attackRadiusSquared;
            } catch (Exception e) {
                // Throwing an uncaught exception makes the robot die, so we need to catch exceptions.
                // Caught exceptions will result in a bytecode penalty.
                System.out.println(e.getMessage());
                e.printStackTrace();
            }
            int dir = 0;
            int move = 0;
            int max = 1; // Corner of box to fill with guys
            int west = 0;
            int south = 0;

            Direction d1 = Direction.WEST;
            Direction d3 = Direction.NORTH;
            while (true) {
                try {
                    // This is a loop to prevent the run() method from returning. Because of the Clock.yield()
                    // at the end of it, the loop will iterate once per game round.
                    move++;
                    // on first move, determine which side of arcon
                    if(move == 1){
                        RobotInfo[] info = rc.senseNearbyRobots(2,myTeam);
                        for(int i = 0; i < info.length; i++){
                            if(info[i].type.equals(RobotType.ARCHON)){
                                int roboStart = 1;
                                MapLocation aR = info[i].location;
                                MapLocation ours = rc.getLocation();
                                if(ours.x < aR.x){
                                    if(ours.y > aR.y){
                                        roboStart = 5;
                                    } else{
                                        roboStart = 7;
                                    }
                                } else if(ours.y > aR.y){
                                    roboStart = 3;
                                }
                                d1 = directions[roboStart - 1];
                                d3 = directions[(roboStart + 1)%8];
                                break;
                            }

                        }
                    }

                    // read the message to increase the bounds
                    /*Signal s = rc.readSignal();
                    Team a = s.getTeam();
                    if(a.equals(myTeam)) max++;*/
                    Signal[] signals = rc.emptySignalQueue();
                    for(int i = 0; i < signals.length; i++){
                        if(signals[i].getTeam().equals(myTeam)) max++;
                    }

                    /*int fate = rand.nextInt(1000);

                    if (fate % 5 == 3) {
                        // Send a normal signal
                        rc.broadcastSignal(80);
                    }*/
                    int round = rc.getRoundNum();
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
                            if(rc.canMove(d3) && (south + west) < max){
                                rc.move(d3);
                                south++;
                            } else if(rc.canMove(d1) && (south + west) < max){
                                rc.move(d1);
                                west++;
                            }
                        }
                    }

                    Clock.yield();
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    e.printStackTrace();
                }
            }
        } else if (rc.getType() == RobotType.TURRET) {
            try {
                myAttackRange = rc.getType().attackRadiusSquared;
            } catch (Exception e) {
                System.out.println(e.getMessage());
                e.printStackTrace();
            }

            while (true) {
                // This is a loop to prevent the run() method from returning. Because of the Clock.yield()
                // at the end of it, the loop will iterate once per game round.
                try {
                    // If this robot type can attack, check for enemies within range and attack one
                    if (rc.isWeaponReady()) {
                        RobotInfo[] enemiesWithinRange = rc.senseNearbyRobots(myAttackRange, enemyTeam);
                        RobotInfo[] zombiesWithinRange = rc.senseNearbyRobots(myAttackRange, Team.ZOMBIE);
                        if (enemiesWithinRange.length > 0) {
                            for (RobotInfo enemy : enemiesWithinRange) {
                                // Check whether the enemy is in a valid attack range (turrets have a minimum range)
                                if (rc.canAttackLocation(enemy.location)) {
                                    rc.attackLocation(enemy.location);
                                    break;
                                }
                            }
                        } else if (zombiesWithinRange.length > 0) {
                            for (RobotInfo zombie : zombiesWithinRange) {
                                if (rc.canAttackLocation(zombie.location)) {
                                    rc.attackLocation(zombie.location);
                                    break;
                                }
                            }
                        }
                    }

                    Clock.yield();
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }
}
