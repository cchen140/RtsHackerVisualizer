package com.illinois.rts.analysis.busyintervals;

import com.illinois.rts.analysis.busyintervals.ArrivalTimeWindow.ArrivalSegment;
import com.illinois.rts.framework.Task;
import com.illinois.rts.visualizer.*;

import java.util.ArrayList;

/**
 * Created by CY on 5/26/2015.
 */
public class BusyIntervalContainer {
    ArrayList<BusyInterval> busyIntervals = new ArrayList<BusyInterval>();

    public BusyIntervalContainer() {}

    public BusyIntervalContainer(ArrayList<BusyInterval> inBusyIntervals) {
        busyIntervals.addAll( inBusyIntervals );
    }

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
                StartTimeEventContainer thisBusyIntervalGroundTruth = new StartTimeEventContainer();
                BusyInterval thisBusyInterval = new BusyInterval(beginTimeStamp, endTimeStamp);

                /* Search for the composition of this busy interval. (ground truth) */
                for (AppEvent currentAppEvent : appEvents)
                {
                    if ( (currentAppEvent.getOrgBeginTimestampNs() >= beginTimeStamp)
                            && (currentAppEvent.getOrgBeginTimestampNs() <= endTimeStamp))
                    { // This app event is within the busy interval.
                        if (currentAppEvent.getNote().equalsIgnoreCase("BEGIN"))
                            thisBusyIntervalGroundTruth.add( currentAppEvent );
                    }
                }
                thisBusyInterval.setStartTimesGroundTruth(thisBusyIntervalGroundTruth);
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

    /* This is used to convert events from Zedboard log. */
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

    public ArrayList<BusyInterval> findBusyIntervalsBeforeTimeStamp(int inTimeStamp)
    {
        ArrayList<BusyInterval> resultBis = new ArrayList<>();
        for (BusyInterval thisBusyInterval : busyIntervals)
        {
            if (thisBusyInterval.getBeginTimeStampNs() <= inTimeStamp)
            {
                resultBis.add(thisBusyInterval);
            }
        }
        return resultBis;
    }

    public ArrayList<BusyInterval> findBusyIntervalsByTask(Task inTask)
    {
        ArrayList<BusyInterval> resultArrayList = new ArrayList<>();
        for (BusyInterval thisBusyInterval : busyIntervals)
        {
            if (thisBusyInterval.containsTaskCheckedByNkValues(inTask) == true)
            {
                resultArrayList.add(thisBusyInterval);
            }
        }

        return  resultArrayList;
    }

    public ArrayList<Event> compositionInferencesToEvents() {
        ArrayList<Event> resultEvents = new ArrayList<>();
        for (BusyInterval thisBusyInterval : busyIntervals)
        {
            resultEvents.addAll(thisBusyInterval.compositionInferenceToEvents());
        }
        return resultEvents;
    }

    public int getEndTime()
    {
        int endTime = 0;
        for (BusyInterval thisBusyInterval : busyIntervals) {
            if (thisBusyInterval.getEndTimeStampNs() > endTime) {
                endTime = thisBusyInterval.getEndTimeStampNs();
            }
        }
        return endTime;
    }

    public int getBeginTime()
    {
        int beginTime = 0;
        Boolean firstLoop = true;
        for (BusyInterval thisBusyInterval : busyIntervals) {
            if (firstLoop == true) {
                beginTime = thisBusyInterval.getBeginTimeStampNs();
                firstLoop = false;
            }

            beginTime = thisBusyInterval.getBeginTimeStampNs() < beginTime ? thisBusyInterval.getBeginTimeStampNs() : beginTime;
        }
        return beginTime;
    }

    public void removeBusyIntervalsBeforeTimeStamp(int inTimeStamp) {
        ArrayList<BusyInterval> biBeforeTimeStamp;
        biBeforeTimeStamp = findBusyIntervalsBeforeTimeStamp(inTimeStamp);
        for (BusyInterval thisBi : biBeforeTimeStamp) {
            busyIntervals.remove(thisBi);
        }
    }

    public void removeTheLastBusyInterval() {
        int lastBeginTime = 0;
        BusyInterval lastBi = null;
        for (BusyInterval thisBi : busyIntervals) {
            if (lastBeginTime < thisBi.getBeginTimeStampNs()) {
                lastBeginTime = thisBi.getBeginTimeStampNs();
                lastBi = thisBi;
            }
        }
        busyIntervals.remove(lastBi);
    }
}
