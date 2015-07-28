package com.illinois.rts.visualizer;

import com.illinois.rts.framework.Task;

import java.util.ArrayList;

/**
 * Created by CY on 2/16/2015.
 */
public class EventContainer {
    public static final int SCHEDULER_EVENT = 0;
    public static final int APP_EVENT = 1;
    public static final int HACKER_EVENT = 2;

    private static final int PAINT_OFFSET_X = 100;
    private static final int PAINT_OFFSET_Y = 150;//-75;//75;
    private static final double SCALE_X = 1.0;
    private static final double SCALE_Y = 1.0;

    private ArrayList<TaskIntervalEvent> schedulerEvents = new ArrayList<TaskIntervalEvent>();
    private ArrayList<AppEvent> appEvents = new ArrayList<AppEvent>();
    private ArrayList<HackerEvent> hackerEvents = new ArrayList<HackerEvent>();
    private TaskContainer taskContainer = new TaskContainer();

    private int scaledEndTimestamp = 0;
    private int orgEndTimestampNs = 0;

    public EventContainer(){}

    public void add(int inEventType, int inTimestampNs, int inEventTaskId, int inData, String inEventString)
    {
        if (inEventType == SCHEDULER_EVENT)
        {// inEventTaskId is 0 as from scheduler, inData is the Id of the task being scheduled.
            if (schedulerEvents.size() > 0) {
                schedulerEvents.get(schedulerEvents.size() - 1).setOrgEndTimestampNs(inTimestampNs);
            }
            schedulerEvents.add(new TaskIntervalEvent(inTimestampNs, taskContainer.getTaskById(inData), inEventString));

            // Assume that the added scheduler event is in order, the latest one should be the latest.
            // if (scaledEndTimestamp < inTimestampNs)
            orgEndTimestampNs = inTimestampNs;
            scaledEndTimestamp = inTimestampNs;
        }
        else if (inEventType == HACKER_EVENT)
        {
            hackerEvents.add(new HackerEvent(inTimestampNs, taskContainer.getTaskById(inEventTaskId), inData, inEventString));
        }
        else if (inEventType == APP_EVENT)
        {
            appEvents.add(new AppEvent(inTimestampNs, taskContainer.getTaskById(inEventTaskId), inData, inEventString));
        }
    }

    public void clearAll()
    {
        schedulerEvents.clear();
        appEvents.clear();
        hackerEvents.clear();
        taskContainer.clear();
    }

    public void setTaskContainer(TaskContainer inputTaskContainer)
    {
        taskContainer = inputTaskContainer;
    }

    public TaskContainer getTaskContainer()
    {
        return taskContainer;
    }

    public ArrayList<TaskIntervalEvent> getSchedulerEvents() { return schedulerEvents; }
    public ArrayList<AppEvent> getAppEvents() { return appEvents; }
    public  ArrayList<HackerEvent> getHackerEvents() { return hackerEvents; }

    public ArrayList<TaskIntervalEvent> getSchedulerEventsOfATask(Task inTask)
    {
        ArrayList resultArrayList = new ArrayList();
        for (TaskIntervalEvent thisEvent : schedulerEvents)
        {
            if (thisEvent.getTask() == inTask)
                resultArrayList.add(thisEvent);
        }
        return resultArrayList;
    }

    public ArrayList<AppEvent> getAppEventsOfATask(Task inTask)
    {
        ArrayList<AppEvent> resultArrayList = new ArrayList();
        for (AppEvent thisEvent : appEvents)
        {
            if (thisEvent.getTask() == inTask)
            {
                resultArrayList.add(thisEvent);
            }
        }
        return resultArrayList;
    }

    public ArrayList<HackerEvent> getLowHackerEvents()
    {
        ArrayList resultArrayList = new ArrayList();
        for (HackerEvent currentEvent : hackerEvents)
        {
            if (currentEvent.getTaskId() == HackerEvent.lowHackerId)
            {
                resultArrayList.add(currentEvent);
            }
        }
        return resultArrayList;
    }

    public ArrayList getAllEvents()
    {
        ArrayList resultArrayList = new ArrayList();
        resultArrayList.addAll(schedulerEvents);
        resultArrayList.addAll(appEvents);
        resultArrayList.addAll(hackerEvents);
        return resultArrayList;
    }

    public ArrayList getAppAndSchedulerEvents()
    {
        ArrayList resultArrayList = new ArrayList();
        resultArrayList.addAll(schedulerEvents);
        resultArrayList.addAll(appEvents);
        return resultArrayList;
    }

    public int getScaledEndTimestamp()
    {
        return scaledEndTimestamp;
    }
    public int getOrgEndTimestampNs() { return orgEndTimestampNs; }

    public void applyHorizontalScale(int inScale)
    {
        for (TaskIntervalEvent currentEvent : schedulerEvents) {
            currentEvent.applyScaleX(inScale);
        }

        for (AppEvent currentEvent : appEvents)
        {
            currentEvent.applyScaleX(inScale);
        }

        for (HackerEvent currentEvent : hackerEvents)
        {
            currentEvent.applyScaleX(inScale);
        }

        scaledEndTimestamp = orgEndTimestampNs /inScale;
    }

    // This method returns the first matched event.
    public TaskIntervalEvent findSchedulerEventByTime(int inTimeStamp)
    {
        for (TaskIntervalEvent thisEvent : schedulerEvents)
        {
            if (thisEvent.contains(inTimeStamp))
                return thisEvent;
        }

        // If no event contains the designated time stamp, then return null.
        return null;
    }

    public ArrayList<TaskIntervalEvent> findSchedulerEventsByTimeWindow(int inBeginTimeStamp, int inEndTimeStamp)
    {
        ArrayList resultArrayList = new ArrayList();
        for (TaskIntervalEvent thisEvent : schedulerEvents)
        {
            if (isValueWithinRange(thisEvent.getOrgBeginTimestampNs(), inBeginTimeStamp, inEndTimeStamp) ||
                isValueWithinRange(thisEvent.getOrgEndTimestampNs(), inBeginTimeStamp, inEndTimeStamp))
            {
                resultArrayList.add(thisEvent);
            }
        }
        return resultArrayList;
    }

    public Boolean isValueWithinRange(int inTargetValue, int inBegin, int inEnd)
    {
        return (inTargetValue>=inBegin && inTargetValue<=inEnd) ? true : false;
    }

}
