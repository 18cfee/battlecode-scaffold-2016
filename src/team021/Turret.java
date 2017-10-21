package team021;

import battlecode.common.*;

public class Turret extends Global {

    public static void init(){};

    static MapLocation target;

    public static void turn() throws GameActionException{

        if (rc.isWeaponReady()) {
            target = null;
            checkSignals();
            if (target == null) {
                RobotInfo[] enemiesWithinRange = rc.senseHostileRobots(myLoc, myAttackRange);
                for (RobotInfo enemy : enemiesWithinRange)
                    if (rc.canAttackLocation(enemy.location)) {
                        target = enemy.location;
                        break;
                    }
            }

            if (!(target == null))
                rc.attackLocation(target);
        }

    }

    private static void checkSignals() throws GameActionException{
        for (Signal signal: signals)
            if (Comm.readSig(signal))
                switch (Comm.channel ) {
                    case TURRET_SHOOT_HERE:
                        if (rc.canAttackLocation(Comm.loc))
                            target = Comm.loc;
                        break;
                    case DISINTEGRATE:
                        if (myLoc.equals(Comm.loc) && !rc.isInfected())
                            rc.disintegrate();
                        break;
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
