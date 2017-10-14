package team012;

import battlecode.common.*;

public class Guard extends Global {

    public static void init() {

    }

    private static Direction moving = Direction.NORTH;
    private static MapLocation oldLoc = myLoc;
    private static boolean clockwise = true;
    private static boolean cont = false;
    private static boolean startedPatrol = false;
    private static int turn = 1;

    public static void turn() throws GameActionException {
        if(rc.isCoreReady()) {
            if(turn == 1){
                for (RobotInfo ally : Global.visibleAllies) {
                    if (ally.type == RobotType.ARCHON) {
                        if (ally.location.distanceSquaredTo(myLoc) < 4) {
                            oldLoc = myLoc;
                            Path.tryMoveDir(ally.location.directionTo(myLoc));
                            moving = oldLoc.directionTo(myLoc);
                        }
                    }
                }
            }

            if(startedPatrol) {
                System.out.println("Guard going into patrol: moving = " + moving + "; turn number: " + Global.roundNum);
                patrolTurrets();
            }else{
                for(RobotInfo ally : visibleAllies){
                    if(ally.type == RobotType.TURRET && myLoc.directionTo(ally.location) == Direction.NORTH_WEST && myLoc.distanceSquaredTo(ally.location) < 5) {
                        startedPatrol = true;
                        oldLoc = myLoc;
                        Path.tryMoveDir(Direction.WEST);
                        moving = Direction.WEST;
                        clockwise = true;
                        break;
                    }else if(ally.type == RobotType.TURRET && myLoc.directionTo(ally.location) == Direction.NORTH_EAST && myLoc.distanceSquaredTo(ally.location) < 5) {
                        startedPatrol = true;
                        oldLoc = myLoc;
                        Path.tryMoveDir(Direction.EAST);
                        moving = Direction.EAST;
                        clockwise = false;
                        break;
                    }else if(ally.type == RobotType.TURRET && myLoc.directionTo(ally.location) == Direction.SOUTH_WEST && myLoc.distanceSquaredTo(ally.location) < 5){
                        startedPatrol = true;
                        oldLoc = myLoc;
                        Path.tryMoveDir(Direction.WEST);
                        moving = Direction.WEST;
                        clockwise = false;
                        System.out.println("First starting patrol: startedPatrol = " + startedPatrol + "; moving = " + moving.toString() + "; clockwise = " + clockwise);
                        break;
                    }else if(ally.type == RobotType.TURRET && myLoc.directionTo(ally.location) == Direction.SOUTH_EAST && myLoc.distanceSquaredTo(ally.location) < 5) {
                        startedPatrol = true;
                        oldLoc = myLoc;
                        Path.tryMoveDir(Direction.EAST);
                        moving = Direction.EAST;
                        clockwise = true;
                        break;
                    }
                }
            }
        }
    }


