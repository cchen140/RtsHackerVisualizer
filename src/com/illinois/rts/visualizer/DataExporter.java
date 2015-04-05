package com.illinois.rts.visualizer;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.security.InvalidParameterException;
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
        if (generateMathematicalData(eventContainer, fileWriter) == true) {
            ProgMsg.putLine("Successfully generate mathematical data file.");
            fileWriter.close();
        }
        else
        {

        }

    }

    /**
     * Generate mathematical data from events..
     * @param inEventContainer the events to be analyzed.
     * @param inFileWriter the BufferedWriter of the file to be written.
     * @return 'true' if succeeded and 'false' if something is wrong.
     */
    protected Boolean generateMathematicalData(EventContainer inEventContainer, BufferedWriter inFileWriter)
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
}
