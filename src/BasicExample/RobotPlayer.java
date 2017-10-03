/**
 * This is an example Bot for MIT's 2016 Battlecode competition. http://www.battlecode.org/
 *
 * This bot was written under Workiva's BlitzCode project
 *
 * The purpose of this project is to help people who are new to battlecode understand the API
 * and to have a starting place to work from.  You should be able to start from this project and
 * build a solid bot just by implementing some of the many TODOS scattered throughout the code.  If you
 * are new to battlecode I recommend that you read through this entire program and then run a few matches.  After
 * getting a basic idea of what is going on you can then go through an implement some of the TODOS that
 * make sense, improving the bot and learning more about battlecode.  Alternatively you can start your own
 * bot from scratch and use this code as a reference when you get stuck or aren't sure how to do something
 *
 * As such this project seeks to be self documenting, but also contains a large number of comments
 * to explain in detail everything that is going on.
 *
 * The complete game specs can be viewed at: https://www.battlecode.org/contestants/releases/
 *
 *
 * This bot has a very simple strategy,
 *   Archons loop through all unit types producing them one at a time as parts
 *            are available and moves randomly otherwise.
 *   Scouts move in a random direction till they reach an edge and then
 *          pick a different direction.  They also broadcast when they see a zombie den or an enemy archon
 *   Soldiers shoot any enemies they see and move in a random direction until they get an enemy archon location
 *            message at which point they head towards it
 *    Guards shoot any enemies and chase down any enemies they see but otherwise move randomly until they get a
 *            Zombie den message at which point they head towards it killing all in their path
 *    Vipers head towards a random enemy Archon start position killing everything in their path
 *    Turrets shoot everything in their path and there is a small probability that they will turn themselves into mobile turrets
 *             If they can't see any enemies
 *    TTM's move around randomly and will set up as a turret if they see an enemy or there is a small random probability of
 *          them setting up without seeing any enemies
 */


// In order for your code to compile the package needs to be set
// in the format teamXXX where XXX is your 3 digit team number with leading 0s
package BasicExample;

// here is where you import all of the battlecode libraries

import battlecode.common.*;

import java.util.Random;

// here is where you import other useful java libraries, however there are a bunch of things you can't do like read/write to a file

// You can have as many objects and files as you want but battlecode will always start at RobotPlayer for every one of your bots
// and it will run RobotPlayer.run(rc); to start each bot when it is created.  If run ever finishes the bot will be destroyed
public class RobotPlayer {

    /////////////////////////////// Define global constants ////////////////////////////

    // These constants are used to define messages
    public static final int ENEMYARCHONMESSAGEX = 0;
    public static final int ENEMYARCHONMESSAGEY = 1;
    public static final int ZOMBIEDENMESSAGEX = 2;
    public static final int ZOMBIEDENMESSAGEY = 3;
    // TODO: add message type for parts and neutral
    // TODO: add message type for Archon in distress that calls allied units to its defense

    ////////////////////////////////  Define global variables //////////////////////////

    // Note:  all variables are static for a couple reasons, 1. the static method run() is used to initiate the code
    //        and 2. a static method call takes one less bytecode than calling an instance variable

    public static Direction[] directions = {Direction.NORTH, Direction.NORTH_EAST, Direction.EAST, Direction.SOUTH_EAST,
            Direction.SOUTH, Direction.SOUTH_WEST, Direction.WEST, Direction.NORTH_WEST};
    // This list is used to determine what unit to build
    public static RobotType[] buildOrder = {RobotType.SCOUT, RobotType.SOLDIER, RobotType.VIPER, RobotType.TURRET};
    // This variable is used by Archons to determine which unit in the above list to build next
    public static int unitToBuildNext = 0;
    // This is defined here so all functions can use the rc which is where we access most of the battlecode API
    public static RobotController rc;
    // Current direction we are heading in
    public static Direction currentDirection;
    // This variable is used to save the last enemy Archon location we know about
    public static MapLocation enemyArchon;
    // This variable is used to save the last known zombie den location
    public static MapLocation zombieDen;
    // This is used to generate Random values
    public static Random rand;
    // This variable is used by vipers to know which enemy archon start location they should head towards
    public static int currentEnemyArchonStartLoc = 0;

