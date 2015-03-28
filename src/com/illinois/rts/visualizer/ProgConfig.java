package com.illinois.rts.visualizer;

import java.awt.*;

/**
 * Created by CY on 3/14/2015.
 */
public class ProgConfig {
    static void ProgConfig() {}

    public static int PANEL_DRAWER_PADDING_Y = 0;//10;
    public static int PANEL_DRAWER_PADDING_X = 0;//10;

    public static int VIRTUAL_PANEL_MARGIN_Y = 0;
    public static int VIRTUAL_PANEL_MARGIN_X = 20;


    /* Configuration from log */
    public static double TIMESTAMP_UNIT_NS = 3; // 3ns

    /* Trace Settings */
    public static int TRACE_HORIZONTAL_SCALE_DIVIDER = 30000;
    public static int TIME_LINE_PERIOD_NS = 10000000; // 10ms
    public static int TRACE_HEIGHT = 100;
    public static int TRACE_GAP_Y = 60;
    public static int TRACE_PANEL_BORDER_WIDTH = 1;
    public static Color TRACE_PANEL_BACKGROUND = Color.GRAY;
    public static Color TRACE_PANEL_FOREGROUND = Color.WHITE;
    public static Color TRACE_PANEL_BORDER_COLOR = Color.LIGHT_GRAY;
    public static Color TRACE_PANEL_TEXT_COLOR = Color.BLACK;

    // Scheduler Traces
    public static boolean DISPLAY_SCHEDULER_SUMMARY_TRACE = true;
    public static boolean DISPLAY_SCHEDULER_TASK_TRACES = true;



}
