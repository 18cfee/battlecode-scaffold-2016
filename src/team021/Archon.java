package team021;

import battlecode.common.*;

import java.util.ArrayList;
import java.util.Random;

public class Archon extends Global {

    static ArrayList<RobotType> buildList;

    enum Destination {NONE, ENEMY_ARCHON, NEUTRAL_ARCHON, NEUTRAL_UNIT, STUCK, HOME, SHIT_IF_I_KNOW}
    static Destination destination;
    static MapLocation destLoc;
    static MapLocation lastLoc;


    static int archonId;
    // Just for testing out healing
    //static MapLocation b = new MapLocation(spawnLoc.x+1,spawnLoc.y);

    public static void init() {
        archonId = 99;
        for (int i = 0; i < ourArchonSpawns.length; i++)
            if (spawnLoc.compareTo(ourArchonSpawns[i]) == 0)
                archonId = i;

        if (archonId == 0) {
            // BASE ARCHON INIT HERE

            current = Direction.NORTH;
        } else {
            // ROAMING ARCHON INIT HERE
            destLoc = rc.getLocation();
            destination = Destination.NONE;


        }
    }

    public static int stage = 0;
    //Stage #   ||  Performing
    //      0   ||  Send Forth Initial Gaurds
    //      1   ||  Move Away From Wall
    //      2   ||  Place Scout
    //      3   ||  Place First Turret Cluster
    public static void turn() throws GameActionException{
        if (archonId != 0) {
            rc.setIndicatorString(2,"I am trying to roam: ");
            roam();
        } else {
            HealAUnitInRange();
            if (healthLost > 25 || healthLost > 5 && myHp < 200 ||getOutnumberFactor() > 2.5f && stage == 3) {
                runaway();
                archonId = -1;
                return;
            }

            // maybe ask about core being ready here instead of in these messages
            if(stage == 3) {
                boolean moved = moveToScout();
                normSquareTurrets();
                clearRubble();
                readyMove(moved);
            }  else if(stage == 2) {
                if(!checkedNeutUnitsHere) tryConvertNeutralConvenience();
                placeInitialScout();
            } else if(stage == 0){
                placeGuardInitially();
            }
        }
    }

    static int archonMoved = 0;
    static boolean moveToScout() throws GameActionException{
        Direction moveTo = myLoc.directionTo(placedScoutLoc);
        if(rc.isCoreReady() && rc.canMove(moveTo)){
            rc.move(moveTo);
            current = moveTo.opposite();
            for(RobotInfo ally: visibleAllies){
                if(ally.ID == placedScoutId){
                    placedScoutLoc = ally.location;
                    archonMoved++;
                    roundsGaurdsSeen = 0;
                    break;
                }
            }
            return true;
        }
        return false;
    }

    static int roundsGaurdsSeen = 0;
    static void readyMove(boolean moved) throws GameActionException{
        if(roundNum%10 == 0 && !moved){
            rc.setIndicatorString(2,String.valueOf(roundsGaurdsSeen));
            Direction check = Direction.NORTH;
            boolean readyMove = true;
            boolean seenGuard = false;
            for(int i = 0; i < 8; i++){
                MapLocation locToCheck = Path.getLocAt(check,myLoc);
                if(rc.onTheMap(locToCheck) && (500 > (Path.senseRubbleFixBug(check)))){
                    RobotInfo atSpot = rc.senseRobotAtLocation(locToCheck);
                    if(!(null == atSpot) && atSpot.type == RobotType.GUARD){
                        seenGuard = true;
                    }
                    if(null == atSpot || atSpot.team.equals(Team.ZOMBIE) || atSpot.team.equals(enemyTeam) || (atSpot.type == RobotType.GUARD && roundsGaurdsSeen < 2))
                    {
                        readyMove = false;
                        break;
                    }
                }
                check = check.rotateLeft();
            }
            if(seenGuard){
                roundsGaurdsSeen++;
            }else{
                roundsGaurdsSeen = 0;
            }
            if(readyMove){
                // Maybe Send only to a scout with a certain ID
                Comm.sendMap(SCOUT_NEXT, placedScoutLoc);
            }
        }
    }

