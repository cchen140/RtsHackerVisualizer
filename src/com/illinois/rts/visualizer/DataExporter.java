package com.illinois.rts.visualizer;

import com.illinois.rts.framework.Task;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by CY on 4/5/2015.
 */
public class DataExporter extends DialogFileHandler{
    private EventContainer eventContainer = null;

    public DataExporter(EventContainer inEventContainer)
    {
        eventContainer = inEventContainer;
    }


    public void exportMathematicalDataFromDialog() throws IOException {

        openWriteFileFromDialog();  // set up fileWriter.
        if (generateBusyIntervalDataFromScheduler(eventContainer, fileWriter) == true) {
            ProgMsg.putLine("Successfully generate busy interval data file.");
            fileWriter.close();
        }
        else
        {
            ProgMsg.errPutline("Failed to generate busy interval data file");
        }

    }

    /**
     * Generate mathematical data from events..
     * @param inEventContainer the events to be analyzed.
     * @param inFileWriter the BufferedWriter of the file to be written.
     * @return 'true' if succeeded and 'false' if something is wrong.
     */
    protected Boolean generateBusyIntervalDataFromHacker(EventContainer inEventContainer, BufferedWriter inFileWriter)
    {
        int previousTimeStamp = 0;
        ArrayList<HackerEvent> lowHackerEvents = inEventContainer.getLowHackerEvents();
        ArrayList<AppEvent> appEvents = inEventContainer.getAppEvents();

        for (HackerEvent currentEvent : lowHackerEvents)
        {
            int currentTimeStamp = currentEvent.getOrgBeginTimestampNs();
            int lowHackerMeasuredInterval = currentEvent.getRecordData();
            if (lowHackerMeasuredInterval >= 800000)
            {
                String groundTruthString = "";
                Boolean firstLoop = true;

                groundTruthString += "[";
                for (AppEvent currentAppEvent : appEvents)
                {
                    if ( (currentAppEvent.getOrgBeginTimestampNs() >= previousTimeStamp)
                            && (currentAppEvent.getOrgBeginTimestampNs() <= currentTimeStamp))
                    {
                        if (firstLoop == true) {
                            firstLoop = false;
                        }
                        else {
                            groundTruthString += ", ";
                        }

                        groundTruthString += currentAppEvent.getTask().getTitle();
                    }

                }
                groundTruthString += "]";

                try {
                    inFileWriter.write(previousTimeStamp + ", " + currentEvent.getRecordData()*3 + ", " + groundTruthString + "\r\n");
                } catch (IOException ex) {
                    ex.printStackTrace();
                    System.err.format("IOException @ generateMathematicalData: Failed to write the data to the file.\r\n");
                    return false;
                }

            }

            previousTimeStamp = currentEvent.getOrgBeginTimestampNs();
        }

        return true;
    }


    /* This method uses the memo attribute of scheduler events to determine whether it is "BEGIN" of
       a task or "IDLE" status.
    */
    protected Boolean generateBusyIntervalDataFromScheduler(EventContainer inEventContainer, BufferedWriter inFileWriter)
    {
        ArrayList<SchedulerEvent> schedulerEvents = inEventContainer.getSchedulerEvents();
        int idleTaskId = 0;

        // Find IDLE task ID
        for (Object currentObject : inEventContainer.getTaskContainer().getTasksAsArray())
        {
            Task currentTask = (Task) currentObject;
            if (currentTask.getTitle().equalsIgnoreCase("IDLE")){
                idleTaskId = currentTask.getId();
                break;
            }
        }

        Boolean busyIntervalFound = false;
        int beginTimeStamp = 0;
        String groundTruthString = "";
        for (SchedulerEvent currentEvent: schedulerEvents)
        {
            if (busyIntervalFound == false)
            {
                if (currentEvent.getTask().getId() == idleTaskId) {
                    continue;
                }
                else
                { // Busy interval is found.
                    busyIntervalFound = true;

                    beginTimeStamp = currentEvent.getOrgBeginTimestampNs();

                    groundTruthString = "[";
                    groundTruthString += currentEvent.getTask().getId();
                    continue;
                }
            }

            if (currentEvent.getTask().getId() == idleTaskId)
            { // This is the end of a busy interval.
                groundTruthString += "]";
                try {
                    inFileWriter.write(beginTimeStamp + ", " + (currentEvent.getOrgBeginTimestampNs()-beginTimeStamp) + ", " + groundTruthString + "\r\n");
                } catch (IOException ex) {
                    ex.printStackTrace();
                    System.err.format("IOException @ generateMathematicalData: Failed to write the data to the file.\r\n");
                    return false;
                }

                busyIntervalFound = false;
            }
            else
            {
                if (currentEvent.getNote().equalsIgnoreCase("BEGIN"))
                { // This is the beginning of a task, not a resuming task that was preempted.
                    groundTruthString += ", " + currentEvent.getTask().getId();
                }
            }

        } // End of scheduler events iteration loop.
        return true;
    }
}

