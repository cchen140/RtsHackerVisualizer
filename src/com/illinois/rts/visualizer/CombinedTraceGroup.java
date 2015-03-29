package com.illinois.rts.visualizer;

import java.awt.*;
import java.util.ArrayList;
//import com.illinois.rts.visualizer.ProgramConfigurations;

/**
 * Created by CY on 3/13/2015.
 */
public class CombinedTraceGroup extends TraceGroup {
//    private int gapY = 5;

    private EventContainer eventContainer = null;
    private TaskContainer taskContainer = null;
    private ArrayList<SchedulerEvent> schedulerEvents = null;

    private int width = 0;
    private int height = 0;
    private DrawRect background = new DrawRect();
//    private DrawTimeLine timeLine = null;
    private DrawTraceGap traceGap = null;

    private TimeLine timeLine = null;

    private Trace combinedTrace = null;
    private ArrayList<Trace> traces = new ArrayList<Trace>();

    public CombinedTraceGroup(EventContainer inEventContainer, TimeLine inTimeLine)
    {
        super();
        eventContainer = inEventContainer;
        taskContainer = inEventContainer.getTaskContainer();
        schedulerEvents = inEventContainer.getSchedulerEvents();

        timeLine = inTimeLine;

        initializeTraces();

        width = calculateWidth();
        height = calculateHeight();

        background.setFillColor(ProgConfig.TRACE_PANEL_FOREGROUND);
        background.setSize(width, height);

//        timeLine = new DrawTimeLine(eventContainer.getScaledEndTimestamp(), (int) ((ProgConfig.TIME_LINE_PERIOD_NS/ProgConfig.TRACE_HORIZONTAL_SCALE_DIVIDER)/ProgConfig.TIMESTAMP_UNIT_NS));

        traceGap = new DrawTraceGap();
        traceGap.setFillColor(ProgConfig.TRACE_PANEL_BORDER_COLOR);

    }

    private void initializeTraces()
    {
        /* Initialize the combined trace. */
        combinedTrace = new Trace("Combined", eventContainer.getAllEvents(), new TimeLine(timeLine));

        /* Initialize traces for each task */
        traces.clear();
        for (Object currentObj : taskContainer.getTasksAsArray()) {
            Task currentTask = (Task) currentObj;

            ArrayList taskEvents = new ArrayList();
            for (SchedulerEvent currentSchEvent : schedulerEvents) {
                if (currentSchEvent.getTask().getId() == currentTask.getId())
                    taskEvents.add(currentSchEvent);
            }

            for (AppEvent currentAppEvent : eventContainer.getAppEvents()) {
                if (currentAppEvent.getTaskId() == currentTask.getId())
                    taskEvents.add(currentAppEvent);
            }

            traces.add( new Trace(currentTask.getTitle(), currentTask, taskEvents, new TimeLine(timeLine)) );
        }

    }

    @Override
    public void draw(Graphics2D g, int offsetX, int offsetY) {

        int currentOffsetX = offsetX;
        int currentOffsetY = offsetY;

        if ((ProgConfig.DISPLAY_SCHEDULER_SUMMARY_TRACE==false) && (ProgConfig.DISPLAY_SCHEDULER_TASK_TRACES==false))
            return;

        // Draw background
        width = calculateWidth();
        height = calculateHeight();
        background.setSize(width+1, height+5);
        background.draw(g, offsetX-1, offsetY-5);
//        System.out.println(offsetX);
//        System.out.println(offsetY);

        // Make some boarder space.
        currentOffsetY += marginY;
        currentOffsetX += marginX;


        //traceGap.draw(g, width+currentOffsetX, 0, currentOffsetY);

        // Insert half Y gap
        currentOffsetY += ProgConfig.TRACE_GAP_Y/2;

        // Draw summary trace
        if (ProgConfig.DISPLAY_SCHEDULER_SUMMARY_TRACE == true) {
            currentOffsetY = combinedTrace.Draw(g, currentOffsetX, currentOffsetY);

            if (ProgConfig.DISPLAY_SCHEDULER_TASK_TRACES == true)
            {// If it's going to draw individual traces, draw gap.
                currentOffsetY += ProgConfig.TRACE_GAP_Y/2;
                traceGap.draw(g, 0, currentOffsetY, width+currentOffsetX);
                currentOffsetY += ProgConfig.TRACE_GAP_Y/2;
            }

        }


        // Draw individuals
        if (ProgConfig.DISPLAY_SCHEDULER_TASK_TRACES == true) {
            for (Trace currentTrace : traces)
            {
                if (currentTrace.getTask().isDisplayBoxChecked() == false)
                    continue;

                // Draw trace
                currentOffsetY = currentTrace.Draw(g, currentOffsetX, currentOffsetY);

                // Move brush and draw trace border (gap)
                currentOffsetY += ProgConfig.TRACE_GAP_Y/2;
                traceGap.draw(g, 0, currentOffsetY, width+currentOffsetX);
                currentOffsetY += ProgConfig.TRACE_GAP_Y/2;
            }
        }

    }

