package team012;

import battlecode.common.*;

public class Guard extends Global {

    public static void init() {

    }

    private static Direction moving = Direction.NORTH;
    private static MapLocation oldLoc = myLoc;
    private static boolean clockwise = true;
    private static boolean startedPatrol = false;
    private static boolean interruptPatrol = false;
    private static int maxDist = 10;
    private static MapLocation patrolLoc = myLoc;
    private static boolean didPatrol = false;
    private static int intMoving = 0;

    public static void turn() throws GameActionException {
        if(rc.isCoreReady()) {
            if(roundNum == spawnRound){
                for (RobotInfo ally : Global.visibleAllies) {
                    if (ally.type == RobotType.ARCHON) {
                        if (ally.location.distanceSquaredTo(myLoc) < 5) {
                            oldLoc = myLoc;
                            Path.tryMoveDir(ally.location.directionTo(myLoc));
                            patrolLoc = myLoc;
                            moving = oldLoc.directionTo(myLoc);
                        }
                    }
                }
            }

            if(visibleHostiles.length > 0){
                interruptPatrol = true;
                if(myLoc.distanceSquaredTo(patrolLoc) <= maxDist){
                    Path.tryMoveDir(myLoc.directionTo(Path.getAverageLoc(visibleHostiles)));
                }
            }else{
                if(interruptPatrol){
                    if(myLoc.x == patrolLoc.x && myLoc.y == patrolLoc.y){
                        interruptPatrol = false;
                    }else{
                        Path.tryMoveDir(myLoc.directionTo(patrolLoc));
                    }
                }
            }

            if(startedPatrol && !interruptPatrol) {
                patrolTurrets();
            }else if(!startedPatrol){
                for(RobotInfo ally : visibleAllies){
                    if(ally.type == RobotType.TURRET && myLoc.directionTo(ally.location) == Direction.NORTH_WEST && myLoc.distanceSquaredTo(ally.location) < 5) {
                        startedPatrol = true;
                        oldLoc = myLoc;
                        Path.tryMoveDir(Direction.WEST);
                        moving = Direction.WEST;
                        intMoving = 6;
                        clockwise = true;
                        break;
                    }else if(ally.type == RobotType.TURRET && myLoc.directionTo(ally.location) == Direction.NORTH_EAST && myLoc.distanceSquaredTo(ally.location) < 5) {
                        startedPatrol = true;
                        oldLoc = myLoc;
                        Path.tryMoveDir(Direction.EAST);
                        moving = Direction.EAST;
                        intMoving = 2;
                        clockwise = false;
                        break;
                    }else if(ally.type == RobotType.TURRET && myLoc.directionTo(ally.location) == Direction.SOUTH_WEST && myLoc.distanceSquaredTo(ally.location) < 5){
                        startedPatrol = true;
                        oldLoc = myLoc;
                        Path.tryMoveDir(Direction.WEST);
                        moving = Direction.WEST;
                        intMoving = 6;
                        clockwise = false;
                        break;
                    }else if(ally.type == RobotType.TURRET && myLoc.directionTo(ally.location) == Direction.SOUTH_EAST && myLoc.distanceSquaredTo(ally.location) < 5) {
                        startedPatrol = true;
                        oldLoc = myLoc;
                        Path.tryMoveDir(Direction.EAST);
                        moving = Direction.EAST;
                        intMoving = 2;
                        clockwise = true;
                        break;
                    }
                }
            }
        }
    }


    public static void patrolTurrets() throws GameActionException{
        boolean cont = false;
        Direction inMoving = moving;
        int inIntMoving = intMoving;

        if(clockwise){
            for(RobotInfo ally : visibleAllies){
                if(ally.type == RobotType.TURRET && myLoc.directionTo(ally.location) == Global.directions[(intMoving + 2)%8] && ally.location.distanceSquaredTo(myLoc) < 4){
                    cont = true;
                    break;
                }
            }
            if(!cont){
                intMoving = (intMoving +2) % 8;
                moving = Global.directions[intMoving];
            }
            System.out.println("Guard: continue path = " + cont + "; Direction moving = " + moving.toString());
            if(rc.canMove(moving)){
                oldLoc = myLoc;
                patrolLoc = myLoc;
                Path.tryMoveDir(moving);
            }else{
                clockwise = !clockwise;
                moving = myLoc.directionTo(oldLoc);
                intMoving = getIndex(moving);
                if(rc.canMove(moving)){
                    oldLoc = myLoc;
                    patrolLoc = myLoc;
                    Path.tryMoveDir(moving);
                }else{
                    clockwise = !clockwise;
                    moving = inMoving;
                    intMoving = inIntMoving;
                }
            }
        }else{
            for(RobotInfo ally : visibleAllies){
                if(ally.type == RobotType.TURRET && myLoc.directionTo(ally.location) == Global.directions[(intMoving + 6)%8] && ally.location.distanceSquaredTo(myLoc) < 4){
                    cont = true;
                    break;
                }
            }
            if(!cont){
                intMoving = (intMoving + 6) % 8;
                moving = Global.directions[intMoving];
            }
            System.out.println("Guard: continue path = " + cont + "; Direction moving = " + moving.toString());
            if(rc.canMove(moving)){
                oldLoc = myLoc;
                patrolLoc = myLoc;
                Path.tryMoveDir(moving);
            }else{
                clockwise = !clockwise;
                moving = myLoc.directionTo(oldLoc);
                intMoving = getIndex(moving);
                if(rc.canMove(moving)){
                    oldLoc = myLoc;
                    patrolLoc = myLoc;
                    Path.tryMoveDir(moving);
                }else{
                    clockwise = !clockwise;
                    moving = inMoving;
                    intMoving = inIntMoving;
                }
            }
        }
    }

    private static int getIndex(Direction d) {

        switch(d){
            case NORTH:
                return 0;
            case EAST:
                return 2;
            case SOUTH:
                return 4;
            default:
                return 6;
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
