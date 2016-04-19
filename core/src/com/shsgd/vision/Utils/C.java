package com.shsgd.vision.Utils;

public class C {
    //C is for CONSTANTS
    //A class to act as a container for all constants
    public static final float PPM = 16,
        gravity_constant = 18;

    //MAP constants. Change if map properties change
    public static final int TILE_WIDTH = 16, TILE_HEIGHT = 16,
            MAP_WIDTH = 16*TILE_WIDTH, MAP_HEIGHT=16*TILE_HEIGHT,
            MENU_MAP_WIDTH = 600, MENU_MAP_HEIGHT = 600;

    //BOX2D BITS.
    public static final short OBJECT_BIT = 1,
        PLAYER_BIT = 2,
        GOAL_BIT = 4,
        KEY_BIT = 8,
        LOCK_BIT = 16,
        SOLID_BIT = 32,
        SIMPLE_HAZARD_BIT = 64,
        DISABLED_BIT = 128,
        PLAYER_FOOT_BIT = 256;

}
