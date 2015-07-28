package com.illinois.rts.visualizer;

import com.illinois.rts.framework.Task;
import com.sun.org.apache.xpath.internal.operations.Bool;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * Created by CY on 6/24/2015.
 */
public class TraceHeadersPanel extends JPanel {
    //    ArrayList<Trace> traces = null;
    TraceGroupContainer traceGroupContainer = null;

    public TraceHeadersPanel(){
        super();
    }

//    public void setTrace(ArrayList<Trace> inTraces)
//    {
//        traces = inTraces;
//        this.repaint();
//    }

    public void setTraceGroupContainer(TraceGroupContainer traceGroupContainer) {
        this.traceGroupContainer = traceGroupContainer;
        this.repaint();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw right border.
        g.setColor(ProgConfig.TRACE_PANEL_BORDER_COLOR);
        g.drawLine(this.getWidth()-1, 0, this.getWidth()-1, this.getHeight());

        int paintingCursorY = 0;
        Boolean firstGroup = true;
        for (TraceGroup thisTraceGroup : traceGroupContainer.getTraceGroups()) {
            if (firstGroup == true)
            {
                firstGroup = false;
            } else {
                /* Fill the gap between two trace groups. */
                g.setColor(ProgConfig.TRACE_PANEL_BACKGROUND);
                g.fillRect(0, paintingCursorY, this.getWidth(), ProgConfig.TRACE_GROUP_MARGIN_Y);

                /* Draw upper and lower border of the filled gap. */
                g.setColor(ProgConfig.TRACE_PANEL_BACKGROUND_BORDER);
                g.drawLine(0, paintingCursorY, this.getWidth(), paintingCursorY);
                paintingCursorY += ProgConfig.TRACE_GROUP_MARGIN_Y;
                g.drawLine(0, paintingCursorY-1, this.getWidth(), paintingCursorY-1);

                // Draw split line
                g.setColor(ProgConfig.TRACE_PANEL_BORDER_COLOR);
                g.drawLine(0, paintingCursorY - 1, this.getWidth(), paintingCursorY - 1);
            }

            int topPaintingPointOfThisTraceGroupY = paintingCursorY;
            for (Trace thisTrace : thisTraceGroup.getTraces()) {
                if (thisTrace.getDoNotShow() == true)
                    continue;

                int orgPaintingCursor = paintingCursorY;
                paintingCursorY += thisTrace.getTraceHeight() / 2;
                paintingCursorY = drawTraceNameAndIcon(g, ProgConfig.TRACE_HEADER_LEFT_MARGIN, paintingCursorY, thisTrace, true);

                if (thisTrace.getTask() != null && thisTrace.getTraceType() == Trace.TRACE_TYPE_TASK) {
                    paintingCursorY += ProgConfig.TRACE_HEADER_TITLE_SUBTITLE_GAP;
                    drawTaskAttributes(g, ProgConfig.TRACE_HEADER_LEFT_MARGIN, paintingCursorY, thisTrace.getTask());
                }

                // Move the painting cursor
                paintingCursorY = orgPaintingCursor + thisTrace.getTraceHeight();

                // Draw split line
                g.setColor(ProgConfig.TRACE_PANEL_BORDER_COLOR);
                g.drawLine(0, paintingCursorY - 1, this.getWidth(), paintingCursorY - 1);
            }
            drawTraceGroupHeader(g, 0, topPaintingPointOfThisTraceGroupY, thisTraceGroup);
        }
    }

