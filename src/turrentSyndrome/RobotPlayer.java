package turrentSyndrome;

import battlecode.common.*;

import javax.xml.stream.Location;
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
            boolean placedScout = false;
            RobotType typeToBuild = RobotType.SCOUT;
            boolean isFirstA = false;
            MapLocation[] a = rc.getInitialArchonLocations(myTeam);
            if(rc.getLocation().equals(a[0])) isFirstA = true;
            Direction current = Direction.EAST;
            //if()
            MapLocation b = new MapLocation(a[0].x+1,a[0].y);
            while (true) {
                // This is a loop to prevent the run() method from returning. Because of the Clock.yield()
                // at the end of it, the loop will iterate once per game round.
                try {
                    if(rc.isCoreReady() && (rc.hasBuildRequirements(typeToBuild))) {
                        if (isFirstA) {
                            if(rc.senseRobotAtLocation(b) != null && rc.senseRobotAtLocation(b).team.equals(myTeam)){
                                rc.repair(b);
                            }
                            for(int i = 0; i < 8; i++){
                                if(rc.canBuild(current,typeToBuild)){
                                    rc.build(current,typeToBuild);
                                    if(!placedScout){
                                        typeToBuild = RobotType.TURRET;
                                        placedScout = true;
                                    }
                                    current = current.rotateLeft();
                                    break;
                                }else{
                                    current = current.rotateLeft();
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
        } else if (rc.getType() == RobotType.SOLDIER) {
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
                            if(max%2 == 0 && rc.canMove(d3) && (south + west) < max){
                                rc.move(d3);
                                south++;
                            } else if(rc.canMove(d1) && (south + west) < max){
                                rc.move(d1);
                                west++;
                            }else if(rc.canMove(d3) && (south + west) < max) {
                                rc.move(d3);
                                south++;
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

                    Clock.yield();
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    e.printStackTrace();
                }
            }
        }else if (rc.getType() == RobotType.SCOUT) {
            Direction current = Direction.NORTH;
            MapLocation[] aRC = rc.getInitialArchonLocations(myTeam);
            myAttackRange = rc.getType().sensorRadiusSquared;
            MapLocation aCenter = rc.getInitialArchonLocations(myTeam)[0];
            int lastTimeD = 0;
            while (true) {
                // This is a loop to prevent the run() method from returning. Because of the Clock.yield()
                // at the end of it, the loop will iterate once per game round.
                try {
                    RobotInfo[] enemiesWithinRange = rc.senseNearbyRobots(myAttackRange, enemyTeam);
                    RobotInfo[] zombiesWithinRange = rc.senseNearbyRobots(myAttackRange, Team.ZOMBIE);
                    int minSD = 100;
                    int xShoot = 0;
                    int yShoot = 0;
                    int myX = rc.getLocation().x;
                    int myY = rc.getLocation().y;
                    for(RobotInfo enemy : enemiesWithinRange) {
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

                    for(RobotInfo enemy : zombiesWithinRange) {
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
                    Clock.yield();
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    e.printStackTrace();
                }
            }
        }

    }
}
