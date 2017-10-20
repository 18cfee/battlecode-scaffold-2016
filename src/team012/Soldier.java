package team012;

import battlecode.common.*;

import java.util.Map;
import java.util.Random;

public class Soldier extends Global {

    static Direction movDir;

    private enum State {CAMP_ARCHON, EXPLORE}
    private static State state;

    public static void init() {

        movDir = randomDir();
        lastHp = rc.getHealth();
    }

    private static void turn() throws GameActionException{
        int zombieAdj = isZombieAdj();

        boolean didFire = false;
        if (rc.isWeaponReady()) {
            if (zombieAdj > -1)
                didFire = tryAttack(visibleZombies[zombieAdj].location);
            else
                didFire = tryAttack(null);
        }

        if(rc.isCoreReady()) {
            if (zombieAdj > -1)
                Path.moveTo(myLoc.add(myLoc.directionTo(visibleZombies[zombieAdj].location).opposite()));
            else if (healthLost > 0)
                Path.runFromEnemies();
            else if (myLoc.distanceSquaredTo(ourArchonSpawns[0]) > 20){
                if (!didFire)
                    Path.moveTo(ourArchonSpawns[0]);
            }
        }

        if (rc.isCoreReady())
            if (!didFire && visibleHostiles.length > 0) {
                Path.moveTo(visibleHostiles[0].location);
                tryAttack(null);
            }

        if(rc.isCoreReady()) {
            clearRubble();
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

    public static void getSignals(){
        for(Signal s : signals) {
            if (s.getTeam() == myTeam) {

            }
        }
    }



}
