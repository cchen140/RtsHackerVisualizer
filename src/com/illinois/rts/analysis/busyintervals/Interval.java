package com.illinois.rts.analysis.busyintervals;

import java.util.ArrayList;

/**
 * Created by CY on 7/13/2015.
 */
public class Interval {
    private int begin;
    private int end;

    public Interval(int inBegin, int inEnd) {
        begin = inBegin;
        end = inEnd;
    }

    // Create a new Interval and duplicate the value from another existing Interval object.
    public Interval(Interval inInterval) {
        this(inInterval.getBegin(), inInterval.getEnd());
    }

    public int getBegin() {
        return begin;
    }

    public void setBegin(int begin) {
        this.begin = begin;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public int getLength() {
        return (end - begin);
    }

    /* Calculate intersection and return a new Interval object. */
    public Interval intersect(Interval inInterval)
    {
        Interval leftInterval, rightInterval;
        int resultBegin=0, resultEnd=0;

        // Check which one is on the left.
        if (begin <= inInterval.getBegin())
        {// Me is on the left
            leftInterval = this;
            rightInterval = inInterval;
        }
        else
        {
            leftInterval = inInterval;
            rightInterval = this;
        }

        /* Determine begin value. */
        if (leftInterval.getEnd() < rightInterval.getBegin())
        {
            // They have no intersection.
            return null;
        }
        else
        {
            resultBegin = rightInterval.getBegin();
        }

        /* Determine end value. */
        if (leftInterval.getEnd() < rightInterval.getEnd())
        {
            resultEnd = leftInterval.getEnd();
        }
        else
        {
            resultEnd = rightInterval.getEnd();
        }

        return new Interval(resultBegin, resultEnd);
    }

    public void shift(int inOffset)
    {
        begin += inOffset;
        end += inOffset;
    }

    public Boolean contains(int inPoint)
    {
        if ((begin <= inPoint)
                && (end >= inPoint))
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public Boolean within(Interval inLarger) {
        if ( (inLarger.begin<=begin) && (inLarger.end>=end) ) {
            return true;
        } else {
            return false;
        }
    }

    public ArrayList<Interval> union(Interval inInterval) {
        ArrayList<Interval> resultIntervals = new ArrayList<>();
        if (intersect(inInterval) != null) {
            // Is continuous.

            int earliestBegin = 0;
            int latestEnd = 0;

            // Find earliest begin time and latest end time.
            earliestBegin = inInterval.begin < begin ? inInterval.begin : begin;
            latestEnd = inInterval.end > end ? inInterval.end : end;

            resultIntervals.add(new Interval(earliestBegin, latestEnd));

        } else {
            resultIntervals.add(new Interval(this));
            resultIntervals.add(new Interval(inInterval));
        }

        return resultIntervals;
    }

}
