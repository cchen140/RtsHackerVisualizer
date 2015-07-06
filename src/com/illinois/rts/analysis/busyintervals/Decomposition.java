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
            int numOfInferences = thisBusyInterval.getComposition().size();
            int countOfInferences = 1;  // It indicates the position of the inference when there are multiple inferences.
            for (ArrayList<Task> thisCompositionArray : thisBusyInterval.getComposition())
            {
                int currentTimeStamp = thisBusyInterval.getBeginTimeStampNs();
                for (Task thisTask : thisCompositionArray)
                {
                    TaskIntervalEvent thisEvent = new TaskIntervalEvent(currentTimeStamp, currentTimeStamp+thisTask.getComputationTimeNs(), thisTask, "a");

                    // Set the number of inferences in this busy interval for graphic display.
                    thisEvent.getDrawInterval().setLayerPosition(numOfInferences, countOfInferences);

                    resultEvents.add(thisEvent);
                    currentTimeStamp += thisTask.getComputationTimeNs();
                }

                countOfInferences++;
            }
            ProgMsg.debugPutline(String.valueOf(countOfInferences));

        }
        return resultEvents;
    }

}