    static Direction camp = null;
    static void setStart() throws GameActionException{
        Direction tryD = Direction.NORTH;
        int dx = 0;
        int dy = 0;
        for(int i =0; i< 4;i++){
            MapLocation step = Path.getLocAt(tryD,spawnLoc);
            if(!rc.onTheMap(Path.getLocAt(tryD,step))){
                dx += tryD.dx;
                dy += tryD.dy;
            }
            tryD = tryD.rotateLeft().rotateLeft();
        }
        if(dx != 0 || dy != 0){
            camp = Path.convertXY(dx,dy).opposite();
        } else{
            // Place in the spot with the most rubble
            Direction that = Direction.SOUTH_EAST; // changed to North-East for factory map
            Direction low = null;
            double min = 100000;
            for (int i = 0; i < 8; i++) {
                double rubAtSpot = Path.senseRubbleFixBug(that);
                if (rc.canBuild(that, RobotType.SCOUT) && min > rubAtSpot && rubAtSpot > 49) {
                    min = rubAtSpot;
                    low = that;
                }
                that = that.rotateLeft();
            }
            camp = low;
        }
    }

    static boolean HealAUnitInRange() throws GameActionException{
        for(RobotInfo robot: rc.senseNearbyRobots(myAttackRange, myTeam)){
            if(robot.health < robot.type.maxHealth){
                rc.repair(robot.location);
                return true;
            }
        }
        return false;
    }

    private static boolean checkedNeutUnitsHere = false;
    public static void tryConvertNeutralConvenience() throws GameActionException{
        if (!rc.isCoreReady()) return;
        RobotInfo[] adjNeutral = rc.senseNearbyRobots(2, Team.NEUTRAL);
        if (adjNeutral.length > 0) {
            rc.activate(adjNeutral[0].location);
        } else{
            checkedNeutUnitsHere = true;
        }
    }

    static Direction buildGuard = Direction.NORTH;
    static int guardsBuiltInStage = 0;
    private static void placeGuardInitially() throws GameActionException{
        if(rc.isCoreReady()){
            for(int i = 0; i < 8; i++){
                if(rc.canBuild(buildGuard,RobotType.GUARD)){
                    rc.build(buildGuard,RobotType.GUARD);
                    buildGuard = buildGuard.opposite();
                    guardsBuiltInStage++;
                    if(guardsBuiltInStage > 1){
                        stage = 2;
                    }
                    break;
                }
                buildGuard = buildGuard.rotateRight();
            }
        }
    }

    static Direction current;
    static RobotType typeToBuild = RobotType.TURRET;
    static int numBuilt = 0;
    private static void normSquareTurrets() throws GameActionException{
        if (rc.isCoreReady() && (rc.hasBuildRequirements(typeToBuild))) {
            for (int i = 0; i < 8; i++) {
                if(archonMoved%3 == 1){
                    typeToBuild = RobotType.SCOUT;
                    archonMoved++;
                }
                if(typeToBuild != RobotType.SCOUT && numBuilt%8 == 7){
                    typeToBuild = RobotType.GUARD;
                }
                if (rc.canBuild(current, typeToBuild)) {
                    rc.build(current, typeToBuild);
                    current = current.rotateLeft();
                    typeToBuild = RobotType.TURRET;
                    numBuilt++;
                    break;
                } else {
                    current = current.rotateLeft();
                }
            }
        }
    }

    static MapLocation placedScoutLoc = null;
    static int placedScoutId = -1;
    static int notPlaced = 0;
    private static void placeInitialScout() throws GameActionException{
        if(camp == null){
            setStart();
        }

        if(camp == null)
            camp = myLoc.directionTo(theirArchonSpawns[0]);

        if (rc.isCoreReady() && rc.canBuild(camp,RobotType.SCOUT)) {
            // Place in spot facing away from boundary (or rand if farther)
            //if
            rc.build(camp, RobotType.SCOUT);
            placedScoutLoc = Path.getLocAt(camp,myLoc);
            placedScoutId = rc.senseRobotAtLocation(placedScoutLoc).ID;
            stage++;
            // Move Up a level in the defense Process
        } else {
            notPlaced++;
        }
    }


