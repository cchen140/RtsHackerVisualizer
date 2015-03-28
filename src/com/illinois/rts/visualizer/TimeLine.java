package com.illinois.rts.visualizer;

import java.awt.*;

/**
 * Created by CY on 3/14/2015.
 */
public class TimeLine extends DrawUnit {
    private static final int TIMELINE_MARKER_LENGTH = 6;
    private int endTimestampNs = 0;
    private int nsPerUnit = 0;
    private int periodInNs = 0;
    private int numOfUnitInPeriod = 0;
    private boolean displayMarkerLabels = true;
    private boolean displayMarkerLabelsInNorth = false;

    public TimeLine(){ super(); }

    public TimeLine(int inEndTimestampNs, int inNsPerUnit, int inPeriodInNs)
    {
        super();
        setSettings(inEndTimestampNs, inNsPerUnit, inPeriodInNs);
    }

    public TimeLine(TimeLine inTimeLine)
    {
        super();
        copyTimeSettings(inTimeLine);
    }

    public void setSettings(int inEndTimestampNs, int inNsPerUnit, int inPeriodInNs)
    {
        endTimestampNs = inEndTimestampNs;
        nsPerUnit = inNsPerUnit;
        periodInNs = inPeriodInNs;

        numOfUnitInPeriod = periodInNs / nsPerUnit;
    }

    public void copyTimeSettings(TimeLine inTimeLine)
    {
        setSettings(inTimeLine.getEndTimestampNs(), inTimeLine.getNsPerUnit(), inTimeLine.getPeriodInNs());
    }


    @Override
    protected void draw(Graphics2D g) {
        g.setColor(Color.black);
        g.drawLine(offsetX, offsetY, offsetX+ endTimestampNs, offsetY);

        /* Draw markers and labels */
        for (int i=0, j=0; i<= endTimestampNs/ nsPerUnit; i+=numOfUnitInPeriod, j++)
        {
            g.drawLine(offsetX+i, offsetY+ TIMELINE_MARKER_LENGTH /2, offsetX+i, offsetY- TIMELINE_MARKER_LENGTH /2);

            if (displayMarkerLabels == true) {
                String timeString = nsToShortString(j*periodInNs);
                g.setFont(new Font("TimesRoman", Font.PLAIN, 14));

                /* Find the center point of the string to be drawn. */
                FontMetrics fm = g.getFontMetrics();
                int stringWidth = fm.stringWidth(timeString);

                if (displayMarkerLabelsInNorth == true) {
                    // Display in north.
                    g.drawString(timeString, offsetX + i - stringWidth / 2, offsetY - 5);
                }
                else {
                    // Display in south.
                    g.drawString(timeString, offsetX + i - stringWidth / 2, offsetY + 20);
                }
            }
        }
    }

    public void setEndTimestampNs(int inEndTimestampeNs)
    {
        endTimestampNs = inEndTimestampeNs;
    }
    public void setDisplayMarkerLabels(boolean inDisplay)
    {
        displayMarkerLabels = inDisplay;
    }

    public void setDisplayMarkerLabelsInNorth(boolean inDisplay)
    {
        displayMarkerLabelsInNorth = inDisplay;
    }

    protected String nsToShortString(int inValue)
    {
        String resultString = new String();
        if (inValue >= 1000000000)
        {
            resultString = String.valueOf((double) inValue/1000000000) + " s";
            return resultString;
        }
        else if (inValue >= 1000000)
        {
            resultString = String.valueOf((double) inValue/1000000) + " ms";
            return resultString;
        }
        else if (inValue >= 1000)
        {
            resultString = String.valueOf((double) inValue/1000) + " us";
            return resultString;
        }
        else
        {
            resultString = String.valueOf((double) inValue) + " ns";
            return resultString;
        }
    }

    int getEndTimestampNs()
    {
        return endTimestampNs;
    }
    int getNsPerUnit()
    {
        return nsPerUnit;
    }
    int getPeriodInNs()
    {
        return periodInNs;
    }
}
