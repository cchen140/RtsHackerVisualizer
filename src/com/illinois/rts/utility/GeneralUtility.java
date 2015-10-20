package com.illinois.rts.utility;

import java.util.Random;

/**
 * Created by CY on 9/30/2015.
 */
public class GeneralUtility {
    public static int getRandom(int min, int max) {
        Random rand = new Random();
        return rand.nextInt(max - min + 1) + min;
    }

    public static String nanoIntToMilliString(long inNum) {
        return String.valueOf((double)inNum/1000_000.0);
    }
}
