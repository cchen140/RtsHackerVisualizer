package com.illinois.rts.simulator;

import com.illinois.rts.framework.Task;
import com.illinois.rts.visualizer.*;

import java.awt.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by CY on 2/16/2015.
 */
public class TaskSetFileHandler extends DialogFileHandler {
    private static final int CONFIG_BLOCK_UNKNOWN = 0;
    private static final int CONFIG_BLOCK_TASK_PARAMETERS = 1;
    private static final int CONFIG_BLOCK_TASK_LIST = 2;
    private static final int CONFIG_BLOCK_PROG_CONFIG = 3;

    private ArrayList<Integer> defaultTaskParamOrder = new ArrayList<>();
    private HashMap<Integer, String> taskParamString = new HashMap<>();
    private ArrayList<Integer> customTaskParamOrder = new ArrayList<>();

    private static final int TASK_PARAM_EMPTY = 0;
    private static final int TASK_PARAM_ID = 1;
    private static final int TASK_PARAM_TYPE = 2;
    private static final int TASK_PARAM_TITLE = 3;
    private static final int TASK_PARAM_PERIOD = 4;
    private static final int TASK_PARAM_COMP = 5;
    private static final int TASK_PARAM_DEADLINE = 6;
    private static final int TASK_PARAM_OFFSET = 7;
    private static final int TASK_PARAM_PRIORITY = 8;
    private static final int TASK_PARAM_COLOR = 9;

    private ArrayList<TaskContainer> taskContainers = new ArrayList<>();

    public TaskSetFileHandler()
    {
        defaultTaskParamOrder.add(TASK_PARAM_ID);
        defaultTaskParamOrder.add(TASK_PARAM_TYPE);
        defaultTaskParamOrder.add(TASK_PARAM_TITLE);
        defaultTaskParamOrder.add(TASK_PARAM_PERIOD);
        defaultTaskParamOrder.add(TASK_PARAM_DEADLINE);
        defaultTaskParamOrder.add(TASK_PARAM_COMP);
        defaultTaskParamOrder.add(TASK_PARAM_PRIORITY);
        defaultTaskParamOrder.add(TASK_PARAM_OFFSET);
        defaultTaskParamOrder.add(TASK_PARAM_COLOR);

        customTaskParamOrder.addAll(defaultTaskParamOrder);

        taskParamString.put(TASK_PARAM_ID, "Id");
        taskParamString.put(TASK_PARAM_TYPE, "Type");
        taskParamString.put(TASK_PARAM_TITLE, "Title");
        taskParamString.put(TASK_PARAM_PERIOD, "Period");
        taskParamString.put(TASK_PARAM_DEADLINE, "Deadline");
        taskParamString.put(TASK_PARAM_COMP, "Computation");
        taskParamString.put(TASK_PARAM_PRIORITY, "Priority");
        taskParamString.put(TASK_PARAM_OFFSET, "Offset");
        taskParamString.put(TASK_PARAM_COLOR, "Color");
    }


    public TaskContainer loadSingleTaskSetFromDialog() throws IOException
    {
        if (openFileFromDialog() == null)
        {
            return null;
        }

        if (loadTaskSets(fileReader) == false)
            throw new InvalidParameterException("Config file is incorrect.");
        else
            return taskContainers.get(0);
    }

    public ArrayList<TaskContainer> loadMultipleTaskSetsFromDialog() throws IOException
    {
        if (openFileFromDialog() == null)
        {
            return null;
        }

        if (loadTaskSets(fileReader) == false)
            throw new InvalidParameterException("Config file is incorrect.");
        else
            return taskContainers;
    }

    public ArrayList<TaskContainer> loadMultipleTaskSetsFromPath(String filePath) throws IOException
    {
        fileReader = openFile(filePath);
        if (fileReader == null)
            throw new IOException("IOException @ openFileFromPath(): File path is incorrect.");

        if (loadTaskSets(fileReader) == false)
            throw new InvalidParameterException("Task set file is incorrect.");
        else
            return taskContainers;
    }

//    public TaskContainer loadDemoConfig() throws IOException
//    {
//        String demoLogFilePath = "./log/demoConfig1.txt";
//        if (loadTaskSets(this.openFile(demoLogFilePath)) == false)
//            throw new InvalidParameterException("Demo config file is incorrect.");
//        else
//            return simTaskContainer;
//    }

