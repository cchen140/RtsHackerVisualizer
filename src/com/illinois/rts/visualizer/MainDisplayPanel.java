package com.illinois.rts.visualizer;

import javax.swing.*;
import java.awt.*;

/**
 * MainDisplayPanel which extends JPanel is where the magic happens.
 * All information about traces, tasks, and events is stored and managed in this class.
 */
public class MainDisplayPanel extends JPanel {
    public boolean doNotDraw = false;
    public EventContainer eventContainer = new EventContainer();

    private TraceGroupContainer traceGroupContainer = new TraceGroupContainer();

    public TraceGroupContainer getTraceGroupContainer() {
        return traceGroupContainer;
    }

//    private CombinedTraceGroup combinedTraceGroup = null;
//    private TraceGroup hackerEventsDrawPanel = null;

    private TimeLine topTimeLine = new TimeLine();

    private TraceHeadersPanel traceHeadersPanel = null;
    private JScrollBar horizontalScrollBar = null;
    private TimeLinePanel timeLinePanel = null;


    public MainDisplayPanel()
    {
        super();
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

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2D = (Graphics2D) g;

        // If nothing is initialized, then do nothing.
        if (eventContainer == null)
            return;

        if (doNotDraw == false)
        {
            traceGroupContainer.draw(g2D, ProgConfig.MAIN_DISPLAY_PANEL_PADDING_X, ProgConfig.MAIN_DISPLAY_PANEL_PADDING_Y);
//            combinedTraceGroup.draw(g2D, ProgConfig.MAIN_DISPLAY_PANEL_PADDING_X, ProgConfig.MAIN_DISPLAY_PANEL_PADDING_Y);
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

    /**
     * Set event container and initialize the display (reset traceGroupContainer).
     * @param inputEventContainer
     */
    public void setEventContainer(EventContainer inputEventContainer)
    {
        eventContainer = inputEventContainer;
        topTimeLine.setEndTimestampNs(eventContainer.getOrgEndTimestampNs());

        /* Update trace group container. */
        traceGroupContainer.clear();

        // By default the combined trace group is initialized.
        traceGroupContainer.addTraceGroup(new CombinedTraceGroup(eventContainer, new TimeLine(topTimeLine)));

        applyNewSettings(); // This will also update the scaledBeginTimestamp in each event.

    }

    public void applyNewSettings()
    {
        /* Change global variables. */
        DrawInterval.intervalHeight = ProgConfig.TRACE_HEIGHT;

//        combinedTraceGroup = new CombinedTraceGroup(eventContainer, new TimeLine(topTimeLine));
        traceGroupContainer.triggerUpdate();

        /* Setup virtual drawing panel */
        traceGroupContainer.setMargin(ProgConfig.VIRTUAL_PANEL_MARGIN_X, ProgConfig.VIRTUAL_PANEL_MARGIN_Y);
//        combinedTraceGroup.setMarginX(ProgConfig.VIRTUAL_PANEL_MARGIN_X);
//        combinedTraceGroup.setMarginY(ProgConfig.VIRTUAL_PANEL_MARGIN_Y);
////            combinedTraceGroup.setScaleX(ProgConfig.TRACE_HORIZONTAL_SCALE_DIVIDER);

        traceGroupContainer.setTraceMarginY(ProgConfig.TRACE_MARGIN_Y);
//        combinedTraceGroup.updateTraceMarginY(ProgConfig.TRACE_MARGIN_Y);

//        eventContainer.applyHorizontalScale(ProgConfig.TRACE_HORIZONTAL_SCALE_DIVIDER);
        traceGroupContainer.applyHorizontalScale(ProgConfig.TRACE_HORIZONTAL_SCALE_DIVIDER);

        /* Update time line. */
//        topTimeLine.setTimeValues(eventContainer.getOrgEndTimestampNs(), ProgConfig.TRACE_HORIZONTAL_SCALE_DIVIDER, ProgConfig.TIME_LINE_PERIOD_NS);
        topTimeLine.setTimeValues(traceGroupContainer.findOrgEndTimeStamp(), ProgConfig.TRACE_HORIZONTAL_SCALE_DIVIDER, ProgConfig.TIME_LINE_PERIOD_NS);
        traceGroupContainer.setTimeLine(topTimeLine);
        timeLinePanel.getTimeLine().copyTimeValues(topTimeLine);

        /* Set panel dimension according to the content to be drawn. */
        this.setPreferredSize(new Dimension(
                traceGroupContainer.getWidth()+ProgConfig.MAIN_DISPLAY_PANEL_PADDING_X *2,
                traceGroupContainer.getHeight()+ProgConfig.MAIN_DISPLAY_PANEL_PADDING_Y *2));

        /* Set scroll panel height for enabling vertical scroll bar. */
        this.getParent().getParent().setPreferredSize(new Dimension(
                -1,//this.getParent().getParent().getWidth(),
                traceGroupContainer.getHeight() + ProgConfig.MAIN_DISPLAY_PANEL_PADDING_Y * 2));

        // Is time line panel initialized?
        if (timeLinePanel != null) {
            /* Set the width of time line panel */
            timeLinePanel.setPreferredSize(new Dimension(
                    traceGroupContainer.getWidth() + ProgConfig.MAIN_DISPLAY_PANEL_PADDING_X * 2,
                    -1
            ));
            timeLinePanel.repaint();
        }

        // Is trace header panel initialized? (Should the headers be displayed and updated?)
        if (traceHeadersPanel != null)
        {// Update trace list.
//            traceHeadersPanel.setTrace(combinedTraceGroup.getTraceListArray());
//            traceHeadersPanel.setTrace(traceGroupContainer.getAllTraces());
            traceHeadersPanel.setTraceGroupContainer(traceGroupContainer);
            traceHeadersPanel.setBackground(ProgConfig.TRACE_PANEL_FOREGROUND);
        }

        /* Scroll zPanelScrollHorizontal panel according to zPanelScrollBarHorizontal */
        if (horizontalScrollBar != null) {
            horizontalScrollBar.setMaximum(traceGroupContainer.getWidth() + ProgConfig.MAIN_DISPLAY_PANEL_PADDING_X * 2);
            horizontalScrollBar.setUnitIncrement(1);
        }

        repaint();
    }

}
