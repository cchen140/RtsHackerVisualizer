package com.illinois.rts.analysis.busyintervals;

import com.illinois.rts.framework.Task;
import com.illinois.rts.visualizer.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * Created by CY on 6/20/2015.
 */
public class AmirDecomposition {
    private TaskContainer taskContainer;
    private BusyIntervalContainer busyIntervalContainer;
    private HashMap<Task, Interval> taskArrivalTimeWindows = new HashMap<Task, Interval>();

    public AmirDecomposition(TaskContainer inTaskContainer, BusyIntervalContainer inBusyIntervalContainer)
    {
        taskContainer = inTaskContainer;
        busyIntervalContainer = inBusyIntervalContainer;
    }

    public Boolean runDecompositionStep1()
    {
        // TODO: test for skipping the first few busy intervals
        busyIntervalContainer.removeBusyIntervalsBeforeTimeStamp(taskContainer.getLargestPeriod());

        // Step one: finding n values for every task in every busy interval.
        for (BusyInterval thisBusyInterval : busyIntervalContainer.getBusyIntervals())
        {
            thisBusyInterval.setComposition(calculateComposition(thisBusyInterval));
        }
        return true;
    }

    public Boolean runDecompositionStep2() throws RuntimeException
    {
        // Step two: creating arrival time window for each task by processing the result from step one.

        int passCount = 1;
        while (true) {
            //ProgMsg.debugPutline("Start calculating arrival time window: %d pass.", passCount);
            calculateArrivalTimeOfAllTasks();

            //ProgMsg.debugPutline("Removing ambiguous inference: %d pass.", passCount);
            Boolean isSomethingChanged = removeAmbiguousInferenceByArrivalTimeWindow();

            if (isSomethingChanged == false) {
                break;
            } else {
                passCount++;
            }
        }
        ProgMsg.debugPutline("Removing ambiguous inference: %d pass.", passCount);
        return true;
    }

    public Boolean runDecompositionStep3()
    {
        reconstructCompositionOfBusyIntervalByArrivalTimeWindows();

        return true;
    }

    public Boolean runDecomposition() throws RuntimeException
    {

        // Step one: finding n values for every task in every busy interval.
        runDecompositionStep1();

        // Step two: creating arrival time window for each task by processing the result from step one.
        runDecompositionStep2();

        // Step three: arrival time to scheduling.
        runDecompositionStep3();

        return true;
    }

    public Boolean runDecompositionWithErrors()
    {
        // Step one: finding n values for every task in every busy interval.
        for (BusyInterval thisBusyInterval : busyIntervalContainer.getBusyIntervals())
        {
            thisBusyInterval.setComposition(calculateCompositionWithErrors(thisBusyInterval));
        }

        // Step two: creating arrival time window for each task by processing the result from step one.
        runDecompositionStep2();

        // Step three: arrival time to scheduling.
        runDecompositionStep3();

        return true;
    }

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

