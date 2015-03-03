package com.illinois.rts.visualizer;

import javax.swing.*;
import java.awt.*;

/**
 * Created by CY on 2/16/2015.
 */
public class TaskListColorIcon implements Icon
{
    private static final int ICON_HEIGHT = 30;
    private static final int ICON_WIDTH = 30;
    private int height = 0;
    private int width = 0;

    private Color fillColor = Color.green;
    private Color borderColor = Color.black;
    private Boolean checked = true;
    private String symbol = "";

    public TaskListColorIcon()
    {
        height = ICON_HEIGHT;
        width = ICON_WIDTH;
    }

    public TaskListColorIcon(Color inputFillColor) {
        this();
        fillColor = inputFillColor;
    }

    public TaskListColorIcon(Color inputFillColor, Boolean inputChecked, String inputSymbol) {
        this(inputFillColor);
        checked = inputChecked;
        symbol = inputSymbol;
    }

    public int getIconHeight()
    {
        return height;
    }

    public int getIconWidth()
    {
        return width;
    }

    public void paintIcon(Component c, Graphics g, int x, int y)
    {
        g.setColor(fillColor);
        g.fillRect(x, y, width, height);

        g.setColor(borderColor);
        g.drawRect(x, y, width, height);


        if (checked == true) {
            //g.setColor(Color.WHITE);
            //g.drawString("V", x + width / 2, y + height / 2);
        }
        else
        {
            g.setColor(Color.WHITE);
            g.fillRect(x+width/6, y+height/6, (int) (width*0.7), (int) (height*0.7));
        }

        if (!symbol.isEmpty())
        {
            g.setColor(Color.BLACK);
            g.setFont(new Font("TimesRoman", Font.BOLD, 18));
            g.drawString(symbol, x + (int)(width*0.3), y + (int)(height*0.75));
        }
    }
}