    /**
     * This method draw the trace name on right along with a colored icon to left for a given trace.
     * @param g graphic object from root JPanel
     * @param x initial x axis position
     * @param y initial y axis position
     * @param trace the trace that the name and icon are to be shown
     * @param vCenterAligned center aligned on the initial y axis line or start drawing from the given y position anyway
     * @return the final y axis position after drawing objects
     */
    private int drawTraceNameAndIcon(Graphics g, int x, int y, Trace trace, Boolean vCenterAligned)
    {
        TaskListColorIcon colorIcon = null;
        if (trace.getTask() == null)
        {
            colorIcon = new TaskListColorIcon(Color.WHITE);
        }
        else {
            colorIcon = new TaskListColorIcon(trace.getTask().getTaskColor(), trace.getTask().isDisplayBoxChecked(), trace.getTask().getSymbol());
        }

        // Set the font for the header string first in order to calculate the height.
        g.setColor(ProgConfig.TRACE_HEADER_TITLE_COLOR);
        g.setFont(ProgConfig.TRACE_HEADER_TITLE_FONT);

        /* Before starting to paint, check where the icon and header string should align. */
        int centerLineY = 0;

        if (vCenterAligned == true)
            centerLineY = y;
        else {
            /* Who is higher? colorIcon or the header name? */
            /* The higher one determines the y axis position in alignment. */
            if (colorIcon.getIconHeight() > g.getFontMetrics().getHeight())
            {
                centerLineY = y + colorIcon.getIconHeight()/2;
            }
            else {
                centerLineY = y + g.getFontMetrics().getHeight()/2;
            }
        }

        // Draw icon.
        colorIcon.paintIcon(null, g, x, centerLineY - colorIcon.getIconHeight() / 2);

        // Draw header string (trace name).
        g.drawString(trace.getName(),   // string
                     x+colorIcon.getIconWidth()+ProgConfig.TRACE_HEADER_ICON_TITLE_GAP, // x axis position
                     centerLineY+g.getFontMetrics().getHeight()/2-2);   // y axis position


        /* Calculate the final position of the painting cursor. */
        if (colorIcon.getIconHeight() > g.getFontMetrics().getHeight())
        {
            return (centerLineY + colorIcon.getIconHeight()/2);
        }
        else {
            return (centerLineY + g.getFontMetrics().getHeight()/2);
        }
    }

    private int drawTaskAttributes(Graphics g, int x, int y, Task inTask)
    {
        int paintingCursorY = y;
        g.setColor(ProgConfig.TRACE_HEADER_SUBTITLE_COLOR);
        g.setFont(ProgConfig.TRACE_HEADER_SUBTITLE_FONT);

        g.drawString("Period:   "+inTask.getPeriodNs()/1000000.0+"ms", x , paintingCursorY+g.getFontMetrics().getHeight()/2-2);
        paintingCursorY += g.getFontMetrics().getHeight();
        g.drawString("Exe Time: "+inTask.getComputationTimeNs()/1000000.0+"ms", x , paintingCursorY+g.getFontMetrics().getHeight()/2-2);
        paintingCursorY += g.getFontMetrics().getHeight();

        return paintingCursorY;
    }

    private void drawTraceGroupHeader(Graphics g, int x, int y, TraceGroup inTraceGroup)
    {
        int headHeight = ProgConfig.TRACE_HEADER_GROUP_HEAD_HEIGHT;
        int leftBarWidth = headHeight/2;

        /* Draw trace group head background*/
        g.setColor(ProgConfig.TRACE_HEADER_GROUP_HEAD_BACKGROUND);
        g.fillArc( x+this.getWidth()-headHeight*2, y-headHeight, headHeight*2, headHeight*2, 270, 90);
        g.fillRect( x, y, x+this.getWidth()-headHeight, headHeight);

        /* Draw left side bar. */
        g.fillRect(x, y, leftBarWidth, inTraceGroup.getHeight());

        /* Draw trace group title */
        g.setColor(ProgConfig.TRACE_HEADER_GROUP_HEAD_TITLE_COLOR);
        g.setFont(ProgConfig.TRACE_HEADER_TITLE_FONT);
        g.drawString(inTraceGroup.title, x+ProgConfig.TRACE_HEADER_LEFT_MARGIN, y+20);
    }

}
