package com.illinois.rts.visualizer;

import java.awt.*;

/**
 * Created by CY on 3/14/2015.
 */
public class DrawTimeLine extends DrawUnit {
    private static final int UNIT_TIME_MARKER_LENGTH = 6;
    private int endTimeStamp = 0;
    private int unitTime = 0;

    public DrawTimeLine(int inEndTimeStamp, int inUnitTime)
    {
        super();
        endTimeStamp = inEndTimeStamp;
        unitTime = inUnitTime;
    }

    @Override
    protected void draw(Graphics2D g) {
        g.setColor(Color.black);
        g.drawLine(offsetX, offsetY, offsetX+endTimeStamp, offsetY);
        for (int i=0; i<=endTimeStamp; i+=unitTime)
        {
            g.drawLine(offsetX+i, offsetY+UNIT_TIME_MARKER_LENGTH/2, offsetX+i, offsetY-UNIT_TIME_MARKER_LENGTH/2);
        }
    }

    public void setEndTimeStamp(int inEndTimeStampe)
    {
        endTimeStamp = inEndTimeStampe;
    }
}
