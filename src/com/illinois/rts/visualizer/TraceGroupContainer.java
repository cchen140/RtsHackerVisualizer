package com.illinois.rts.visualizer;

import org.omg.CORBA.PRIVATE_MEMBER;

import java.awt.*;
import java.util.ArrayList;

/**
 * Created by CY on 6/24/2015.
 */
public class TraceGroupContainer {
    private ArrayList<TraceGroup> traceGroups = new ArrayList<TraceGroup>();
    private TimeLine timeLine = null;   // Note that the timeLine object in each trace should be independent.

    public TraceGroupContainer() {
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
        for (TraceGroup thisTraceGroup : traceGroups)
        {
            thisTraceGroup.draw(g, offsetX, offsetY);
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
        int maxHeight = 0;
        for (TraceGroup thisTraceGroup : traceGroups)
        {
            int thisHeight = thisTraceGroup.getHeight();
            maxHeight = (thisHeight > maxHeight) ? thisHeight : maxHeight;
        }
        return maxHeight;
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
}
