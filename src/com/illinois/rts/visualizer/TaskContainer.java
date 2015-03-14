package com.illinois.rts.visualizer;

import com.sun.javafx.scene.control.skin.ColorPalette;

import javax.swing.text.StyleConstants;
import java.awt.*;
import java.awt.color.ColorSpace;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by CY on 2/17/2015.
 */
public class TaskContainer {
    //private ArrayList<Task> tasks = new ArrayList<Task>();
    public HashMap<Integer, Task> tasks = new HashMap<Integer, Task>();
    private ArrayList<Color> colorList = new ArrayList<Color>();


    public TaskContainer()
    {
        initColorList();
    }

    public Boolean addTask(int taskId, String taskTitle)
    {
        String reTaskTitle = taskTitle.toLowerCase().trim();
        if (tasks.containsKey(reTaskTitle))
        {
            return false;
        }

        tasks.put(taskId, new Task(taskId, taskTitle, getColorByIndex(tasks.size())));
        return true;

    }

    public Task getTaskById(int searchId)
    {
        return tasks.get(searchId);
    }

    public Object[] getTasksAsArray()
    {
        return tasks.values().toArray();
    }

    public Color getColorByIndex(int index)
    {
        // ArrayList index starts from 0.
        return colorList.get(index);
    }

    private void initColorList()
    {
        final int center = 128;
        final int width = 127;
        final double frequency = 2.4;
        colorList.clear();
        generateColorList(frequency, frequency, frequency, 0, 2, 4, center, width, 50);
    }

    /* Source from: http://krazydad.com/tutorials/makecolors.php */
    private void generateColorList(double frequency1, double frequency2, double frequency3,
                               int phase1, int phase2, int phase3,
                               int center, int width, int len)
    {
        if (center ==0)   center = 128;
        if (width == 0)    width = 127;
        if (len == 0)      len = 50;

        for (int i = 0; i < len; ++i)
        {
            int red = (int) (Math.sin(frequency1*i + phase1) * width + center);
            int grn = (int) (Math.sin(frequency2*i + phase2) * width + center);
            int blu = (int) (Math.sin(frequency3*i + phase3) * width + center);
            colorList.add(new Color(red, grn, blu));
            //System.out.println(colorList.get(i).toString());
        }
    }

    public void clear()
    {
        tasks.clear();
    }

    public int size() { return tasks.size(); }
}
