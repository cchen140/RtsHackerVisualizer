package com.illinois.rts.visualizer;

import java.awt.*;

/**
 * Created by CY on 3/14/2015.
 */
public class DrawTimeLine extends DrawUnit {
    private static final int UNIT_TIME_MARKER_LENGTH = 6;
    private int endTimeStamp = 0;
    private int unitTime = 0;
    private boolean displayTimeStamp = true;
    private boolean displayTimeStampInNorth = false;

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

            if (displayTimeStamp == true) {
                g.setFont(new Font("TimesRoman", Font.PLAIN, 14));

                /* Find the center point of the string to be drawn. */
                FontMetrics fm = g.getFontMetrics();
                int stringWidth = fm.stringWidth(String.valueOf(i));

                if (displayTimeStampInNorth == true) {
                    // Display in north.
                    g.drawString(String.valueOf(i), offsetX + i - stringWidth / 2, offsetY - 5);
                }
                else {
                    // Display in south.
                    g.drawString(String.valueOf(i), offsetX + i - stringWidth / 2, offsetY + 20);
                }
            }
        }
    }

    public void setEndTimeStamp(int inEndTimeStampe)
    {
        endTimeStamp = inEndTimeStampe;
    }
    public void setDisplayTimeStamp(boolean inDisplay)
    {
        displayTimeStamp = inDisplay;
    }

    public void setDisplayTimeStampInNorth(boolean inDisplay)
    {
        displayTimeStampInNorth = inDisplay;
    }
}
