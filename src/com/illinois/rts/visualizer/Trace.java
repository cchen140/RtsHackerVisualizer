package com.illinois.rts.visualizer;

import com.illinois.rts.framework.*;

import java.awt.*;
import java.util.ArrayList;

/**
 * Created by CY on 3/13/2015.
 */
public class Trace {
    private ArrayList eventArray = null;
    private TimeLine timeLine = null;
    private int endTimestampNs = 0;
    private int marginY = 0;
    private TraceSpace traceSpace = null;
    private int offsetX = 0;
    private int offsetY = 0;

    private Color traceColor = null;
    private String traceName = null;
    private Task traceTask = null; // If it is a trace for a specific task, then this value should be set.

//    protected int scaleX = 1;

    private boolean timeLineEnabled = true;

    public Trace(ArrayList inEventArray, TimeLine inTimeLine)
    {
        eventArray = inEventArray;
        timeLine = inTimeLine;

        endTimestampNs = findEndTimestampNs();
        timeLine.setEndTimestampNs(endTimestampNs);

        traceSpace = calculateTraceSpace();
    }

    public Trace(String inTraceName, ArrayList inEventArray, TimeLine inTimeLine)
    {
        this(inEventArray, inTimeLine);
        traceName = inTraceName;
        traceTask = null;
    }

    public Trace(String inTraceName, Task inTask, ArrayList inEventArray, TimeLine inTimeLine)
    {
        this(inTraceName, inEventArray, inTimeLine);
        traceTask = inTask;
    }

//    public void setOffset(int inOffsetX, int inOffsetY)
//    {
//        offsetX = inOffsetX;
//        offsetY = inOffsetY;
//    }

    private int findEndTimestampNs()
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

    public int getTraceHeight()
    {
        return traceSpace.getHeight() + marginY*2;
    }
}
