package team012;

import battlecode.common.*;

public class HackedRobotInfo {

    public final RobotType type;
    public final MapLocation loc;
    public final int hp;

    public HackedRobotInfo(RobotType type, int hp, MapLocation loc) {
        this.type = type;
        this.loc = loc;
        this.hp = hp;
    }

    public String toString() {
        return "Type:"+type.toString()+" Location:"+Global.mapLocationToString(loc)+" HP:"+ String.valueOf(hp);
    }

}