    /**
     * Load task set(s) from the file and put them in corresponding task container.
     * @param fileReader The BufferedReader of the opened file.
     * @return return 'false' if it fails to load the log file. Otherwise, return 'true'.
     */
    private Boolean loadTaskSets(BufferedReader fileReader)
    {
        int lineCounter = 1;
        TaskContainer currentTaskContainer = null;

        try {
            String line = null;
            taskContainers.clear();
            int currentLogBlock = CONFIG_BLOCK_UNKNOWN;
            while ((line = fileReader.readLine()) != null)
            {
                lineCounter++;

                if (line.trim().length()==0) // Empty line
                    continue;
                else if (line.trim().substring(0, 1).equalsIgnoreCase("#")) // Comment line
                    continue;
                else if (line.trim().toLowerCase().equalsIgnoreCase("@TaskParameters")) {
                    currentLogBlock = CONFIG_BLOCK_TASK_PARAMETERS;
                    continue;
                }
                else if (line.trim().toLowerCase().equalsIgnoreCase("@ProgConfig")) {
                    currentLogBlock = CONFIG_BLOCK_PROG_CONFIG;
                    continue;
                }
                else if (line.trim().toLowerCase().contains("@TaskList".toLowerCase())) {
                    currentLogBlock = CONFIG_BLOCK_TASK_LIST;    // Reading task list block
                    currentTaskContainer = new TaskContainer();
                    taskContainers.add(currentTaskContainer);
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
                        Task newTask = parseTaskListLine(line);
                        if (newTask != null) {
                            currentTaskContainer.addTask(newTask);
                        } else {
                            System.err.format("Incorrect config file format at line %d.\n", lineCounter);
                            return false;
                        }
                        break;
                    case CONFIG_BLOCK_TASK_PARAMETERS:
                        parseTaskParametersLine(line);
                        break;
                    case CONFIG_BLOCK_PROG_CONFIG:
                        parseProgConfigLine(line);
                        break;
                    case CONFIG_BLOCK_UNKNOWN:  // Current parse as comments.
                    default:
                        break;
                }

            }

            if (currentLogBlock == 0) {
                //System.err.format("No valid block in the file.\r\n");
                //return false;
            }
        }
        catch (IOException x)
        {
            System.err.format("IOException @ reading file: %s%n", x);
            return false;
        }

        ProgMsg.sysPutLine("%d lines loaded from the task set file.", lineCounter);
        ProgMsg.sysPutLine("%d task set(s) loaded.", taskContainers.size());
        ProgMsg.debugPutline("%d task in first task set.", taskContainers.get(0).size());

        return true;
    }

    private Boolean parseTaskParametersLine(String line) {
        String splitStrings[] = line.split(",");

        customTaskParamOrder.clear();   // Reset variable before fills with new settings.
        for (String thisString : splitStrings) {
            // Note: Get ride of the "$" notation of each parameter and .
            customTaskParamOrder.add(taskParamStringToTypeInt(thisString.trim().substring(1)));
        }
        return true;
    }

    private Boolean parseProgConfigLine(String line) {
        String splitStrings[] = line.split("=");
        if (splitStrings.length == 2) {
            return ProgConfig.assignValueByVariableName(splitStrings[0].trim().substring(1), splitStrings[1].trim());
        } else {
            ProgMsg.errPutline("Invalid ProgConfig line: \"" + line + "\"");
            return false;
        }
    }

    /**
     * Parse one log line for task list block.
     * @param line One log line string to be parsed.
     * @return Task created task from the parsed line.
     */
    // id, task type, task name, period, computation time, priority
    private Task parseTaskListLine(String line)
    {
        String splitStrings[] = line.split(",");
        if (splitStrings.length == customTaskParamOrder.size()) {

            Task newTask = new Task();
            int paramIndex = 0;
            for (String thisLine : splitStrings) {
                setTaskAttributeByValueString(newTask, customTaskParamOrder.get(paramIndex), thisLine);
                paramIndex++;
            }
//
//            int taskId = Integer.valueOf(splitStrings[0].trim()).intValue();
//            int taskType = Integer.valueOf(splitStrings[1].trim()).intValue();
//            String taskTitle = splitStrings[2].trim().substring(1, splitStrings[2].trim().length() - 1);
//            int taskPeriod = Integer.valueOf(splitStrings[3].trim()).intValue();
//            int taskDeadline = Integer.valueOf(splitStrings[4].trim()).intValue();
//            int taskComputationTime = Integer.valueOf(splitStrings[5].trim()).intValue();
//            int taskPriority = Integer.valueOf(splitStrings[6].trim()).intValue();
//            int taskInitialOffset = Integer.valueOf(splitStrings[7].trim()).intValue();
//            Color taskColor = new Color( Integer.valueOf(splitStrings[8].trim()).intValue() ); //RGB
//
//            Task newTask = new Task(taskId, taskTitle, taskType, taskPeriod, taskComputationTime, taskPriority, taskDeadline);
//
//            newTask.initialOffset = 0;

            newTask.nextReleaseTime = newTask.initialOffset;

            newTask.WCRT = Long.MIN_VALUE;
            newTask.jobSeqNo = 0;
            newTask.lastReleaseTime = -1;
            newTask.lastFinishTime = 0;

//            newTask.setInitialOffset(taskInitialOffset);
//            newTask.setColor(taskColor);

            return newTask;
        }
        else {
            // Incorrect task list log format.
            ProgMsg.errPutline("Not enough parameters in task list.");
            return null;
        }
    }

