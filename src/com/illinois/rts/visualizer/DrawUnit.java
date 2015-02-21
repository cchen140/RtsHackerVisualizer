package com.illinois.rts.visualizer;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;

/**
 * Created by CY on 2/16/2015.
 */
public abstract class DrawUnit {
    protected String label;
    protected Color labelColor;
    protected Color fillColor;
    protected Color edgeColor;
    protected int offsetX = 0;
    protected int offsetY = 0;

    private FontRenderContext context;
    private Font font;

    public DrawUnit()
    {
        label = "";
        labelColor = Color.black;
        fillColor = Color.white;
        edgeColor = Color.black;
    }

    public void setLabel(String inputLabel)
    {
        label = inputLabel.toString();
    }
    public String getLabel()
    {
        return label;
    }
    public void setlabelColor(Color inputColor)
    {
        labelColor = inputColor;
    }
    public Color getLabelColor()
    {
        return labelColor;
    }

    public void setFillColor(Color inputColor)
    {
        fillColor = inputColor;
    }
    public Color getFillColor()
    {
        return fillColor;
    }

    public void setEdgeColor(Color inputColor)
    {
        edgeColor = inputColor;
    }
    public Color getEdgeColor()
    {
        return edgeColor;
    }

    public void setOffset(int inOffsetX, int inOffsetY)
    {
        offsetX = inOffsetX;
        offsetY = inOffsetY;
    }

    public void draw(Graphics2D g, int inOffsetX, int inOffsetY)
    {
        setOffset(inOffsetX, inOffsetY);
        this.draw(g);
    }

    protected abstract void draw(Graphics2D g);

//    private Rectangle2D getLabelBounds()
//    {
//        return font.getStringBounds(label, context);
//    }
//
//    protected double getLabelWidth()
//    {
//        Rectangle2D bounds = getLabelBounds();
//        return bounds.getWidth();
//    }
}