    /**
     * run() is the method that is called when a robot is instantiated in the Battlecode world.
     * If this method returns, the robot dies!
     *
     * Note: Every bot runs on a isolated virtual machine, this means that every single one
     *       of your battlecode bots will start here at run and your bots will NOT share variables
     *       the only way to communicate is by sending messages.  This allows all variables/methods
     *       to be static as each bot will be run separately
     *
     **/
    @SuppressWarnings("unused")
    public static void run(RobotController robotController) {
        // You can print out stuff to the console using System.out.println()
        System.out.println("we have created a new robot!! " + robotController.getType());
        // set rc to robotController so you can use it throughout the rest of your code
        rc = robotController;
        // initialize random with a unique seed so that all the bots will be different
        rand = new Random(rc.getID());

        // if you exit this function the robot dies so make sure that doesn't happen!!
        while (true) {
            // make sure that all code that could throw an error is wrapped in a try-catch b/c an uncaught error will cause
            // your bot to self-destruct
            try {
                // all bots will run this code so we check to run appropriate code based on our type
                if (rc.getType() == RobotType.ARCHON) {
                    runArchon();
                } else if (rc.getType() == RobotType.SOLDIER) {
                    runSoldier();
                } else if (rc.getType() == RobotType.GUARD) {
                    runGuard();
                } else if (rc.getType() == RobotType.VIPER) {
                    runViper();
                } else if (rc.getType() == RobotType.TURRET) {
                    runTurret();
                } else if (rc.getType() == RobotType.TTM) {
                    runTTM();
                } else if (rc.getType() == RobotType.SCOUT) {
                    runScout();
                }
            } catch (Exception e) {
                // Throwing an uncaught exception makes the robot die, so we need to catch exceptions.
                // Caught exceptions will result in a bytecode penalty.
                System.out.println(e.getMessage());
                e.printStackTrace();
            }

            // after each iteration of the loop you should yield to specify the end of your turn
            // otherwise you will just waste bytecodes as your bot will just loop but can't do anything
            // until you have used your bytecode limit at which point your code will halt and start again at
            // that spot next turn, which we don't want to happen
            Clock.yield();
        }
    }

    // TODO: Break out the code for the various units into there own classes/files keeping everything static

    /////////////////////////////////  Unit Run methods /////////////////////////////////////////////

    // This method is where we control our archons
    public static void runArchon() throws Exception {
        // if we can't move or build then don't do anything
        if (!rc.isCoreReady()) return;

        // Build units
        if (buildUnits());
            // If we don't build try to move
        else if (ArchonMove());
    }

    // This method is where we control our soldiers
    public static void runSoldier() throws Exception {
        // set soldier move direction
        changeSoldierDirection();

        // try to attack
        if (rc.isWeaponReady() && shootEnemies());
            // try to move if we don't shoot
        else if (rc.isCoreReady() && move(currentDirection));
    }

    // This method is where we control our guards
    public static void runGuard() throws Exception {
        // set Guard move direction
        changeGuardDirection();

        // try to attack
        if (rc.isWeaponReady() && shootEnemies());
            // try to move if we don't shoot
        else if (rc.isCoreReady() && move(currentDirection));
    }

    // This method is where we control our vipers
    public static void runViper() throws Exception {
        // Check to see if there is an enemy and chase them otherwise head towards next
        // enemy archon start location
        changeViperDirection();

        // try to attack
        if (rc.isWeaponReady() && shootEnemies());
            // try to move if we don't attack
        else if (rc.isCoreReady() && move(currentDirection));
    }

    // This method is where we control our Turrets
    public static void runTurret() throws Exception {
        // try to attack
        if (rc.isWeaponReady() && shootEnemies());
            // check to see if we should pack
        else if (rc.isCoreReady() && shouldPack()) rc.pack();
    }

    // This method is where we control our TTMs
    public static void runTTM() throws Exception {
        // set the TTMs direction
        setTTMDirection();

        // check to see if we should unpack so we can fight
        if (shouldUnPack()) rc.unpack();
            // if we don't want to unpack then
        else if (rc.isCoreReady() && move(currentDirection));
    }

    // This method is where we control our scouts
    public static void runScout() throws Exception {
        if (!rc.isCoreReady()) return;

        // broadcast if we see anything
        sendScoutMessages();

        // update our scout's direction if we have reached an edge
        updateScoutDirection();

        // try to move the scout
        scoutMove(currentDirection);
    }


