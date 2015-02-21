package com.illinois.rts.visualizer;

import java.awt.*;

/**
 * Created by CY on 2/16/2015.
 */
public class DrawRect extends DrawUnit {
    private int width = 0;
    private int height = 0;
    private Boolean labelVisible = false;

    @Override
    public void draw(Graphics2D g) {
        g.setColor(fillColor);
        g.fillRect(offsetX, offsetY, width, height);
        g.setColor(edgeColor);
        g.drawRect(offsetX, offsetY, width, height);

        if (labelVisible == true)
        {
            g.drawString(label, offsetX, offsetY+height/2);
        }
    }

    public void setWidth(int inputWidth)
    {
        width = inputWidth;
    }
    public void setHeight(int inputHeight)
    {
        height = inputHeight;
    }
    public void setSize(int inputWidth, int inputHeight)
    {
        width = inputWidth;
        height = inputHeight;
    }
    public void setLabelVisible(Boolean isVisible)
    {
        labelVisible = isVisible;
    }
}
