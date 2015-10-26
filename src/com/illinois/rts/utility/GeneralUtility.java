package com.illinois.rts.utility;

import java.util.ArrayList;
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

    public static ArrayList<Long> integerFactorization(long inNum) {
        ArrayList<Long> factors = new ArrayList<>();
        long thisNum = inNum;

        long currentFactor = 2;
        long remainder;
        while (thisNum != 1) {
            remainder = thisNum % currentFactor;
            if (remainder == 0) {
                factors.add(currentFactor);
                thisNum = thisNum/currentFactor;

                // Use the same currentFactor to check again.
                continue;
            } else {
                // currentFactor is not a factor of inNum, thus continue next value.
                currentFactor++;
            }
        }
        return factors;
    }
}
