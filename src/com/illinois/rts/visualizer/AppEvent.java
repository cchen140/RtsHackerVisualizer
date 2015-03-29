package com.illinois.rts.visualizer;

import java.awt.*;
import java.awt.geom.AffineTransform;

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
            g.drawString(note, currentOffsetX-5, offsetY + ProgConfig.TRACE_HEIGHT + 25);
            drawArrow(g, currentOffsetX, offsetY + ProgConfig.TRACE_HEIGHT + 5, currentOffsetX, offsetY + ProgConfig.TRACE_HEIGHT);

            /* Display in North */
//            g.drawString(note, scaledOffsetX, offsetY - 70);
//            drawArrow(g, scaledOffsetX, offsetY - 50, scaledOffsetX, offsetY);
        }
    }

    @Override
    public int getGraphHeight() {
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

    public int getTaskId(){
        return task.getId();
    }
}
