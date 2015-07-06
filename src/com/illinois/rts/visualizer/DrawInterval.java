package com.illinois.rts.visualizer;

import java.awt.*;

/**
 * Created by CY on 2/16/2015.
 */
public class DrawInterval extends DrawRect {

    /* The following two variables are used when multiple layers are desired. */
    protected int numOfLayers = 1;
    protected int layer = 1;

    public DrawInterval() {
        super();
        setHeight(ProgConfig.TRACE_HEIGHT);

    }

    public DrawInterval(int inNumOfLayers, int inLayer)
    {
        super();
        setLayerPosition(inNumOfLayers, inLayer);
    }

    @Override
    public void draw(Graphics2D g) {
        // Calculating Y-axis offset if multiple-layer is used.
        // offsetY remains the same if there is only one layer (single layer by default).
        offsetY += (ProgConfig.TRACE_HEIGHT/numOfLayers)*(layer-1);

        super.draw(g);
    }

    public int getHeight() {
        // This returns the default interval height at all times (even when multiple layers are being used).
        return ProgConfig.TRACE_HEIGHT;
    }

    public void setLayerPosition(int inNumOfLayers, int inLayer)
    {
        numOfLayers = inNumOfLayers;
        layer = inLayer;
        setHeight(ProgConfig.TRACE_HEIGHT/numOfLayers);
    }

}
