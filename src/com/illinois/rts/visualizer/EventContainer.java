package com.illinois.rts.visualizer;

import java.awt.*;
import java.util.ArrayList;

/**
 * Created by CY on 2/16/2015.
 */
public class EventContainer {
    private static final int PAINT_OFFSET_X = 100;
    private static final int PAINT_OFFSET_Y = 150;//-75;//75;
    private static final double SCALE_X = 1.0;
    private static final double SCALE_Y = 1.0;

    private ArrayList<SchedulerEvent> schedulerEvents = new ArrayList<SchedulerEvent>();
    private ArrayList<AppEvent> appEvents = new ArrayList<AppEvent>();
    private ArrayList<HackerEvent> hackerEvents = new ArrayList<HackerEvent>();
    private TaskContainer taskContainer = new TaskContainer();

    private int endTimeStamp = 0;

    public EventContainer(){}

    public void add(int inTimeStamp, int inEventTaskId, int inData, String inEventString)
    {
        if (inEventTaskId == SchedulerEvent.EVENT_SCHEDULER) {
            if (schedulerEvents.size() > 0) {
                schedulerEvents.get(schedulerEvents.size() - 1).setEndTimeStamp(inTimeStamp);
            }
            schedulerEvents.add(new SchedulerEvent(inTimeStamp, taskContainer.getTaskById(inData), inEventString));

            // Assume that the added scheduler event is in order, the latest one should be the latest.
            // if (endTimeStamp < inTimeStamp)
            endTimeStamp = inTimeStamp;
        }
        else if ((inEventTaskId==HackerEvent.highHackerId) || (inEventTaskId==HackerEvent.lowHackerId))
        {
            hackerEvents.add(new HackerEvent(inTimeStamp, taskContainer.getTaskById(inEventTaskId), inData, inEventString));
        }
        else
        {
            appEvents.add(new AppEvent(inTimeStamp, taskContainer.getTaskById(inEventTaskId), inData, inEventString));
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
    public ArrayList getAllEvents()
    {
        ArrayList resultArrayList = new ArrayList();
        resultArrayList.addAll(schedulerEvents);
        resultArrayList.addAll(appEvents);
        resultArrayList.addAll(hackerEvents);
        return resultArrayList;
    }

    public int getEndTimeStamp()
    {
        return endTimeStamp;
    }

}
