package team012;

import battlecode.common.*;


public class Turret extends Global {

    public static void init(){
        enemies = new HackedRobotInfo[50];
        enemiesIdx = 0;
    }

    static HackedRobotInfo enemies[];
    static int enemiesIdx;
    static MapLocation target;

    public static void turn() throws GameActionException{
        int bytes = Clock.getBytecodeNum();
        processSignals();
        bytes = Clock.getBytecodeNum() - bytes;
        rc.setIndicatorString(0, "BYTECODES USED FOR SIGNAL PROCESSING:"+String.valueOf(bytes));


        // If this robot type can attack, check for enemies within range and attack one
        if (rc.isWeaponReady()) {
            // this should return a target location that is attackable
            boolean haveTarget = selectTarget();
            if (haveTarget) {
                rc.attackLocation(target);
            }
        }
    }

    // stores locations of enemies from signals
    private static void processSignals() throws GameActionException{
        enemiesIdx = 0;
        for (int i = 0; i < Math.min(10, signals.length); i++)
            if (Comm.readSig(signals[i]))
                if (Comm.channel == ENEMY_HERE)
                    enemies[enemiesIdx] = Comm.robot;
    }

    // selects the best based on highest attack power
    private static boolean selectTarget() {
        for (RobotInfo hostile :visibleHostiles)
            if(rc.canAttackLocation(hostile.location)) {
                target = hostile.location;
                return true;
            }

        for (int i = 0; i < enemiesIdx; i++)
            if(rc.canAttackLocation(enemies[enemiesIdx].loc)) {
                target = enemies[enemiesIdx].loc;
                return true;
            }
        return false;
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
