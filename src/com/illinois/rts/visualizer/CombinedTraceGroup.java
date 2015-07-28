package com.illinois.rts.visualizer;

import com.illinois.rts.framework.Task;

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
    private ArrayList<TaskIntervalEvent> schedulerEvents = null;

    private Trace combinedTrace = null;

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

//        timeLine = new DrawTimeLine(eventContainer.getScaledEndTimestamp(), (int) ((ProgConfig.TIME_LINE_PERIOD_NS/ProgConfig.TRACE_HORIZONTAL_SCALE_DIVIDER)/ProgConfig.TIMESTAMP_UNIT_NS));

        traceGap = new DrawTraceGap();
        traceGap.setFillColor(ProgConfig.TRACE_PANEL_BORDER_COLOR);

        title = "Scheduler";
    }

    private void initializeTraces()
    {
        /* Initialize the combined trace. */
        combinedTrace = new Trace("Combined", eventContainer.getAppAndSchedulerEvents(), new TimeLine(timeLine));
        combinedTrace.setTraceType(Trace.TRACE_TYPE_OTHER);

        /* Initialize traces for each task */
        traces.clear();
//        traces.add(combinedTrace);
        for (Object currentObj : taskContainer.getTasksAsArray()) {
            Task currentTask = (Task) currentObj;

            ArrayList taskEvents = new ArrayList();
            for (TaskIntervalEvent currentSchEvent : schedulerEvents) {
                if (currentSchEvent.getTask().getId() == currentTask.getId())
                    taskEvents.add(currentSchEvent);
            }

            for (AppEvent currentAppEvent : eventContainer.getAppEvents()) {
                if (currentAppEvent.getTaskId() == currentTask.getId())
                    taskEvents.add(currentAppEvent);
            }

            for (HackerEvent currentHackerEvent : eventContainer.getHackerEvents())
            {
                if (currentHackerEvent.getTaskId() == currentTask.getId())
                    taskEvents.add(currentHackerEvent);
            }

            int thisTraceType = Trace.TRACE_TYPE_TASK;
            if (currentTask.getTaskType()==Task.TASK_TYPE_IDLE || currentTask.getTaskType()==Task.TASK_TYPE_SYS)
                thisTraceType = Trace.TRACE_TYPE_SYSTEM;

            traces.add( new Trace(currentTask.getTitle(), currentTask, taskEvents, new TimeLine(timeLine), thisTraceType) );
        }

    }

    @Override
    public void triggerUpdate()
    {
        for (Trace thisTrace: traces)
        {
            if (thisTrace.getTask().isDisplayBoxChecked() == true)
                thisTrace.setDoNotShow(false);
            else
                thisTrace.setDoNotShow(true);
        }

        if (ProgConfig.DISPLAY_SCHEDULER_SUMMARY_TRACE == true) {
            combinedTrace.setDoNotShow(false);
        } else {
            combinedTrace.setDoNotShow(true);
        }

        if (ProgConfig.DISPLAY_SCHEDULER_TASK_TRACES == false) {
            for (Trace thisTrace: traces)
            {
                thisTrace.setDoNotShow(true);
            }
        }
    }

    @Override
    public int draw(Graphics2D g, int offsetX, int offsetY) {

        int currentOffsetX = offsetX;
        int currentOffsetY = offsetY;

        if ((ProgConfig.DISPLAY_SCHEDULER_SUMMARY_TRACE==false) && (ProgConfig.DISPLAY_SCHEDULER_TASK_TRACES==false))
            return offsetY;

        // Draw background
        width = calculateWidth();
        height = calculateHeight();
        drawBackground(g, offsetX, offsetY);

        // Make some boarder space.
        currentOffsetY += marginY;
        currentOffsetX += marginX;

        //traceGap.draw(g, width+currentOffsetX, 0, currentOffsetY);

        // Insert half Y gap
//        currentOffsetY += ProgConfig.TRACE_GAP_Y/2;

        // Draw summary trace
        if (ProgConfig.DISPLAY_SCHEDULER_SUMMARY_TRACE == true) {
            currentOffsetY = combinedTrace.Draw(g, currentOffsetX, currentOffsetY);

            if (ProgConfig.DISPLAY_SCHEDULER_TASK_TRACES == true)
            {// If task traces will be drawn, draw gap.
                traceGap.draw(g, 0, currentOffsetY, width+currentOffsetX);
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

                // Draw trace border
                traceGap.draw(g, 0, currentOffsetY, width+currentOffsetX);
            }
        }

        return currentOffsetY;

    }

//    public ArrayList<Trace> getTraceListArray()
//    {
//        ArrayList<Trace> resultArray = new ArrayList();
//        // summary trace
//        if (ProgConfig.DISPLAY_SCHEDULER_SUMMARY_TRACE == true) {
//            resultArray.add(combinedTrace);
//        }
//        // Draw individuals
//        if (ProgConfig.DISPLAY_SCHEDULER_TASK_TRACES == true) {
//            for (Trace currentTrace : traces) {
//                if (currentTrace.getTask().isDisplayBoxChecked() == true)
//                {
//                    resultArray.add(currentTrace);
//                }
//            }
//        }
//
//        return resultArray;
//    }

    @Override
    public void updateTraceMarginY(int inTraceMarginY) {
        traceMarginY = inTraceMarginY;

        // Update margin for combined trace.
        combinedTrace.updateMarginY(traceMarginY);

        // Update margin for each task trace.
        for (Trace currentTrace : traces)
        {
            currentTrace.updateMarginY(traceMarginY);
        }
    }

    @Override
    protected int calculateWidth()
    {
        int resultWidth = 0;
        resultWidth += marginX*2;    // Left and right borders.
//        resultWidth += eventContainer.getScaledEndTimestamp();
        resultWidth += this.findScaledEndTimeStamp();
        return resultWidth;
    }

    @Override
    protected int calculateHeight()
    {
        int resultHeight = 0;

        if (ProgConfig.DISPLAY_SCHEDULER_SUMMARY_TRACE==false && ProgConfig.DISPLAY_SCHEDULER_TASK_TRACES==false)
            return 0;

        resultHeight += ProgConfig.VIRTUAL_PANEL_MARGIN_Y *2;   // Upper and lower borders.

        if (ProgConfig.DISPLAY_SCHEDULER_SUMMARY_TRACE == true)
            resultHeight += combinedTrace.getTraceHeight();  // Summary trace.

        if (ProgConfig.DISPLAY_SCHEDULER_TASK_TRACES == true) {
            for (Trace currentTrace : traces)
            {
                if (currentTrace.getTask().isDisplayBoxChecked() == true)
                {
                    resultHeight += currentTrace.getTraceHeight();
                }
            }

        }

        return resultHeight;
    }

    @Override
    public void copyTimeLineValues(TimeLine inTimeLine)
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

    @Override
    public ArrayList<Trace> getTraces() {
        ArrayList<Trace> resultTraces = new ArrayList<>();
        resultTraces.add(combinedTrace);
        resultTraces.addAll(traces);
        return resultTraces;
    }

}
