package com.illinois.rts.visualizer;

import java.awt.*;

/**
 * Created by CY on 2/19/2015.
 */
public abstract class Event {
    //public static final int EVENT_SCHEDULER = 0;

    protected int timeStamp = 0;
    protected int endTimeStamp = 0;

    public Event(){}

    public int getTimeStamp()
    {
        return timeStamp;
    }
    public void setEndTimeStamp(int inputTimeStamp)
    {
        endTimeStamp = inputTimeStamp;
    }
    public int getEndTimeStamp() { return endTimeStamp; }

    public abstract void drawEvent(Graphics2D g, int offsetX, int offsetY, double scaleX, double scaleY);
    public abstract int getDrawHeight();
}
