package com.illinois.rts.analysis.busyintervals;

import com.illinois.rts.framework.Task;
import com.illinois.rts.visualizer.TaskContainer;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * Created by CY on 5/21/2015.
 */
public class Decomposition {
    TaskContainer taskContainer;

    public Decomposition(TaskContainer inTaskContainer)
    {
        taskContainer = inTaskContainer;
    }

    public Boolean calculateCompositionToBusyIntervalContainer(BusyIntervalContainer inBusyIntervalContainer)
    {
        for (BusyInterval thisBusyInterval : inBusyIntervalContainer.getBusyIntervals())
        {
            thisBusyInterval.setComposition(calculateComposition(thisBusyInterval));
        }
        return true;
    }

    /* */
    public ArrayList<ArrayList<Task>> calculateComposition(BusyInterval inBusyInterval)
    {
        int intervalNs = inBusyInterval.getIntervalNs();
//        int matchingInterval = 0;

        /* Calculate N of each task. */
        HashMap<Integer, ArrayList<Integer>> nOfTasks = new HashMap<Integer, ArrayList<Integer>>();
        for (Object thisObject: taskContainer.getAppTasksAsArray())
        {
            Task thisTask = (Task) thisObject;
            ArrayList<Integer> thisResult = new ArrayList<Integer>();

            int thisP = thisTask.getPeriodNs();
            int thisC = thisTask.getComputationTimeNs();

            int numberOfCompletePeriods = (int) Math.floor(intervalNs / thisP);
            int subIntervalNs = intervalNs - numberOfCompletePeriods*thisP;

            if (subIntervalNs < thisC)
            {// This task can only have occurred 0 time in this sub-interval.
                thisResult.add(numberOfCompletePeriods + 0);
            }
            else if (subIntervalNs < (thisP-thisC))
            {// This task can have occurred 0 or 1 time in this sub-interval.
                thisResult.add(numberOfCompletePeriods + 0);
                thisResult.add(numberOfCompletePeriods + 1);
            }
            else // if (subIntervalNs < thisP)
            {// This task can only have occurred 1 times in this sub-interval.
                thisResult.add(numberOfCompletePeriods + 1);
            }

            nOfTasks.put(thisTask.getId(), thisResult);
//            matchingInterval += thisResult.get(0);
        }

        // Find Ns match this interval.
        ArrayList<ArrayList<Task>> resultCompositions;// = new ArrayList<HashMap<Integer, Integer>>();//HashMap<Integer, Integer>();
        resultCompositions = findMatchingCompositions(nOfTasks, intervalNs, null);
//        System.out.println(resultsNOfTasks);
        return resultCompositions;

    }

    private ArrayList<ArrayList<Task>> findMatchingCompositions(HashMap<Integer, ArrayList<Integer>> inNOfTasks, int inTargetInterval, HashMap<Integer, Integer> inProcessingNOfTasks)
    {
        ArrayList<ArrayList<Task>> resultCompositions = new ArrayList<ArrayList<Task>>();

        if (inNOfTasks.isEmpty())
        {
            /* Compute the interval from current compositions. */
            int compositeInterval = 0;
            for (int thisTaskId : inProcessingNOfTasks.keySet())
            {
                compositeInterval += taskContainer.getTaskById(thisTaskId).getComputationTimeNs() * inProcessingNOfTasks.get(thisTaskId);
            }
//            System.out.format("End of recursive calls, %d\r\n", compositeInterval);

            /* Check whether current composite interval equals target interval or not. */
            if (compositeInterval == inTargetInterval)
            {
//                System.out.println("Matched!!");
//                System.out.println(inProcessingNOfTasks);

                ArrayList thisResultComposition = new ArrayList<Task>();
                for (int thisTaskId : inProcessingNOfTasks.keySet())
                {
                    for (int loop=0; loop<inProcessingNOfTasks.get(thisTaskId); loop++)
                    {
                        thisResultComposition.add(taskContainer.getTaskById(thisTaskId));
                    }
                }
                resultCompositions.add(thisResultComposition);
                return resultCompositions;
            }
            else
            {
                /* Because addAll() doesn't accept null pointer, thus returning empty arrayList instead. */
                //return null;
                return resultCompositions;
            }
        }

        /* Select an unsorted task to process and create a list which contains rest of n values of unsorted tasks. */
        int thisTaskId = inNOfTasks.keySet().iterator().next();
        ArrayList<Integer> nOfThisTask = inNOfTasks.get(thisTaskId);
        HashMap<Integer, ArrayList<Integer>> restNOfTasks = new HashMap<Integer, ArrayList<Integer>>(inNOfTasks);
        restNOfTasks.remove(thisTaskId);

        if (inProcessingNOfTasks == null)
        { // For the first time the program gets here, inProcessingNOfTasks has to be initialized.
            inProcessingNOfTasks = new HashMap<Integer, Integer>();
        }

        // Iterate every possible n value of current task and pass the value down recursively.
        for (Integer thisN: nOfThisTask)
        {
            inProcessingNOfTasks.put(thisTaskId, thisN);
            resultCompositions.addAll(findMatchingCompositions(restNOfTasks, inTargetInterval, inProcessingNOfTasks));
            inProcessingNOfTasks.remove(thisTaskId);
        }
        return resultCompositions;
    }



}
