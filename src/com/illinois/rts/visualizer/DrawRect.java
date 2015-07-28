package com.illinois.rts.visualizer;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created by CY on 2/16/2015.
 */
public class DrawRect extends DrawUnit {
    private int width = 0;
    private int height = 0;
    private Boolean labelVisible = false;

    private Boolean fillWithTexture = false;

    @Override
    public void draw(Graphics2D g) {

        if (fillWithTexture == true)
        {
            BufferedImage bi = new BufferedImage(5, 5, BufferedImage.TYPE_INT_RGB);
            Graphics2D big = bi.createGraphics();
            big.setColor(fillColor);
            big.fillRect(0, 0, 5, 5);
            big.setColor(Color.white);
            big.fillOval(0, 0, 5, 5);
            g.setPaint(new TexturePaint(bi, new Rectangle(0, 0, 5, 5)));
        }
        else
        {
            g.setColor(fillColor);
        }

        g.fillRect(offsetX, offsetY, width, height);
        g.setColor(edgeColor);
        g.drawRect(offsetX, offsetY, width, height);

        if (labelVisible == true)
        {
            g.drawString(label, offsetX, offsetY + height / 2);
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
    public int getHeight() { return height; }
    public void setSize(int inputWidth, int inputHeight)
    {
        width = inputWidth;
        height = inputHeight;
    }
    public void setLabelVisible(Boolean isVisible)
    {
        labelVisible = isVisible;
    }

    public void setFillWithTexture(Boolean enableTexture)
    {
        fillWithTexture = enableTexture;
    }
}
