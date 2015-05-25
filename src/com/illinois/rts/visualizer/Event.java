package com.illinois.rts.visualizer;

import com.illinois.rts.visualizer.TraceSpace;

import java.awt.*;
import java.awt.geom.AffineTransform;

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

    public int getOrgBeginTimestampNs() { return orgBeginTimestampNs; }
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
    public abstract TraceSpace getGraphSpace();

    public void applyScaleX(int inScaleX)
    {
        scaledBeginTimestamp = orgBeginTimestampNs /inScaleX;
        scaledEndTimestamp = orgEndTimestampNs /inScaleX;
    }

    protected void drawArrow(Graphics g1, int x1, int y1, int x2, int y2) {
        int ARR_SIZE = 7;
        Graphics2D g = (Graphics2D) g1.create();

        double dx = x2 - x1, dy = y2 - y1;
        double angle = Math.atan2(dy, dx);
        int len = (int) Math.sqrt(dx*dx + dy*dy);
        AffineTransform at = AffineTransform.getTranslateInstance(x1, y1);
        at.concatenate(AffineTransform.getRotateInstance(angle));
        g.transform(at);

        // Draw horizontal arrow starting in (0, 0)
        g.drawLine(0, 0, len, 0);
        g.fillPolygon(new int[] {len, len-ARR_SIZE, len-ARR_SIZE, len},
                new int[] {0, -ARR_SIZE, ARR_SIZE, 0}, 4);
    }

    protected int getGraphicStringWidth(Graphics2D g, String inString) {
        FontMetrics fm = g.getFontMetrics();
        return fm.stringWidth(inString);
    }
}
