package com.illinois.rts.visualizer;

/**
 * Created by CY on 3/29/2015.
 */
public class TraceSpace {
    private int northHeight = 0;
    private int southHeight = 0;

    public TraceSpace(int inNorthHeight, int inSouthHeight)
    {
        setSpace(inNorthHeight, inSouthHeight);
    }

    public int getNorthHeight()
    {
        return northHeight;
    }

    public int getSouthHeight()
    {
        return southHeight;
    }

    void extendSpace(TraceSpace inTraceSpace)
    {
        if (inTraceSpace.getNorthHeight() > northHeight)
        {
            northHeight = inTraceSpace.getNorthHeight();
        }

        if (inTraceSpace.getSouthHeight() > southHeight)
        {
            southHeight = inTraceSpace.getSouthHeight();
        }
    }

    public int getHeight()
    {
        return northHeight + southHeight;
    }

    public void copySpace(TraceSpace inTraceSpace)
    {
        setSpace(inTraceSpace.getNorthHeight(), inTraceSpace.getSouthHeight());
    }

    public void setSpace(int inNorthHeight, int inSouthHeight)
    {
        northHeight = inNorthHeight;
        southHeight = inSouthHeight;
    }
}
