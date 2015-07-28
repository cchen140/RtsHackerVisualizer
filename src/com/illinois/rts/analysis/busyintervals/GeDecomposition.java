package com.illinois.rts.analysis.busyintervals;

import com.illinois.rts.framework.Task;
import com.illinois.rts.visualizer.ProgMsg;
import com.illinois.rts.visualizer.TaskContainer;
import com.sun.org.apache.xpath.internal.operations.Bool;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by CY on 6/11/2015.
 */
public class GeDecomposition {
    private TaskContainer taskContainer;
    private BusyIntervalContainer busyIntervalContainer;
//    private ArrayList<Integer> DeductedBusyIntervals = new ArrayList<Integer>();
    private HashMap<BusyInterval, Integer> deductedBusyIntervals = new HashMap<BusyInterval, Integer>();

    public GeDecomposition(TaskContainer inTaskContainer, BusyIntervalContainer inBusyIntervalContainer)
    {
        taskContainer = inTaskContainer;
        busyIntervalContainer = inBusyIntervalContainer;

        /* Initialize DeBusyIntervals with original intervals. */
        initializeDeductedBusyIntervals();
    }

    private void initializeDeductedBusyIntervals()
    {
        /* Initialize DeBusyIntervals with original intervals. */
        for (BusyInterval thisBusyInterval : busyIntervalContainer.getBusyIntervals())
        {
            deductedBusyIntervals.put(thisBusyInterval, thisBusyInterval.getIntervalNs());
        }
    }


    public Boolean RunDecomposition()
    {
        ArrayList<Task> appTaskArray;

        // TODO: Start with the task having shortest computation time or the one having smallest period?
        /* Sort tasks in ascending order by computation time. */
        appTaskArray = taskContainer.getAppTaskAsArraySortedByComputationTime();

        /* Loop to deduct the task from busy intervals starting from the task with shortest computation time. */
        for (Task thisTask : appTaskArray)
        {
            if (deductATaskFromDeductedBusyIntervals(thisTask) == false)
            {
                ProgMsg.sysPutLine("Deduct the task %s from intervals failed.", thisTask.getTitle());
                return false;
            }
            else {
                ProgMsg.sysPutLine("Successfully deduct the task %s from intervals.", thisTask.getTitle());
            }
        }
        ProgMsg.sysPutLine("Ge's algorithm passed.");
        return true;
    }

    private BusyInterval findFirstMatchingDeductedBusyInterval(int inInterval)
    {
        if (deductedBusyIntervals.containsValue(inInterval) == false)
        {
            // No matching interval is found.
            ProgMsg.sysPutLine("No independent targeted busy interval has been found for Ge's algorithm.");
            return null;
        }

        for (BusyInterval thisBusyInterval : busyIntervalContainer.getBusyIntervals())
        {
            if (deductedBusyIntervals.get(thisBusyInterval) == inInterval)
            {
                // Return the first found matching deducted busy interval.
                return thisBusyInterval;
            }
        }

        // The program should not be reaching here. Every condition should already be considered.
        return null;
    }

    private Boolean deductATaskFromDeductedBusyIntervals(Task inTask)
    {
        BusyInterval referenceBusyInterval = findFirstMatchingDeductedBusyInterval(inTask.getComputationTimeNs());
        if (referenceBusyInterval == null)
        {// Out of Ge's algorithm restriction.
            return false;
        }

        int referenceBeginTimeStamp = referenceBusyInterval.getBeginTimeStampNs();

        /* Check busy intervals one by one to see if they contain this task. */
        for (BusyInterval thisBusyInterval : busyIntervalContainer.getBusyIntervals())
        {
            int thisDeductedBusyInterval = deductedBusyIntervals.get(thisBusyInterval);
            int nthPeriodAway = (referenceBeginTimeStamp - thisBusyInterval.getBeginTimeStampNs())/inTask.getPeriodNs();
            int inferredTimeStampOfInTaskScheduledInThisInterval = referenceBeginTimeStamp - inTask.getPeriodNs()*nthPeriodAway;
            if (thisBusyInterval.contains(inferredTimeStampOfInTaskScheduledInThisInterval) == true)
            {
                int numOfInTaskIncluded = ((thisBusyInterval.getEndTimeStampNs()-inferredTimeStampOfInTaskScheduledInThisInterval) / inTask.getPeriodNs()) + 1;

                ProgMsg.sysPutLine("Busy interval %d:%d contains %d task %s.", thisBusyInterval.getIntervalNs(), thisDeductedBusyInterval, numOfInTaskIncluded, inTask.getTitle());

                thisDeductedBusyInterval -= numOfInTaskIncluded * inTask.getComputationTimeNs();
                deductedBusyIntervals.replace(thisBusyInterval, thisDeductedBusyInterval);

                // Update referred composition.
                for (int loop=0; loop<numOfInTaskIncluded; loop++)
                    thisBusyInterval.getFirstComposition().add(inTask);

                assert thisDeductedBusyInterval >= 0 : "Deducted busy interval becomes negative.";

            }
        }
        return true;
    }



}
