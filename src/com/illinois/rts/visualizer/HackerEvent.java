package com.illinois.rts.visualizer;

import java.awt.*;
import java.awt.geom.AffineTransform;

/**
 * Created by CY on 2/20/2015.
 */
public class HackerEvent extends Event {
    private Task task = null;
    private int recordData = 0;
    private String note = "";

    HackerEvent(int inTimeStamp, Task inTask, int inData, String inNote)
    {
        timeStamp = inTimeStamp;
        task = inTask;
        recordData = inData;
        note = inNote;
    }

    @Override
    public void drawEvent(Graphics2D g, int offsetX, int offsetY, double scaleX, double scaleY)
    {
        int scaledOffsetX = offsetX + (int) (timeStamp*scaleX);
        int movedOffsetY = 0;
        if (task.isBoxChecked()) {
            g.setFont(new Font("TimesRoman", Font.BOLD, 18));
            if (task.getTitle().equalsIgnoreCase("Hacker-H"))
            {
                g.drawString(note, scaledOffsetX-5, offsetY + SchedulerEvent.DRAW_HEIGHT + 80);
                drawArrow(g, scaledOffsetX, offsetY + SchedulerEvent.DRAW_HEIGHT + 50, scaledOffsetX, offsetY + SchedulerEvent.DRAW_HEIGHT);
            }
            else {

                g.drawString(note, scaledOffsetX, offsetY - 70);
                drawArrow(g, scaledOffsetX, offsetY - 50, scaledOffsetX, offsetY);
            }
        }
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
