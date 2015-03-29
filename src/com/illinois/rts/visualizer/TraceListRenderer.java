package com.illinois.rts.visualizer;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

/**
 * Created by CY on 2/16/2015.
 */
public class TraceListRenderer extends JLabel implements ListCellRenderer {
        protected DefaultListCellRenderer defaultRenderer = new DefaultListCellRenderer();

    public Component getListCellRendererComponent(JList list, Object value, int index,
                                                  boolean isSelected, boolean cellHasFocus) {
        Font theFont = null;
        Color theForeground = null;
        Icon theIcon = null;
        String theText = null;

        JLabel renderer = (JLabel) defaultRenderer.getListCellRendererComponent(list, value, index,
                isSelected, cellHasFocus);

        Trace currentTrace = (Trace) value;

        if (currentTrace.getTask() != null)
        {
            renderer.setIcon( new TaskListColorIcon(currentTrace.getTask().getTaskColor(), currentTrace.getTask().isDisplayBoxChecked(), currentTrace.getTask().getSymbol()) );
        }
        else
        {
            renderer.setIcon(new TaskListColorIcon(Color.WHITE));
        }

        renderer.setText(currentTrace.getName());
        renderer.setFont(new Font("TimesRoman", Font.PLAIN, 20));
//        renderer.setHorizontalAlignment(CENTER);
        renderer.setForeground(ProgConfig.TRACE_PANEL_TEXT_COLOR);    // Set the text color
        renderer.setIconTextGap(10);

        /* Create margin space by creating inside and outside borders. */
        /* The third parameter (bottom border) is handled with Math.ceil and minus 1 to correct the height error
         * when the corresponding trace height is an odd number which would introduce an offset after divided by 2.
         * The subtraction of 1 in the end is to correct the border width (1px) created here. */
        renderer.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, ProgConfig.TRACE_PANEL_BORDER_WIDTH, ProgConfig.TRACE_PANEL_BORDER_WIDTH, ProgConfig.TRACE_PANEL_BORDER_COLOR),   // top, left, bottom, right
                BorderFactory.createEmptyBorder(currentTrace.getTraceHeight()/2 - renderer.getIcon().getIconHeight()/2,
                                                10,
                                                (int) Math.ceil((double)currentTrace.getTraceHeight()/2 - (double)renderer.getIcon().getIconHeight()/2) - 1,
                                                10))); // top, left, bottom, right

        return renderer;

    }

}
