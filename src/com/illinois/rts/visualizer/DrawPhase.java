package com.illinois.rts.visualizer;

import java.awt.*;

/**
 * Created by CY on 2/16/2015.
 */
public class DrawPhase extends DrawRect {
    public static final int PHASE_HEIGHT = 150;
    private DrawRect phase2D;
    private int timeStamp;
    private double scaleY = 1.0;

    public DrawPhase()
    {
        super();
        setSize(50, PHASE_HEIGHT);
        //setHeight(30);
    }

    public void drawUnderLine(Graphics2D g, int offsetX, int offsetY)
    {
        setHeight(1);
        draw(g, offsetX, offsetY + PHASE_HEIGHT-1);
        setHeight(PHASE_HEIGHT);
    }

    public void setHeightScale(double intputScaleY)
    {
        scaleY = intputScaleY;
        setHeight((int) (PHASE_HEIGHT*intputScaleY));
    }

}
