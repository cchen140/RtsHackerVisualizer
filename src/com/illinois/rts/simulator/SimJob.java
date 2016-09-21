package com.illinois.rts.simulator;

import com.illinois.rts.framework.Task;
import com.illinois.rts.simulator.*;

/**
 * Created by CY on 5/26/2015.
 */
public class SimJob {
    public long seqNo;
    public Task task;
    public long remainingExecTime;
    public long releaseTime;
    public long responseTime;

    public boolean hasStarted;

    public SimJob(){}

    public SimJob( Task inTask, long inReleaseTime, long inRemainingExecTime )
    {
        task = inTask;
        releaseTime = inReleaseTime;
        remainingExecTime = inRemainingExecTime;
        hasStarted = false;
    }
}
