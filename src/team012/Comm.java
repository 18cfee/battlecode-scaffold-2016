package team012;

import battlecode.common.*;

public class Comm extends Global{

    public static int broadcastStrength;

    public static int channel;
    public static MapLocation loc;

    public static void init(){
        broadcastStrength = mySensorRange;
        channel = 0;
        loc = myLoc;
    }

    public static void sendMsgMap(int channelIn, MapLocation loc) throws GameActionException{
        int x = loc.x;
        x  = x << 16;
        rc.broadcastMessageSignal( channelIn, x | loc.y, broadcastStrength);
    }

    public static void sendMsgXY(int channelIn, int x, int y) throws GameActionException{
        x  = x << 16;
        rc.broadcastMessageSignal(channelIn, x | y, broadcastStrength);
    }

    public static boolean readSig(Signal s) throws GameActionException{
        if (s.getMessage() == null || s.getTeam() != myTeam)
            return false;

        int test = s.getMessage()[0];
        int y = s.getMessage()[1]; // TEAM SHOULD BE OUR TEAM
        int x = y >> 16;
        y = y & 0x0000FFFF;
        channel = s.getMessage()[0];
        loc = new MapLocation(x, y);
        return true;
    }

}
