package com.illinois.rts.visualizer;

import java.awt.*;

/**
 * Created by CY on 7/2/2015.
 */
public class IntervalEvent extends Event{

    protected DrawInterval drawInterval = new DrawInterval();

    public IntervalEvent(int inBeginTimeStamp, int inEndTimeStamp)
    {
        orgBeginTimestampNs = inBeginTimeStamp;
        scaledBeginTimestamp = inBeginTimeStamp;

        orgEndTimestampNs = inEndTimeStamp;
        scaledEndTimestamp = inEndTimeStamp;
    }

    public IntervalEvent(int inBeginTimeStamp, int inEndTimeStamp, String inNote)
    {
        this(inBeginTimeStamp, inEndTimeStamp);
        note = inNote;
        noteVisible = true;
    }

    public void drawEvent(Graphics2D g, int offsetX, int offsetY)
    {
        int eventWidth = scaledEndTimestamp - scaledBeginTimestamp;
        int currentOffsetX = offsetX + scaledBeginTimestamp;

        drawInterval.setWidth(eventWidth);
        drawInterval.draw(g, currentOffsetX, offsetY - drawInterval.getHeight());

        /* Draw note if enabled. */
        if (noteVisible == true)
        {
            /* Display in north area. */
            String drawnString = note;
            int stringWidth = getGraphicStringWidth(g, drawnString);
            g.drawString(drawnString, currentOffsetX-stringWidth/2, offsetY + 25);
        }
    }

    @Override
    public TraceSpace getGraphSpace() {
        return new TraceSpace(drawInterval.getHeight(), 0);
    }

    public void setColor(Color inColor)
    {
        drawInterval.setFillColor(inColor);
    }

    public void enableTexture()
    {
        drawInterval.setFillWithTexture(true);
    }


}