    static MapLocation spot = rc.getInitialArchonLocations(myTeam)[0];
    static int lastTimeD = 0;
    static Random rand = new Random(rc.getID());
    static private void roamAway() throws GameActionException{
        if (rc.isCoreReady()) {
            //Outside of safe areas
            int minD = 50;

            MapLocation now = rc.getLocation();
            int xD = Math.abs(spot.x - now.x);
            int yD = Math.abs(spot.y - now.y);
            int distance = (int)Math.sqrt(xD*xD + yD*yD);
            minD = distance;

            boolean movingAway = (minD >= lastTimeD);
            boolean outOfCircles = (minD > 15);
            lastTimeD = minD;
            RobotInfo[] infoE = rc.senseNearbyRobots(10,enemyTeam);
            RobotInfo[] infoZ = rc.senseNearbyRobots(10,Team.ZOMBIE);

            if(rc.canMove(current) &&(outOfCircles || movingAway)) {
            } else {
                current = directions[(rand.nextInt(8))];
                for(int i = 0; i < 8;i++){
                    if(rc.canMove(current)){
                        break;
                    }
                    current.rotateLeft();
                }
            }
            // NearbyZombies
            if(infoZ.length != 0){
                //Nearby Enemy, then stop (never calls move command)
                if(infoE.length == 0){
                    boolean moved = false;
                    Direction curLeft = current.rotateLeft();
                    Direction current2 = current;
                    for(int i = 0; i < 4; i++){
                        boolean leftC = true;
                        int lX = curLeft.dx;
                        int lY = curLeft.dy;
                        boolean currentC = true;
                        int cX = current2.dx;
                        int cY = current2.dy;
                        // Check that extended line does not hit any zombies
                        // May want it to check different ranges at some point
                        for(int j = 0; j < infoZ.length; j++){
                            //if current is bad
                            RobotInfo z = infoZ[i];
                            int changeX = z.location.x - rc.getLocation().x;
                            int changeY = z.location.y - rc.getLocation().y;
                            if(cX == Math.abs(changeX)/changeX || cY == Math.abs(changeY)/changeY){
                                currentC = false;
                            }
                            if(lX == Math.abs(changeX)/changeX || lY == Math.abs(changeY)/changeY){
                                leftC = false;
                            }
                        }
                        if(currentC && rc.canMove(current2)){
                            rc.move(current2);
                            moved = true;
                            break;
                        } else if(leftC && rc.canMove(curLeft)){
                            rc.move(curLeft);
                            moved = true;
                            break;
                        }
                        current2.rotateRight();
                        curLeft.rotateLeft();
                    }
                    if(!moved){
                        rc.move(current);
                    }
                }
            } else{
                if(infoE.length == 0) current.opposite();
                rc.move(current);
            }

        }



    }

    public static void loop() {
        while (true) {
                try {
                // BEGIN TURN
                update();
//                processSignals();
                turn();
                lastLoc = rc.getLocation();

                    // END OF TURN
            } catch(Exception e) {
                e.printStackTrace();
            }
            Clock.yield();
        }
    }


    //Auto heal any adjacent units

    // Code for telling Roaming they can Build, must have used all original resources and then climbed up to a certainPoint
    private static boolean roamingAllowedToBuild(){
        return (rc.getRoundNum() > 200 && rc.getTeamParts() > 150);
    }

    ///////////////////////////////////// Keegan Below This point ////////////////////////////////////
    private static int cooldown;
    private static Direction runDir = null;
    private static boolean abandoningBase = false;