    /////////////////////////////// Helper functions ////////////////////////////////////////////

    // This method is called by Archons to build units
    // it returns true if a unit is built and false otherwise
    public static boolean buildUnits() throws Exception {
        // TODO: if we are being chased by a bunch of zombies it may be better to run than trying to build units???
        // TODO: come up with a smarter way of building units than just looping through all possibilities
        //       For example the round # could be used so that early on soldiers and guards are built and later
        //       Vipers and turrets, or it could be resource based

        // first determine which unit we are suppose to build next
        RobotType typeToBuild = buildOrder[unitToBuildNext % buildOrder.length];

        // if we have enough parts to build a unit then try to build it
        if (rc.hasBuildRequirements(typeToBuild)) {
            // Choose a random direction to try to build in
            Direction dirToBuild = directions[rand.nextInt(8)];
            for (int i = 0; i < 8; i++) {
                // If possible, build in this direction
                if (rc.canBuild(dirToBuild, typeToBuild)) {
                    unitToBuildNext++;
                    rc.build(dirToBuild, typeToBuild);
                    return true;
                } else {
                    // Rotate the direction to try
                    dirToBuild = dirToBuild.rotateLeft();
                }
            }
        }

        return false;
    }

    // This function handles movement for Archons
    public static boolean ArchonMove() throws Exception {
        // TODO look for zombies or enemies and run away from them if there are any using these:
        RobotInfo[] enemiesWithinSightRange = rc.senseNearbyRobots(rc.getType().sensorRadiusSquared, rc.getTeam().opponent());
        RobotInfo[] zombiesWithinSightRange = rc.senseNearbyRobots(rc.getType().sensorRadiusSquared, Team.ZOMBIE);

        // TODO search for parts to move towards if there are no enemies close by
        MapLocation[] parts = rc.sensePartLocations(rc.getType().sensorRadiusSquared);

        // TODO search for neutral units and activate them!
        RobotInfo[] neutrals = rc.senseNearbyRobots(rc.getType().sensorRadiusSquared, Team.NEUTRAL);

        // TODO: activate nearby neutrals with this function (note you need to be adjacent to a neutral or a distance Squared
        // of less than 2 in order to activate a neutral unit
        // rc.activate(<LocationGoesHere>);

        // TODO: don't pick a random direction, cluster Archons for defense? Spread out to find rss?  Go to corner for safety?
        //       Charge the enemy b/c fortune favors the bold??  Just randomly do something not random!!!! :p
        // randomly determine our next direction
        int fate = rand.nextInt(10);

        // only change the direction 10% of the time to widen our range
        if (fate < 1 ||currentDirection == null) {
            currentDirection = directions[fate % 8];
        }

        // try to move in the direction we randomly picked
        return move(currentDirection);
    }

    // This method sets the direction the solider is to move in next
    // the current strategy is to listen for a message about enemy archon
    // locations and if we hear of one to move towards it otherwise we move randomly
    public static void changeSoldierDirection() throws Exception {
        // TODO: if there are zombies in range of us move backwards to kite them
        // TODO: if we see enemies charge? or clump? or flee? maybe based on # of allies
        //       and enemies or our health?

        // check message signals to see if we have received a new enemy archon message
        MapLocation temp = getLocationFromMessageType(ENEMYARCHONMESSAGEX, ENEMYARCHONMESSAGEY);

        // if we see enemy archon save that spot
        if (temp != null) {
            enemyArchon = temp;
        }

        // if we know of an enemy archon we do not want to go in a random direction
        if (enemyArchon == null) {
            // TODO: don't pick directions randomly... just don't!

            // randomly determine if we should change our direction to widen the range that we will sweep
            int fate = rand.nextInt(10);
            // only change direction 10% of the time
            if (fate < 1 || currentDirection == null) {
                // pick a random direction if it is time to change direction.
                fate = rand.nextInt(8);
                currentDirection = directions[fate];
            }
        } else {
            // set our direction to be towards the enemy archon's position
            currentDirection = rc.getLocation().directionTo(enemyArchon);
        }
    }

