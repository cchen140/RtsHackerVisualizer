package com.illinois.rts.analysis.busyintervals;

import com.illinois.rts.framework.Task;
import com.illinois.rts.visualizer.Event;
import com.illinois.rts.visualizer.TaskIntervalEvent;

import java.util.ArrayList;

/**
 * Created by CY on 5/21/2015.
 */
public class BusyInterval {
    private int beginTimeStampNs = 0;
    private int endTimeStampNs = 0;
    TaskReleaseEventContainer compositionGroundTruth;
    TaskReleaseEventContainer compositionInference = new TaskReleaseEventContainer();
    TaskArrivalEventContainer arrivalInference = new TaskArrivalEventContainer();
    ArrayList schedulingInference = new ArrayList<>();

    // There may have multiple inferences, so two-layer array is used here.
    private ArrayList<ArrayList<Task>> composition = new ArrayList<>();

    public BusyInterval(int inBeginTimeStamp, int inEndTimeStamp)
    {
        beginTimeStampNs = inBeginTimeStamp;
        endTimeStampNs = inEndTimeStamp;
    }

    public void setCompositionGroundTruth(TaskReleaseEventContainer inGroundTruth)
    {
        compositionGroundTruth = inGroundTruth;
    }

    public void setComposition(ArrayList<ArrayList<Task>> inComposition)
    {
        composition = inComposition;
    }

    public int getIntervalNs()
    {
        return (endTimeStampNs - beginTimeStampNs);
    }

    public int getBeginTimeStampNs()
    {
        return beginTimeStampNs;
    }

    public TaskReleaseEventContainer getCompositionGroundTruth()
    {
        return  compositionGroundTruth;
    }
    
    public ArrayList<ArrayList<Task>> getComposition()
    {
        return composition;
    }

    public int getEndTimeStampNs() {
        return endTimeStampNs;
    }

    public Boolean contains(int inTimeStamp)
    {
        if ((beginTimeStampNs <= inTimeStamp)
            && (endTimeStampNs >= inTimeStamp))
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public Boolean containsComposition(Task inTask)
    {
        // If any inferred compositions contain inTask, then return true.
        for (ArrayList<Task> thisComposition : composition)
        {
            if (thisComposition.contains(inTask) == true)
                return true;
        }
        return false;
    }

    /* Get the first element in the composition array.
     * This method is used when there is only one inference in each busy interval.
     */
    public ArrayList<Task> getFirstComposition()
    {
        if (composition.size() == 0)
        {
            composition.add(new ArrayList<Task>());
        }

        return composition.get(0);
    }

    public ArrayList<Event> compositionInferenceToEvents()
    {
        ArrayList<Event> resultEvents = new ArrayList<>();
        int numOfInferences = getComposition().size();
        int countOfInferences = 1;  // It indicates the position of the inference when there are multiple inferences.
        for (ArrayList<Task> thisCompositionArray : getComposition())
        {
            int currentTimeStamp = getBeginTimeStampNs();
            for (Task thisTask : thisCompositionArray)
            {
                TaskIntervalEvent thisEvent = new TaskIntervalEvent(currentTimeStamp, currentTimeStamp+thisTask.getComputationTimeNs(), thisTask, "a");

                // Set the number of inferences in this busy interval for graphic display.
                thisEvent.getDrawInterval().setLayerPosition(numOfInferences, countOfInferences);

                resultEvents.add(thisEvent);
                currentTimeStamp += thisTask.getComputationTimeNs();
            }

            countOfInferences++;
        }
        return resultEvents;
    }

}
