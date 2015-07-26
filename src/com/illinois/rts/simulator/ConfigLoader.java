package com.illinois.rts.simulator;

import com.illinois.rts.framework.Task;
import com.illinois.rts.visualizer.*;

import java.awt.*;
import java.awt.print.Book;
import java.io.BufferedReader;
import java.io.BufferedWriter;
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
        if (splitStrings.length >= 9) {
            int taskId = Integer.valueOf(splitStrings[0].trim()).intValue();
            int taskType = Integer.valueOf(splitStrings[1].trim()).intValue();
            String taskTitle = splitStrings[2].trim().substring(1, splitStrings[2].trim().length() - 1);
            int taskPeriod = Integer.valueOf(splitStrings[3].trim()).intValue();
            int taskDeadline = Integer.valueOf(splitStrings[4].trim()).intValue();
            int taskComputationTime = Integer.valueOf(splitStrings[5].trim()).intValue();
            int taskPriority = Integer.valueOf(splitStrings[6].trim()).intValue();
            int taskInitialOffset = Integer.valueOf(splitStrings[7].trim()).intValue();
            Color taskColor = new Color( Integer.valueOf(splitStrings[8].trim()).intValue() ); //RGB

            simTaskContainer.addTask(taskId, taskTitle, taskType, taskPeriod, taskDeadline, taskComputationTime, taskPriority);
            Task thisSimTask = simTaskContainer.getTaskById(taskId);

            thisSimTask.initialOffset = 0;

            thisSimTask.nextReleaseTime = thisSimTask.initialOffset;

            thisSimTask.WCRT = Long.MIN_VALUE;
            thisSimTask.jobSeqNo = 0;
            thisSimTask.lastReleaseTime = -1;
            thisSimTask.lastFinishTime = 0;

            thisSimTask.setInitialOffset( taskInitialOffset );
            thisSimTask.setColor( taskColor );

            return true;
        }
        else {
            // Incorrect task list log format.
            System.out.println("Task List is wrong in the log file.");
            return false;
        }
    }

    public Boolean exportTaskConfigsByDialog(TaskContainer taskContainer) {

        // TODO: Should check whether taskContainer is empty or contains no task?

        /* Show dialog to assign and open a file for the output. */
        try {
            openWriteFileFromDialog();  // set up "fileWriter".
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        if ( fileWriter == null ) {
            // Selection of the file path is canceled.
            return false;
        }

        /* Generate and write task configs to the file. */
        Boolean writeFileReturn;
        writeFileReturn = writeTaskConfigsToFile(taskContainer, fileWriter);
        if ( writeFileReturn == true ) {
            // Writing config to file successfully.
            try {
                // Close the file for making the writing affect.
                fileWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        } else {
            // Should never reach here.
            ProgMsg.debugPutline("Something wrong.");
            return false;
        }

    }

    /**
     * Generate a string line of task configuration for a specified task.
     * The format is as follows:
     * ID, Task Type, Name, Period, Deadline, Computation Time, Priority, Optional:( Initial Offset, Color )
     * @param inTask the task to be read to generate the config string.
     * @return
     */
    protected String generateTaskConfigLine(Task inTask)
    {
        if ( inTask == null ) {
            return "";
        }

        String thisTaskLine = "";

        // Task ID
        thisTaskLine += String.valueOf(inTask.getId());
        thisTaskLine += ", ";

        // Task Type
        thisTaskLine += String.valueOf(inTask.getTaskType());
        thisTaskLine += ", ";

        // Task Name
        thisTaskLine += '\"' + inTask.getTitle() + '\"';
        thisTaskLine += ", ";

        // Task Period
        thisTaskLine += String.valueOf(inTask.getPeriodNs());
        thisTaskLine += ", ";

        // Deadline
        thisTaskLine += String.valueOf( inTask.getDeadlineNs() );
        thisTaskLine += ", ";

        // Task Computation Time
        thisTaskLine += String.valueOf(inTask.getComputationTimeNs());
        thisTaskLine += ", ";

        // Task Priority
        thisTaskLine += String.valueOf( inTask.getPriority() );
        thisTaskLine += ", ";

        // Task Initial Offset
        thisTaskLine += String.valueOf(inTask.getInitialOffset());
        thisTaskLine += ", ";

        // Task Color
        thisTaskLine += String.valueOf(inTask.getTaskColor().getRGB());
        //thisTaskLine += ", ";

        return thisTaskLine;
    }

    protected String generateAllTaskConfigLines(TaskContainer inTaskContainer)
    {
        String resultLines = "";
        Boolean firstLoop = true;
        for (Task thisTask : inTaskContainer.getTasksAsArray()) {
            if ( firstLoop == true ) {
                firstLoop = false;
            } else {
                resultLines += "\r\n";
            }
            resultLines += generateTaskConfigLine( thisTask );
        }
        return resultLines;
    }

    protected Boolean writeTaskConfigsToFile(TaskContainer inTaskContainer, BufferedWriter inFileWriter)
    {
        String taskConfigLines = "";
        taskConfigLines += "# Format: ID, Type, Name, Period, Deadline, Computation Time, Priority, Initial Offset, RGB";
        taskConfigLines += "\r\n";
        taskConfigLines += "@TaskList";
        taskConfigLines += "\r\n";

        taskConfigLines += generateAllTaskConfigLines( inTaskContainer );
        ProgMsg.debugPutline(taskConfigLines);

        try {
            inFileWriter.write( taskConfigLines );
        } catch (IOException ex) {
            ex.printStackTrace();
            System.err.format("IOException @ writeTaskConfigToFile: Failed to write the data to the file.\r\n");
            return false;
        }

        return true;
    }

}
