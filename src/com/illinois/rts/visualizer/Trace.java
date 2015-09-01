package com.illinois.rts.visualizer;

import com.illinois.rts.framework.*;

import java.awt.*;
import java.util.ArrayList;

/**
 * Created by CY on 3/13/2015.
 */
public class Trace {
    public static int TRACE_TYPE_UNKNOWN = 0;
    public static int TRACE_TYPE_SYSTEM = 1;
    public static int TRACE_TYPE_TASK = 2;
    public static int TRACE_TYPE_OTHER = 3;

    public static int TRACE_HEADER_DISPLAY_MODE_NORMAL = 0;
    public static int TRACE_HEADER_DISPLAY_MODE_NAME = 1;
    public static int TRACE_HEADER_DISPLAY_MODE_DETAIL = 2;

    private ArrayList eventArray = null;
    private TimeLine timeLine = null;

//    public int getEndTimestampNs() {
//        return endTimestampNs;
//    }

    private int endTimestampNs = 0;
    private int marginY = 0;
    private TraceSpace traceSpace = null;
    private int offsetX = 0;
    private int offsetY = 0;

    private Color traceColor = null;
    private String traceName = null;
    private Task traceTask = null; // If it is a trace for a specific task, then this value should be set.

    public void setTraceType(int traceType) {
        this.traceType = traceType;
    }

    public int getTraceType() {
        return traceType;
    }

    private int traceType = TRACE_TYPE_UNKNOWN;
//    protected int scaleX = 1;

    private Boolean timeLineEnabled = true;

    public Boolean getDoNotShow() {
        return doNotShow;
    }

    public void setDoNotShow(Boolean doNotShow) {
        this.doNotShow = doNotShow;
    }

    private Boolean doNotShow = false;
    private int headerDisplayMode = TRACE_HEADER_DISPLAY_MODE_NAME;

    public Trace(ArrayList inEventArray, TimeLine inTimeLine)
    {
        eventArray = inEventArray;
        timeLine = inTimeLine;

        endTimestampNs = findOrgEndTimestampNs();
        timeLine.setEndTimestampNs(endTimestampNs);

        traceSpace = calculateTraceSpace();
    }

    public Trace(String inTraceName, ArrayList inEventArray, TimeLine inTimeLine)
    {
        this(inEventArray, inTimeLine);
        traceName = inTraceName;
        traceTask = null;
    }

    public Trace(String inTraceName, Task inTask, ArrayList inEventArray, TimeLine inTimeLine, int inTraceType)
    {
        this(inTraceName, inEventArray, inTimeLine);
        traceTask = inTask;
        traceType = inTraceType;
    }

//    public void setOffset(int inOffsetX, int inOffsetY)
//    {
//        offsetX = inOffsetX;
//        offsetY = inOffsetY;
//    }

    public int findOrgEndTimestampNs()
    {
        int resultEndTimestampNs = 0;
        for (Object currentObj : eventArray)
        {
            Event currentEvent = (Event) currentObj;
            if (currentEvent.getOrgEndTimestampNs() > resultEndTimestampNs)
            {
                resultEndTimestampNs = currentEvent.getOrgEndTimestampNs();
            }
        }
        return resultEndTimestampNs;
    }

    public int findScaledEndTimestamp()
    {
        int resultScaledEndTimeStamp = 0;
        for (Event currentEvent : (ArrayList<Event>)eventArray)
        {
            int thisScaledEndTimeStamp = currentEvent.getScaledEndTimestamp();
            resultScaledEndTimeStamp = (thisScaledEndTimeStamp>resultScaledEndTimeStamp) ? thisScaledEndTimeStamp : resultScaledEndTimeStamp;
        }
        return resultScaledEndTimeStamp;
    }

    private TraceSpace calculateTraceSpace()
    {
        TraceSpace resultTraceSpace = new TraceSpace(0, 0);
        for (Object currentObj : eventArray)
        {
            Event currentEvent = (Event) currentObj;
            resultTraceSpace.extendSpace(currentEvent.getGraphSpace());
        }
        return resultTraceSpace;
    }

    public int Draw(Graphics2D g, int inOffsetX, int inOffsetY)
    {
        int currentOffsetY = inOffsetY + marginY;

        currentOffsetY += traceSpace.getNorthHeight();

        // Draw trace
        for (Object currentObj : eventArray) {
            Event currentEvent = (Event) currentObj;
            currentEvent.drawEvent(g, inOffsetX, currentOffsetY);
        }

        // Draw time line
        if (timeLineEnabled == true) {
            timeLine.setDisplayMarkerLabels(false);
            timeLine.draw(g, inOffsetX, currentOffsetY);
        }

        currentOffsetY += traceSpace.getSouthHeight();

        return currentOffsetY+marginY;
    }

//    public void setScaleX(int inScaleX)
//    {
//        scaleX = inScaleX;
//    }


    public boolean getTimeLineEnabled(){ return timeLineEnabled; }
    public void setTimeLineEnabled(boolean inEnable)
    {
        timeLineEnabled = inEnable;
    }

    public void updateMarginY(int inMarginY)
    {
        // Update marginY value.
        marginY = inMarginY;
    }

    public TimeLine getTimeLine()
    {
        return timeLine;
    }

    public Task getTask()
    {
        return traceTask;
    }

    public String getName()
    {
        return traceName;
    }

    public void setTraceName(String inTraceName)
    {
        traceName = inTraceName;
    }

    public int getHeaderDisplayMode() {
        return headerDisplayMode;
    }

    public void setHeaderDisplayMode(int headerDisplayMode) {
        this.headerDisplayMode = headerDisplayMode;
    }

    public int getTraceHeight()
    {
        traceSpace = calculateTraceSpace();
        return traceSpace.getHeight() + marginY*2;
    }

    public void applyHorizontalScale(int inScale)
    {
        for (Object thisEvent : eventArray) {
            ((Event)thisEvent).applyScaleX(inScale);
        }
//        scaledEndTimestamp = orgEndTimestampNs /inScale;
    }

}