    public Boolean exportSingleTaskSetByDialog(TaskContainer inTaskContainer) {
        TaskSetContainer singleTaskSetContainer = new TaskSetContainer();
        singleTaskSetContainer.addTaskSet(inTaskContainer);
        return exportTaskSetsByDialog(singleTaskSetContainer);
    }

    public Boolean exportTaskSetsByDialog(TaskSetContainer inTaskSets) {

        if (inTaskSets.size() == 0) {
            ProgMsg.debugPutline("No task set to be exported.");
            return true;
        }

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
        writeFileReturn = writeTaskSetsToFile(inTaskSets, fileWriter);
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
            ProgMsg.errPutline("Something wrong.");
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
    protected String generateTaskLines(Task inTask)
    {
        if ( inTask == null ) {
            return "";
        }

        String thisTaskLine = "";

        Boolean firstLoop = true;
        for (Integer paramType : defaultTaskParamOrder) {
            if (firstLoop == true) {
                firstLoop = false;
            } else {
                thisTaskLine += ", ";
            }

            thisTaskLine += getTaskAttributeAsString(inTask, paramType);
        }

        return thisTaskLine;
    }

    protected String generateTaskSetLines(TaskContainer inTaskContainer)
    {
        String resultLines = "";
        Boolean firstLoop = true;
        for (Task thisTask : inTaskContainer.getAppTasksAsArray()) {
            if ( firstLoop == true ) {
                firstLoop = false;
            } else {
                resultLines += "\r\n";
            }
            resultLines += generateTaskLines(thisTask);
        }
        return resultLines;
    }

    public String generateTaskSetContainerLines(TaskSetContainer inTaskSets) {
        String outputLines = "";
        int taskSetIndex = 1;
        for (TaskContainer thisTaskSet : inTaskSets.getTaskContainers()) {
            outputLines += "@TaskList " + taskSetIndex;
            outputLines += "\r\n";

            outputLines += generateTaskSetLines(thisTaskSet);
            //ProgMsg.debugPutline(outputLines);

            outputLines += "\r\n";
            outputLines += "#HP=" + thisTaskSet.calHyperPeriod()*ProgConfig.TIMESTAMP_UNIT_TO_MS_MULTIPLIER + " ms";

            outputLines += "\r\n";
            taskSetIndex++;
        }
        return outputLines;
    }

    public String generateTaskParamsLines() {
        String outputLines = "";

        outputLines += "@TaskParameters";
        outputLines += "\r\n";

        Boolean firstLoop = true;
        for (Integer thisParamType : defaultTaskParamOrder) {
            if (firstLoop == true) {
                firstLoop = false;
            } else {
                outputLines += ", ";
            }
            outputLines += "$" + taskParamTypeToString(thisParamType);
        }
        outputLines += "\r\n";
        return outputLines;
    }

    public String generateProgConfigLines() {
        String outputLines = "";

        outputLines += "@ProgConfig";
        outputLines += "\r\n";

        outputLines += "$TIMESTAMP_UNIT_NS = " + String.valueOf(ProgConfig.TIMESTAMP_UNIT_NS);
        outputLines += "\r\n";

        outputLines += "$TRACE_HORIZONTAL_SCALE_FACTOR = " + String.valueOf(ProgConfig.TRACE_HORIZONTAL_SCALE_FACTOR);
        outputLines += "\r\n";

        return outputLines;
    }

    protected Boolean writeTaskSetsToFile(TaskSetContainer inTaskSets, BufferedWriter inFileWriter)
    {
        String outputLines = "";

        outputLines += generateProgConfigLines();

//        outputLines += "@TaskParameters";
//        outputLines += "\r\n";
//
//        Boolean firstLoop = true;
//        for (Integer thisParamType : defaultTaskParamOrder) {
//            if (firstLoop == true) {
//                firstLoop = false;
//            } else {
//                outputLines += ", ";
//            }
//            outputLines += "$" + taskParamTypeToString(thisParamType);
//        }
//        outputLines += "\r\n";
        outputLines += generateTaskParamsLines();


//        int taskSetIndex = 1;
//        for (TaskContainer thisTaskSet : inTaskSets.getTaskContainers()) {
//            outputLines += "@TaskList " + taskSetIndex;
//            outputLines += "\r\n";
//
//            outputLines += generateTaskSetLines(thisTaskSet);
//            //ProgMsg.debugPutline(outputLines);
//
//            outputLines += "\r\n";
//            taskSetIndex++;
//        }

        outputLines += generateTaskSetContainerLines(inTaskSets);


        try {
            inFileWriter.write( outputLines );
        } catch (IOException ex) {
            ex.printStackTrace();
            System.err.format("IOException @ writeTaskConfigToFile: Failed to write the data to the file.\r\n");
            return false;
        }

        return true;
    }

    private Integer taskParamStringToTypeInt(String inString) {
        for (Map.Entry<Integer, String> paramSet : taskParamString.entrySet()) {
            if (inString.trim().equalsIgnoreCase(paramSet.getValue())) {
                return paramSet.getKey();
            }
        }
        return 0;
    }

    private String taskParamTypeToString(int inParamType) {
        // TODO: Need to handle in the case the parameter type is not found.
        return taskParamString.get(inParamType);
    }

    private Boolean setTaskAttributeByValueString(Task inTask, int inParamType, String inValueString) {

        int integerValue = 0;
        String stringValue = "";

        /* Convert the value to what we need first. */
        switch (inParamType) {
            case TASK_PARAM_ID:
            case TASK_PARAM_TYPE:
            case TASK_PARAM_COMP:
            case TASK_PARAM_PERIOD:
            case TASK_PARAM_DEADLINE:
            case TASK_PARAM_OFFSET:
            case TASK_PARAM_COLOR:
                integerValue = Integer.valueOf(inValueString.trim()).intValue();
                break;

            case TASK_PARAM_TITLE:
                stringValue = inValueString.trim().substring(1, inValueString.trim().length() - 1);
                break;

            default:
                return false;
        }

        /* Set the attribute to the task. */
        switch (inParamType) {
            case TASK_PARAM_ID:
                inTask.setId(integerValue);
                break;
            case TASK_PARAM_TYPE:
                inTask.setTaskType(integerValue);
                break;
            case TASK_PARAM_COMP:
                inTask.setComputationTimeNs(integerValue);
                break;
            case TASK_PARAM_PERIOD:
                inTask.setPeriodNs(integerValue);
                break;
            case TASK_PARAM_DEADLINE:
                inTask.setDeadlineNs(integerValue);
                break;
            case TASK_PARAM_OFFSET:
                inTask.setInitialOffset(integerValue);
                break;
            case TASK_PARAM_COLOR:
                inTask.setColor(new Color(integerValue));
                break;

            case TASK_PARAM_TITLE:
                inTask.setTitle(stringValue);
                break;

            default:
                return false;
        }

        return true;
    }

    private String getTaskAttributeAsString(Task inTask, int inParamType) {
        String resultString = "";
        switch (inParamType) {
            case TASK_PARAM_ID:
                resultString = String.valueOf(inTask.getId());
                break;
            case TASK_PARAM_TYPE:
                resultString = String.valueOf(inTask.getTaskType());
                break;
            case TASK_PARAM_COMP:
                resultString = String.valueOf(inTask.getComputationTimeNs());
                break;
            case TASK_PARAM_PERIOD:
                resultString = String.valueOf(inTask.getPeriodNs());
                break;
            case TASK_PARAM_DEADLINE:
                resultString = String.valueOf(inTask.getDeadlineNs());
                break;
            case TASK_PARAM_OFFSET:
                resultString = String.valueOf(inTask.getInitialOffset());
                break;
            case TASK_PARAM_COLOR:
                resultString = String.valueOf(inTask.getTaskColor().getRGB());
                break;
            case TASK_PARAM_TITLE:
                resultString = "\"" + inTask.getTitle() + "\"";
                break;

            default:
                return "";
        }
        return resultString;
    }

}
