package com.illinois.rts.analysis.busyintervals;

import com.illinois.rts.framework.Task;
import com.illinois.rts.visualizer.AppEvent;
import com.illinois.rts.visualizer.EventContainer;
import com.illinois.rts.visualizer.IntervalEvent;
import com.illinois.rts.visualizer.TaskIntervalEvent;

import java.util.ArrayList;

/**
 * Created by CY on 5/26/2015.
 */
public class BusyIntervalContainer {
    ArrayList<BusyInterval> busyIntervals = new ArrayList<BusyInterval>();

    public Boolean createBusyIntervalsFromEvents(EventContainer inEventContainer)
    {
        ArrayList<TaskIntervalEvent> schedulerEvents = inEventContainer.getSchedulerEvents();
        ArrayList<AppEvent> appEvents = inEventContainer.getAppEvents();
        int idleTaskId = 0;

        // Reset the variable.
        busyIntervals.clear();

        // Find IDLE task ID
        for (Object currentObject : inEventContainer.getTaskContainer().getTasksAsArray())
        {
            Task currentTask = (Task) currentObject;
            if (currentTask.getTitle().equalsIgnoreCase("IDLE")){
                idleTaskId = currentTask.getId();
                break;
            }
        }

        Boolean busyIntervalFound = false;
        int beginTimeStamp = 0;
        for (TaskIntervalEvent currentEvent: schedulerEvents)
        {
            if (busyIntervalFound == false)
            {
                if (currentEvent.getTask().getId() == idleTaskId) {
                    continue;
                }
                else
                { // Start of a busy interval is found.
                    busyIntervalFound = true;
                    beginTimeStamp = currentEvent.getOrgBeginTimestampNs();
                    continue;
                }
            }

            if (currentEvent.getTask().getId() == idleTaskId)
            { // This is the end of a busy interval.
                int endTimeStamp = currentEvent.getOrgBeginTimestampNs();
                ArrayList<Task> thisBusyIntervalGroundTruth = new ArrayList<Task>();
                BusyInterval thisBusyInterval = new BusyInterval(beginTimeStamp, endTimeStamp);

                /* Search for the composition of this busy interval. (ground truth) */
                for (AppEvent currentAppEvent : appEvents)
                {
                    if ( (currentAppEvent.getOrgBeginTimestampNs() >= beginTimeStamp)
                            && (currentAppEvent.getOrgBeginTimestampNs() <= endTimeStamp))
                    { // This app event is within the busy interval.
                        if (currentAppEvent.getNote().equalsIgnoreCase("BEGIN"))
                            thisBusyIntervalGroundTruth.add(currentAppEvent.getTask());
                    }
                }
                thisBusyInterval.setCompositionGroundTruth(thisBusyIntervalGroundTruth);
                busyIntervals.add(thisBusyInterval);

                // Reset flag to search next busy interval.
                busyIntervalFound = false;
            }
            else
            { // current task is not idle, thus it is still within a busy interval. Continue searching for the idle task.

            }

        } // End of scheduler events iteration loop.
        return true;
    }

    public Boolean createBusyIntervalsFromIntervalEvents(ArrayList<IntervalEvent> inEvents)
    {
        // Reset the variable.
        busyIntervals.clear();

        for (IntervalEvent thisEvent : inEvents)
        {
            int thisBeginTimeStamp = thisEvent.getOrgBeginTimestampNs();
            int thisEndTimeStamp = thisEvent.getOrgEndTimestampNs();
            BusyInterval thisBusyInterval = new BusyInterval(thisBeginTimeStamp, thisEndTimeStamp);
            busyIntervals.add(thisBusyInterval);
        }
        return true;
    }

    public ArrayList<BusyInterval> getBusyIntervals()
    {
        return busyIntervals;
    }

    public BusyInterval findBusyIntervalByTimeStamp(int inTimeStamp)
    {
        for (BusyInterval thisBusyInterval : busyIntervals)
        {
            if (thisBusyInterval.contains(inTimeStamp) == true)
            {
                return thisBusyInterval;
            }
        }

        // If the program reaches here, that means no interval contains the input time stamp.
        return null;
    }
}
