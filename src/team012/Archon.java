package team012;

import battlecode.common.*;

import java.util.ArrayList;
import java.util.Random;

public class Archon extends Global {

    static ArrayList<RobotType> buildList;

    enum Destination {NONE, ENEMY_ARCHON, NEUTRAL_ARCHON, NEUTRAL_UNIT, STUCK, SHIT_IF_I_KNOW}
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

            buildList = new ArrayList<>();
            buildList.add(RobotType.SCOUT);
            buildList.add(RobotType.TURRET);
            buildList.add(RobotType.SOLDIER);
            buildList.add(RobotType.SOLDIER);
            buildList.add(RobotType.SOLDIER);
            buildList.add(RobotType.SOLDIER);
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
        HealAUnitInRange();
        if (archonId > 0) {
            roam();
            return;
        } else if (archonId == 0) {
            rc.setIndicatorString(5,"carl: " + String.valueOf(Path.zombieHealthAroundMe(visibleZombies,visibleAllies)));
            // maybe ask about core being ready here instead of in these messages
            if(stage == 3) {
                moveToScout();
                normSquareTurrets();
                clearRubble();
                readyMove();
            }  else if(stage == 2) {
                if(!checkedNeutUnitsHere) tryConvertNeutralConvenience();
                placeInitialScout();
            } else if(stage == 0){
                placeGuardInitially();
            }
        }
    }

    static void moveToScout() throws GameActionException{
        Direction moveTo = myLoc.directionTo(placedScoutLoc);
        if(rc.isCoreReady() && rc.canMove(moveTo)){
            rc.move(moveTo);
            for(RobotInfo ally: visibleAllies){
                if(ally.ID == placedScoutId){
                    placedScoutLoc = ally.location;
                    break;
                }
            }
        }
    }

    static void readyMove() throws GameActionException{
        if(roundNum%10 == 0){
            Direction check = Direction.NORTH;
            boolean readyMove = true;
            for(int i = 0; i < 8; i++){
                MapLocation locToCheck = Path.getLocAt(check,myLoc);
                if(rc.onTheMap(locToCheck) && (500 > (Path.senseRubbleFixBug(check)))){
                    RobotInfo atSpot = rc.senseRobotAtLocation(locToCheck);
                    if(null == atSpot || atSpot.team.equals(Team.ZOMBIE) || atSpot.team.equals(enemyTeam))
                    {
                        readyMove = false;
                        break;
                    }
                }
                check = check.rotateLeft();
            }
            if(readyMove){
                // Maybe Send only to a scout with a certain ID
                Comm.sendMsgMap(SCOUT_NEXT, placedScoutLoc);
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
            camp = Direction.NORTH;
        }
    }

    static void HealAUnitInRange() throws GameActionException{
        for(RobotInfo robot: rc.senseNearbyRobots(myAttackRange, myTeam)){
            if(robot.health < robot.type.maxHealth){
                rc.repair(robot.location);
                break;
            }
        }
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
    private static void normSquareTurrets() throws GameActionException{
        if (rc.isCoreReady() && (rc.hasBuildRequirements(typeToBuild))) {
            for (int i = 0; i < 8; i++) {
                if (rc.canBuild(current, typeToBuild)) {
                    rc.build(current, typeToBuild);
                    current = current.rotateLeft();
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

    private static void roam() throws GameActionException {

        rc.setIndicatorLine(destLoc, rc.getLocation(), 255, 0, 0);
        if (rc.isCoreReady()) {
                if (destination == Destination.STUCK) {
                    if (cooldown++ < 13)
                        if (!Path.runFromEnemies())
                            Path.moveSomewhereOrLeft(Path.lastDirMoved);
                    else
                        destination = Destination.NONE;
                }

                if (destination == Destination.NONE && !scanForNearNeutral()) {
                    if (roamingAllowedToBuild() && !Path.canEnemyAttack(myLoc))
                        tryBuildAnyDir(Path.awayEnemyDirection.opposite(), RobotType.SOLDIER);
                    else if (healthLost > 5)
                        if (!Path.runFromEnemies())
                            Path.moveSomewhereOrLeft(Path.lastDirMoved);
                } else {
                    if (getOutnumberFactor() > 2)
                        if (!Path.runFromEnemies())
                            Path.moveSomewhereOrLeft(Path.lastDirMoved);

                    if (!tryConvertNeutral())
                        if (!Path.moveSafeTo(destLoc)) {
                            Path.moveTo(destLoc);
                            if (lastLoc == rc.getLocation()) {
                                if (myLoc.distanceSquaredTo(destLoc) < 4 &&
                                        rc.senseRubble(Path.getLocAt(myLoc.directionTo(destLoc),myLoc)) < 100)
                                    clearRubble(myLoc.directionTo(destLoc));
                            } else {
                                destination = Destination.STUCK;
                                Path.lastDirMoved = rc.getLocation().directionTo(destLoc).opposite();
                                cooldown = 0;
                            }

                        }

                }

        }
        if (rc.isCoreReady())
            if (!Path.moveRandom())
                Path.moveSomewhereOrLeft(Path.lastDirMoved);

        lastLoc = rc.getLocation();

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







}
