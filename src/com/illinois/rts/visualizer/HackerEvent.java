package com.illinois.rts.visualizer;

import java.awt.*;
import java.awt.geom.AffineTransform;

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
                //g.drawLine(currentOffsetX, offsetY - 50, currentOffsetX, offsetY);
                //drawObject.setFillColor(task.getTaskColor());
//                g.fillRect(currentOffsetX - BAR_WIDTH / 2, offsetY - scaledRecordData, BAR_WIDTH, scaledRecordData);
                //g.drawString(note, currentOffsetX, offsetY - 70);
                //drawArrow(g, currentOffsetX, offsetY - 50, currentOffsetX, offsetY);
            }
            else if (task.getId() == lowHackerId)
            {
                /* Display in South */
                g.setColor(task.getTaskColor());
                g.drawString(note, currentOffsetX-5, offsetY + ProgConfig.TRACE_HEIGHT + 25);
                drawArrow(g, currentOffsetX, offsetY + ProgConfig.TRACE_HEIGHT + 5, currentOffsetX, offsetY + ProgConfig.TRACE_HEIGHT);
            }
            else
            {
                // Shouldn't reach here.
                System.err.println("Error occurs in HackerEvent drawEvent function!");
            }
        }
    }

    @Override
    public int getDrawHeight() {
        return 0;
    }

    private  void drawArrow(Graphics g1, int x1, int y1, int x2, int y2) {
        int ARR_SIZE = 7;
        Graphics2D g = (Graphics2D) g1.create();

        double dx = x2 - x1, dy = y2 - y1;
        double angle = Math.atan2(dy, dx);
        int len = (int) Math.sqrt(dx*dx + dy*dy);
        AffineTransform at = AffineTransform.getTranslateInstance(x1, y1);
        at.concatenate(AffineTransform.getRotateInstance(angle));
        g.transform(at);

        // Draw horizontal arrow starting in (0, 0)
        g.drawLine(0, 0, len, 0);
        g.fillPolygon(new int[] {len, len-ARR_SIZE, len-ARR_SIZE, len},
                new int[] {0, -ARR_SIZE, ARR_SIZE, 0}, 4);
    }
}
