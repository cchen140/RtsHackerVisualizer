package com.illinois.rts.visualizer;

import com.illinois.rts.framework.*;

import java.awt.*;

/**
 * Created by CY on 2/19/2015.
 */
public class AppEvent extends Event {
    private Task task = null;
    private int recordData = 0;
    private String note = "";

    AppEvent(int inTimeStamp, Task inTask, int inData, String inNote)
    {
        orgBeginTimestampNs = inTimeStamp;
        scaledBeginTimestamp = inTimeStamp;
        task = inTask;
        recordData = inData;
        note = inNote;
    }

    public void drawEvent(Graphics2D g, int offsetX, int offsetY)
    {
//        int scaledOffsetX = offsetX + (int) (scaledBeginTimestamp/scaleX);
        int currentOffsetX = offsetX + scaledBeginTimestamp;
        int movedOffsetY = 0;
        if (task.isDisplayBoxChecked()) {
            g.setFont(new Font("TimesRoman", Font.BOLD, 16));
            g.setColor(task.getTaskColor());

            /* Display in South */
            g.drawString(note, currentOffsetX-5, offsetY + 25);
            drawArrow(g, currentOffsetX, offsetY + 5, currentOffsetX, offsetY);

            /* Display in North */
//            g.drawString(note, scaledOffsetX, offsetY - 70);
//            drawArrow(g, scaledOffsetX, offsetY - 50, scaledOffsetX, offsetY);
        }
    }

    @Override
    public TraceSpace getGraphSpace() {
        return new TraceSpace(0, 25);
    }



    public int getTaskId(){
        return task.getId();
    }

    Task getTask() { return task; }
}
