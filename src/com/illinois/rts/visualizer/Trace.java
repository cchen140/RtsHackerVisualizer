package com.illinois.rts.visualizer;

import java.awt.*;
import java.util.ArrayList;

/**
 * Created by CY on 3/13/2015.
 */
public class Trace {
    private ArrayList eventArray = null;
    private TimeLine timeLine = null;
    private int endTimestampNs = 0;
    private int height = 0;
    private int offsetX = 0;
    private int offsetY = 0;

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

        height = findMaxGraphHeight();
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
        traceName = null;
    }

    public void setOffset(int inOffsetX, int inOffsetY)
    {
        offsetX = inOffsetX;
        offsetY = inOffsetY;
    }

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

    private int findMaxGraphHeight()
    {
        int resultMaxHeight = 0;
        for (Object currentObj : eventArray)
        {
            Event currentEvent = (Event) currentObj;
            if (currentEvent.getGraphHeight() > resultMaxHeight)
            {
                resultMaxHeight  = currentEvent.getGraphHeight();
            }
        }
        return resultMaxHeight;
    }

    public int Draw(Graphics2D g, int inOffsetX, int inOffsetY)
    {
        int currentOffsetY = inOffsetY;

        // Draw trace
        for (Object currentObj : eventArray) {
            Event currentEvent = (Event) currentObj;
            currentEvent.drawEvent(g, inOffsetX, inOffsetY);
        }

        currentOffsetY += ProgConfig.TRACE_HEIGHT;

        // Draw time line
        if (timeLineEnabled == true) {
            timeLine.setDisplayMarkerLabels(false);
            timeLine.draw(g, inOffsetX, currentOffsetY);
        }

        return currentOffsetY;
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

    public TimeLine getTimeLine()
    {
        return timeLine;
    }

    public Task getTask()
    {
        return traceTask;
    }
}
