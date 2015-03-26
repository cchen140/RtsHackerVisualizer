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

        if (value instanceof Object) {
            theText = (String) value;
//            theIcon = new TaskListColorIcon(((Task)value).getTaskColor(), ((Task)value).isDisplayBoxChecked(), ((Task)value).getSymbol());
//            theText = ((Task)value).getTitle();
        } else {
            theFont = list.getFont();
            theForeground = list.getForeground();
            theText = "TaskListRenderer Error";
        }
        if (!isSelected) {
            renderer.setForeground(theForeground);
        }
        if (theIcon != null) {
            renderer.setIcon(theIcon);
        }

        /* Create border. */
        renderer.setBorder(BorderFactory.createMatteBorder(0, 0, ProgConfig.TRACE_PANEL_BORDER_WIDTH, ProgConfig.TRACE_PANEL_BORDER_WIDTH, ProgConfig.TRACE_PANEL_BORDER_COLOR));   // top, left, bottom, right

        renderer.setText(theText);
        renderer.setFont(new Font("TimesRoman", Font.PLAIN, 20));
        renderer.setHorizontalAlignment(CENTER);
        renderer.setForeground(ProgConfig.TRACE_PANEL_TEXT_COLOR);    // Set the text color
        renderer.setIconTextGap(10);

        return renderer;

    }

}
