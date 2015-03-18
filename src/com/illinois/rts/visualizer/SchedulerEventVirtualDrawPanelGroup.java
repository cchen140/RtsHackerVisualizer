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

        if ((ProgConfig.DISPLAY_SCHEDULER_SUMMARY_TRACE==false) && (ProgConfig.DISPLAY_SCHEDULER_TASK_TRACES==false))
            return;

        // Draw background
        width = calculateWidth();
        height = calculateHeight();
        background.setSize(width, height);
        background.draw(g, offsetX, offsetY);

        // Make some boarder space.
        currentOffsetY += ProgConfig.VIRTUAL_PANEL_MARGIN_Y;
        currentOffsetX += ProgConfig.VIRTUAL_PANEL_MARGIN_X;

        // Draw summary trace
        if (ProgConfig.DISPLAY_SCHEDULER_SUMMARY_TRACE == true) {
            TraceVirtualDrawPanel schedulerDrawPanel = new TraceVirtualDrawPanel(schedulerEvents);
            currentOffsetY = schedulerDrawPanel.Draw(g, currentOffsetX, currentOffsetY, scaleX, scaleY);
            currentOffsetY += ProgConfig.TRACE_GAP_Y;
        }


        // Draw individuals
        if (ProgConfig.DISPLAY_SCHEDULER_TASK_TRACES == true) {
            for (Object currentObj : taskContainer.getTasksAsArray()) {
                Task currentTask = (Task) currentObj;
                if (currentTask.isDisplayBoxChecked() == false) {
                    // This task is not displaying, go check next task.
                    continue;
                }
                ArrayList taskSchedulerEvents = new ArrayList();
                for (SchedulerEvent currentSchEvent : schedulerEvents) {
                    if (currentSchEvent.getTask().getId() == currentTask.getId())
                        taskSchedulerEvents.add(currentSchEvent);
                }

                // Draw trace
                TraceVirtualDrawPanel taskDrawPanel = new TraceVirtualDrawPanel(taskSchedulerEvents);
                currentOffsetY = taskDrawPanel.Draw(g, currentOffsetX, currentOffsetY, scaleX, scaleY);
                currentOffsetY += ProgConfig.TRACE_GAP_Y;
            }
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
        resultWidth += ProgConfig.VIRTUAL_PANEL_MARGIN_X *2;    // Left and right borders.
        //resultWidth += eventContainer.getSchedulerEvents().get(eventContainer.getSchedulerEvents().size()-2).getEndTimeStamp(); // Length of event records.
        resultWidth += eventContainer.getEndTimeStamp();
        return resultWidth;
    }

    private int calculateHeight()
    {
        int resultHeight = 0;

        if (ProgConfig.DISPLAY_SCHEDULER_SUMMARY_TRACE==false && ProgConfig.DISPLAY_SCHEDULER_TASK_TRACES==false)
            return 0;

        resultHeight += ProgConfig.VIRTUAL_PANEL_MARGIN_Y *2;   // Upper and lower borders.

        if (ProgConfig.DISPLAY_SCHEDULER_SUMMARY_TRACE == true)
            resultHeight += eventContainer.getSchedulerEvents().get(0).getDrawHeight();  // Summary trace.

        if (ProgConfig.DISPLAY_SCHEDULER_TASK_TRACES == true) {
            for (Object currentObj : taskContainer.getTasksAsArray()) {
                Task currentTask = (Task) currentObj;
                if (currentTask.isDisplayBoxChecked() == true) {// Yes, this task is gonna be displayed
                    resultHeight += ProgConfig.TRACE_GAP_Y;//gapY;
                    resultHeight += schedulerEvents.get(0).getDrawHeight();
                }
            }

            // If summary trace is not displayed, then the gap should be decrease by one unit.
            if (ProgConfig.DISPLAY_SCHEDULER_SUMMARY_TRACE == false)
                resultHeight -= ProgConfig.TRACE_GAP_Y;
        }

        return resultHeight;
    }

}
