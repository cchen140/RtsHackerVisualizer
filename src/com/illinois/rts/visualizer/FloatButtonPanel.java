package com.illinois.rts.visualizer;

import javax.swing.*;
import java.awt.*;

/**
 * Created by CY on 2/17/2015.
 */
/* How to use it?
put the following codes before initialization of main frame.
        private FloatButtonPanel floatBtnPanel = new FloatButtonPanel();

        floatBtnPanel.setOpaque(false);
        frame.getRootPane().setGlassPane(floatBtnPanel);
        floatBtnPanel.setVisible(true);

        frame.setVisible(true);
        //zPanel.toggleDoNotDraw();

 */
public class FloatButtonPanel extends JPanel {

    private static final int PANEL_WIDTH = 50;
    private static final int PANEL_HEIGHT = 50;

    private JButton btn = new JButton();

    public FloatButtonPanel()
    {
        super();
        setSize(PANEL_WIDTH, PANEL_HEIGHT);
        add(btn);
    }

    public void paintComponent(Graphics g)
    {
        g.drawOval(10, 10, getWidth() - 20, getHeight() - 20);
    }
}
