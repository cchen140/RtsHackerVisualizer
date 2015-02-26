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
    private TaskContainer taskContainer = new TaskContainer();

    public EventContainer(){}

    public void add(int inTimeStamp, int inEventTaskId, int inData, String inEventString)
    {
        if (inEventTaskId == SchedulerEvent.EVENT_SCHEDULER) {
            if (schedulerEvents.size() > 0) {
                schedulerEvents.get(schedulerEvents.size() - 1).setEndTimeStamp(inTimeStamp);
            }
            schedulerEvents.add(new SchedulerEvent(inTimeStamp, taskContainer.getTaskById(inData), inEventString));
        }
        else
        {
            appEvents.add(new AppEvent(inTimeStamp, taskContainer.getTaskById(inEventTaskId), inData, inEventString));
        }
    }

    public void drawVerticalCenter(Graphics2D g, int canvasHeight)
    {
        for (SchedulerEvent currentSchEvent : schedulerEvents)
        {
//            currentSchEvent.drawEvent(g, PAINT_OFFSET_X, (canvasHeight/2)-PAINT_OFFSET_Y, SCALE_X, SCALE_Y);
            currentSchEvent.drawEvent(g, PAINT_OFFSET_X, PAINT_OFFSET_Y, SCALE_X, SCALE_Y);
        }
        for (AppEvent currentAppEvent : appEvents)
        {
//            currentAppEvent.drawEvent(g, PAINT_OFFSET_X, (canvasHeight/2)-PAINT_OFFSET_Y, SCALE_X, SCALE_Y);
             currentAppEvent.drawEvent(g, PAINT_OFFSET_X, PAINT_OFFSET_Y, SCALE_X, SCALE_Y);
        }
    }


    public void clearAll()
    {
        schedulerEvents.clear();
        appEvents.clear();
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

}
