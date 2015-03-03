package com.illinois.rts.visualizer;

import java.io.BufferedReader;
import java.io.IOException;
import java.security.InvalidParameterException;

/**
 * Created by CY on 2/16/2015.
 */
public class LogLoader extends DialogFileLoader {
    private static final int LOG_BLOCK_UNKNOWN = 0;
    private static final int LOG_BLOCK_TASK_LIST = 1;
    private static final int LOG_BLOCK_MIXED_LOG = 2;
    private static final int LOG_BLOCK_HACKER_LIST = 3;

    private EventContainer eventContainer = new EventContainer();
    private TaskContainer taskContainer = null;
    private int firstTimeStamp = -1; // The variable to save the earliest timestamp in the log.

    public LogLoader()
    {
        taskContainer = eventContainer.getTaskContainer();
    }


    public EventContainer loadLogFromDialog() throws IOException
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

    public EventContainer loadDemoLog() throws IOException
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

        try {
            String line = null;
            eventContainer.clearAll();
            int currentLogBlock = LOG_BLOCK_UNKNOWN;
            while ((line = fileReader.readLine()) != null)
            {
                if (line.trim().length()==0) // Empty line
                    continue;
                else if (line.trim().substring(0, 1).equalsIgnoreCase("#")) // Comment line
                    continue;
                else if (line.trim().toLowerCase().equalsIgnoreCase("@TaskList")) {
                    currentLogBlock = LOG_BLOCK_TASK_LIST;    // Reading task list block
                    continue;
                }
                else if (line.trim().toLowerCase().equalsIgnoreCase("@MixedLog")) {
                    currentLogBlock = LOG_BLOCK_MIXED_LOG;    // Reading log block
                    continue;
                }
                else if (line.trim().toLowerCase().equalsIgnoreCase("@HackerList")) {
                    currentLogBlock = LOG_BLOCK_HACKER_LIST;
                    continue;
                }
                else  if (currentLogBlock == LOG_BLOCK_UNKNOWN) // Unknown lines.
                    continue;


                Boolean parseResult = false;
                switch (currentLogBlock)
                {
                    case LOG_BLOCK_TASK_LIST:
                        parseResult = parseLogLineTaskList(line);
                        break;
                    case LOG_BLOCK_MIXED_LOG:
                        parseResult = parseLogLineMixedLog(line);
                        break;
                    case LOG_BLOCK_HACKER_LIST:
                        parseResult = parseLogLineHackerList(line);
                        break;
                    default:
                        break;
                }

                if (parseResult == false)
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
    public EventContainer getEventContainer()
    {
        return eventContainer;
    }

//    public TaskContainer getTaskContainer()
//    {
//        return taskContainer;
//    }

    /**
     * Parse one log line for task list block.
     * @param line One log line string to be parsed. Format: [taskId, TaskName]
     * @return 'true' for successfully parsing the line, whereas 'false' for not.
     */
    private Boolean parseLogLineTaskList(String line)
    {
        /* Format: [taskId, TaskName] */
        String splitStrings[] = line.split(",");
        if (splitStrings.length == 2) {
            int taskId = Integer.valueOf(splitStrings[0].trim()).intValue();
            String taskTitle = splitStrings[1].trim().substring(1, splitStrings[1].trim().length() - 1);
            taskContainer.addTask(taskId, taskTitle);
            return true;
        }
        else {
            // Incorrect task list log format.
            return false;
        }
    }

    private Boolean parseLogLineMixedLog(String line)
    {
        /* format: [timestamp, event_taskId, event_data, event_string]. (62039364, 0, 3, "IN") */
        String splitStrings[] = line.split(",");
        if (splitStrings.length == 4) {
            int timeStamp = Double.valueOf(splitStrings[0].trim()).intValue();
            if (firstTimeStamp == -1) {
                // Initialize the earliest timestamp
                firstTimeStamp = timeStamp;
            }
            /* TODO: the scale of the timestamp should be flexible and configurable. */
            timeStamp = (timeStamp - firstTimeStamp) / 10000;

            int eventTaskId = Integer.valueOf(splitStrings[1].trim()).intValue();
            int eventData = Integer.valueOf(splitStrings[2].trim()).intValue();
            String eventString = splitStrings[3].trim().substring(1, splitStrings[3].trim().length() - 1);

            eventContainer.add(timeStamp, eventTaskId, eventData, eventString);
            return true;
        }
        else {
            // Incorrect task list log format.
            return false;
        }
    }

    private Boolean parseLogLineHackerList(String line)
    {
        String splitStrings[] = line.split(",");
        if (splitStrings.length == 2) {
            String hacker = splitStrings[1].trim();
            if (hacker.equalsIgnoreCase("H")) {
                HackerEvent.setHighHackerId(Integer.valueOf(splitStrings[0].trim()).intValue());
                return true;
            } else if (hacker.equalsIgnoreCase("L")) {
                HackerEvent.setLowHackerId(Integer.valueOf(splitStrings[0].trim()).intValue());
                return true;
            }
        }

        // Incorrect task list log format.
        return false;
    }
}