    private static void roam() throws GameActionException {
        rc.setIndicatorString(0, destination.toString());
        int zombieAdj = isZombieAdj();

        if(abandoningBase && rc.isCoreReady()) {
            if (rc.senseRubble(myLoc.add(runDir)) > 49)
                clearRubble(runDir);
            else if (rc.canMove(runDir)) {
                rc.move(runDir);
                abandoningBase = false;
                destination = Destination.NONE;
            }
        }

        RobotInfo[] adjNeutral = rc.senseNearbyRobots(1, Team.NEUTRAL);
        if (adjNeutral.length > 0)
            tryConvertNeutral();

        rc.setIndicatorLine(destLoc, rc.getLocation(), 255, 0, 0);
        if (rc.isCoreReady()) {
            if (destination == Destination.STUCK) {
                if (cooldown++ < 13)
                    if (zombieAdj > -1)
                        Path.tryMoveDir(visibleZombies[zombieAdj].location.directionTo(myLoc));
                    else {
                        if (cooldown < 5)
                            Path.tryMoveDir(myLoc.directionTo(destLoc).opposite());
                        else
                            Path.moveSomewhereOrLeft(Path.lastDirMoved);
                    }
                else
                    destination = Destination.NONE;

            }

            if (roundNum > 300 || myHp < 900) {
                destination = Destination.HOME;
                destLoc = ourArchonSpawns[0].add(ourArchonSpawns[0].directionTo(theirArchonSpawns[0]).opposite(), 5);
            }
            if (destination == Destination.NONE ) {
                scanForNearNeutral();
            }

            if (rc.isCoreReady()) {
                if (zombieAdj > -1) {
                    Path.tryMoveDir(visibleZombies[zombieAdj].location.directionTo(myLoc));
                } else if (destination == Destination.HOME) {
                        if (myLoc.distanceSquaredTo(destLoc) < 14) {
                            if(healthLost > 0) {
                                Path.runToAllies();
                            } else {
                                if (rc.isCoreReady() && roundNum > 300 && rc.getTeamParts() > 300 && canBuy(RobotType.TURRET))
                                    tryBuildAnyDir(myLoc.directionTo(destLoc).opposite(), RobotType.TURRET);
                            }
                        } else if (healthLost > 5)
                            Path.runFromEnemies();
                        else if (myLoc.distanceSquaredTo(destLoc) > 20 && (!Path.moveSafeTo(destLoc)) && (!Path.moveTo(destLoc))) {
                            destination = Destination.STUCK;
                            cooldown = 0;
                            Path.tryMoveDir(myLoc.directionTo(destLoc).opposite());
                        }
                } else if (destination != Destination.STUCK || destination != Destination.NONE) {
                    if (healthLost > 0)
                        Path.runFromEnemies();
                    else if ((!Path.moveSafeTo(destLoc))&& (!Path.moveTo(destLoc))) {
                        destination = Destination.STUCK;
                        cooldown = 0;
                        Path.tryMoveDir(myLoc.directionTo(destLoc).opposite());
                    }
                } else if (healthLost > 0)
                    Path.runFromEnemies();
            }

            if (rc.isCoreReady()) {
                if (zombieAdj > -1 ) {
                    MapLocation desire = myLoc.add(visibleZombies[zombieAdj].location.directionTo(myLoc));
                    if (rc.onTheMap(desire)) {
                        Direction awayDir = myLoc.directionTo(desire);
                        if (rc.canMove(awayDir) && rc.isCoreReady())
                            rc.move(awayDir);
                        else
                            clearRubble(awayDir);
                    }
                    if(rc.isCoreReady())
                        Path.moveRandom();
                } else {
                    if (destination == Destination.HOME && healthLost < 1) {
                        clearRubble();
                        if (rc.isWeaponReady() && visibleAllies.length > 0)
                            HealAUnitInRange();
                        return;
                    }


                }
            }

            boolean didHeal = false;
            if (rc.isWeaponReady() && visibleAllies.length > 0)
                didHeal = HealAUnitInRange();

            if (!didHeal && rc.isCoreReady())
                if (!Path.moveRandom())
                    Path.moveSomewhereOrLeft(Path.lastDirMoved);

        }
    }


    public boolean buildFromList(Direction dir) throws GameActionException {
        if (buildList.isEmpty()) {
            if (tryBuild(dir, buildList.get(0))) {
                buildList.remove(0);
                return true;
            }
        }
        return false;
    }

