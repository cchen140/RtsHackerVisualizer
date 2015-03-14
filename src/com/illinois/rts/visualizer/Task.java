package com.illinois.rts.visualizer;

import java.awt.*;

/**
 * Created by CY on 2/17/2015.
 */
public class Task {
    private String title = "";
    private String symbol = ""; // Mainly for representing high/low hacker with symbol "H"/"L" in the icon.
    private Color color = Color.black;
    private Boolean displayBoxChecked = true;
    private int id = 0;

    public Task(){}

    public Task(String inTitle)
    {
        this();
        title = inTitle;
    }

    public Task(String inTitle, Color inColor)
    {
        this(inTitle);
        color = inColor;
    }

    public Task(int inTaskId, String inTitle, Color inColor)
    {
        this(inTitle, inColor);
        id = inTaskId;
    }

    public int getId()
    {
        return id;
    }

    public void setSymbol(String inSymbol)
    {
        symbol = inSymbol;
    }
    public String getSymbol()
    {
        return symbol;
    }

    public void setColor(Color inputColor)
    {
        color = inputColor;
    }
    public Color getTaskColor()
    {
        return color;
    }

    public void setTitle(String inTitle)
    {
        title = inTitle;
    }
    public String getTitle()
    {
        return title;
    }

    public void setDisplayBoxChecked(Boolean inputChecked)
    {
        displayBoxChecked = inputChecked;
    }
    public Boolean isDisplayBoxChecked()
    {
        return displayBoxChecked;
    }
}