    public Object[] getTraceListArray()
    {
        ArrayList resultArray = new ArrayList();
        // summary trace
        if (ProgConfig.DISPLAY_SCHEDULER_SUMMARY_TRACE == true) {
            resultArray.add("Combined");
        }
        // Draw individuals
        if (ProgConfig.DISPLAY_SCHEDULER_TASK_TRACES == true) {
            for (Object currentObj : taskContainer.getTasksAsArray()) {
                Task currentTask = (Task) currentObj;
                if (currentTask.isDisplayBoxChecked() == false) {
                    // This task is not displaying, go check next task.
                    continue;
                }
                else
                {
                    resultArray.add(currentTask.getTitle());
                }
            }
        }

        return resultArray.toArray();
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
        resultWidth += marginX*2;    // Left and right borders.
        //resultWidth += eventContainer.getSchedulerEvents().get(eventContainer.getSchedulerEvents().size()-2).getScaledEndTimestamp(); // Length of event records.
        resultWidth += eventContainer.getScaledEndTimestamp();
        return resultWidth;
    }

    private int calculateHeight()
    {
        int resultHeight = 0;

        if (ProgConfig.DISPLAY_SCHEDULER_SUMMARY_TRACE==false && ProgConfig.DISPLAY_SCHEDULER_TASK_TRACES==false)
            return 0;

        resultHeight += ProgConfig.VIRTUAL_PANEL_MARGIN_Y *2;   // Upper and lower borders.

        if ((ProgConfig.DISPLAY_SCHEDULER_SUMMARY_TRACE==true)
                || (ProgConfig.DISPLAY_SCHEDULER_TASK_TRACES==true)) {
            resultHeight += ProgConfig.TRACE_GAP_Y;   // 1/2 Y gap before first trace and another 1/2 /Y gap after last trace.
        }

        if (ProgConfig.DISPLAY_SCHEDULER_SUMMARY_TRACE == true)
            resultHeight += eventContainer.getSchedulerEvents().get(0).getGraphHeight();  // Summary trace.

        if (ProgConfig.DISPLAY_SCHEDULER_TASK_TRACES == true) {
            for (Object currentObj : taskContainer.getTasksAsArray()) {
                Task currentTask = (Task) currentObj;
                if (currentTask.isDisplayBoxChecked() == true) {// Yes, this task is gonna be displayed
                    resultHeight += ProgConfig.TRACE_GAP_Y;//gapY;
                    resultHeight += schedulerEvents.get(0).getGraphHeight();
                }
            }

            // If summary trace is not displayed, then the gap should be decrease by one unit.
            if (ProgConfig.DISPLAY_SCHEDULER_SUMMARY_TRACE == false)
                resultHeight -= ProgConfig.TRACE_GAP_Y;
        }

        return resultHeight;
    }

    void copyTimeLineValues(TimeLine inTimeLine)
    {
        timeLine.copyTimeValues(inTimeLine);

        // Update time line setting for combined trace.
        combinedTrace.getTimeLine().copyTimeValues(inTimeLine);

        // Update time line settings for traces.
        for (Trace currentTrace : traces)
        {
            currentTrace.getTimeLine().copyTimeValues(inTimeLine);
        }
    }

//    int findHighestDrawHeight

}
