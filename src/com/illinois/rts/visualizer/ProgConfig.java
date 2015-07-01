package com.illinois.rts.visualizer;

import java.awt.*;

/**
 * Created by CY on 3/14/2015.
 */
public class ProgConfig {
    static void ProgConfig() {}

    public static int MAIN_DISPLAY_PANEL_PADDING_Y = 0;//10;
    public static int MAIN_DISPLAY_PANEL_PADDING_X = 0;//10;

    public static int VIRTUAL_PANEL_MARGIN_Y = 0;
    public static int VIRTUAL_PANEL_MARGIN_X = 20;


    /* Configuration from log */
    public static double TIMESTAMP_UNIT_NS = 1; // 1 tick represents 1ns

    /* Trace group settings */
    public static int TRACE_GROUP_MARGIN_Y = 10;

    /* Trace Settings */
    public static int TRACE_HORIZONTAL_SCALE_DIVIDER = 30000;
    public static int TIME_LINE_PERIOD_NS = 10000000; // 10ms
    public static int TRACE_HEIGHT = 100;
    public static int TRACE_MARGIN_Y = 30;
    public static int TRACE_PANEL_BORDER_WIDTH = 1;
    public static Color TRACE_PANEL_BACKGROUND = Color.GRAY;
    public static Color TRACE_PANEL_BACKGROUND_BORDER = Color.BLACK;
    public static Color TRACE_PANEL_FOREGROUND = Color.WHITE;
    public static Color TRACE_PANEL_BORDER_COLOR = Color.LIGHT_GRAY;
    public static Color TRACE_PANEL_TEXT_COLOR = Color.BLACK;

    /* Trace header appearance */
    public static int TRACE_HEADER_PANEL_DEFAULT_WIDTH = 200;
    public static int TRACE_HEADER_LEFT_MARGIN = 50;
    public static int TRACE_HEADER_ICON_TITLE_GAP = 10;
    public static int TRACE_HEADER_TITLE_SUBTITLE_GAP = 20;
    public static Font TRACE_HEADER_TITLE_FONT = new Font("TimesRoman", Font.PLAIN, 20);
    public static Color TRACE_HEADER_TITLE_COLOR = Color.BLACK;
    public static Font TRACE_HEADER_SUBTITLE_FONT = new Font("TimesRoman", Font.PLAIN, 16);
    public static Color TRACE_HEADER_SUBTITLE_COLOR = Color.BLACK;

    public static int TRACE_HEADER_GROUP_HEAD_HEIGHT = 30;
    public static Color TRACE_HEADER_GROUP_HEAD_BACKGROUND = Color.BLUE;
    public static Font TRACE_HEADER_GROUP_HEAD_TITLE_FONT = new Font("TimesRoman", Font.PLAIN, 16);
    public static Color TRACE_HEADER_GROUP_HEAD_TITLE_COLOR = Color.WHITE;

    // Scheduler Traces
    public static boolean DISPLAY_SCHEDULER_SUMMARY_TRACE = true;
    public static boolean DISPLAY_SCHEDULER_TASK_TRACES = true;



}
