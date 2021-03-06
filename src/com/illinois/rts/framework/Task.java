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

    public Task(Task inTask) {
        cloneSettings(inTask);
    }

    public int getId()
    {
        return id;
    }

    public int getComputationTimeNs() {
        return computationTimeNs;
    }

    public void setComputationTimeNs(int computationTimeNs) {
        this.computationTimeNs = computationTimeNs;
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

    public void setPeriodNs(int inPeriod) {
        periodNs = inPeriod;
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


    /* Added by CY. */
    public Boolean validateAttributes()
    {
        if (periodNs <= 0)
            return false;

        if (computationTimeNs<=0 || computationTimeNs>=periodNs)
            return false;

        // Deadline
        // The deadline is not yet used yet.

        // Everything is fine.
        return true;
    }

    public int getInitialOffset() {
        return initialOffset;
    }

    public void setInitialOffset(int initialOffset) {
        this.initialOffset = initialOffset;
    }

    public void cloneSettings(Task inTask) {
        id = inTask.id;
        title = inTask.title;
        symbol = inTask.symbol;
        color = inTask.color;
        displayBoxChecked = inTask.displayBoxChecked;
        taskType = inTask.taskType;
        periodNs = inTask.periodNs;
        computationTimeNs = inTask.computationTimeNs;
        computationTimeErrorNs = inTask.computationTimeErrorNs;
        priority = inTask.priority;
        deadlineNs = inTask.deadlineNs;
        initialOffset = inTask.initialOffset;

        /*
        The following variables are not cloned.

        public long WCRT;
        public long jobSeqNo;
        public long lastReleaseTime;
        public long lastFinishTime;
        public long nextReleaseTime;

        public LinkedList<Long> responseTimeHistory = new LinkedList<Long>();
        public LinkedList<Long> interarrivalTImeHistory = new LinkedList<Long>();
        public LinkedList<Long> execTimeHistory = new LinkedList<Long>();
        */
    }

    public Task clone() {
        Task cloneTask = new Task();
        cloneTask.cloneSettings(this);
        return cloneTask;
    }

    public void clearSimData() {
        WCRT = 0;
        jobSeqNo = 0;
        lastReleaseTime = 0;
        lastFinishTime = 0;
        nextReleaseTime = 0;

        responseTimeHistory.clear();
        interarrivalTImeHistory.clear();
        execTimeHistory.clear();
    }
}
