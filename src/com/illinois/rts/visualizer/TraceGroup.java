package com.illinois.rts.visualizer;

import com.sun.javafx.scene.layout.region.Margins;
import javafx.print.Printer;

import javax.swing.plaf.basic.BasicBorders;
import java.awt.*;

/**
 * Created by CY on 3/13/2015.
 */
public abstract class TraceGroup {
    // scaleX Refer to how many nano seconds one drawing unit is since by default one drawing unit is one nano second.
//    protected int scaleX = 1;

    protected int marginX = 0;
    protected int marginY = 0;

//    public abstract void draw(Graphics2D g, int offsetX, int offsetY, double scaleX, double scaleY);
    public abstract void draw(Graphics2D g, int offsetX, int offsetY);
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
//    public void setScaleX(int inScaleX)
//    {
//        scaleX = inScaleX;
//    }
    public void setMarginX(int inMarginX)
    {
        marginX = inMarginX;
    }
    public void setMarginY(int inMarginY)
    {
        marginY = inMarginY;
    }
}
