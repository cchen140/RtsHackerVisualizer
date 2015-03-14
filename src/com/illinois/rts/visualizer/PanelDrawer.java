package com.illinois.rts.visualizer;

import com.illinois.premsim.gui.ZoomablePanel;

import java.awt.*;

/**
 * Created by CY on 2/12/2015.
 */
public class PanelDrawer extends ZoomablePanel {
    public boolean doNotDraw = false;
    public EventContainer eventContainer = new EventContainer();

    private SchedulerEventVirtualDrawPanelGroup schedulerEventsDrawPanel = null;
    private VirtualDrawPanelGroup hackerEventsDrawPanel = null;


    public PanelDrawer()
    {
        super();
    }

   // @Override
    protected void draw(Graphics2D g) {

        if (doNotDraw == false) {
            //g.scale(1, 1);
            //g.translate(100, -75);
            //eventContainer.drawVerticalCenter(g, this.getHeight());//draw(g);
            //System.out.println(this.getHeight());

            schedulerEventsDrawPanel.draw(g, ProgConfig.PANEL_DRAWER_PADDING_X, ProgConfig.PANEL_DRAWER_PADDING_Y, 1, 1);
            this.setPreferredSize(new Dimension(
                    schedulerEventsDrawPanel.getWidth()+ProgConfig.PANEL_DRAWER_PADDING_X*2,
                    schedulerEventsDrawPanel.getHeight()+ProgConfig.PANEL_DRAWER_PADDING_Y*2));
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
        //scheduleEventContainer.clearAll();
        eventContainer = inputEventContainer;
        schedulerEventsDrawPanel = new SchedulerEventVirtualDrawPanelGroup(eventContainer);
        repaint();
    }

/*TODO: default point? movng to center of the screen should be done by panel.*/
}
