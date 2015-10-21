package com.illinois.rts.visualizer;

import java.awt.*;

/**
 * Created by CY on 3/14/2015.
 */
public class TimeLine extends DrawUnit {
    private static final int TIMELINE_MARKER_LENGTH = 6;
    private int endTimestampOrg = 0;
    private double countPerUnit = 1; // how many counts per TIMESTAMP_UNIT_NS
    private int periodInNs = 1;
    private int unitPerPeriod = 1;
    private boolean displayMarkerLabels = true;
    private boolean displayMarkerLabelsInNorth = false;

    public TimeLine(){ super(); }

    public TimeLine(int inEndTimestampNs, int inNsPerUnit, int inPeriodInNs)
    {
        super();
        setTimeValues(inEndTimestampNs, inNsPerUnit, inPeriodInNs);
    }

    public TimeLine(TimeLine inTimeLine)
    {
        super();
        copyTimeValues(inTimeLine);
    }

    public void setTimeValues(int inEndTimestampOrg, double inCountPerUnit, int inPeriodInNs)
    {
        endTimestampOrg = inEndTimestampOrg;
        countPerUnit = inCountPerUnit;
        periodInNs = inPeriodInNs;

//        unitPerPeriod = (int) (periodInNs / countPerUnit);
        unitPerPeriod = (int) (periodInNs/ProgConfig.TIMESTAMP_UNIT_NS);
    }

    public void copyTimeValues(TimeLine inTimeLine)
    {
        setTimeValues(inTimeLine.getEndTimestampOrg(), inTimeLine.getCountPerUnit(), inTimeLine.getPeriodInNs());
    }


    @Override
    protected void draw(Graphics2D g) {
        g.setColor(Color.black);
        g.drawLine(offsetX, offsetY, offsetX+ (int)((double) endTimestampOrg * countPerUnit), offsetY);
        g.fillOval(offsetX + 1 + (int)((double) endTimestampOrg * countPerUnit), offsetY-5, 10, 10);

        /* Draw markers and labels */
        for (int i=0, j=0; i<= endTimestampOrg; i+= unitPerPeriod, j++)
        {
            g.drawLine(offsetX+(int)(i*countPerUnit), offsetY+ TIMELINE_MARKER_LENGTH /2, offsetX+(int)(i*countPerUnit), offsetY- TIMELINE_MARKER_LENGTH /2);

            if (displayMarkerLabels == true) {
                String timeString = nsToShortString((long)j*(long)periodInNs);
                g.setFont(new Font("TimesRoman", Font.PLAIN, 14));

                /* Find the center point of the string to be drawn. */
                FontMetrics fm = g.getFontMetrics();
                int stringWidth = fm.stringWidth(timeString);

                if (displayMarkerLabelsInNorth == true) {
                    // Display in north.
                    g.drawString(timeString, offsetX + (int)(i*countPerUnit) - stringWidth / 2, offsetY - 5);
                }
                else {
                    // Display in south.
                    g.drawString(timeString, offsetX + (int)(i*countPerUnit) - stringWidth / 2, offsetY + 20);
                }
            }
        }
    }

    public void setEndTimestampOrg(int inEndTimestampeNs)
    {
        endTimestampOrg = inEndTimestampeNs;
    }
    public void setDisplayMarkerLabels(boolean inDisplay)
    {
        displayMarkerLabels = inDisplay;
    }

    public void setDisplayMarkerLabelsInNorth(boolean inDisplay)
    {
        displayMarkerLabelsInNorth = inDisplay;
    }

    protected String nsToShortString(long inValue)
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

    int getEndTimestampOrg()
    {
        return endTimestampOrg;
    }
    double getCountPerUnit()
    {
        return countPerUnit;
    }
    int getPeriodInNs()
    {
        return periodInNs;
    }
}
