package com.illinois.rts.visualizer;

import com.illinois.premsim.gui.ZoomablePanel;

import java.awt.*;

/**
 * Created by CY on 2/12/2015.
 */
public class PanelDrawer extends ZoomablePanel {
    public boolean doNotDraw = false;
    public EventContainer eventContainer = new EventContainer();

    public PanelDrawer()
    {
        super();
    }

   // @Override
    protected void draw(Graphics2D g) {

        if (doNotDraw == false) {
            //g.scale(1, 1);
            //g.translate(100, -75);
            eventContainer.drawVerticalCenter(g, this.getHeight());//draw(g);
            //System.out.println(this.getHeight());
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
        repaint();
    }

/*TODO: default point? movng to center of the screen should be done by panel.*/
}
