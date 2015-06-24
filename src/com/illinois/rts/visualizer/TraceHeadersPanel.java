package com.illinois.rts.visualizer;

import com.illinois.rts.framework.Task;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * Created by CY on 6/24/2015.
 */
public class TraceHeadersPanel extends JPanel {
    ArrayList<Trace> traces = null;

    public TraceHeadersPanel(){}

    public void setTrace(ArrayList<Trace> inTraces)
    {
        traces = inTraces;
        this.repaint();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw right border.
        g.setColor(ProgConfig.TRACE_PANEL_BORDER_COLOR);
        g.drawLine(this.getWidth()-1, 0, this.getWidth()-1, this.getHeight());

        int paintingCursorY = 0;
        for (Trace thisTrace : traces)
        {
            int orgPaintingCursor = paintingCursorY;
            paintingCursorY += thisTrace.getTraceHeight() / 2;
            paintingCursorY = drawTraceNameAndIcon(g, ProgConfig.TRACE_HEADER_LEFT_MARGIN, paintingCursorY, thisTrace, true);

            if (thisTrace.getTask()!=null) {
                paintingCursorY += ProgConfig.TRACE_HEADER_TITLE_SUBTITLE_GAP;
                drawTaskAttributes(g, ProgConfig.TRACE_HEADER_LEFT_MARGIN, paintingCursorY, thisTrace.getTask());
            }

            // Move the painting cursor
            paintingCursorY = orgPaintingCursor + thisTrace.getTraceHeight();

            // Draw split line
            g.setColor(ProgConfig.TRACE_PANEL_BORDER_COLOR);
            g.drawLine(0, paintingCursorY - 1, this.getWidth(), paintingCursorY - 1);
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

}