        // TODO: test if we don't calculate C in the beginning
        resultCompositions = getWhateverCompositions(nOfTasks, intervalNs, null);
        //resultCompositions = findMatchingCompositions(nOfTasks, intervalNs, null);
//        System.out.println(resultsNOfTasks);
        return resultCompositions;

    }

    public ArrayList<ArrayList<Task>> calculateCompositionWithErrors(BusyInterval inBusyInterval)
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
            int thisCError = thisTask.getComputationTimeErrorNs();

            int numberOfCompletePeriods = (int) Math.floor(intervalNs / thisP);
            int subIntervalNs = intervalNs - numberOfCompletePeriods*thisP;

            if (subIntervalNs < thisC-thisCError)
            {// This task can only have occurred 0 time in this sub-interval.
                thisResult.add(numberOfCompletePeriods + 0);
            }
            else if (subIntervalNs < (thisP-thisC+thisCError))
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
        resultCompositions = findMatchingCompositionsWithErrors(nOfTasks, intervalNs, null);
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

    private ArrayList<ArrayList<Task>> findMatchingCompositionsWithErrors(HashMap<Integer, ArrayList<Integer>> inNOfTasks, int inTargetInterval, HashMap<Integer, Integer> inProcessingNOfTasks)
    {
        ArrayList<ArrayList<Task>> resultCompositions = new ArrayList<ArrayList<Task>>();
        if (inNOfTasks.isEmpty())
        {
            /* Compute the interval from current compositions. */
            int compositeInterval = 0;
            int accumulatedCErrors = 0;
            for (int thisTaskId : inProcessingNOfTasks.keySet())
            {
                int thisC = taskContainer.getTaskById(thisTaskId).getComputationTimeNs();
                int thisCError = taskContainer.getTaskById(thisTaskId).getComputationTimeErrorNs();
                compositeInterval += thisC * inProcessingNOfTasks.get(thisTaskId);
                accumulatedCErrors += thisCError * inProcessingNOfTasks.get(thisTaskId);
            }
            //ProgMsg.debugPutline("End of recursive calls, %d\r\n", compositeInterval);

            /* Check whether current composite interval equals target interval or not. */
            if (areEqualWithinError(compositeInterval, inTargetInterval, accumulatedCErrors) == true)
            {
                //ProgMsg.debugPutline("Matched!!");
                //ProgMsg.debugPutline(String.valueOf(inProcessingNOfTasks));

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
            resultCompositions.addAll(findMatchingCompositionsWithErrors(restNOfTasks, inTargetInterval, inProcessingNOfTasks));
            inProcessingNOfTasks.remove(thisTaskId);
        }
        return resultCompositions;
    }

    private ArrayList<ArrayList<Task>> getWhateverCompositions(HashMap<Integer, ArrayList<Integer>> inNOfTasks, int inTargetInterval, HashMap<Integer, Integer> inProcessingNOfTasks)
    {
        ArrayList<ArrayList<Task>> resultCompositions = new ArrayList<ArrayList<Task>>();

        if (inNOfTasks.isEmpty())
        {
            /* Compute the interval from current compositions. */
//            int compositeInterval = 0;
//            for (int thisTaskId : inProcessingNOfTasks.keySet())
//            {
//                compositeInterval += taskContainer.getTaskById(thisTaskId).getComputationTimeNs() * inProcessingNOfTasks.get(thisTaskId);
//            }
//            System.out.format("End of recursive calls, %d\r\n", compositeInterval);

            /* Check whether current composite interval equals target interval or not. */
            //if (compositeInterval == inTargetInterval)
            if (true)
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

    public Boolean areEqualWithinError(int inNum01, int inNum02, int inErrorRange)
    {
        return Math.abs(inNum01-inNum02)<=inErrorRange ? true : false;
    }

    public Boolean calculateArrivalTimeOfAllTasks() throws RuntimeException
    {
        for (Task thisTask : taskContainer.getAppTasksAsArray()){
            Interval thisInterval = calculateArrivalTimeWindowOfTask(thisTask, taskArrivalTimeWindows.get(thisTask));
            if (thisInterval == null) {
                //return false;
            } else {
                taskArrivalTimeWindows.put(thisTask, thisInterval);
                //ProgMsg.debugPutline("%s, %d:%d ms", thisTask.getTitle(), (int)(thisInterval.getBegin()*ProgConfig.TIMESTAMP_UNIT_TO_MS_MULTIPLIER), (int)(thisInterval.getEnd()*ProgConfig.TIMESTAMP_UNIT_TO_MS_MULTIPLIER));
            }
        }

        // TODO: Should return false if any arrival time window is null? No
        return true;
    }

    public Interval calculateArrivalTimeWindowOfTask(Task inTask, Interval inFirstWindow) throws RuntimeException
    {
        ArrayList<BusyInterval> thisTaskBusyIntervals;

        /* Find the busy intervals that contain inTask. */
        thisTaskBusyIntervals = busyIntervalContainer.findBusyIntervalsByTask(inTask);

        Interval firstWindow = null;

        if (inFirstWindow != null) {
            firstWindow = inFirstWindow;
        }
        else {
            BusyInterval shortestBusyIntervalContainingTask = findShortestBusyIntervalContainingTask(inTask);
            if (shortestBusyIntervalContainingTask == null) {
                // Unable to find the initial busy interval for calculating the arrival time window.
                ProgMsg.errPutline("Unable to find the initial busy interval for calculating the arrival time window for %s.", inTask.getTitle());
                //throw new RuntimeException(String.format("Unable to find the initial busy interval for calculating the arrival time window for task '%s'.", inTask.getTitle()));
                return null;
            } else {
                firstWindow = calculateArrivalTimeWindowOfTaskInABusyInterval(shortestBusyIntervalContainingTask, inTask);
            }
        }
        // Move the window to around zero point.
        firstWindow.shift(-(firstWindow.getEnd() / inTask.getPeriodNs()) * inTask.getPeriodNs());
        //ProgMsg.debugPutline("first window of %s, %d:%d ms", inTask.getTitle(), (int)(firstWindow.getBegin()*ProgConfig.TIMESTAMP_UNIT_TO_MS_MULTIPLIER), (int)(firstWindow.getEnd()*ProgConfig.TIMESTAMP_UNIT_TO_MS_MULTIPLIER));



        Boolean anyoneHasTwoIntersectionsInTheEnd = false;
        Interval lastWindow = new Interval(firstWindow);
//        int DEBUG_COUNT = 0;
//        do {
//            DEBUG_COUNT++;
            anyoneHasTwoIntersectionsInTheEnd = false;
            lastWindow.setBegin(firstWindow.getBegin());
            lastWindow.setEnd(firstWindow.getEnd());

            for (BusyInterval thisBusyInterval : thisTaskBusyIntervals) {
                // The first busy interval should be the leftmost one (or the first valid one).
//                if (firstLoop == true) {
//                    firstWindow = calculateArrivalTimeWindowOfTaskInABusyInterval(thisBusyInterval, inTask);
//                    // If this windows is invalid, then search for next valid window.
//                    if (firstWindow != null) {
//                        firstLoop = false;
//                    }
//                    continue;
//                }


                /* Get current arrival window and check whether the window is valid or not. */
                Interval thisWindow = calculateArrivalTimeWindowOfTaskInABusyInterval(thisBusyInterval, inTask);
                if (thisWindow == null) {
                    /* This could happen if it contains 0 or 1 arrival. */
                    continue;
                }

                Integer smallestShiftPeriodValue = findSmallestPeriodShiftValueWithIntersection(firstWindow, thisWindow, inTask.getPeriodNs());

                if (smallestShiftPeriodValue == null) {// No intersection.
                    ProgMsg.errPutline("No intersection! Should not ever happen!!");
                    ProgMsg.errPutline("\t- %d:%d ms", (int) (thisWindow.getBegin() * ProgConfig.TIMESTAMP_UNIT_TO_MS_MULTIPLIER), (int) (thisWindow.getEnd() * ProgConfig.TIMESTAMP_UNIT_TO_MS_MULTIPLIER));
                    continue;
                } else {// Has intersection.
                    Interval shiftedThisWindow = new Interval(thisWindow);
                    shiftedThisWindow.shift(smallestShiftPeriodValue * inTask.getPeriodNs());

                    // Shift one more -period to see if there is another intersection
                    shiftedThisWindow.shift(-inTask.getPeriodNs());
                    if (firstWindow.intersect(shiftedThisWindow) != null) {// Has two intersections, thus skip intersecting this window.
                        //ProgMsg.debugPutline("Two windows have multiple intersections!! Skip intersecting this window for now.");
                        anyoneHasTwoIntersectionsInTheEnd = true;
                        continue;
                    }
                    // No intersection with moving -1 period.

                    // Now testing the intersection with moving +1 period.
                    // Shift one more +period to see if there is another intersection
                    shiftedThisWindow.shift(2 * inTask.getPeriodNs());
                    if (firstWindow.intersect(shiftedThisWindow) != null) {// Has two intersections, thus skip intersecting this window.
                        //ProgMsg.debugPutline("Two windows have multiple intersections!! Skip intersecting this window for now.");
                        anyoneHasTwoIntersectionsInTheEnd = true;
                        continue;
                    }

                    // In the end, it has only one intersection, so apply the intersection to firstWindow.
                    thisWindow.shift(smallestShiftPeriodValue * inTask.getPeriodNs());
                    firstWindow = firstWindow.intersect(thisWindow);
                }

            } // End of for loop.
            //}
//        } while(lastWindow.getBegin()!=firstWindow.getBegin() || lastWindow.getEnd()!=firstWindow.getEnd());
//        ProgMsg.debugPutline("Interscetion loop: %d while loops", DEBUG_COUNT);

        // TODO: Check this variable to see whether we have two intersections for a pair of window in the end.
        if (anyoneHasTwoIntersectionsInTheEnd == true) {
//            ProgMsg.errPutline("Still got someone having two intersections in the last parse for %s", inTask.getTitle());
        } else {
//            ProgMsg.debugPutline("2-intersection test pass for %s", inTask.getTitle());
        }

        assert firstWindow!=null : "No arrival window is found in all busy intervals for this task";
        if (firstWindow == null) {
            ProgMsg.debugPutline("No arrival window is found in all busy intervals for %s task", inTask.getTitle());
        }

        // Move the window to around zero point.
        firstWindow.shift(-(firstWindow.getBegin() / inTask.getPeriodNs()) * inTask.getPeriodNs());

        return firstWindow;
    }


    // Integer type for the returned value is used because "null" will be returned if no intersection is found.
    public Integer findSmallestPeriodShiftValueWithIntersection(Interval fixedInterval, Interval shiftingInterval, int inPeriod)
    {

        int periodShiftValue = (fixedInterval.getBegin()-shiftingInterval.getBegin()) / inPeriod;

        Interval newInstanceShiftingInterval = new Interval(shiftingInterval);
        newInstanceShiftingInterval.shift(periodShiftValue*inPeriod);

        /* Check whether the intersection exists. */
        if (fixedInterval.intersect(newInstanceShiftingInterval) != null)
        {// Has intersection.
            return periodShiftValue;
        }


        /* Shift one more to see if they have intersection. */
        if (fixedInterval.getBegin() >= shiftingInterval.getBegin()) {
            periodShiftValue++;
            newInstanceShiftingInterval.shift(inPeriod);
        } else {
            periodShiftValue--;
            newInstanceShiftingInterval.shift(-inPeriod);
        }

        if (fixedInterval.intersect(newInstanceShiftingInterval) != null) {
            // Has intersection.
            return periodShiftValue;
        } else {
            return null;
        }
    }

    public Interval calculateArrivalTimeWindowOfTaskInABusyInterval(BusyInterval inBusyInterval, Task inTask)
    {
        // First, check whether this busy interval contains inTask or not.
        if (inBusyInterval.containsComposition(inTask) == false)
            return null;

        Boolean hasAmbiguousInferenceForThisTask = false;

        /* How many inTask are there in this busy interval? */
        /* Check whether the inference of the composition contains ambiguity for "inTask" or not. */
        if (inBusyInterval.getComposition().size() > 1)
        {// More than one possible answers.
            /* Check if*/
            Boolean firstLoop = true;
            int numOfInTask = 0;
            hasAmbiguousInferenceForThisTask = false;
            for (ArrayList<Task> thisInference : inBusyInterval.getComposition())
            {
                int thisNumOfInTask = 0;
                /* Calculate the number of inTask contained in this inference. */
                thisNumOfInTask = Collections.frequency(thisInference, inTask);

                if (firstLoop == true) {
                    firstLoop = false;
                    numOfInTask = thisNumOfInTask;
                    continue;
                }

                if (numOfInTask != thisNumOfInTask)
                {// Has ambiguity.

                    if (numOfInTask==0 || thisNumOfInTask==0)
                    {// One of the inference contains no inTask, thus it's not doable.
                        // TODO: watch this if too many busy intervals are skipped for the inference of arrival window for a task.
                        //ProgMsg.debugPutline("One of the inference contains 0 of %s.", inTask.getTitle());
                        return null;
                    }
                    else
                    {
                        // Has ambiguity but it's doable. (different inferences contain different number of inTask but not zero.)
                        hasAmbiguousInferenceForThisTask = true;
                    }
                }
                else
                {
                    // This inference of inTask is consistent with last one.
                    //continue; // Continue to check next.
                }
            }
        }

        /* Create the arrival window for the first computation period in this busy interval. */
        int resultBeginTime = inBusyInterval.getBeginTimeStampNs();
        int resultEndTime = Math.min(inBusyInterval.getEndTimeStampNs() - inTask.getComputationTimeNs(),
                inBusyInterval.getBeginTimeStampNs() + inTask.getPeriodNs() - inTask.getComputationTimeNs());
        Interval resultArrivalTimeWindow = new Interval(resultBeginTime, resultEndTime);

        /* If it has no ambiguity and has more than one inTask, then narrow the window with the one got from last period in this busy interval. */
        if (hasAmbiguousInferenceForThisTask==false)
        {
            // No ambiguity means only one inference is available, thus just get the first inference from busy interval.
            int numOfInTask = Collections.frequency(inBusyInterval.getFirstComposition(), inTask);
            if (numOfInTask > 1)
            {
                // Calculate the window of the last period in this busy interval.
                int lastPeriodBeginTime = inBusyInterval.getBeginTimeStampNs() + inTask.getPeriodNs()*(numOfInTask-1);
                int lastPeriodEndTime = inBusyInterval.getEndTimeStampNs();
                Interval lastPeriodArrivalTimeWindow = new Interval(lastPeriodBeginTime, lastPeriodEndTime-inTask.getComputationTimeNs());

                // Shift the window of last period to the first window
                lastPeriodArrivalTimeWindow.shift( -(inTask.getPeriodNs()*(numOfInTask - 1)) );

                // Get intersection of two.
                resultArrivalTimeWindow = resultArrivalTimeWindow.intersect(lastPeriodArrivalTimeWindow);

                assert (resultArrivalTimeWindow!=null) : "Got an empty Arrival Time Window from the intersection.";
                if (resultArrivalTimeWindow == null)
                { // It should not ever happen.
                    ProgMsg.debugPutline("Got an empty Arrival Time Window from the intersection.");
                }
            }
        } else { // (hasAmbiguousInferenceForThisTask == true)
            // It means that for sure we know there is inTask in this busy interval,
            // but the number of inTask in this busy interval remain unknown since different inferences have inconsistent guess.
            // ProgMsg.debugPutline("Has ambiguity to be solved.");

            // resultArrivalTimeWindow will be the arrival time window then.
        }

        /** End **/

        return resultArrivalTimeWindow;
    }

    public Trace buildTaskArrivalTimeWindowTrace(Task inTask)
    {
        if (taskArrivalTimeWindows.get(inTask) == null)
        {
            ProgMsg.debugPutline("%s task has null arrival time window! Can't process.", inTask.getTitle());
            return null;
        }

        Interval taskArrivalTimeWindow = taskArrivalTimeWindows.get(inTask);
        int windowLength = taskArrivalTimeWindow.getLength();

        ArrayList<IntervalEvent> intervalEvents = new ArrayList<>();
        int thisWindowBeginTime = taskArrivalTimeWindow.getBegin(); // Initialize window time with the first window.
        int taskPeriod = inTask.getPeriodNs();
        int endTime = busyIntervalContainer.getEndTime();

        while ((thisWindowBeginTime + windowLength) <= endTime)
        {
            IntervalEvent thisIntervalEvent = new IntervalEvent(thisWindowBeginTime, thisWindowBeginTime+windowLength);
            thisIntervalEvent.setColor(inTask.getTaskColor());
            thisIntervalEvent.enableTexture();
            intervalEvents.add(thisIntervalEvent);

            thisWindowBeginTime += taskPeriod;
        }

        return new Trace(inTask.getTitle() + " Arr. Window", inTask, intervalEvents, new TimeLine(), Trace.TRACE_TYPE_OTHER);
    }

    public ArrayList<Trace> buildTaskArrivalTimeWindowTracesForAllTasks()
    {
        ArrayList<Trace> resultTraces = new ArrayList<Trace>();
        for (Task thisTask : taskContainer.getAppTasksAsArray())
        {
            resultTraces.add(buildTaskArrivalTimeWindowTrace(thisTask));
        }
        return resultTraces;
    }

    public Trace buildCompositionTrace()
    {
        return new Trace("Step2 Inf.", busyIntervalContainer.compositionInferencesToEvents(), new TimeLine());
    }

    public Trace buildSchedulingInferenceTrace()
    {
        ArrayList resultEvents = new ArrayList();
        for ( BusyInterval thisBI : busyIntervalContainer.getBusyIntervals() ) {
            resultEvents.addAll( thisBI.schedulingInference );
        }
        return new Trace("Inf. Schedule", resultEvents, new TimeLine());
    }

    public ArrayList<Trace> buildResultTraces()
    {
        ArrayList<Trace> resultTraces = new ArrayList<>();

        // Scheduling inference
        resultTraces.add(buildSchedulingInferenceTrace());

        // Composition trace (inference of N values)
        //resultTraces.add(buildCompositionTrace());

        // Arrival time window traces
        resultTraces.addAll(buildTaskArrivalTimeWindowTracesForAllTasks());

        return resultTraces;
    }

    /* It will skip the busy intervals that have ambiguity for the given task. */
    public BusyInterval findShortestBusyIntervalContainingTask(Task inTask)
    {
        BusyInterval shortestBusyInterval = null;
        Boolean firstLoop = true;
        for (BusyInterval thisBusyInterval : busyIntervalContainer.findBusyIntervalsByTask(inTask))
        {
            /* Check whether this busy interval is ambiguous for inTask. */
            if (thisBusyInterval.getComposition().size() > 1)
            { // This busy interval has more than one inferences.

                Boolean hasValueZero = false;
                for (ArrayList<Task> thisInference : thisBusyInterval.getComposition())
                {
                    int thisNumOfInTask = 0;

                    /* Calculate the number of inTask contained in this inference. */
                    thisNumOfInTask = Collections.frequency(thisInference, inTask);

                    if (thisNumOfInTask == 0) {
                        hasValueZero = true;
                        break;
                    }
                }

                if (hasValueZero == true)
                {// This busy interval is ambiguous for inTask, thus skip.
                    continue;
                }
            }

            if (firstLoop == true)
            {
                shortestBusyInterval = thisBusyInterval;
                firstLoop = false;
                continue;
            }

            if (thisBusyInterval.getIntervalNs() < shortestBusyInterval.getIntervalNs())
                shortestBusyInterval = thisBusyInterval;

        }

        if (shortestBusyInterval == null) {
            ProgMsg.debugPutline("shortestBusyInterval is null.");
        }

        return shortestBusyInterval;
    }

    /**
     * Use the existed arrival time window of each task to eliminate invalid inferences in each busy interval.
     * @return true for one or more ambiguous inferences have been removed in this pass, false for nothing has been changed.
     */
    public Boolean removeAmbiguousInferenceByArrivalTimeWindow()
    {
        Boolean isSomethingChanged = false;
        for (BusyInterval thisBusyInterval : busyIntervalContainer.getBusyIntervals())
        {
            // If this busy interval has no ambiguous inference, then continue to next.
            if (thisBusyInterval.getComposition().size() == 1)
                continue;

            int orgNumOfInferences = thisBusyInterval.getComposition().size();

            ArrayList<ArrayList<Task>> potentialInferences = new ArrayList<>();
            for (ArrayList<Task> thisInference : thisBusyInterval.getComposition())
            {
                /* Check if the number of thisTask is consistent with thisInference.
                *  If not, then thisInference may not be true answer.
                *  ### We are removing those who are not the answer for sure and leave ambiguous ones!! ###
                */

                // Iterate by tasks
                Boolean thisIsMismatchForSure = false;
                for (Task thisTask : taskContainer.getAppTasksAsArray())
                {
                    /* Calculate the number of thisTask contained in this inference. */
                    int numOfThisTaskByInference = Collections.frequency(thisInference, thisTask);
                    int numOfThisTaskByWindow = calculateNumOfGivenTaskInBusyIntervalByArrivalTimeWindow(thisBusyInterval, thisTask);

                    if (numOfThisTaskByWindow == -1) {
                        // Skip this one for now since it's still possible to be the answer.
                        continue;
                    }

                    // TODO: !!Bug!! if the arrival window is too big, then it's likely to infer incorrect number. ###FIXED!!!?
                    if (numOfThisTaskByInference > numOfThisTaskByWindow) {
                        thisIsMismatchForSure = true;
                        break;
                    }
                }

                if (thisIsMismatchForSure == false) {
                    // This is not mismatch and still is likely to be an answer.
                    potentialInferences.add(thisInference);
                }

            }

            if (potentialInferences.size()>0 && potentialInferences.size()!=orgNumOfInferences) {
                // Note that we have skipped the busy intervals without ambiguity in the beginning of the for loop,
                // thus anything reaches here means something has been removed.
                isSomethingChanged = true;
                thisBusyInterval.getComposition().clear();
                thisBusyInterval.getComposition().addAll(potentialInferences);
            } else {
                //ProgMsg.debugPutline("No matched inference is found for bi " + String.valueOf((double)thisBusyInterval.getBeginTimeStampNs()*ProgConfig.TIMESTAMP_UNIT_TO_MS_MULTIPLIER) + "ms: %d", thisBusyInterval.getComposition().size());
            }

            if (thisBusyInterval.getComposition().size() == 0) {
                // This will never happen.
                ProgMsg.debugErrPutline("One of busy interval's inferences become 0! It should never happen!");
            }

        }

        if (isSomethingChanged == true) {
            return true;
        } else {
            return false;
        }
    }

    public int calculateNumOfGivenTaskInBusyIntervalByArrivalTimeWindow(BusyInterval inBusyInterval, Task inTask)
    {
        if (taskArrivalTimeWindows.get(inTask) == null) {
            // Arrival time window for inTask is empty.
            return -1;
        }

        Interval thisTaskArrivalWindow = new Interval(taskArrivalTimeWindows.get(inTask));
        Interval intervalBusyInterval = new Interval(inBusyInterval.getBeginTimeStampNs(), inBusyInterval.getEndTimeStampNs());

        Integer shiftValue = findSmallestPeriodShiftValueWithIntersection(intervalBusyInterval, thisTaskArrivalWindow, inTask.getPeriodNs());
        if (shiftValue == null)
            return 0;

        thisTaskArrivalWindow.shift(shiftValue * inTask.getPeriodNs());

        // Check shifting direction
        int shiftingPositiveNegativeFactor = 1;
        if (shiftValue >= 0) {
            shiftingPositiveNegativeFactor = 1;
        }
        else {
            shiftingPositiveNegativeFactor = -1;
        }

        int countIntersectedTaskPeriod = 0;
        while (true)
        {
            if (thisTaskArrivalWindow.intersect(intervalBusyInterval) != null)
            {
                thisTaskArrivalWindow.shift(shiftingPositiveNegativeFactor * inTask.getPeriodNs());
                countIntersectedTaskPeriod++;
            }
            else
            {
                break;
            }
        }

        return countIntersectedTaskPeriod;
    }

    public Boolean reconstructCompositionOfBusyIntervalByArrivalTimeWindows()
    {
        for ( BusyInterval thisBusyInterval : busyIntervalContainer.getBusyIntervals() ) {
            if (thisBusyInterval.getComposition().size() > 1) {
                // TODO: What we can do when there are multiple possible inferences while reconstructing the schedule for a busy interval?
                ProgMsg.debugErrPutline("Skipped!! Reconstruction Step: more than one inference in busy interval at " + String.valueOf((double)thisBusyInterval.getBeginTimeStampNs()*ProgConfig.TIMESTAMP_UNIT_TO_MS_MULTIPLIER) + " ms");
                continue;
            }

            // Here we assume that the number of possible inferences is reduced to one, thus process the only one inference.
            ArrayList<Task> thisInference = thisBusyInterval.getFirstComposition();
            TaskArrivalEventContainer thisArrivalInference = thisBusyInterval.arrivalInference;
            thisArrivalInference.clear();

            /* Start constructing arrival time sequence of all tasks in this busy interval. */
            for ( Task thisTask : taskContainer.getAppTasksAsArray() ) {
                if ( thisBusyInterval.containsComposition(thisTask) == false ) {
                    continue;
                }

                int numOfThisTask = Collections.frequency(thisInference, thisTask);
                Interval thisArrivalWindow = findClosestArrivalTimeOfTask( thisBusyInterval.getBeginTimeStampNs(), thisTask );

                for (int loop=0; loop<numOfThisTask; loop++) {
                    /* Note that after added element will be sorted by time. */
                    thisArrivalInference.add( thisArrivalWindow.getBegin(), thisTask );
                    thisArrivalWindow.shift( thisTask.getPeriodNs() );
                }

            } /* Arrival time arrangement for this busy interval finished. */

            /* Reconstruct the busy interval according to the arrival time of each task in this busy interval. */
            QuickRmScheduling.constructSchedulingOfBusyIntervalByArrivalWindow( thisBusyInterval );

        }

        return true;

    }

//    public Task findEarliestArrivalTimeTask( int referenceTimePoint, ArrayList<Task> inTasks )
//    {
//        Interval earliestArrivalWindow = null;
//        Task earliestArrivalTask = null;
//
//        Boolean firstLoop = true;
//        for ( Task thisTask : inTasks ) {
//            if ( firstLoop == true ) {
//                firstLoop = false;
//                earliestArrivalWindow = findClosestArrivalTimeOfTask( referenceTimePoint, thisTask );
//                earliestArrivalTask = thisTask;
//                continue;
//            }
//
//            Interval thisEarliestArrivalWindow;
//            thisEarliestArrivalWindow = findClosestArrivalTimeOfTask( referenceTimePoint, thisTask );
//
//            // Check whether this task has earliest arrival time and higher priority.
//            // Note that, in priority, a smaller number stands for a higher priority.
//            if ( ( thisEarliestArrivalWindow.getBegin() <= earliestArrivalWindow.getBegin() )
//                    && ( thisTask.getPriority() < earliestArrivalTask.getPriority() ) ) {
//                earliestArrivalWindow = thisEarliestArrivalWindow;
//                earliestArrivalTask = thisTask;
//            }
//        }
//
//        return earliestArrivalTask;
//
//    }


    // This will find the first arrival time after the reference point.
    public Interval findClosestArrivalTimeOfTask( int referenceTimePoint, Task inTask )
    {
        // Create a new Interval instance based on input value.
        Interval taskWindow = new Interval( taskArrivalTimeWindows.get( inTask ) );

        int difference = referenceTimePoint - taskWindow.getBegin();
        int shiftFactor = difference / inTask.getPeriodNs();
        if ( difference % inTask.getPeriodNs() == 0 ) {
            // shiftFactor remains unchanged.
        } else if ( difference > 0 ) {
            // referencePoint is bigger
            shiftFactor++;
        } else {
            // reference Point is smaller
            // shiftFactor is negative and will remain the unchanged.
        }

        taskWindow.shift( shiftFactor * inTask.getPeriodNs() );
        return taskWindow;
    }

    // TODO: The range of the deviation between ground truth and inference has to be specified further.
    private Boolean verifySchedulingInferenceSingleBusyInterval(BusyInterval bi) {
        if ((bi.getStartTimesGroundTruth() == null) || (bi.getStartTimesInference()==null))
            return false;

        if (bi.getStartTimesGroundTruth().size() != bi.getStartTimesInference().size())
            return false;

        bi.getStartTimesInference().sortTaskReleaseEventsByTime();
        bi.getStartTimesGroundTruth().sortTaskReleaseEventsByTime();

        int countOfElements = bi.getStartTimesGroundTruth().size();
        for (int i=0; i<countOfElements; i++) {
            AppEvent startTimeGroundTruth = bi.getStartTimesGroundTruth().get(i);
            AppEvent startTimeInference = bi.getStartTimesInference().get(i);

            if (false == areEqualWithinError(startTimeGroundTruth.getOrgBeginTimestampNs(), startTimeInference.getOrgBeginTimestampNs(), 0))
                return false;
        }

        return true;
    }

    public Boolean verifySchedulingInference() {
        Boolean overallResult = true;
        for (BusyInterval bi : busyIntervalContainer.getBusyIntervals()) {
            Boolean verificationResult = verifySchedulingInferenceSingleBusyInterval(bi);

            if (verificationResult == false) {
                ProgMsg.debugPutline("Busy interval verification failed in busy interval at " + String.valueOf((double)bi.getBeginTimeStampNs() * (double)ProgConfig.TIMESTAMP_UNIT_TO_MS_MULTIPLIER) + " ms");
                overallResult = false;
            }
        }

        //ProgMsg.debugPutline("Overall verification done: " + overallResult.toString());
        return overallResult;
    }
}
