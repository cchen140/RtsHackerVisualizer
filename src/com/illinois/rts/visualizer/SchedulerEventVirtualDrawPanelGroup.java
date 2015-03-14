package com.illinois.rts.visualizer;

import java.awt.*;
import java.util.ArrayList;
//import com.illinois.rts.visualizer.ProgramConfigurations;

/**
 * Created by CY on 3/13/2015.
 */
public class SchedulerEventVirtualDrawPanelGroup extends VirtualDrawPanelGroup {
//    private int gapY = 5;

    private EventContainer eventContainer = null;
    private TaskContainer taskContainer = null;
    private ArrayList<SchedulerEvent> schedulerEvents = null;

    private int width = 0;
    private int height = 0;
    private DrawRect background = new DrawRect();
    private DrawTimeLine timeLine = null;


    public SchedulerEventVirtualDrawPanelGroup(EventContainer inEventContainer)
    {
        super();
        eventContainer = inEventContainer;
        taskContainer = inEventContainer.getTaskContainer();
        schedulerEvents = inEventContainer.getSchedulerEvents();

        width = calculateWidth();
        height = calculateHeight();

        background.setFillColor(Color.white);
        background.setSize(width, height);

        timeLine = new DrawTimeLine(eventContainer.getEndTimeStamp(), ProgConfig.TIME_LINE_UNIT_TIME);

    }

    @Override
    public void draw(Graphics2D g, int offsetX, int offsetY, double scaleX, double scaleY) {

        int currentOffsetX = offsetX;
        int currentOffsetY = offsetY;

        // Draw background
        height = calculateHeight();
        background.setSize(width, height);
        background.draw(g, offsetX, offsetY);

        // Make some boarder space.
        currentOffsetY += ProgConfig.VIRTUAL_PANEL_PADDING_Y;
        currentOffsetX += ProgConfig.VIRTUAL_PANEL_PADDING_X;

        // Draw summary trace
        TraceVirtualDrawPanel schedulerDrawPanel = new TraceVirtualDrawPanel(schedulerEvents);
        currentOffsetY = schedulerDrawPanel.Draw(g, currentOffsetX, currentOffsetY, scaleX, scaleY);


        // Draw individuals
        for (Object currentObj : taskContainer.getTasksAsArray()) {
            Task currentTask = (Task) currentObj;
            if (currentTask.isDisplayBoxChecked() == false) {
                // This task is not displaying, go check next task.
                continue;
            }
            ArrayList taskSchedulerEvents = new ArrayList();
            for (SchedulerEvent currentSchEvent : schedulerEvents)
            {
                if (currentSchEvent.getTask().getId() == currentTask.getId())
                    taskSchedulerEvents.add(currentSchEvent);
            }
            currentOffsetY += ProgConfig.TRACE_GAP_Y;

            // Draw trace
            TraceVirtualDrawPanel taskDrawPanel = new TraceVirtualDrawPanel(taskSchedulerEvents);
            currentOffsetY = taskDrawPanel.Draw(g, currentOffsetX, currentOffsetY, scaleX, scaleY);

        }

    }

    @Override
    public int getHeight() {
        return calculateHeight();
    }

    @Override
    public int getWidth() {
        return calculateWidth();
    }

    private int calculateWidth()
    {
        int resultWidth = 0;
        resultWidth += ProgConfig.VIRTUAL_PANEL_PADDING_X*2;    // Left and right borders.
        resultWidth += eventContainer.getSchedulerEvents().get(eventContainer.getSchedulerEvents().size()-2).getEndTimeStamp(); // Length of event records.
        return resultWidth;
    }

    private int calculateHeight()
    {
        int resultHeight = 0;
        resultHeight += ProgConfig.VIRTUAL_PANEL_PADDING_Y*2;   // Upper and lower borders.
        resultHeight += eventContainer.getSchedulerEvents().get(0).getDrawHeight();  // Summary trace.

        for (Object currentObj : taskContainer.getTasksAsArray())
        {
            Task currentTask = (Task) currentObj;
            if (currentTask.isDisplayBoxChecked() == true)
            {// Yes, this task is gonna be displayed
                resultHeight += ProgConfig.TRACE_GAP_Y;//gapY;
                resultHeight += schedulerEvents.get(0).getDrawHeight();
            }
        }
        return resultHeight;
    }

}
