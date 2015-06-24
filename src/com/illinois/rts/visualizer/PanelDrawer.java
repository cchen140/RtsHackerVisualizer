package com.illinois.rts.visualizer;

import com.illinois.premsim.gui.ZoomablePanel;

import javax.swing.*;
import java.awt.*;

/**
 * Created by CY on 2/12/2015.
 */
public class PanelDrawer extends ZoomablePanel {
    public boolean doNotDraw = false;
    public EventContainer eventContainer = new EventContainer();

    private CombinedTraceGroup combinedTraceGroup = null;
    private TraceGroup hackerEventsDrawPanel = null;

    private TimeLine topTimeLine = new TimeLine();

    private TraceHeadersPanel traceHeadersPanel = null;
    private JScrollBar horizontalScrollBar = null;
    private TimeLinePanel timeLinePanel = null;


    public PanelDrawer()
    {
        super();
//        combinedTraceGroup = new CombinedTraceGroup(eventContainer, topTimeLine);
    }

    public void setTraceHeadersPanel(TraceHeadersPanel inTraceHeadersPanel)
    {
        traceHeadersPanel = inTraceHeadersPanel;
    }
    public void setTimeLinePanel(TimeLinePanel inTimeLinePanel)
    {
        timeLinePanel = inTimeLinePanel;
    }

    public void setHorizontalScrollBar(JScrollBar inScrollBar)
    {
        horizontalScrollBar = inScrollBar;
    }


   // @Override
    protected void draw(Graphics2D g) {

        // If nothing is initialized, then do nothing.
        if (eventContainer == null)
            return;

        if (doNotDraw == false)
        {
            combinedTraceGroup.draw(g, ProgConfig.PANEL_DRAWER_PADDING_X, ProgConfig.PANEL_DRAWER_PADDING_Y);
        }
    }

    public void toggleDoNotDraw()
    {
        if (doNotDraw == true)
        {
            doNotDraw = false;
        }
        else
        {
            doNotDraw = true;
        }
        repaint();
    }

    //@Override
    protected void postDraw(Graphics2D g)
    {

    }

//    @Override
//    protected void paintComponent(Graphics g)
//    {
//        super.paintComponents(g);
//        draw2((Graphics2D)g);
//
//
//    }

    public void setEventContainer(EventContainer inputEventContainer)
    {
        eventContainer = inputEventContainer;
        topTimeLine.setEndTimestampNs(eventContainer.getOrgEndTimestampNs());

        applyNewSettings(); // This will also update the scaledBeginTimestamp in each event.

    }

    public void applyNewSettings()
    {
        combinedTraceGroup = new CombinedTraceGroup(eventContainer, new TimeLine(topTimeLine));

        /* Setup virtual drawing panel */
        combinedTraceGroup.setMarginX(ProgConfig.VIRTUAL_PANEL_MARGIN_X);
        combinedTraceGroup.setMarginY(ProgConfig.VIRTUAL_PANEL_MARGIN_Y);
//            combinedTraceGroup.setScaleX(ProgConfig.TRACE_HORIZONTAL_SCALE_DIVIDER);

        combinedTraceGroup.updateTraceMarginY(ProgConfig.TRACE_MARGIN_Y);

        eventContainer.applyHorizontalScale(ProgConfig.TRACE_HORIZONTAL_SCALE_DIVIDER);

        /* Update time line. */
        topTimeLine.setTimeValues(eventContainer.getOrgEndTimestampNs(), ProgConfig.TRACE_HORIZONTAL_SCALE_DIVIDER, ProgConfig.TIME_LINE_PERIOD_NS);
        combinedTraceGroup.copyTimeLineValues(topTimeLine);
        timeLinePanel.getTimeLine().copyTimeValues(topTimeLine);

                    /* Set panel dimension according to the content to be drawn. */
        this.setPreferredSize(new Dimension(
                combinedTraceGroup.getWidth()+ProgConfig.PANEL_DRAWER_PADDING_X*2,
                combinedTraceGroup.getHeight()+ProgConfig.PANEL_DRAWER_PADDING_Y*2));

            /* Set scroll panel height for enabling vertical scroll bar. */
        this.getParent().getParent().setPreferredSize(new Dimension(
                -1,//this.getParent().getParent().getWidth(),
                combinedTraceGroup.getHeight() + ProgConfig.PANEL_DRAWER_PADDING_Y * 2));

        // Is time line panel initialized?
        if (timeLinePanel != null) {
            /* Set the width of time line panel */
            timeLinePanel.setPreferredSize(new Dimension(
                    combinedTraceGroup.getWidth() + ProgConfig.PANEL_DRAWER_PADDING_X * 2,
                    -1
            ));
            timeLinePanel.repaint();
        }

        // Is trace header panel initialized? (Should the headers be displayed and updated?)
        if (traceHeadersPanel != null)
        {// Update trace list.
            traceHeadersPanel.setTrace(combinedTraceGroup.getTraceListArray());
            traceHeadersPanel.setBackground(ProgConfig.TRACE_PANEL_FOREGROUND);
        }

        /* Scroll zPanelScrollHorizontal panel according to zPanelScrollBarHorizontal */
        if (horizontalScrollBar != null) {
            horizontalScrollBar.setMaximum(combinedTraceGroup.getWidth() + ProgConfig.PANEL_DRAWER_PADDING_X * 2);
            horizontalScrollBar.setUnitIncrement(1);
        }

        repaint();
    }

/*TODO: default point? movng to center of the screen should be done by panel.*/
}
