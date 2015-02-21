package com.illinois.rts.visualizer;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.security.InvalidParameterException;

/**
 * Created by CY on 2/16/2015.
 */
public class LogLoader extends DialogFileLoader {
    private ScheduleEventContainer eventContainer = new ScheduleEventContainer();
    private TaskContainer taskContainer = null;

    public LogLoader()
    {
        taskContainer = eventContainer.getTaskContainer();
    }


    public ScheduleEventContainer loadLogFromDialog() throws IOException
    {
        if (openFileFromDialog() == null)
        {
            return null;
        }

        if (loadLog(fileReader) == false)
            throw new InvalidParameterException("Log file is incorrect.");
        else
            return eventContainer;
    }

    public ScheduleEventContainer loadDemoLog() throws IOException
    {
        String demoLogFilePath = "./log/demoLog2.txt";
        if (loadLog(this.openFile(demoLogFilePath)) == false)
            throw new InvalidParameterException("Demo log file is incorrect.");
        else
            return eventContainer;
    }

    /**
     * Load logs from the file and put them in the Events Container.
     * @param fileReader The BufferedReader of the opened file.
     * @return return 'false' if it fails to load the log file. Otherwise, return 'true'.
     */
    private Boolean loadLog(BufferedReader fileReader)
    {
        int lineCounter = 1;
        int firstTimeStamp = -1;

        try {
            String line = null;
            eventContainer.clearAll();
            int currentLogBlock = 0;
            while ((line = fileReader.readLine()) != null)
            {
                if (line.trim().length()==0)
                    continue;
//                else if (line.trim().substring(0, 1).equalsIgnoreCase("#"))
//                    continue;
                else if (line.trim().toLowerCase().equalsIgnoreCase("@TaskList")) {
                    currentLogBlock = 1;    // Reading task list block
                    continue;
                }
                else if (line.trim().toLowerCase().equalsIgnoreCase("@MixedLog")) {
                    currentLogBlock = 2;    // Reading log block
                    continue;
                }
                else  if (currentLogBlock == 0) // Unknown lines.
                    continue;


                String splitStrings[] = line.split(",");

                if (splitStrings.length > 1) {
                    if (currentLogBlock == 1)
                    {// Building task list
                        int taskId = Integer.valueOf(splitStrings[0].trim()).intValue();
                        String taskTitle = splitStrings[1].trim().substring(1, splitStrings[1].trim().length()-1);
                        taskContainer.addTask(taskId, taskTitle);
                    }
                    else if (currentLogBlock == 2)
                    {// format: timestamp, event_taskId, event_data, event_string. (62039364, 0, 3, "IN")
                        int timeStamp = Double.valueOf(splitStrings[0].trim()).intValue();
                        if (firstTimeStamp == -1)
                        {
                            firstTimeStamp = timeStamp;
                        }
                        timeStamp = (timeStamp-firstTimeStamp) / 10000;

                        int eventTaskId = Integer.valueOf(splitStrings[1].trim()).intValue();
                        int eventData = Integer.valueOf(splitStrings[2].trim()).intValue();
                        String eventString = splitStrings[3].trim().substring(1, splitStrings[3].trim().length()-1);

                        eventContainer.add(timeStamp, eventTaskId, eventData, eventString);
                    }
                }
                else
                {
                    System.err.format("Incorrect log file format at line %d.\n", lineCounter);
                    return false;
                }

                lineCounter++;
            }

            if (currentLogBlock == 0) {
                System.err.format("No valid block in the file.\r\n");
                return false;
            }
        }
        catch (IOException x)
        {
            System.err.format("IOException @ reading file: %s%n", x);
            return false;
        }

        System.out.format("%d lines loaded from the log file.\n", lineCounter);
        return true;
    }

    /**
     * Get the Events Container from the log loader.
     * @return The initialized Events Container.
     */
    public ScheduleEventContainer getEventContainer()
    {
        return eventContainer;
    }

//    public TaskContainer getTaskContainer()
//    {
//        return taskContainer;
//    }
}
