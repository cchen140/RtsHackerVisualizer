package com.illinois.rts.visualizer;

import java.awt.*;

/**
 * Created by CY on 2/16/2015.
 */
public class DrawInterval extends DrawRect {
    //public static int intervalHeight = ProgConfig.TRACE_HEIGHT; // A default value should be assigned globally. i.e., ProgConfig.TRACE_HEIGHT.

    /* The following two variables are used when multiple layers are desired. */
    protected int numOfLayers = 1;
    protected int layer = 1;

    public DrawInterval() {
        super();
        //setHeight(intervalHeight);
        setHeight(ProgConfig.TRACE_HEIGHT);
    }

    public DrawInterval(int inNumOfLayers, int inLayer)
    {
        super();
        setLayerPosition(inNumOfLayers, inLayer);
    }

    @Override
    public void draw(Graphics2D g) {
        // Update the height every time so that the display can change in time when the configuration has changed.
        //setHeight(intervalHeight);
        updateHeightPositionByLayer();



        super.draw(g);
    }

//    public int getHeight() {
//        return intervalHeight;
//    }

    public void setLayerPosition(int inNumOfLayers, int inLayer)
    {
        numOfLayers = inNumOfLayers;
        layer = inLayer;

        updateHeightPositionByLayer();
    }

    public void updateHeightPositionByLayer() {
        setHeight(ProgConfig.TRACE_HEIGHT/numOfLayers);

        // Calculating Y-axis offset if multiple-layer is used.
        // offsetY remains the same if there is only one layer (single layer by default).
        offsetY -= (ProgConfig.TRACE_HEIGHT/numOfLayers)*(layer-1);
    }

}
