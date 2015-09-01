package com.illinois.rts.visualizer;

import java.awt.*;
import java.util.ArrayList;

/**
 * Created by CY on 6/24/2015.
 */
public class TraceGroupContainer {

    private int scaledEndTimestamp = 0;
    private int orgEndTimestampNs = 0;

    private ArrayList<TraceGroup> traceGroups = new ArrayList<TraceGroup>();
    private TimeLine timeLine = null;   // Note that the timeLine object in each trace should be independent.

    public TraceGroupContainer() {
    }

    public ArrayList<TraceGroup> getTraceGroups() {
        return traceGroups;
    }

    public void addTraceGroup(TraceGroup inTraceGroup)
    {
        traceGroups.add(inTraceGroup);
    }

    public int getMaxLength()
    {
        int maxTraceLength = 0;
        for (TraceGroup thisTraceGroup : traceGroups)
        {
            int thisLength = thisTraceGroup.getWidth();
            maxTraceLength = (maxTraceLength > thisLength) ? maxTraceLength : thisLength;
        }
        return maxTraceLength;
    }

    public void draw(Graphics2D g, int offsetX, int offsetY) {
        int currentCursorY = offsetY;
        for (TraceGroup thisTraceGroup : traceGroups)
        {
            currentCursorY = thisTraceGroup.draw(g, offsetX, currentCursorY);
            currentCursorY += ProgConfig.TRACE_GROUP_MARGIN_Y;
        }
    }

    public void setMargin(int marginX, int marginY)
    {
        for (TraceGroup thisTraceGroup : traceGroups) {
            thisTraceGroup.setMarginX(marginX);
            thisTraceGroup.setMarginY(marginY);
        }
    }

    public void setTraceMarginY(int traceMarginY)
    {
        for (TraceGroup thisTraceGroup : traceGroups) {
            thisTraceGroup.updateTraceMarginY(traceMarginY);
        }
    }

    public void setTimeLine(TimeLine inTimeLine)
    {
        for (TraceGroup thisTraceGroup : traceGroups) {
            thisTraceGroup.copyTimeLineValues(inTimeLine);
        }
    }

    public void clear() {
        traceGroups.clear();
    }

    public int getWidth() {
        int maxWidth = 0;
        for (TraceGroup thisTraceGroup : traceGroups)
        {
            int thisWidth = thisTraceGroup.getWidth();
            maxWidth = (thisWidth > maxWidth) ? thisWidth : maxWidth;
        }
        return maxWidth;
    }

    public int getHeight() {
        int resultHeight = 0;
        Boolean firstTraceGroup = true;
        for (TraceGroup thisTraceGroup : traceGroups)
        {
            if (firstTraceGroup == true) {
                firstTraceGroup = false;
            } else {
                // There is a gap between each trace group.
                resultHeight += ProgConfig.TRACE_GROUP_MARGIN_Y;
            }
            resultHeight += thisTraceGroup.getHeight();
        }
        return resultHeight;
    }

    public ArrayList<Trace> getAllTraces()
    {
        ArrayList<Trace> resultTraces = new ArrayList<>();
        for (TraceGroup thisTraceGroup : traceGroups)
        {
            resultTraces.addAll(thisTraceGroup.getTraces());
        }
        return resultTraces;
    }

    public void triggerUpdate()
    {
        for (TraceGroup thisTraceGroup : traceGroups)
        {
            thisTraceGroup.triggerUpdate();
        }
    }

    public void applyHorizontalScale(int inScale)
    {
        for (TraceGroup thisTraceGroup : traceGroups)
        {
            thisTraceGroup.applyHorizontalScale(inScale);
        }
    }

    public int findOrgEndTimeStamp()
    {
        int resultEndTimeStamp = 0;
        for (TraceGroup thisTraceGroup : traceGroups)
        {
            int thisEndTimeStamp = thisTraceGroup.findOrgEndTimeStampNs();
            resultEndTimeStamp = (thisEndTimeStamp>resultEndTimeStamp) ? thisEndTimeStamp : resultEndTimeStamp;
        }
        return resultEndTimeStamp;
    }

    public int findScaledEndTimeStamp()
    {
        int resultScaledEndTimeStamp = 0;
        for (TraceGroup thisTraceGroup : traceGroups)
        {
            int thisScaledEndTimeStamp = thisTraceGroup.findScaledEndTimeStamp();
            resultScaledEndTimeStamp = (thisScaledEndTimeStamp>resultScaledEndTimeStamp) ? thisScaledEndTimeStamp : resultScaledEndTimeStamp;
        }
        return resultScaledEndTimeStamp;
    }

    public Trace findTraceByYPosition( int targetY )
    {
        int accuY = 0;

        /**/
        Boolean firstTraceGroup = true;
        for (TraceGroup thisGroup : traceGroups) {
            if (firstTraceGroup == true) {
                firstTraceGroup = false;
            } else {
                // There is a gap between each trace group.
                accuY += ProgConfig.TRACE_GROUP_MARGIN_Y;
            }

            accuY += thisGroup.getHeight();
            if (accuY >= targetY) {
                // Found the trace group that the target y is located!
                return thisGroup.findTraceByYPosition( targetY - (accuY-thisGroup.getHeight()));
            }
        }

        // If nothing is found, then return null.
        return null;
    }
}
