package com.illinois.rts.visualizer;

import javax.swing.*;
import java.awt.*;

/**
 * Created by CY on 3/25/2015.
 */
public class TimeLinePanel extends JPanel {
    DrawTimeLine timeLine = null;

    public TimeLinePanel() {
        super();
        timeLine = new DrawTimeLine(0, (int) (ProgConfig.TIME_LINE_UNIT_NS/ProgConfig.TIMESTAMP_SCALE_DIVIDER));
        timeLine.setDisplayTimeStampInNorth(true);
//        timeLine.setDisplayTimeStamp(true);
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D)g;

        timeLine.setEndTimeStamp(this.getWidth());
        timeLine.draw(g2d, ProgConfig.VIRTUAL_PANEL_MARGIN_X, this.getHeight());

    }


}
