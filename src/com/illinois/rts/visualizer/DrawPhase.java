package com.illinois.rts.visualizer;

import java.awt.*;

/**
 * Created by CY on 2/16/2015.
 */
public class DrawPhase extends DrawRect {

    public DrawPhase()
    {
        super();
        setHeight(ProgConfig.TRACE_HEIGHT);
    }

    @Override
    public void draw(Graphics2D g) {
        setHeight(ProgConfig.TRACE_HEIGHT);
        super.draw(g);
    }

    public int getHeight() {
        setHeight(ProgConfig.TRACE_HEIGHT);
        return ProgConfig.TRACE_HEIGHT;
    }

}
