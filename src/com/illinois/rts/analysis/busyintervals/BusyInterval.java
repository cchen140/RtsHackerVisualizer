package com.illinois.rts.analysis.busyintervals;

import com.illinois.rts.framework.Task;
import com.illinois.rts.visualizer.Event;
import com.illinois.rts.visualizer.TaskContainer;
import com.illinois.rts.visualizer.TaskIntervalEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * Created by CY on 5/21/2015.
 */
public class BusyInterval {
    private int beginTimeStampNs = 0;
    private int endTimeStampNs = 0;
    
    // Ground truth contains the start point of each job in this busy interval.
    StartTimeEventContainer startTimesGroundTruth;
    StartTimeEventContainer startTimesInference = new StartTimeEventContainer();
    ArrayList schedulingInference = new ArrayList<>();  // for plotting result trace.
    
    // The inference of the arrival points for each job.
    TaskArrivalEventContainer arrivalInference = new TaskArrivalEventContainer();
    
    // There may have multiple inferences, so two-layer array is used here.
    private ArrayList<ArrayList<Task>> composition = new ArrayList<>();

    private HashMap<Task, ArrayList<Integer>> NkValues = new HashMap<>();

    private HashMap<Task, Boolean> isArrivalTimeWindowParsedAndFixed = new HashMap<>();

    public BusyInterval(int inBeginTimeStamp, int inEndTimeStamp)
    {
        beginTimeStampNs = inBeginTimeStamp;
        endTimeStampNs = inEndTimeStamp;
    }

    public void setStartTimesGroundTruth(StartTimeEventContainer inGroundTruth)
    {
        startTimesGroundTruth = inGroundTruth;
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

    public StartTimeEventContainer getStartTimesInference() {
        return startTimesInference;
    }

    public StartTimeEventContainer getStartTimesGroundTruth()
    {
        return startTimesGroundTruth;
    }
    
    public ArrayList<ArrayList<Task>> getComposition()
    {
        return composition;
    }

    public int getEndTimeStampNs() {
        return endTimeStampNs;
    }

    public HashMap<Task, ArrayList<Integer>> getNkValues() {
        return NkValues;
    }

    public ArrayList<Integer> getNkValuesOfTask(Task inTask) {
        return NkValues.get(inTask);
    }

    public void setNkValuesOfTask(Task inTask, ArrayList<Integer> nkValues) {
        NkValues.put(inTask, nkValues);
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

    public Boolean containsTaskCheckedByNkValues(Task inTask) {
        if (getMaxNkValueOfTask(inTask) > 0) {
            return true;
        } else {
            return false;
        }

    }

    public int getMaxNkValueOfTask(Task inTask) {
        int maxValue = 0;
        for (int thisNk : getNkValues().get(inTask)) {
            maxValue = thisNk > maxValue ? thisNk : maxValue;
        }
        return maxValue;
    }

    public int getMinNkValueOfTask(Task inTask) {
        int minValue = 0;
        Boolean firstLoop = true;
        for (int thisNk : getNkValues().get(inTask)) {
            if (firstLoop == true) {
                minValue = thisNk;
                firstLoop = false;
                continue;
            }
            minValue = thisNk < minValue ? thisNk : minValue;
        }
        return minValue;
    }

//    public Boolean containsComposition(Task inTask)
//    {
//        // If any inferred compositions contain inTask, then return true.
//        for (ArrayList<Task> thisComposition : composition)
//        {
//            if (thisComposition.contains(inTask) == true)
//                return true;
//        }
//        return false;
//    }

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

    public Boolean getIsArrivalTimeWindowParsedAndFixed(Task inTask) {
        return isArrivalTimeWindowParsedAndFixed.get(inTask);
    }

    public void setIsArrivalTimeWindowParsedAndFixed(Task inTask, Boolean isParsedAndFixed) {
        isArrivalTimeWindowParsedAndFixed.put(inTask, isParsedAndFixed);
    }

    public Boolean isNkValuesAmbiguous() {
        for (ArrayList<Integer> thisNks : NkValues.values()) {
            if (thisNks.size() > 1) {
                return true;
            }
        }
        return false;
    }

    public void updateNkValuesFromCompositions(TaskContainer inTaskContainer) {
        NkValues.clear();
        for (ArrayList<Task> thisComposition : composition) {
            for (Task thisTask : inTaskContainer.getAppTasksAsArray()) {
                int thisNkOfTask = Collections.frequency(thisComposition, thisTask);
                ArrayList<Integer> existingNkOfTask = NkValues.get(thisTask);
                if ( existingNkOfTask == null) {
                    // First value
                    existingNkOfTask = new ArrayList<>();
                    existingNkOfTask.add(thisNkOfTask);
                    NkValues.put(thisTask, existingNkOfTask);
                } else if ( existingNkOfTask.contains(thisNkOfTask) == false ) {
                    existingNkOfTask.add(thisNkOfTask);
                }
            }
        }
    }
}
