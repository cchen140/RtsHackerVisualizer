package com.illinois.rts.analysis.busyintervals;

import com.illinois.rts.framework.Task;
import com.illinois.rts.visualizer.*;

import java.util.ArrayList;


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

    public Trace BuildInferenceTrace(BusyIntervalContainer inBusyIntervalContainer)
    {
        Trace resultTrace = new Trace("Inference", this.BusyIntervalInferenceToEvents(inBusyIntervalContainer), new TimeLine());
        return resultTrace;
    }

    public ArrayList<Event> BusyIntervalInferenceToEvents(BusyIntervalContainer inBusyIntervalContainer)
    {
        ArrayList<Event> resultEvents = new ArrayList<>();
        for (BusyInterval thisBusyInterval : inBusyIntervalContainer.getBusyIntervals())
        {
            // TODO: 2nd potential answers to be handled.
            for (ArrayList<Task> thisCompositionArray : thisBusyInterval.getComposition())
            {
                int currentTimeStamp = thisBusyInterval.getBeginTimeStampNs();
                for (Task thisTask : thisCompositionArray)
                {
                    DecompositionEvent thisEvent = new DecompositionEvent(currentTimeStamp, currentTimeStamp+thisTask.getComputationTimeNs(), thisTask, "a");
                    resultEvents.add(thisEvent);
                    currentTimeStamp += thisTask.getComputationTimeNs();
                }
                break;
            }

        }
        return resultEvents;
    }

}
