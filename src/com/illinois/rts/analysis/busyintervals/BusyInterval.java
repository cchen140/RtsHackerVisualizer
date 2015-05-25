package com.illinois.rts.analysis.busyintervals;
import com.illinois.rts.framework.Task;

import java.util.ArrayList;

/**
 * Created by CY on 5/21/2015.
 */
public class BusyInterval {
    private int beginTimeStampNs = 0;
    private int endTimeStampNs = 0;
    private ArrayList<Task> composition;
    private ArrayList<Task> compositionGroundTruth;

    public BusyInterval(int inBeginTimeStamp, int inEndTimeStamp)
    {
        beginTimeStampNs = inBeginTimeStamp;
        endTimeStampNs = inEndTimeStamp;
    }

    public void setCompositionGroundTruth(ArrayList<Task> inGroundTruth)
    {
        compositionGroundTruth = inGroundTruth;
    }

    public void setComposition(ArrayList<Task> inComposition)
    {
        composition = inComposition;
    }

    public int getIntervalNs()
    {
        return (endTimeStampNs - beginTimeStampNs);
    }
}
