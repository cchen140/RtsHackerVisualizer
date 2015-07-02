package com.illinois.rts.visualizer;

import com.illinois.rts.framework.Task;

import java.awt.*;

/**
 * Created by CY on 7/2/2015.
 */
public class PhaseEvent extends Event{

    protected DrawPhase drawPhase = new DrawPhase();

    public PhaseEvent(int inBeginTimeStamp, int inEndTimeStamp)
    {
        orgBeginTimestampNs = inBeginTimeStamp;
        scaledBeginTimestamp = inBeginTimeStamp;

        orgEndTimestampNs = inEndTimeStamp;
        scaledEndTimestamp = inEndTimeStamp;

//            drawPhase.setLabel("");
//            drawPhase.setFillColor();
    }

    public PhaseEvent(int inBeginTimeStamp, int inEndTimeStamp, String inNote)
    {
        this(inBeginTimeStamp, inEndTimeStamp);
        note = inNote;
        noteVisible = true;
    }

    public void drawEvent(Graphics2D g, int offsetX, int offsetY)
    {
        int eventWidth = scaledEndTimestamp - scaledBeginTimestamp;
        int currentOffsetX = offsetX + scaledBeginTimestamp;

        drawPhase.setWidth(eventWidth);
        drawPhase.draw(g, currentOffsetX, offsetY - drawPhase.getHeight());

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
        return new TraceSpace(drawPhase.getHeight(), 0);
    }


}
