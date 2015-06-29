package com.illinois.rts.analysis.busyintervals;

import com.illinois.rts.framework.Task;
import com.illinois.rts.visualizer.Event;
import com.illinois.rts.visualizer.SchedulerEvent;
import com.illinois.rts.visualizer.TraceSpace;

import java.awt.*;

/**
 * Created by CY on 6/27/2015.
 */
public class DecompositionEvent extends SchedulerEvent {

    public DecompositionEvent(int inBeginTimeStamp, int inEndTimeStamp, Task inTask, String inNote) {
        super(inBeginTimeStamp, inEndTimeStamp, inTask, inNote);
    }

}