    // This method is used to set a Guards direction
    // The current strategy is to run towards a random enemy or zombie if we see any.
    // otherwise we listen for zombie den locations, if we hear of one we run towards
    // it otherwise we move randomly
    public static void changeGuardDirection() throws Exception {
        // TODO: add overall guard strategy other than chasing enemies and zombie dens

        // get my sight range
        int myAttackRange = rc.getType().sensorRadiusSquared;
        // look for enemies and zombies
        RobotInfo[] enemiesWithinSightRange = rc.senseNearbyRobots(myAttackRange, rc.getTeam().opponent());
        RobotInfo[] zombiesWithinSightRange = rc.senseNearbyRobots(myAttackRange, Team.ZOMBIE);

        // if we see enemies run towards them
        if (enemiesWithinSightRange.length > 0) {
            // TODO: pick which enemy to chase based on some criteria other than randomness
            MapLocation enemy = enemiesWithinSightRange[rand.nextInt(enemiesWithinSightRange.length)].location;
            // set our direction to head towards the enemy we have chosen
            currentDirection = rc.getLocation().directionTo(enemy);
            // exit out of the function to avoid overriding currentDirection
            return;
        } else if (zombiesWithinSightRange.length > 0) {
            // run towards zombies
            // TODO: pick which zombie to run towards smarter than random
            MapLocation zombie = zombiesWithinSightRange[rand.nextInt(zombiesWithinSightRange.length)].location;
            // set our current direction to head towards the zombie that we picked
            currentDirection = rc.getLocation().directionTo(zombie);
            // exit out of the function to avoid overriding currentDirection
            return;
        }

        // check message signals to see if we have received a new enemy archon message
        MapLocation temp = getLocationFromMessageType(ZOMBIEDENMESSAGEX, ZOMBIEDENMESSAGEY);

        // if we see a zombie den save its location to attack!
        if (temp != null) {
            zombieDen = temp;
        }

        // if we know of an enemy archon we do not want to go in a random direction
        if (zombieDen == null) {
            // TODO: don't pick directions randomly... just don't!

            // randomly determine if we should change our direction to widen the range that we will sweep
            int fate = rand.nextInt(10);
            // only change direction 10% of the time
            if (fate < 1 || currentDirection == null) {
                // pick a random direction if it is time to change direction.
                fate = rand.nextInt(8);
                currentDirection = directions[fate];
            }
        } else {
            // set our direction to be towards the enemy archon's position
            currentDirection = rc.getLocation().directionTo(zombieDen);
        }
    }

    // This method determines what direction a viper should move in next
    // Currently a viper runs around near enemy archon locations
    public static void changeViperDirection() throws Exception {
        // TODO: run towards enemies if we see them and are out of range
        // TODO: avoid zombies as vipers are weak against them or kite them
        // TODO: listen for scouts reporting enemy archon sightings and head to those locations
        // TODO: Gather in "squads" before attacking??

        // if we don't have a enemy archon to go to set it
        if (enemyArchon == null) {
            // set enemy Archon to the current start location we want
            enemyArchon = rc.getInitialArchonLocations(rc.getTeam().opponent())[currentEnemyArchonStartLoc];
        }

        // if we are close to enemy archon start position then go to the next one
        if (rc.getLocation().distanceSquaredTo(enemyArchon) < 5) {
            // increment to go to the next enemy Archon start location with a modus so we wrap around to the begining instead
            // of throwing an Array out of bounds exception
            currentEnemyArchonStartLoc = (currentEnemyArchonStartLoc + 1) % rc.getInitialArchonLocations(rc.getTeam().opponent()).length;
            // set enemy Archon location
            enemyArchon = rc.getInitialArchonLocations(rc.getTeam().opponent())[currentEnemyArchonStartLoc];
        }

        currentDirection = rc.getLocation().directionTo(enemyArchon);
    }

    // This method determines what direction a TTM should move in
    // Currently a ttm moves totally randomly
    public static void setTTMDirection() throws Exception {
        // TODO: move smarter than random, maybe forming groups with soldiers / guards / vipers to kill zombie dens
        //       or to go after enemy archons or to follow our Archons to defend them?

        int fate = rand.nextInt(10);

        // currently we only change direction 10% of the time
        if (fate < 1 || currentDirection == null) {
            currentDirection = directions[rand.nextInt(8)];
        }
    }

