package com.illinois.rts.visualizer;

import com.illinois.rts.framework.*;

import java.awt.*;

/**
 * Created by CY on 2/16/2015.
 */
public class TaskIntervalEvent extends Event {
//    public static final int EVENT_SCHEDULER = 0;
//    public static final int DRAW_HEIGHT = 150;

    private Task task = null;
    private DrawInterval drawInterval = new DrawInterval();
    private String note = "";

    public TaskIntervalEvent(int inTimeStamp, Task inTask, String inNote)
    {
        orgBeginTimestampNs = inTimeStamp;
        scaledBeginTimestamp = inTimeStamp;
        task = inTask;
        note = inNote;

        drawInterval.setLabel(task.getTitle());
        drawInterval.setFillColor(task.getTaskColor());
    }

    public TaskIntervalEvent(int inBeginTimeStamp, int inEndTimeStamp, Task inTask, String inNote)
    {
        this(inBeginTimeStamp, inTask, inNote);
        orgEndTimestampNs = inEndTimeStamp;
        scaledEndTimestamp = inEndTimeStamp;
    }

    public void drawEvent(Graphics2D g, int offsetX, int offsetY)
    {

        if (scaledEndTimestamp > 0) {
//            int scaledWidth = (int) ((scaledEndTimestamp - scaledBeginTimestamp) / scaleX);
            int eventWidth = scaledEndTimestamp - scaledBeginTimestamp;
            int currentOffsetX = offsetX + scaledBeginTimestamp;
            if (task.isDisplayBoxChecked() == true) {
//                drawPhase.setHeightScale(scaleY);
                drawInterval.setFillColor(task.getTaskColor());
                drawInterval.setWidth(eventWidth);
                drawInterval.draw(g, currentOffsetX, offsetY - drawInterval.getHeight());
            }
            else
            {
//                drawPhase.drawUnderLine(g, currentOffsetX, offsetY);
            }

        }
        else
        {
            // TODO: Need to handle the scheduler events which have time stamp less than 0.
            //((DrawPhase) drawPhase).draw(g, scaledBeginTimestamp, 20);
//            System.err.println("Drawing ScheduleEvent error: TimeStamp <= 0");
        }
        //System.out.println(scaledBeginTimestamp);
    }

    @Override
    public TraceSpace getGraphSpace() {
        return new TraceSpace(drawInterval.getHeight(), 0);
    }

    public Task getTask() { return task; }

    public String getNote()
    {
        return note;
    }

    public DrawInterval getDrawInterval() {
        return drawInterval;
    }
}
