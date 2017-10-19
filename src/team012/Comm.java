package team012;

import battlecode.common.*;

public class Comm extends Global{

    // holds 4 useless values at end so that we dont Access Array out of bounds
    private static final RobotType[] types = {RobotType.ARCHON, RobotType.SCOUT, RobotType.GUARD, RobotType.SOLDIER,
            RobotType.TTM, RobotType.TURRET, RobotType.VIPER, RobotType.BIGZOMBIE, RobotType.FASTZOMBIE,
            RobotType.RANGEDZOMBIE, RobotType.STANDARDZOMBIE, RobotType.ZOMBIEDEN, RobotType.SCOUT, RobotType.SCOUT,
            RobotType.SCOUT, RobotType.SCOUT};

    static int broadcastStrength;

    static int channel;
    static MapLocation loc;
    static HackedRobotInfo robot;
    static int extra;
    static long longData;

    public static void init(){
        broadcastStrength = mySensorRange;
        channel = 0;
        extra = 0;
        loc = myLoc;
    }

    public static void sendMap(int channel, MapLocation loc) throws GameActionException{
        int x = loc.x;
        x  = x << 16;
        rc.broadcastMessageSignal( channel, x | loc.y, broadcastStrength);
    }

    public static void sendXY(int channel, int x, int y) throws GameActionException{
        x  = x << 16;
        rc.broadcastMessageSignal(channel, x | y, broadcastStrength);
    }

    public static void sendRobot(int channel, RobotInfo info) throws GameActionException{

        int first = getIntFromType(info.type) << 28 ;
        first |=  (int)info.health << 10;
        first |= channel | 0x300;

        int second =  info.location.x << 16;
        second |=  info.location.y;

        rc.broadcastMessageSignal(first, second, broadcastStrength);
    }

    public static void sendMapAndInt(int channel, MapLocation loc, int extra) throws GameActionException{
        int x = loc.x;
        x  = x << 16;
        channel |= (extra << 10) | 0x100;
        rc.broadcastMessageSignal( channel, x | loc.y, broadcastStrength);
    }

    // this is insane dont do this
    // can send 54 bits
    public static void sendLong(int channel, long bigGuy) throws GameActionException{
        int first = (int)(bigGuy >> 22);
        first <<= 22;
        first |= channel | 0x200;

        rc.broadcastMessageSignal(first, (int)bigGuy, broadcastStrength);
    }

    public static boolean readSig(Signal s) throws GameActionException{

        int[] message = s.getMessage();
        if (message == null || s.getTeam() != myTeam)
            return false;

        channel = message[0] & 0xFF;

        int mType = message[0] & 0x300;
        switch (mType) {
            case 0x000:
                // Map
                int y = message[1];
                int x = y >>> 16;
                y &= 0x0000FFFF;
                loc = new MapLocation(x, y);
                break;

            case 0x100:
                // Map + int
                int yI = message[1];
                int xI = yI >>> 16;
                yI &= 0x0000FFFF;
                loc = new MapLocation(xI, yI);
                extra = message[0] >> 10;
                break;

            case 0x200:
                //long
                longData = (long)(message[0] >> 10) << 32;
                longData |= message[1];
                break;

            case 0x300:
                // Robot
                int yR = message[1];
                int xR = yR >>> 16;
                yR &= 0x0000FFFF;
                loc = new MapLocation(xR, yR);

                int type = message[0] >>> 28;
                int hp = (message[0] >>> 10) & 0x3FFFF;
                robot = new HackedRobotInfo(types[type], hp, loc);
                break;
        }
        return true;
    }

    private static int getIntFromType(RobotType type) {
        switch (type) {
            case ARCHON: return 0;
            case SCOUT: return 1;
            case GUARD: return 2;
            case SOLDIER: return 3;
            case TTM: return 4;
            case TURRET: return 5;
            case VIPER: return 6;
            case BIGZOMBIE: return 7;
            case FASTZOMBIE: return 8;
            case RANGEDZOMBIE: return 9;
            case STANDARDZOMBIE: return 10;
            case ZOMBIEDEN: return 11;
        }
        return 0;
    }

    private static String msgToString(int n1, int n2) {
        return String.format("%16s", Integer.toBinaryString(n1))+" "+String.format("%16s", Integer.toBinaryString(n2));
    }

    private static String robotToString(RobotInfo info) {
        return "Type:"+info.type.toString()+" Location:"+Global.mapLocationToString(info.location)+" HP: "
                +String.valueOf((int)info.health);
    }

}
