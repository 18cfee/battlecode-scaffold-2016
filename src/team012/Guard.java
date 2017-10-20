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
    private static int maxDist = 0;
    private static MapLocation patrolLoc = myLoc;
    private static int intMoving = 0;
    private static boolean notInit = true;

    public static void turn() throws GameActionException {

        if (rc.isWeaponReady()){
            tryAttack(null);
        }

        if(rc.isCoreReady()) {
            if (myLoc.distanceSquaredTo(ourArchonSpawns[0]) > 24) {
                int zombieAdj = isZombieAdj();
                if (zombieAdj > -1) {
                    if (!Path.tryMoveDir(visibleZombies[zombieAdj].location.directionTo(myLoc)))
                        Path.moveRandom();
                } else {
                    Path.moveTo(ourArchonSpawns[0]);
                }

                if (rc.isCoreReady())
                    clearRubble(myLoc.directionTo(ourArchonSpawns[0]));

            } else if(notInit){
                notInit = false;
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

            rc.setIndicatorDot(ourArchonSpawns[0], 250, 0, 250);
            rc.setIndicatorString(1, "Patrol Loc: " + patrolLoc.toString());
            RobotInfo target = null;
            boolean hasTarget = false;
            if(visibleHostiles.length > 0){
                interruptPatrol = true;
                for(RobotInfo hostile: visibleHostiles) {
                    if(myLoc.distanceSquaredTo(hostile.location) < myAttackRange){
                        hasTarget = true;
                        target = hostile;
                        break;
                    }
                }
                if(myLoc.distanceSquaredTo(patrolLoc) <= maxDist && !hasTarget){
                    Path.tryMoveDir(myLoc.directionTo(Path.getAverageLoc(visibleHostiles)));
                }
            }else{
                if(interruptPatrol){
                    if(myLoc.x == patrolLoc.x && myLoc.y == patrolLoc.y){
                        interruptPatrol = false;
                        startedPatrol = false;
                    }else{
                        Path.tryMoveDir(myLoc.directionTo(patrolLoc));
                    }
                }
            }

            if(startedPatrol && !interruptPatrol) {
                patrolTurrets();
            }else if(!startedPatrol){
                for(RobotInfo ally : visibleAllies){
                    if(ally.type == RobotType.TURRET && myLoc.directionTo(ally.location) == Direction.EAST && myLoc.distanceSquaredTo(ally.location) < 5){
                        startedPatrol = true;
                        oldLoc = myLoc;
                        Path.tryMoveDir(Direction.NORTH);
                        moving = Direction.NORTH;
                        intMoving = 0;
                        clockwise = true;
                        rc.setIndicatorDot(ally.location, 250, 250, 250);
                        break;
                    }else if(ally.type == RobotType.TURRET && myLoc.directionTo(ally.location) == Direction.WEST && myLoc.distanceSquaredTo(ally.location) < 5) {
                        startedPatrol = true;
                        oldLoc = myLoc;
                        Path.tryMoveDir(Direction.SOUTH);
                        moving = Direction.SOUTH;
                        intMoving = 4;
                        clockwise = true;
                        rc.setIndicatorDot(ally.location, 250, 250, 250);
                        break;
                    }else if(ally.type == RobotType.TURRET && myLoc.directionTo(ally.location) == Direction.NORTH && myLoc.distanceSquaredTo(ally.location) < 5) {
                        startedPatrol = true;
                        oldLoc = myLoc;
                        Path.tryMoveDir(Direction.WEST);
                        moving = Direction.WEST;
                        intMoving = 6;
                        clockwise = true;
                        rc.setIndicatorDot(ally.location, 250, 250, 250);
                        break;
                    }else if(ally.type == RobotType.TURRET && myLoc.directionTo(ally.location) == Direction.SOUTH && myLoc.distanceSquaredTo(ally.location) < 5) {
                        startedPatrol = true;
                        oldLoc = myLoc;
                        Path.tryMoveDir(Direction.EAST);
                        moving = Direction.EAST;
                        intMoving = 2;
                        clockwise = true;
                        rc.setIndicatorDot(ally.location, 250, 250, 250);
                        break;
                    }else if(ally.type == RobotType.TURRET && myLoc.directionTo(ally.location) == Direction.NORTH_WEST && myLoc.distanceSquaredTo(ally.location) < 5) {
                        startedPatrol = true;
                        oldLoc = myLoc;
                        Path.tryMoveDir(Direction.WEST);
                        moving = Direction.WEST;
                        intMoving = 6;
                        clockwise = true;
                        rc.setIndicatorDot(ally.location, 250, 250, 250);
                        break;
                    }else if(ally.type == RobotType.TURRET && myLoc.directionTo(ally.location) == Direction.NORTH_EAST && myLoc.distanceSquaredTo(ally.location) < 5) {
                        startedPatrol = true;
                        oldLoc = myLoc;
                        Path.tryMoveDir(Direction.EAST);
                        moving = Direction.EAST;
                        intMoving = 2;
                        clockwise = false;
                        rc.setIndicatorDot(ally.location, 250, 250, 250);
                        break;
                    }else if(ally.type == RobotType.TURRET && myLoc.directionTo(ally.location) == Direction.SOUTH_WEST && myLoc.distanceSquaredTo(ally.location) < 5){
                        startedPatrol = true;
                        oldLoc = myLoc;
                        Path.tryMoveDir(Direction.WEST);
                        moving = Direction.WEST;
                        intMoving = 6;
                        clockwise = false;
                        rc.setIndicatorDot(ally.location, 250, 250, 250);
                        break;
                    }else if(ally.type == RobotType.TURRET && myLoc.directionTo(ally.location) == Direction.SOUTH_EAST && myLoc.distanceSquaredTo(ally.location) < 5) {
                        startedPatrol = true;
                        oldLoc = myLoc;
                        Path.tryMoveDir(Direction.EAST);
                        moving = Direction.EAST;
                        intMoving = 2;
                        clockwise = true;
                        rc.setIndicatorDot(ally.location, 250, 250, 250);
                        break;
                    }
                }
                rc.setIndicatorString(0,"Guard: continue path = " + moving.toString() + "; Clockwise: " + clockwise);
            }
        }
        if (rc.isWeaponReady()){
            tryAttack(null);
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
            rc.setIndicatorString(0,"Guard: continue path = " + cont + "; Direction moving = " + moving.toString() + "; Clockwise: " + clockwise);
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
            rc.setIndicatorString(0,"Guard: continue path = " + cont + "; Direction moving = " + moving.toString() + "; Clockwise: " + clockwise);
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

    private static int getIndex(Direction d) throws GameActionException{

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
