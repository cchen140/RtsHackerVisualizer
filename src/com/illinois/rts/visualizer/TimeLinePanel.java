package com.illinois.rts.visualizer;

import javax.swing.*;
import java.awt.*;

/**
 * Created by CY on 3/25/2015.
 */
public class TimeLinePanel extends JPanel {
    TimeLine timeLine = null;

//    public TimeLinePanel() {
//        super();
//        timeLine = new TimeLine(0, (int) (ProgConfig.TIME_LINE_PERIOD_NS /ProgConfig.TRACE_HORIZONTAL_SCALE_DIVIDER));
//        timeLine.setDisplayMarkerLabelsInNorth(true);
////        timeLine.setDisplayMarkerLabels(true);
//    }

    public TimeLinePanel(){
        super();
        timeLine = new TimeLine();
        timeLine.setDisplayMarkerLabelsInNorth(true);
    }

    public TimeLinePanel(TimeLine inTimeLine)
    {
        super();
        timeLine = inTimeLine;
        timeLine.setDisplayMarkerLabelsInNorth(true);
    }

    void updateTimeLineSettings(TimeLine inTimeLine)
    {
        timeLine.copyTimeSettings(inTimeLine);
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D)g;

//        timeLine.setEndTimestampNs(this.getWidth());
        timeLine.draw(g2d, ProgConfig.VIRTUAL_PANEL_MARGIN_X, this.getHeight());

    }


}
