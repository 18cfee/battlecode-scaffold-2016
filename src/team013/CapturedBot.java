package team013;

import battlecode.common.*;

// this class is for bots that dont have a specific class built for them at this time
public class CapturedBot extends Global{

    public static Direction lastDir;
    public static void init() {
        lastDir = myLoc.directionTo(theirArchonSpawns[0]);
    }

    public void runFrame() throws GameActionException {
        lastDir = Path.moveSomewhereOrLeft(lastDir);
    }
}

