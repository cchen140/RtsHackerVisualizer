package com.illinois.rts.visualizer;

import com.illinois.rts.framework.*;

import java.awt.*;

/**
 * Created by CY on 2/20/2015.
 */
public class HackerEvent extends Event {
    /* */
    public static int highHackerId = 0;
    public static int lowHackerId = 0;

    private static final int BAR_WIDTH = 30;
    private static final int HIGH_HACKER_DATA_SCALE = 50;
    private static final int LOW_HACKER_DATA_SCALE = 10000;

    private Task task = null;
    private int recordData = 0;
    private int scaledRecordData = 0;
    private String note = "";

    HackerEvent(int inTimestampNs, Task inTask, int inData, String inNote)
    {
        orgBeginTimestampNs = inTimestampNs;
        scaledBeginTimestamp = inTimestampNs;
        task = inTask;
        recordData = inData;

        if (inTask.getId() == highHackerId) {
            scaledRecordData = inData / HIGH_HACKER_DATA_SCALE;
            task.setSymbol("H");
        }
        else if (inTask.getId() == lowHackerId)
        {
            scaledRecordData = inData / LOW_HACKER_DATA_SCALE;
            task.setSymbol("L");
        }

        note = inNote;
    }

    public static void setHighHackerId(int inId)
    {
        highHackerId = inId;
    }

    public static void setLowHackerId(int inId)
    {
        lowHackerId = inId;
    }

    @Override
    public void drawEvent(Graphics2D g, int offsetX, int offsetY)
    {
//        int currentOffsetX = offsetX + (int) (scaledBeginTimestamp/scaleX);
        int currentOffsetX = offsetX + scaledBeginTimestamp;
        int movedOffsetY = 0;
        if (task.isDisplayBoxChecked()) {
            g.setFont(new Font("TimesRoman", Font.BOLD, 16));

            if (task.getId() == highHackerId)
            {
                /* Display in North */
                g.setColor(Color.black);

                String drawnString = String.valueOf(recordData);
                int stringWidth = getGraphicStringWidth(g, drawnString);

                //g.drawLine(currentOffsetX, offsetY - 50, currentOffsetX, offsetY);
                //drawObject.setFillColor(task.getTaskColor());
//                g.fillRect(currentOffsetX - BAR_WIDTH / 2, offsetY - scaledRecordData, BAR_WIDTH, scaledRecordData);
                g.drawString(drawnString, currentOffsetX-stringWidth/2, offsetY + 25);
                //g.drawString(note, currentOffsetX, offsetY - 70); // H label
                //drawArrow(g, currentOffsetX, offsetY - 50, currentOffsetX, offsetY);
            }
            else if (task.getId() == lowHackerId)
            {
                /* Display in South */
                g.setColor(task.getTaskColor());
                g.drawString(note, currentOffsetX-5, offsetY + 25);
                drawArrow(g, currentOffsetX, offsetY + 5, currentOffsetX, offsetY);
            }
            else
            {
                // Shouldn't reach here.
                System.err.println("Error occurs in HackerEvent drawEvent function!");
            }
        }
    }

    public int getTaskId(){
        return task.getId();
    }

    @Override
    public TraceSpace getGraphSpace() {
        if (getTaskId() == highHackerId) {
            return new TraceSpace(0, 25);
        }
        else //if (getTaskId() == lowHackerId)
        {
            return new TraceSpace(0, 25);
        }
    }

    public int getRecordData()
    {
        return recordData;
    }
}
