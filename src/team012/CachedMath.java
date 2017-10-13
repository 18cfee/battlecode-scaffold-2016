package team012;

import battlecode.common.*;

public class CachedMath {

    public static final float[] pow = {  0.000000f,  1.000000f,  2.297397f,  3.737193f,  5.278032f,  6.898649f,  8.585815f, 10.330413f, 12.125734f, 13.966612f, 15.848934f, 17.769339f, 19.725024f, 21.713612f, 23.733058f, 25.781582f, 27.857622f, 29.959790f, 32.086849f, 34.237684f, 36.411289f, 38.606748f, 40.823222f, 43.059945f, 45.316207f, 47.591356f, 49.884783f, 52.195923f, 54.524251f, 56.869272f, 59.230524f, 61.607573f, 64.000011f, 66.407449f, 68.829525f, 71.265892f, 73.716223f, 76.180206f, 78.657546f, 81.147960f,};
    public static final String rand8 = "\1\5\5\0\7\1\1\2\5\2\2\5\7\2\1\0\4\6\7\3\0\6\7\4\0\6\3\6\7\4\1\1\7\7\4\4\1\1\2\5\7\3\6\1\4\6\2\4\5\2\6\2\7\7\7\7\6\7\1\7\6\7\1\6\6\6\1\4\4\0\7\3\5\5\0\0\4\2\3\2\4\4\5\2\7\4\7\4\2\1\2\7\6\3\7\2\3\6\6\7\1\5\1\5\4\2\6\5\0\3\4\4\2\7\0\0\1\4\3\4\0\4\2\6\0\1\4\7\5\1\0\7\6\4\1\6\1\5\6\0\4\6\5\1\5\4\6\6\4\4\6\0\1\0\0\2\2\6\3\7\5\1\0\0\5\7\1\2\6\6\5\7\2\0\1\1\3\4\0\4\5\4\1\7\1\0\7\0\2\0\3\6\7\4\5\1\4\5\1\7\5\0\6\7\6\1\5\7\1\4\1\4\4\1\7\1\2\7\7\3\7\5\1\5\7\5\3\6\5\4\3\2\6\3\4\4\1\7\7\7\6\7\7\1\5\5\5\5\2\7\1\3\4\0\2\1";
    public static final String floorSqrt = "\0\1\1\1\2\2\2\2\2\3\3\3\3\3\3\3\4\4\4\4\4\4\4\4\4\5\5\5\5\5\5\5\5\5\5\5\6\6\6\6\6\6\6\6\6\6\6\6\6\7\7\7\7\7\7\7\7\7\7\7\7\7\7\7\10\10\10\10\10\10\10\10\10\10\10\10\10\10\10\10\10\11\11\11\11\11\11\11\11\11\11\11\11\11\11\11\11\11\11\11\12\12\12\12\12\12\12\12\12\12\12\12\12\12\12\12\12\12\12\12\12\13\13\13\13\13\13\13\13\13\13\13\13\13\13\13\13\13\13\13\13\13\13\13\14\14\14\14\14\14\14\14\14\14\14\14\14\14\14\14\14\14\14\14\14\14\14\14\14\15\15\15\15\15\15\15\15\15\15\15\15\15\15\15\15\15\15\15\15\15\15\15\15\15\15\15\16\16\16\16\16\16\16\16\16\16\16\16\16\16\16\16\16\16\16\16\16\16\16\16\16\16\16\16\16\17\17\17\17\17\17\17\17\17\17\17\17\17\17\17\17\17\17\17\17\17\17\17\17\17\17\17\17\17\17\17\20\20\20\20\20\20\20\20\20\20\20\20\20\20\20\20\20\20\20\20\20\20\20\20\20\20\20\20\20\20\20\20\20\21\21\21\21\21\21\21\21\21\21\21\21\21\21\21\21\21\21\21\21\21\21\21\21\21\21\21\21\21\21\21\21\21\21\21\22\22\22\22\22\22\22\22\22\22\22\22\22\22\22\22\22\22\22\22\22\22\22\22\22\22\22\22\22\22\22\22\22\22\22\22\22\23\23\23\23\23\23\23\23\23\23\23\23\23\23\23\23\23\23\23\23\23\23\23\23\23\23\23\23\23\23\23\23\23\23\23\23\23\23\23\24\24\24\24\24\24\24\24\24\24\24\24\24\24\24\24\24\24\24\24\24\24\24\24\24\24\24\24\24\24\24\24\24\24\24\24\24\24\24\24\24\25";

    public static int rand8Idx = (RobotPlayer.rc.getID()*7)%256;

    public static float getPow(int number) {
        return pow[number];
    }

    public static int getRand8() {
        rand8Idx = rand8Idx == 256 ? 0 : rand8Idx;
        return rand8.charAt(rand8Idx++);
    }

    public static Direction getRandomDir() {
        rand8Idx = rand8Idx == 256 ? 0 : rand8Idx;
        return Global.directions[rand8Idx++];
    }

    public static int getFloorSqrt(int idx) {
        return floorSqrt.charAt(idx);
    }

}