    public static boolean tryBuild(Direction dir, RobotType rType) throws GameActionException {
        if (rc.isCoreReady() && rc.canBuild(dir, rType)) {
            rc.build(dir, rType);
            return true;
        }
        return false;
    }
    static boolean tryBuildAnyDir(Direction base, RobotType type) throws GameActionException{
        if (base == Direction.NONE || base ==  Direction.OMNI)
            base = randomDir();
        for (int i = 0; i < 8; i++) {
            if(rc.canBuild(base, type)) {
                rc.build(base, type);
                return true;
            }
        }
        return false;
    }


    public static boolean tryConvertNeutral() throws GameActionException{
        if (!rc.isCoreReady())
            return false;
        RobotInfo[] adjNeutral = rc.senseNearbyRobots(2, Team.NEUTRAL);
        if (adjNeutral.length > 0) {
            rc.activate(adjNeutral[0].location);
            if (adjNeutral[0].location.compareTo(destLoc) == 0) {
                destLoc = rc.getLocation();
                destination = Destination.NONE;
            }
            return true;
        }
        return false;
    }

    public static boolean scanForNearNeutral() {
        RobotInfo[] neutrals = rc.senseNearbyRobots(-1, Team.NEUTRAL);

        for(RobotInfo neutralRobot : neutrals) {
            if (neutralRobot.type == RobotType.ARCHON) {
                destLoc = neutralRobot.location;
                destination = Destination.NEUTRAL_ARCHON;
                return true;
            }
            destination = Destination.NEUTRAL_UNIT;
            destLoc = neutralRobot.location;
        }
        return neutrals.length > 0;
    }

    public static void processSignals() throws GameActionException {
        for (Signal s : signals) {
            if (s.getTeam() != myTeam) continue;
            if (Comm.readSig(s)) {
                switch (Comm.channel) {
                    case ENEMY_ARCHON_LOCATION:


                        break;
                    case EMPTY:


                        break;

                    case FORM_UNIT_ON_ME:


                        break;

                    case SCOUT_NEXT:


                        break;
                    case TURRET_SHOOT_HERE:
                        rc.setIndicatorString(0, "TURRET_SHOOT_HERE " + mapLocationToString(Comm.loc));

                }
            }
        }
    }

    static boolean isZombieAdjacent() {
        RobotInfo[] hostiles = rc.senseHostileRobots(myLoc,1);
        for (RobotInfo robot : hostiles) {
            if (robot.team == Team.ZOMBIE)
                return true;
        }
        return false;
    }

    private static void runaway() throws GameActionException {
        abandoningBase = true;

        RobotInfo[] zombieAdj = rc.senseNearbyRobots(1, Team.ZOMBIE);

        Direction away = myLoc.directionTo(Path.getAverageLoc(zombieAdj)).opposite();
        MapLocation awayLoc = myLoc.add(away);

        if (rc.isCoreReady())
            if (Path.tryMoveDir(away))
                return;

        if (rc.onTheMap(awayLoc)) {
            RobotInfo robot = rc.senseRobotAtLocation(awayLoc);
            if (robot == null) {
                clearRubble(away);
                return;
            } else if (robot.team == myTeam && robot.zombieInfectedTurns == 0) {
                Comm.sendMap(DISINTEGRATE, awayLoc);
                runDir = away;
                rc.setIndicatorString(1, "DESTROY HERE" + mapLocationToString(awayLoc));
                return;
            }

            away = away.rotateLeft();
            awayLoc = myLoc.add(away);
            robot = rc.senseRobotAtLocation(awayLoc);
            if (robot == null) {
                clearRubble(away);
                return;
            } else if (robot.team == myTeam && robot.zombieInfectedTurns == 0) {
                Comm.sendMap(DISINTEGRATE, awayLoc);
                runDir = away;
                rc.setIndicatorString(1, "DESTROY HERE" + mapLocationToString(awayLoc));
                return;
            }

            away = away.rotateRight().rotateRight();
            awayLoc = myLoc.add(away);
            robot = rc.senseRobotAtLocation(awayLoc);
            if (robot == null) {
                clearRubble(away);
            } else if (robot.team == myTeam && robot.zombieInfectedTurns == 0) {
                Comm.sendMap(DISINTEGRATE, awayLoc);
                runDir = away;
                rc.setIndicatorString(1, "DESTROY HERE" + mapLocationToString(awayLoc));
            }
        }
    }







}
