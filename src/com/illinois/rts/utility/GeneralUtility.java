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
}
