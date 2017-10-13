package team012;

import battlecode.common.*;

import javax.xml.stream.Location;
import java.util.ArrayList;

public class Archon extends Global {

    static ArrayList<RobotType> buildList;
    static Direction current;
    static boolean placedScout;
    static RobotType typeToBuild = RobotType.SCOUT;

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
            placedScout = false;

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

    public static void turn() throws GameActionException{
        if (archonId > 0) {
            roam();
            return;
        } else if (archonId == 0) {
            if (rc.isCoreReady() && (rc.hasBuildRequirements(typeToBuild))) {
                for (int i = 0; i < 8; i++) {
                    if (rc.canBuild(current, typeToBuild)) {
                        rc.build(current, typeToBuild);
                        if (!placedScout) {
                            typeToBuild = RobotType.TURRET;
                            placedScout = true;
                        }
                        current = current.rotateLeft();
                        break;
                    } else {
                        current = current.rotateLeft();
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
                // END OF TURN
            } catch(Exception e) {
                e.printStackTrace();
            }
            Clock.yield();
        }
    }


    // Code for telling Roaming they can Build, must have used all original resources and then climbed up to a certainPoint

    private static boolean roamingAllowedToBuild(Direction a, RobotType typeBot){
        if(rc.getRoundNum() > 200 && rc.getTeamParts() > 150 && rc.canBuild(a, typeBot)) return true;
        else return false;
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
