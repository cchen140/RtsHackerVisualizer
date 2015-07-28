package com.illinois.rts.visualizer;

import com.illinois.rts.framework.Task;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

/**
 * Created by CY on 2/16/2015.
 */
public class TaskListRenderer extends JLabel implements ListCellRenderer {
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
            theIcon = new TaskListColorIcon(((Task)value).getTaskColor(), ((Task)value).isDisplayBoxChecked(), ((Task)value).getSymbol());
            theText = ((Task)value).getTitle();
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

        /* Create simulated padding effect from creating border. */
        Border paddingBorder = BorderFactory.createEmptyBorder(8, 10, 8, 5); // top, left, bottom, right
        Border border = BorderFactory.createLineBorder(Color.WHITE);
        renderer.setBorder(BorderFactory.createCompoundBorder(border,paddingBorder));

        renderer.setText(theText);
        renderer.setFont(new Font("TimesRoman", Font.PLAIN, 18));

        renderer.setIconTextGap(10);

        return renderer;

    }

}
