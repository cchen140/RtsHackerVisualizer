package com.illinois.rts.visualizer;

import java.awt.*;

/**
 * Created by CY on 2/16/2015.
 */
public class SchedulerEvent extends Event {
    public static final int EVENT_SCHEDULER = 0;
//    public static final int DRAW_HEIGHT = 150;

    private Task task = null;
    private DrawPhase drawObject = new DrawPhase();

    public SchedulerEvent(int inTimeStamp, Task inTask, String inNote)
    {
        timeStamp = inTimeStamp;
        task = inTask;

        drawObject.setLabel(task.getTitle());
        drawObject.setFillColor(task.getTaskColor());
    }

    public void drawEvent(Graphics2D g, int offsetX, int offsetY)
    {

        if (endTimeStamp > 0) {
//            int scaledWidth = (int) ((endTimeStamp - timeStamp) / scaleX);
            int eventWidth = endTimeStamp - timeStamp;
            int currentOffsetX = offsetX + timeStamp;
            if (task.isDisplayBoxChecked() == true) {
//                drawObject.setHeightScale(scaleY);
                drawObject.setWidth(eventWidth);
                drawObject.draw(g, currentOffsetX, offsetY);
            }
            else
            {
                drawObject.drawUnderLine(g, currentOffsetX, offsetY);
            }

        }
        else
        {
            //((DrawPhase) drawObject).draw(g, timeStamp, 20);
            System.err.println("Drawing ScheduleEvent error: TimeStamp <= 0");
        }
        //System.out.println(timeStamp);
    }

    @Override
    public int getDrawHeight() {
        return drawObject.getHeight();
    }

    Task getTask() { return task; }
}
