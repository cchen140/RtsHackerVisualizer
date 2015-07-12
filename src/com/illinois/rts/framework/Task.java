package com.illinois.rts.framework;

import java.awt.*;
import java.util.LinkedList;

/**
 * Created by CY on 2/17/2015.
 */
public class Task {
    public static int TASK_TYPE_UNKNOWN = 0;
    public static int TASK_TYPE_SYS = 1;
    public static int TASK_TYPE_APP = 2;
    public static int TASK_TYPE_IDLE = 3;
    public static int TASK_TYPE_HACK = 4;

    private String title = "";
    private String symbol = ""; // Mainly for representing high/low hacker with symbol "H"/"L" in the icon.
    private Color color = Color.black;
    private Boolean displayBoxChecked = true;
    protected int id = 0;

    private int taskType = TASK_TYPE_UNKNOWN;

    protected int periodNs = 0;

    protected int computationTimeNs = 0;
    protected int computationTimeErrorNs = 500000;  // The error should be positive.

    protected int priority = 0;
    protected int deadlineNs = 0;

    public Task(){}

    public Task(String inTitle)
    {
        this();
        title = inTitle;
    }

    public Task(String inTitle, Color inColor)
    {
        this(inTitle);
        color = inColor;
    }

    public Task(int inTaskId, String inTitle, Color inColor)
    {
        this(inTitle, inColor);
        id = inTaskId;
    }

    public Task(int inTaskId, String inTitle, Color inColor, int inType, int inPeriod, int inComputationTime, int inPriority)
    {
        this(inTaskId, inTitle, inColor);
        taskType = inType;
        periodNs = inPeriod;
        computationTimeNs = inComputationTime;
        priority = inPriority;
    }

    public int getId()
    {
        return id;
    }

    public int getComputationTimeNs() {
        return computationTimeNs;
    }

    public void setSymbol(String inSymbol)
    {
        symbol = inSymbol;
    }
    public String getSymbol()
    {
        return symbol;
    }

    public int getPeriodNs() {
        return periodNs;
    }

    public void setColor(Color inputColor)
    {
        color = inputColor;
    }
    public Color getTaskColor()
    {
        return color;
    }

    public void setTitle(String inTitle)
    {
        title = inTitle;
    }
    public String getTitle()
    {
        return title;
    }

    public void setDisplayBoxChecked(Boolean inputChecked)
    {
        displayBoxChecked = inputChecked;
    }
    public Boolean isDisplayBoxChecked()
    {
        return displayBoxChecked;
    }

    public int getTaskType()
    {
        return taskType;
    }

    public int getDeadlineNs() { return deadlineNs; }

    public int getPriority() { return priority; }

    public void setPriority(int inPriority)
    {
        priority = inPriority;
    }

    public void setDeadlineNs(int inDeadlineNs)
    {
        deadlineNs = inDeadlineNs;
    }


    public int getComputationTimeErrorNs() {
        return computationTimeErrorNs;
    }


    /* The following section is from Man-Ki's RM scheduling simulator. */
    public int initialOffset;

    public long WCRT;

    public long jobSeqNo;
    public long lastReleaseTime;
    public long lastFinishTime;

    public long nextReleaseTime;

    public Task(int inTaskId, String inTitle, int inType, int inPeriod, int inComputationTime, int inPriority, int inDeadline)
    {
        this(inTaskId, inTitle, null, inType, inPeriod, inComputationTime, inPriority);
        deadlineNs = inDeadline;
    }

    @Override
    public String toString() {
        return "[Task " + id + "] p = " + periodNs + ", e = " + computationTimeNs
                + ", d = " + deadlineNs + ", prio = " + priority;
    }

    public LinkedList<Long> responseTimeHistory = new LinkedList<Long>();
    public LinkedList<Long> interarrivalTImeHistory = new LinkedList<Long>();
    public LinkedList<Long> execTimeHistory = new LinkedList<Long>();
}
