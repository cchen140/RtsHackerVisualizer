package com.illinois.rts.visualizer;

import java.awt.*;

/**
 * Created by CY on 2/16/2015.
 */
public class SchedulerEvent extends Event {
//    public static final int EVENT_SCHEDULER = 0;
//    public static final int DRAW_HEIGHT = 150;

    private Task task = null;
    private DrawPhase drawObject = new DrawPhase();
    private String note = "";

    public SchedulerEvent(int inTimeStamp, Task inTask, String inNote)
    {
        orgBeginTimestampNs = inTimeStamp;
        scaledBeginTimestamp = inTimeStamp;
        task = inTask;
        note = inNote;

        drawObject.setLabel(task.getTitle());
        drawObject.setFillColor(task.getTaskColor());
    }

    public void drawEvent(Graphics2D g, int offsetX, int offsetY)
    {

        if (scaledEndTimestamp > 0) {
//            int scaledWidth = (int) ((scaledEndTimestamp - scaledBeginTimestamp) / scaleX);
            int eventWidth = scaledEndTimestamp - scaledBeginTimestamp;
            int currentOffsetX = offsetX + scaledBeginTimestamp;
            if (task.isDisplayBoxChecked() == true) {
//                drawObject.setHeightScale(scaleY);
                drawObject.setWidth(eventWidth);
                drawObject.draw(g, currentOffsetX, offsetY-drawObject.getHeight());
            }
            else
            {
//                drawObject.drawUnderLine(g, currentOffsetX, offsetY);
            }

        }
        else
        {
            //((DrawPhase) drawObject).draw(g, scaledBeginTimestamp, 20);
            System.err.println("Drawing ScheduleEvent error: TimeStamp <= 0");
        }
        //System.out.println(scaledBeginTimestamp);
    }

    @Override
    public TraceSpace getGraphSpace() {
        return new TraceSpace(drawObject.getHeight(), 0);
    }

    Task getTask() { return task; }

    String getNote()
    {
        return note;
    }
}
