package com.illinois.rts.analysis.busyintervals;

import com.illinois.rts.framework.Task;
import com.illinois.rts.visualizer.*;

import java.util.ArrayList;


/**
 * Created by CY on 5/21/2015.
 */
public class Decomposition {
    TaskContainer taskContainer;

    AmirDecomposition amirDecomposition = null;
    GeDecomposition geDecomposition = null;

    public Decomposition(TaskContainer inTaskContainer)
    {
        taskContainer = inTaskContainer;
    }

    public Boolean runAmirDecomposition(BusyIntervalContainer inBusyIntervalContainer)
    {
        amirDecomposition = new AmirDecomposition(taskContainer, inBusyIntervalContainer);
        return amirDecomposition.runDecomposition();
    }

    public Boolean runAmirDecompositionStep1(BusyIntervalContainer inBusyIntervalContainer)
    {
        amirDecomposition = new AmirDecomposition(taskContainer, inBusyIntervalContainer);
        return amirDecomposition.runDecompositionStep1();
    }

    public Boolean runAmirDecompositionStep2()
    {
        return amirDecomposition.runDecompositionStep2();
    }

    public Boolean runAmirDecompositionWithErrors(BusyIntervalContainer inBusyIntervalContainer)
    {
        amirDecomposition = new AmirDecomposition(taskContainer, inBusyIntervalContainer);
        return amirDecomposition.runDecompositionWithErrors();
    }

    public Boolean runGeDecomposition(BusyIntervalContainer inBusyIntervalContainer)
    {
        geDecomposition = new GeDecomposition(taskContainer, inBusyIntervalContainer);
        return geDecomposition.RunDecomposition();
    }

//    public Trace BuildInferenceTrace(BusyIntervalContainer inBusyIntervalContainer)
//    {
//        Trace resultTrace = new Trace("Inference", inBusyIntervalContainer.compositionInferencesToEvents(), new TimeLine());
//        return resultTrace;
//    }

    public Trace buildAmirDecompositionStep1ResultTrace()
    {
        Trace resultTrace = amirDecomposition.buildCompositionTrace();
        resultTrace.setTraceName("Step 1");
        return resultTrace;
    }

    public ArrayList<Trace> buildAmirDecompositionResultTraces()
    {
        return amirDecomposition.buildResultTraces();
    }

//    public ArrayList<Trace> buildArri

//    public ArrayList<Event> BusyIntervalInferenceToEvents(BusyIntervalContainer inBusyIntervalContainer)
//    {
//        ArrayList<Event> resultEvents = new ArrayList<>();
//        for (BusyInterval thisBusyInterval : inBusyIntervalContainer.getBusyIntervals())
//        {
//            int numOfInferences = thisBusyInterval.getComposition().size();
//            int countOfInferences = 1;  // It indicates the position of the inference when there are multiple inferences.
//            for (ArrayList<Task> thisCompositionArray : thisBusyInterval.getComposition())
//            {
//                int currentTimeStamp = thisBusyInterval.getBeginTimeStampNs();
//                for (Task thisTask : thisCompositionArray)
//                {
//                    TaskIntervalEvent thisEvent = new TaskIntervalEvent(currentTimeStamp, currentTimeStamp+thisTask.getComputationTimeNs(), thisTask, "a");
//
//                    // Set the number of inferences in this busy interval for graphic display.
//                    thisEvent.getDrawInterval().setLayerPosition(numOfInferences, countOfInferences);
//
//                    resultEvents.add(thisEvent);
//                    currentTimeStamp += thisTask.getComputationTimeNs();
//                }
//
//                countOfInferences++;
//            }
//        }
//        return resultEvents;
//    }

}
