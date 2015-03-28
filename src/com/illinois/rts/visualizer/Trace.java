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
    private int offsetX = 0;
    private int offsetY = 0;

//    protected int scaleX = 1;

    private boolean timeLineEnabled = true;

    public Trace(ArrayList inEventArray, TimeLine inTimeLine)
    {
        eventArray = inEventArray;
        endTimestampNs = findEndTimestampNs();

        timeLine = inTimeLine;
//        timeLine = new DrawTimeLine(scaledEndTimestamp, ProgConfig.TIME_LINE_UNIT_TIME);
//        timeLine = new TimeLine(endTimestampNs, (int) ((ProgConfig.TIME_LINE_PERIOD_NS /ProgConfig.TRACE_HORIZONTAL_SCALE_DIVIDER)/ProgConfig.TIMESTAMP_UNIT_NS));
//        timeLine.setDisplayMarkerLabels(false);
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
}
