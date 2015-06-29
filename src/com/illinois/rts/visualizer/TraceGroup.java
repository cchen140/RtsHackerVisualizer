package com.illinois.rts.visualizer;

import java.awt.*;
import java.util.ArrayList;

/**
 * Created by CY on 3/13/2015.
 */
public class TraceGroup {
    // scaleX Refer to how many nano seconds one drawing unit is since by default one drawing unit is one nano second.
//    protected int scaleX = 1;
    protected String title = "";

    protected int width = 0;
    protected int height = 0;

    protected int marginX = 0;
    protected int marginY = 0;

    protected int traceMarginY = 0;

    protected DrawRect background = new DrawRect();
    protected DrawTraceGap traceGap = new DrawTraceGap();

    protected TimeLine timeLine = new TimeLine();

    public ArrayList<Trace> getTraces() {
        return traces;
    }

    protected ArrayList<Trace> traces = new ArrayList<Trace>();

    public TraceGroup()
    {
        background.setFillColor(ProgConfig.TRACE_PANEL_FOREGROUND);
        background.setEdgeColor(ProgConfig.TRACE_PANEL_BACKGROUND_BORDER);
    }

    public void AddTrace(Trace inTrace)
    {
        traces.add(inTrace);
    }

    public void setTitle(String title) {
        this.title = title;
    }

    // Override if necessary.
    public void triggerUpdate(){}

    public int draw(Graphics2D g, int offsetX, int offsetY)
    {

        int currentOffsetX = offsetX;
        int currentOffsetY = offsetY;

        // Draw background
        width = calculateWidth();
        height = calculateHeight();
        drawBackground(g, offsetX, offsetY);

        // Make some boarder space.
        currentOffsetY += marginY;
        currentOffsetX += marginX;

        // Draw individuals

        for (Trace currentTrace : traces)
        {
            if (currentTrace.getDoNotShow() == true)
                continue;

            // Draw trace
            currentOffsetY = currentTrace.Draw(g, currentOffsetX, currentOffsetY);

            // Draw trace border
            traceGap.draw(g, 0, currentOffsetY, width+currentOffsetX);
        }

        // Return the last painting cursor position.,
        return currentOffsetY;
    }

    public int getHeight() {
        return calculateHeight();
    }
    public int getHeight(double inScaleY)
    {
        return (int) (getHeight()*inScaleY);
    }

    public int getWidth() {
        return calculateWidth();
    }
    public int getWidth(double inScaleX)
    {
        return (int) (getWidth()*inScaleX);
    }

//    public void setScaleX(int inScaleX)
//    {
//        scaleX = inScaleX;
//    }
    public void setMarginX(int inMarginX)
    {
        marginX = inMarginX;
    }
    public void setMarginY(int inMarginY)
    {
        marginY = inMarginY;
    }

    public void updateTraceMarginY(int inTraceMarginY) {
        traceMarginY = inTraceMarginY;

        // Update margin for each task trace.
        for (Trace thisTrace : traces)
        {
            thisTrace.updateMarginY(traceMarginY);
        }
    }

    protected void drawBackground(Graphics2D g, int offsetX, int offsetY)
    {
//        width = calculateWidth();
//        height = calculateHeight();

//        background.setSize(width+1, height+5);
//        background.draw(g, offsetX-1, offsetY-5);
        background.setSize(width+1, height+1);
        background.draw(g, offsetX-1, offsetY-1);
    }

    // Width should be calculated through scaled time stamps.
    protected int calculateWidth()
    {
        int resultWidth = 0;
        int maxScaledEndTimeStamp = 0;

        resultWidth += marginX*2;    // Left and right borders.

        for (Trace thisTrace : traces)
        {
            if (thisTrace.getDoNotShow() == true)
                continue;

            int thisScaledEndTimeStampNs = thisTrace.findScaledEndTimestamp();//findOrgEndTimestampNs();
            if (thisScaledEndTimeStampNs > maxScaledEndTimeStamp)
                maxScaledEndTimeStamp = thisScaledEndTimeStampNs;
        }
        resultWidth += maxScaledEndTimeStamp;
        return resultWidth;
    }

    protected int calculateHeight()
    {
        int resultHeight = 0;

        resultHeight += ProgConfig.VIRTUAL_PANEL_MARGIN_Y *2;   // Upper and lower borders.

        for (Trace thisTrace : traces)
        {
            if (thisTrace.getDoNotShow() == true)
                continue;

            resultHeight += thisTrace.getTraceHeight();
        }

        return resultHeight;
    }

    public void copyTimeLineValues(TimeLine inTimeLine)
    {
        timeLine.copyTimeValues(inTimeLine);

        // Update time line settings for traces.
        for (Trace currentTrace : traces)
        {
            currentTrace.getTimeLine().copyTimeValues(inTimeLine);
        }
    }

    public void applyHorizontalScale(int inScale)
    {
        for (Trace currentTrace : traces)
        {
            currentTrace.applyHorizontalScale(inScale);
        }
    }

    public int findOrgEndTimeStampNs()
    {
        int resultEndTimestampNs = 0;
        for (Trace thisTrace : traces)
        {
            if (thisTrace.findOrgEndTimestampNs() > resultEndTimestampNs)
            {
                resultEndTimestampNs = thisTrace.findOrgEndTimestampNs();
            }
        }
        return resultEndTimestampNs;
    }

    public int findScaledEndTimeStamp()
    {
        int resultScaledEndTimeStamp = 0;
        for (Trace thisTrace : traces)
        {
            int thisScaledEndTimeStamp = thisTrace.findScaledEndTimestamp();
            resultScaledEndTimeStamp = (thisScaledEndTimeStamp>resultScaledEndTimeStamp) ? thisScaledEndTimeStamp : resultScaledEndTimeStamp;
        }
        return resultScaledEndTimeStamp;
    }
}
