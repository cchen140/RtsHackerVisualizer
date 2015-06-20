package com.illinois.rts.analysis.busyintervals;

import com.illinois.rts.visualizer.TaskContainer;


/**
 * Created by CY on 5/21/2015.
 */
public class Decomposition {
    TaskContainer taskContainer;

    public Decomposition(TaskContainer inTaskContainer)
    {
        taskContainer = inTaskContainer;
    }

    public Boolean runAmirDecomposition(BusyIntervalContainer inBusyIntervalContainer)
    {
        AmirDecomposition amirDecomposition = new AmirDecomposition(taskContainer, inBusyIntervalContainer);
        return amirDecomposition.runDecomposition();
    }

    public Boolean runGeDecomposition(BusyIntervalContainer inBusyIntervalContainer)
    {
        GeDecomposition geDecomposition = new GeDecomposition(taskContainer, inBusyIntervalContainer);
        return geDecomposition.RunDecomposition();
    }

}
