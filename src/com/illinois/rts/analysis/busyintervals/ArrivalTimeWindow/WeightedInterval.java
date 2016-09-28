package com.illinois.rts.analysis.busyintervals.ArrivalTimeWindow;

import com.illinois.rts.analysis.busyintervals.Interval;
import com.illinois.rts.visualizer.ProgMsg;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * Created by dogs0 on 9/27/2016.
 * This class does not support shifting or adjustment on begin and end times.
 */
public class WeightedInterval extends Interval {
    HashMap<Integer, Integer> weightMap = new HashMap<>();

    public WeightedInterval(int inBegin, int inEnd) {
        super(inBegin, inEnd);
    }

    public WeightedInterval(Interval inInterval) {
        super(inInterval);
    }

    public void resetWeightMap() {
        weightMap.clear();
    }

    public void applyWeight(Interval inInterval) {
        Interval intersection = this.intersect(inInterval);

        if (intersection == null) {
            return;
        }

        for (int i=intersection.getBegin(); i<=intersection.getEnd(); i++) {
            int increasedWeight = weightMap.get(i)==null ? 1 : weightMap.get(i)+1;
            weightMap.put(i, increasedWeight);
        }
    }

    public ArrayList<Interval> getMostWeightedIntervals() {
        ArrayList<Interval> resultIntervals = new ArrayList<>();
        int highestWeight = Collections.max(weightMap.values());;
        Boolean isBuildingAInterval = false;
        int currentIntervalBegin = 0;
        for (int i=getBegin(); i<=getEnd(); i++) {
            int currentWeight = weightMap.get(i)==null ? 0 : weightMap.get(i);

            if (currentWeight == highestWeight) {
                if (isBuildingAInterval) {
                    continue;
                } else {
                    currentIntervalBegin = i;
                    isBuildingAInterval = true;
                }
            } else {
                if (isBuildingAInterval) {
                    Interval newInterval = new Interval(currentIntervalBegin, i-1);
                    resultIntervals.add(newInterval);
                    isBuildingAInterval = false;
                }
            }
        }

        if (resultIntervals.size() == 0) {
            ProgMsg.debugErrPutline("It should never happen.");
        }

        return resultIntervals;
    }


}
