package team012;

import battlecode.common.*;

import javax.xml.stream.Location;
import java.util.ArrayList;
import java.util.Random;

public class Archon extends Global {

    static ArrayList<RobotType> buildList;

    static enum Destination {NONE, ENEMY_ARCHON, NEUTRAL_ARCHON, NEUTRAL_UNIT, STUCK, SHIT_IF_I_KNOW}
    static Destination destination;
    static MapLocation destLoc;

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
        rc.setIndicatorString(2, String.valueOf(archonId));
    }

    public static int stage = 0;
    //Stage #   ||  Performing
    //      0   ||  Send Forth Initial Gaurds
    //      1   ||  Move Away From Wall
    //      2   ||  Place Scout
    //      3   ||  Place First Turret Cluster
    public static void turn() throws GameActionException{
        if (archonId > 0) {
            roam();
            return;
        } else if (archonId == 0) {
            // maybe ask about core being ready here instead of in these messages
            if(stage == 3) {
                normSquareTurrets();
                clearRubble();
            } else if(stage == 2) {
                placeInitialScout();
            } else if(stage == 0){
                placeGuard();
            }
        }
    }

    static Direction buildGuard = Direction.NORTH;
    static int guardsBuiltInStage = 0;
    private static void placeGuard() throws GameActionException{
        if(rc.isCoreReady()){
            if(guardsBuiltInStage > 0){
                buildGuard = buildGuard.opposite();
            }
            for(int i = 0; i < 8; i++){
                if(rc.canBuild(buildGuard,RobotType.GUARD)){
                    rc.build(buildGuard,RobotType.GUARD);
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

    static Direction currentlyClearing = null;
    private static void clearRubble() throws GameActionException{
        if(currentlyClearing == null){
            //select lowest rubble in visinity
            Direction that = Direction.NORTH;
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
            currentlyClearing = low;
        }
        if(currentlyClearing != null && rc.isCoreReady()) {
            rc.clearRubble(currentlyClearing);
            if(Path.senseRubbleFixBug(currentlyClearing) < 50){
                currentlyClearing = null;
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
    private static void placeInitialScout() throws GameActionException{
        if (rc.isCoreReady() && (rc.hasBuildRequirements(typeToBuild))) {
            // Find most rubble spot
            Direction that = Direction.SOUTH;
            Direction build = that;
            double max = -1;
            for (int i = 0; i < 8; i++) {
                double rubAtSpot = Path.senseRubbleFixBug(that);
                if (rc.canBuild(that, RobotType.SCOUT) && max < rubAtSpot) {
                    max = rubAtSpot;
                    build = that;
                }
                that = that.rotateLeft();
            }
            rc.build(build, RobotType.SCOUT);
            placedScoutLoc = Path.getLocAt(build, rc.getLocation());
            stage++;
            // Move Up a level in the defense Process
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
    private static boolean roamingAllowedToBuild(Direction a, RobotType typeBot){
        return (rc.getRoundNum() > 200 && rc.getTeamParts() > 150);
    }



    ///////////////////////////////////// Keegan Below This point ////////////////////////////////////
    private static Direction lastDir = myLoc.directionTo(theirArchonSpawns[0]);
    private static MapLocation lastLocation = null;
    private static int cooldown;



    private static void roam() throws GameActionException {

        rc.setIndicatorLine(destLoc, rc.getLocation(), 255, 0, 0);

        if (rc.isCoreReady()) {

            if (destination == Destination.STUCK) {
                if (cooldown++ < 13)
                    lastDir = Path.moveSomewhereOrLeft(lastDir);
                else
                    destination = Destination.NONE;
            }

            if (destination == Destination.NONE && !scanForNearNeutral()) {
                lastDir = Path.moveSomewhereOrLeft(lastDir);
            } else {
                if (!tryConvertNeutral())
                    if (!Path.moveSafeTo(destLoc)) {
                        Path.moveTo(destLoc);
                        if (lastLocation == rc.getLocation()) {
                            destination = Destination.STUCK;
                            lastDir = rc.getLocation().directionTo(destLoc).opposite();
                            cooldown = 0;
                        }
                    }

            }
            lastLocation = rc.getLocation();
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

}
