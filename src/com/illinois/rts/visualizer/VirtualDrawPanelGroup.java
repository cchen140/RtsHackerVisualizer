package com.illinois.rts.visualizer;

import java.awt.*;

/**
 * Created by CY on 3/13/2015.
 */
public abstract class VirtualDrawPanelGroup {

    public abstract void draw(Graphics2D g, int offsetX, int offsetY, double scaleX, double scaleY);
    public abstract int getHeight();
    public int getHeight(double inScaleY)
    {
        return (int) (getHeight()*inScaleY);
    }
    public abstract int getWidth();
    public int getWidth(double inScaleX)
    {
        return (int) (getWidth()*inScaleX);
    }
}
