package team012;

import battlecode.common.*;

import java.util.Map;

public class Path extends Global {

    public static boolean moveSafeTo(MapLocation loc) throws GameActionException{
        Direction dir = myLoc.directionTo(loc);
        if (rc.canMove(dir) && !canEnemyAttack(getLocAt(dir, myLoc))) {
            rc.move(dir);
            return true;
        }
        if (rc.canMove(dir.rotateLeft()) && !canEnemyAttack(getLocAt(dir.rotateLeft(), myLoc))) {
            rc.move(dir.rotateLeft());
            return true;
        }
        if (rc.canMove(dir.rotateRight()) && !canEnemyAttack(getLocAt(dir.rotateRight(), myLoc))) {
            rc.move(dir);
            return true;
        }
        return false;
    }

    public static boolean moveTo(MapLocation loc) throws GameActionException{
        if (rc.getLocation() == loc) return false;
        return tryMoveDir(rc.getLocation().directionTo(loc));
    }

    public static boolean tryMoveDir(Direction dir) throws GameActionException{
        if (rc.canMove(dir)) {
            rc.move(dir);
            return true;
        }
        if (rc.canMove(dir.rotateLeft())) {
            rc.move(dir.rotateLeft());
            return true;
        }
        if (rc.canMove(dir.rotateRight())) {
            rc.move(dir.rotateRight());
            return true;
        }

        return false;
    }

    public static boolean canEnemyAttack(MapLocation loc) {
        for (RobotInfo enemy : visibleHostiles) {
            if (enemy.type.canAttack()) {
                if (enemy.type.attackRadiusSquared > enemy.location.distanceSquaredTo(loc))
                    return true;
            }
        }
        return false;
    }

    public static double enemyDmgAtLocation(MapLocation loc) {
        double dmg = 0d;
        for (RobotInfo enemy : visibleHostiles) {
            if (enemy.type.canAttack()) {
                if (enemy.type.attackRadiusSquared > enemy.location.distanceSquaredTo(loc))
                    dmg += enemy.type.attackPower;
            }
        }
        return dmg;
    }

    public static MapLocation getLocAt(Direction dir, MapLocation loc){
        switch (dir) {
            case EAST:
                return new MapLocation(loc.x+1,loc.y);
            case WEST:
                return new MapLocation(loc.x-1, loc.y);
            case NORTH:
                return new MapLocation(loc.x, loc.y-1);
            case SOUTH:
                return new MapLocation(loc.x, loc.y+1);
            case SOUTH_EAST:
                return new MapLocation(loc.x+1, loc.y+1);
            case SOUTH_WEST:
                return new MapLocation(loc.x-1, loc.y+1);
            case NORTH_WEST:
                return new MapLocation(loc.x-1, loc.y-1);
            case NORTH_EAST:
                return new MapLocation(loc.x+1, loc.y-1);
            default: return new MapLocation(loc.x, loc.y);
        }
    }

    public static Direction convertXY(int x, int y){
        Direction test = Direction.NORTH;
        for(int i = 0; i < 8; i++){
            if(test.dx == x && test.dy == y){
                return test;
            }
            test = test.rotateLeft();
        }
        return Direction.NONE;
    }

    public static Direction moveSomewhereOrLeft(Direction dir) throws GameActionException{
        for (int i = 0; i < 8; i++) {
            if (rc.canMove(dir)) {
                rc.move(dir);
                return dir;
            }
            dir = dir.rotateLeft();
        }
        return dir;
    }

    public static double senseRubbleFixBug (Direction sense) throws GameActionException{
        /*switch(sense){
            case NORTH:
                sense = Direction.SOUTH;
                break;
            case NORTH_EAST:
                sense = Direction.SOUTH_EAST;
                break;
            case NORTH_WEST:
                sense = Direction.SOUTH_WEST;
                break;
            case SOUTH:
                sense = Direction.NORTH;
                break;
            case SOUTH_EAST:
                sense = Direction.NORTH_EAST;
                break;
            case SOUTH_WEST:
                sense = Direction.NORTH_WEST;
                break;
        }*/
        MapLocation old = getLocAt(sense, rc.getLocation());
        //MapLocation senseAt = new MapLocation(old.x,old.y - 2*sense.dy);
        return rc.senseRubble(old);
    }

}
