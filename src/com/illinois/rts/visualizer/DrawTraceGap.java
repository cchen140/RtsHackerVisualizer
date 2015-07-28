package com.illinois.rts.visualizer;

import java.awt.*;

/**
 * Created by CY on 3/22/2015.
 */
public class DrawTraceGap extends DrawUnit {
    private int length = 0;

    public DrawTraceGap(){}
    public DrawTraceGap(int inLength){length = inLength;}

    @Override
    protected void draw(Graphics2D g) {
        Stroke orgStroke = g.getStroke();
        Color orgColor = g.getColor();

        g.setColor(this.fillColor);
        g.setStroke(new BasicStroke(ProgConfig.TRACE_PANEL_BORDER_WIDTH));
        g.drawLine(offsetX, offsetY-ProgConfig.TRACE_PANEL_BORDER_WIDTH/2-1, offsetX+length, offsetY-ProgConfig.TRACE_PANEL_BORDER_WIDTH/2-1);

        g.setStroke(orgStroke);
        g.setColor(orgColor);
    }

    public void draw(Graphics2D g, int inOffsetX, int inOffsetY, int inLength)
    {
        length = inLength;
        this.draw(g, inOffsetX, inOffsetY);
    }

}