    // This method is used by scouts to set the next direction of travel
    // Currently scouts run till they run into a wall and then they pick a new random direction
    public static void updateScoutDirection() throws Exception {
        // TODO: avoid zombies and enemies at all costs as we have no defenses
        // TODO: add code to allow scouts to pair up with turrets to provide extended vision
        // TODO: add code to "follow" enemy Archon's reporting their location

        if (currentDirection == null) {
            currentDirection = directions[rand.nextInt(8)];
        }

        // if we will be going off the map then switch directions
        if (!rc.onTheMap(rc.getLocation().add(currentDirection, 3))) {
            // TODO: pick directions slightly smarter so that we don't go in directions we have already gone
            currentDirection = directions[rand.nextInt(8)];
        }
    }

    // This method is used by scouts to send out messages of enemy archons and zombie dens
    public static void sendScoutMessages() throws Exception {
        // TODO: broadcast part and neutral locations
        // TODO: optimize the signal strength to reach allied units without wasting core delay

        // currently randomly picking signal strength
        int signalStrength = rand.nextInt(1000) + 100;

        // grab all enemies we can see
        RobotInfo[] enemiesWithinRange = rc.senseNearbyRobots(rc.getType().sensorRadiusSquared, rc.getTeam().opponent());
        RobotInfo[] zombiesWithinRange = rc.senseNearbyRobots(rc.getType().sensorRadiusSquared, Team.ZOMBIE);

        // In battlecode loops are done backwards as in this format to cut down on bytecodes
        // This is the same loop as for (int i = 0; i < signals.length; i++) {...} except that it
        // starts from the end and works backwards and costs less bytecodes
        for (int i = enemiesWithinRange.length; --i>=0; ) {
            // if we see an enemy archon send message to allies
            if (enemiesWithinRange[i].type == RobotType.ARCHON) {
                // send out x coord
                rc.broadcastMessageSignal(ENEMYARCHONMESSAGEX, enemiesWithinRange[i].location.x, signalStrength);
                // send out y coord
                rc.broadcastMessageSignal(ENEMYARCHONMESSAGEX, enemiesWithinRange[i].location.x, signalStrength);
            }
        }

        // TODO: set this loop to run backwards like the example above to save on bytecodes
        for (int i = 0; i < zombiesWithinRange.length; i++) {
            if (zombiesWithinRange[i].type == RobotType.ZOMBIEDEN) {
                // send out x coord
                rc.broadcastMessageSignal(ZOMBIEDENMESSAGEX, zombiesWithinRange[i].location.x, signalStrength);
                // send out y coord
                rc.broadcastMessageSignal(ZOMBIEDENMESSAGEY, zombiesWithinRange[i].location.y, signalStrength);
            }
        }
    }

    // This method determines if a ttm should pack up into a turret
    public static boolean shouldUnPack() throws Exception {
        // TODO: unpack smartly, maybe when in range of zombie dens or when we are in a good defensible position
        // TODO: switch to using rc.senseHostileRobots(<Location>, <radiusSquared>); instead of both zombies and enemies
        RobotInfo[] enemiesWithinRange = rc.senseNearbyRobots(rc.getType().sensorRadiusSquared, rc.getTeam().opponent());
        RobotInfo[] zombiesWithinRange = rc.senseNearbyRobots(rc.getType().sensorRadiusSquared, Team.ZOMBIE);

        if (enemiesWithinRange.length > 0 || zombiesWithinRange.length > 0) return true;

        // 5% of the time we will unpack
        if (rand.nextInt(100) < 5) return true;

        return false;
    }

    // This method determines if a Turret should pack up and move
    public static boolean shouldPack() throws Exception {
        // TODO: don't pack up unless there is somewhere we want to be...

        RobotInfo[] enemiesWithinRange = rc.senseNearbyRobots(rc.getType().sensorRadiusSquared, rc.getTeam().opponent());
        RobotInfo[] zombiesWithinRange = rc.senseNearbyRobots(rc.getType().sensorRadiusSquared, Team.ZOMBIE);

        // if we see enemies or zombies don't unpack
        if (enemiesWithinRange.length > 0 || zombiesWithinRange.length > 0) return false;

        // pack up 5% of the time
        if (rand.nextInt(100) < 5) return true;

        return false;
    }

