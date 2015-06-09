package com.illinois.rts.simulator;

import com.illinois.rts.framework.Task;
import com.illinois.rts.visualizer.*;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.ArrayList;

/**
 * Created by CY on 2/16/2015.
 */
public class ConfigLoader extends DialogFileHandler {
    private static final int CONFIG_BLOCK_UNKNOWN = 0;
    private static final int CONFIG_BLOCK_TASK_LIST = 1;

//    private ArrayList<SimTask> simTasks = new ArrayList<SimTask>();
    private TaskContainer simTaskContainer = new TaskContainer();

    public ConfigLoader()
    {
    }


    public TaskContainer loadConfigFromDialog() throws IOException
    {
        if (openFileFromDialog() == null)
        {
            return null;
        }

        if (loadConfig(fileReader) == false)
            throw new InvalidParameterException("Config file is incorrect.");
        else
            return simTaskContainer;
    }

    public TaskContainer loadDemoConfig() throws IOException
    {
        String demoLogFilePath = "./log/demoConfig1.txt";
        if (loadConfig(this.openFile(demoLogFilePath)) == false)
            throw new InvalidParameterException("Demo config file is incorrect.");
        else
            return simTaskContainer;
    }

    /**
     * Load logs from the file and put them in the Events Container.
     * @param fileReader The BufferedReader of the opened file.
     * @return return 'false' if it fails to load the log file. Otherwise, return 'true'.
     */
    private Boolean loadConfig(BufferedReader fileReader)
    {
        int lineCounter = 1;

        try {
            String line = null;
            simTaskContainer.clear();
            int currentLogBlock = CONFIG_BLOCK_UNKNOWN;
            while ((line = fileReader.readLine()) != null)
            {
                lineCounter++;

                if (line.trim().length()==0) // Empty line
                    continue;
                else if (line.trim().substring(0, 1).equalsIgnoreCase("#")) // Comment line
                    continue;
                else if (line.trim().toLowerCase().equalsIgnoreCase("@TaskList")) {
                    currentLogBlock = CONFIG_BLOCK_TASK_LIST;    // Reading task list block
                    continue;
                }
                else if (line.trim().substring(0, 1).equalsIgnoreCase("@")) {// Comment line
                    currentLogBlock = CONFIG_BLOCK_UNKNOWN;
                    continue;
                }


                Boolean parseResult = false;
                switch (currentLogBlock)
                {
                    case CONFIG_BLOCK_TASK_LIST:
                        parseResult = parseLogLineTaskList(line);
                        break;
                    case CONFIG_BLOCK_UNKNOWN:
                    default:
                        break;
                }

                if (parseResult == false)
                {
                    System.err.format("Incorrect config file format at line %d.\n", lineCounter);
                    return false;
                }

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

        ProgMsg.putLine("%d lines loaded from the config file.", lineCounter);
        System.out.format("%d lines loaded from the config file.\n", lineCounter);

        return true;
    }


    /**
     * Parse one log line for task list block.
     * @param line One log line string to be parsed. Format: [taskId, TaskName]
     * @return 'true' for successfully parsing the line, whereas 'false' for not.
     */
    // id, task type, task name, period, computation time, priority
    private Boolean parseLogLineTaskList(String line)
    {
        /* Format: [taskId, taskType, taskName, taskPeriod, taskComputationTime, taskPriority] */
        String splitStrings[] = line.split(",");
        if (splitStrings.length == 6) {
            int taskId = Integer.valueOf(splitStrings[0].trim()).intValue();
            int taskType = Integer.valueOf(splitStrings[1].trim()).intValue();
            String taskTitle = splitStrings[2].trim().substring(1, splitStrings[2].trim().length() - 1);
            int taskPeriod = Integer.valueOf(splitStrings[3].trim()).intValue();
            int taskDeadline = taskPeriod;
            int taskComputationTime = Integer.valueOf(splitStrings[4].trim()).intValue();
            int taskPriority = Integer.valueOf(splitStrings[5].trim()).intValue();

            simTaskContainer.addTask(taskId, taskTitle, taskType, taskPeriod, taskDeadline, taskComputationTime, taskPriority);
            Task thisSimTask = simTaskContainer.getTaskById(taskId);

            thisSimTask.initialOffset = 0;

            thisSimTask.nextReleaseTime = thisSimTask.initialOffset;

            thisSimTask.WCRT = Long.MIN_VALUE;
            thisSimTask.jobSeqNo = 0;
            thisSimTask.lastReleaseTime = -1;
            thisSimTask.lastFinishTime = 0;

            return true;
        }
        else {
            // Incorrect task list log format.
            System.out.println("Task List is wrong in the log file.");
            return false;
        }
    }

}
