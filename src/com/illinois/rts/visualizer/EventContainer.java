package com.illinois.rts.visualizer;

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

    private ArrayList<SchedulerEvent> schedulerEvents = new ArrayList<SchedulerEvent>();
    private ArrayList<AppEvent> appEvents = new ArrayList<AppEvent>();
    private ArrayList<HackerEvent> hackerEvents = new ArrayList<HackerEvent>();
    private TaskContainer taskContainer = new TaskContainer();

    private int scaledEndTimestamp = 0;
    private int orgEndTimestampNs = 0;

    public EventContainer(){}

    public void add(int inEventType, int inTimestampNs, int inEventTaskId, int inData, String inEventString)
    {
        if (inEventType == SCHEDULER_EVENT) {
            if (schedulerEvents.size() > 0) {
                schedulerEvents.get(schedulerEvents.size() - 1).setOrgEndTimestampNs(inTimestampNs);
            }
            schedulerEvents.add(new SchedulerEvent(inTimestampNs, taskContainer.getTaskById(inData), inEventString));

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

    public ArrayList<SchedulerEvent> getSchedulerEvents() { return schedulerEvents; }
    public ArrayList<AppEvent> getAppEvents() { return appEvents; }
    public  ArrayList<HackerEvent> getHackerEvents() { return hackerEvents; }

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
        for (SchedulerEvent currentEvent : schedulerEvents) {
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

}