    public static void patrolTurrets() throws GameActionException{
        cont = false;
        Direction inMoving = moving;
        if(clockwise){
            if(moving == Direction.SOUTH){
                for (RobotInfo ally : visibleAllies){
                    if(ally.type == RobotType.TURRET && myLoc.directionTo(ally.location) == Direction.WEST && ally.location.distanceSquaredTo(myLoc) < 4){
                        cont = true;
                        break;
                    }
                }
                if(!cont){
                    moving = Direction.WEST;
                }
                if(rc.canMove(moving)){
                    oldLoc = myLoc;
                    Path.tryMoveDir(moving);
                }else{
                    clockwise = !clockwise;
                    moving = myLoc.directionTo(oldLoc);
                    if(rc.canMove(moving)){
                        oldLoc = myLoc;
                        Path.tryMoveDir(moving);
                    }else{
                        clockwise = !clockwise;
                        moving = inMoving;
                    }
                }
            }else if(moving == Direction.WEST) {
                for (RobotInfo ally : visibleAllies) {
                    if (ally.type == RobotType.TURRET && myLoc.directionTo(ally.location) == Direction.NORTH && ally.location.distanceSquaredTo(myLoc) < 4) {
                        cont = true;
                        break;
                    }
                }
                if (!cont) {
                    moving = Direction.NORTH;
                }
                if(rc.canMove(moving)){
                    oldLoc = myLoc;
                    Path.tryMoveDir(moving);
                }else{
                    clockwise = !clockwise;
                    moving = myLoc.directionTo(oldLoc);
                    if(rc.canMove(moving)){
                        oldLoc = myLoc;
                        Path.tryMoveDir(moving);
                    }else{
                        clockwise = !clockwise;
                        moving = inMoving;
                    }
                }
            }else if(moving == Direction.NORTH){
                for (RobotInfo ally : visibleAllies){
                    if(ally.type == RobotType.TURRET && myLoc.directionTo(ally.location) == Direction.EAST && ally.location.distanceSquaredTo(myLoc) < 4){
                        cont = true;
                        break;
                    }
                }
                if(!cont){
                    moving = Direction.EAST;
                }
                if(rc.canMove(moving)){
                    oldLoc = myLoc;
                    Path.tryMoveDir(moving);
                }else{
                    clockwise = !clockwise;
                    moving = myLoc.directionTo(oldLoc);
                    if(rc.canMove(moving)){
                        oldLoc = myLoc;
                        Path.tryMoveDir(moving);
                    }else{
                        clockwise = !clockwise;
                        moving = inMoving;
                    }
                }
            }else if(moving == Direction.EAST){
                for (RobotInfo ally : visibleAllies){
                    if(ally.type == RobotType.TURRET && myLoc.directionTo(ally.location) == Direction.SOUTH && ally.location.distanceSquaredTo(myLoc) < 4){
                        cont = true;
                        break;
                    }
                }
                if(!cont){
                    moving = Direction.SOUTH;
                }
                if(rc.canMove(moving)){
                    oldLoc = myLoc;
                    Path.tryMoveDir(moving);
                }else{
                    clockwise = !clockwise;
                    moving = myLoc.directionTo(oldLoc);
                    if(rc.canMove(moving)){
                        oldLoc = myLoc;
                        Path.tryMoveDir(moving);
                    }else{
                        clockwise = !clockwise;
                        moving = inMoving;
                    }
                }
            }
        }else{
            if(moving == Direction.SOUTH){
                for (RobotInfo ally : visibleAllies){
                    if(ally.type == RobotType.TURRET && myLoc.directionTo(ally.location) == Direction.EAST && ally.location.distanceSquaredTo(myLoc) < 4){
                        cont = true;
                        break;
                    }
                }
                if(!cont){
                    moving = Direction.EAST;
                }
                if(rc.canMove(moving)){
                    oldLoc = myLoc;
                    Path.tryMoveDir(moving);
                }else{
                    clockwise = !clockwise;
                    moving = myLoc.directionTo(oldLoc);
                    if(rc.canMove(moving)){
                        oldLoc = myLoc;
                        Path.tryMoveDir(moving);
                    }else{
                        clockwise = !clockwise;
                        moving = inMoving;
                    }
                }
            }else if(moving == Direction.WEST){
                for (RobotInfo ally : visibleAllies){
                    if(ally.type == RobotType.TURRET && myLoc.directionTo(ally.location) == Direction.SOUTH && ally.location.distanceSquaredTo(myLoc) < 4){
                        cont = true;
                        break;
                    }
                }
                if(!cont){
                    moving = Direction.SOUTH;
                    System.out.println("Changing direction : " + moving.toString() + "; turn number = " + roundNum);
                }
                if(rc.canMove(moving)){
                    oldLoc = myLoc;
                    Path.tryMoveDir(moving);
                }else{
                    clockwise = !clockwise;
                    moving = myLoc.directionTo(oldLoc);
                    if(rc.canMove(moving)){
                        oldLoc = myLoc;
                        Path.tryMoveDir(moving);
                    }else{
                        clockwise = !clockwise;
                        moving = inMoving;
                    }
                }
            }else if(moving == Direction.NORTH){
                for (RobotInfo ally : visibleAllies){
                    if(ally.type == RobotType.TURRET && myLoc.directionTo(ally.location) == Direction.WEST && ally.location.distanceSquaredTo(myLoc) < 4){
                        cont = true;
                        break;
                    }
                }
                if(!cont){
                    moving = Direction.WEST;
                }
                if(rc.canMove(moving)){
                    oldLoc = myLoc;
                    Path.tryMoveDir(moving);
                }else{
                    clockwise = !clockwise;
                    moving = myLoc.directionTo(oldLoc);
                    if(rc.canMove(moving)){
                        oldLoc = myLoc;
                        Path.tryMoveDir(moving);
                    }else{
                        clockwise = !clockwise;
                        moving = inMoving;
                    }
                }
            }else if(moving == Direction.EAST){
                for (RobotInfo ally : visibleAllies){
                    if(ally.type == RobotType.TURRET && myLoc.directionTo(ally.location) == Direction.NORTH && ally.location.distanceSquaredTo(myLoc) < 4){
                        cont = true;
                        break;
                    }
                }
                if(!cont){
                    moving = Direction.NORTH;
                }
                if(rc.canMove(moving)){
                    oldLoc = myLoc;
                    Path.tryMoveDir(moving);
                }else{
                    clockwise = !clockwise;
                    moving = myLoc.directionTo(oldLoc);
                    if(rc.canMove(moving)){
                        oldLoc = myLoc;
                        Path.tryMoveDir(moving);
                    }else{
                        clockwise = !clockwise;
                        moving = inMoving;
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
                turn++;
                // END OF TURN
            } catch(Exception e) {
                e.printStackTrace();
            }
            Clock.yield();
        }
    }
}
