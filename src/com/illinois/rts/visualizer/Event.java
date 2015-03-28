package com.illinois.rts.visualizer;

import java.awt.*;

/**
 * Created by CY on 2/19/2015.
 */
public abstract class Event {
    //public static final int EVENT_SCHEDULER = 0;
    protected int orgBeginTimestampNs = 0;
    protected int orgEndTimestampNs = 0;

    protected int scaledBeginTimestamp = 0;
    protected int scaledEndTimestamp = 0;

    public Event(){}

    public int getScaledBeginTimestamp()
    {
        return scaledBeginTimestamp;
    }
    public void setOrgEndTimestampNs(int inputTimeStamp)
    {
        orgEndTimestampNs = inputTimeStamp;
    }
    public int getScaledEndTimestamp() { return scaledEndTimestamp; }
    public int getOrgEndTimestampNs()
    {
        return orgEndTimestampNs;
    }

    public abstract void drawEvent(Graphics2D g, int offsetX, int offsetY);
    public abstract int getDrawHeight();

    public void applyScaleX(int inScaleX)
    {
        scaledBeginTimestamp = orgBeginTimestampNs /inScaleX;
        scaledEndTimestamp = orgEndTimestampNs /inScaleX;
    }
}
