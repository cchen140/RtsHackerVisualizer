package com.illinois.rts.visualizer;

import java.awt.*;
import java.util.ArrayList;

/**
 * Created by CY on 3/13/2015.
 */
public class TraceVirtualDrawPanel {
    private ArrayList eventArray = null;
    private DrawTimeLine timeLine = null;
    private int endTimeStamp = 0;
    private int offsetX = 0;
    private int offsetY = 0;

//    protected int scaleX = 1;

    private boolean timeLineEnabled = true;

    public TraceVirtualDrawPanel(ArrayList inEventArray)
    {
        eventArray = inEventArray;
        endTimeStamp = findEndTimeStamp();

//        timeLine = new DrawTimeLine(endTimeStamp, ProgConfig.TIME_LINE_UNIT_TIME);
        timeLine = new DrawTimeLine(endTimeStamp, (int) ((ProgConfig.TIME_LINE_UNIT_NS/ProgConfig.TIMESTAMP_SCALE_DIVIDER)/ProgConfig.TIMESTAMP_UNIT_NS));
        timeLine.setDisplayTimeStamp(false);
    }

    public void setOffset(int inOffsetX, int inOffsetY)
    {
        offsetX = inOffsetX;
        offsetY = inOffsetY;
    }

    private int findEndTimeStamp()
    {
        int resultEndTimeStamp = 0;
        for (Object currentObj : eventArray)
        {
            Event currentEvent = (Event) currentObj;
            if (currentEvent.getEndTimeStamp() > resultEndTimeStamp)
            {
                resultEndTimeStamp = currentEvent.getEndTimeStamp();
            }
        }
        return resultEndTimeStamp;
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
        if (timeLineEnabled == true)
            timeLine.draw(g, inOffsetX, currentOffsetY);

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