    // This method is used to grab a map Location from a
    public static MapLocation getLocationFromMessageType(int xType, int yType) {
        // TODO: refactor messages to store entire mapLocation inside of one int
        Signal[] signals = rc.emptySignalQueue();
        int x = 0;
        int y = 0;
        boolean foundLoc = false;

        // TODO: set this for loop to run backwards to save on bytecodes like in sendScoutMessages()
        for (int i = 0; i < signals.length; i++) {
            Signal currentSignal = signals[i];

            // make sure that the signal is from a bot on our team
            if (currentSignal.getTeam() == rc.getTeam()) {
                // get the int array from
                int[] msg = currentSignal.getMessage();
                // verify that we did not receive a basic message
                if (msg != null) {
                    // if we received a msg for the x position update the x position
                    if (msg[0] == xType) {
                        x = msg[1];
                        foundLoc = true;
                    } else if (msg[0] == yType) { // update the y position
                        y = msg[1];
                        foundLoc = true;
                    }
                }
            }
        }

        // if we didn't find a location we return null
        if (!foundLoc) {
            return null;
        }

        return new MapLocation(x, y);
    }

    // This method is used to move in a direction
    public static boolean move(Direction direction) throws Exception {
        // loop till we find a direction we can move in
        for (int i = 0; i < 8; i++) {
            // First we need to check if there is too much rubble for us to move
            if (rc.senseRubble(rc.getLocation().add(direction)) >= GameConstants.RUBBLE_OBSTRUCTION_THRESH) {
                // TODO: look to go around rubble if possible instead of blindly mining through it
                // Too much rubble, so I should clear it
                rc.clearRubble(direction);
                return true;
            } else if (rc.canMove(direction)) { // Check if I can move in this direction
                rc.move(direction);
                return true;
            } else {
                // TODO: update direction smarter to pick the closest direction rather than just looking left always
                // such as looking rotateLeft() then rotateRight() then rotateLeft().rotateLeft() etc...
                // update the direction to allow us to go around the target
                direction = direction.rotateLeft();
            }
        }

        return false;
    }

    // This method is used to move scouts who can ignore rubble
    public static boolean scoutMove(Direction direction) throws Exception {
        if (!rc.isCoreReady()) return false;

        // TODO: check for enemies and zombies and avoid them at all costs as we are completely defenseless

        // loop over all directions till we find one we can move in starting with desired direction
        for (int i = 0; i < 8; i++) {
            if (rc.canMove(direction)) {
                rc.move(direction);
                return true;
            } else {
                // TODO: be smarter than just moving left each time like in move() above
                direction = direction.rotateLeft();
            }
        }
        return false;
    }

    // This method is used to shoot enemies
    public static boolean shootEnemies() throws Exception {
        // get attack range
        int myAttackRange = rc.getType().attackRadiusSquared;
        // look for enemies and zombies
        RobotInfo[] enemiesWithinRange = rc.senseNearbyRobots(myAttackRange, rc.getTeam().opponent());
        RobotInfo[] zombiesWithinRange = rc.senseNearbyRobots(myAttackRange, Team.ZOMBIE);

        // TODO: send out basic message if we see enemies or zombies to call nearby allies to us??? Or just if we see important target
        //       like an enemy archon or a zombie den?
        // rc.broadcastSignal(<SomeIntForDistanceSquaredGoesHere>);


        // if we have an enemy shoot them!!
        if (enemiesWithinRange.length > 0) {
            // TODO: pick which enemy to attack intelligently such as based on lowest health
            // or strongest attack power
            MapLocation enemy = enemiesWithinRange[rand.nextInt(enemiesWithinRange.length)].location;
            // Check if we can attack location and kill them if we can
            if (rc.canAttackLocation(enemy)) {
                rc.attackLocation(enemy);
                return true;
            }
        } else if (zombiesWithinRange.length > 0) {
            // TODO: pick which zombie to attack intelligently such as based on lowest health
            // or strongest attack power
            MapLocation zombie = zombiesWithinRange[rand.nextInt(zombiesWithinRange.length)].location;
            // Check if we can attack location
            if (rc.canAttackLocation(zombie)) {
                rc.attackLocation(zombie);
                return true;
            }
        }
        return false;
    }
}