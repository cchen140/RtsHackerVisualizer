package com.illinois.rts.visualizer;

import java.awt.*;

/**
 * Created by CY on 2/16/2015.
 */
public class DrawPhase extends DrawRect {
    private int timeStamp;
    private double scaleY = 1.0;

    public DrawPhase()
    {
        super();
        setSize(50, ProgConfig.TRACE_HEIGHT);
        //setHeight(30);
    }

    public void drawUnderLine(Graphics2D g, int offsetX, int offsetY)
    {
        setHeight(1);
        draw(g, offsetX, offsetY + ProgConfig.TRACE_HEIGHT-1);
        setHeight(ProgConfig.TRACE_HEIGHT);
    }

    public void setHeightScale(double intputScaleY)
    {
        scaleY = intputScaleY;
        setHeight((int) (ProgConfig.TRACE_HEIGHT*intputScaleY));
    }

}
