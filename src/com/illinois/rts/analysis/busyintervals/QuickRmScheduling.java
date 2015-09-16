package com.illinois.rts.analysis.busyintervals;

import com.illinois.rts.framework.Task;
import com.illinois.rts.simulator.SimJob;
import com.illinois.rts.visualizer.AppEvent;
import com.illinois.rts.visualizer.TaskIntervalEvent;

import java.util.ArrayList;

/**
 * Created by CY on 8/18/2015.
 */
public class QuickRmScheduling {
//    private TaskContainer taskContainer;
//
//    public QuickRmScheduling( TaskContainer inTaskContainer )
//    {
//        taskContainer = inTaskContainer;
//    }

    // Write scheduling inference to ArrayList<TaskIntervalEvent> inside the busy interval.
    /* Prerequisite: arrival time windows */
    // TODO: It doesn't handle the scheduling correctly when multiple tasks have the same period.
    public static void constructSchedulingOfBusyIntervalByArrivalWindow( BusyInterval bi )
    {
        // Get the arrival events by replicating the container since we'll be modifying the container later (pop events).
        TaskArrivalEventContainer arrivalEventContainer = new TaskArrivalEventContainer( bi.arrivalInference );

        if ( arrivalEventContainer.size() == 0 ) {
            // No arrival event is available thus cannot proceed simulation.
            return;
        }

        QuickRmSimJobContainer jobContainer = arrivalWindowsToSimJobs( bi );
//        ProgMsg.debugPutline(jobContainer.toString());
        ArrayList resultSchedulingEvents = bi.schedulingInference;

        Task currentRunTask = null;
        SimJob currentJob = null;
        SimJob nextJob;
        int currentTimeStamp = bi.getBeginTimeStampNs();    // TODO: may have to check whether it includes the error.

        Boolean anyJobRunning = false;
        while ( true ) {

            if ( anyJobRunning == false ) {
                anyJobRunning = true;
                currentJob = jobContainer.popNextHighestPriorityJobByTime(currentTimeStamp);

                if ( currentJob == null )
                    break;

                currentRunTask = currentJob.task;
                if (currentTimeStamp > (int)currentJob.releaseTime) {
                    currentJob.releaseTime = (long)currentTimeStamp;
                }

                if ( (int)currentJob.remainingExecTime == currentRunTask.getComputationTimeNs() ) {
                    AppEvent thisReleaseEvent = new AppEvent((int) currentJob.releaseTime, currentRunTask, 0, "BEGIN");
                    resultSchedulingEvents.add(thisReleaseEvent);
                    bi.startTimesInference.add(thisReleaseEvent);
                }

                continue;
            }

            /* There is a job running. */

            // Get the job that will preempt current job before within the remaining computation time.
            nextJob = jobContainer.popNextEarliestHigherPriorityJobByTime(currentRunTask.getPriority(), (int) (currentTimeStamp + currentJob.remainingExecTime));

            if ( nextJob != null ) {
                // Current job is being preempted. Create and push the updated current job.
                TaskIntervalEvent currentJobEvent = new TaskIntervalEvent((int) currentJob.releaseTime, (int) nextJob.releaseTime, currentRunTask, "");
                resultSchedulingEvents.add(currentJobEvent);

                currentJob.remainingExecTime -= (nextJob.releaseTime - currentTimeStamp);
                currentJob.releaseTime = nextJob.releaseTime;
                jobContainer.add(currentJob);

                currentJob = nextJob;
                currentRunTask = currentJob.task;
                currentTimeStamp = (int)currentJob.releaseTime;

                // Check if it is the beginning of a new job.
                if ( ((int)currentJob.remainingExecTime) == currentRunTask.getComputationTimeNs() ) {
                    AppEvent thisReleaseEvent = new AppEvent((int) currentJob.releaseTime, currentRunTask, 0, "BEGIN");
                    resultSchedulingEvents.add(thisReleaseEvent);
                    bi.startTimesInference.add(thisReleaseEvent);
                }


            } else {

                // No next higher priority event, thus finish the last remaining job.
                TaskIntervalEvent currentJobEvent = new TaskIntervalEvent( currentTimeStamp, (int)(currentTimeStamp+currentJob.remainingExecTime), currentRunTask, "END");
                resultSchedulingEvents.add(currentJobEvent);

                anyJobRunning = false;
                currentTimeStamp = (int)(currentTimeStamp+currentJob.remainingExecTime);
            }

            continue;
        }


    }

    public static QuickRmSimJobContainer arrivalWindowsToSimJobs( BusyInterval bi )
    {
        QuickRmSimJobContainer resultSimJobs = new QuickRmSimJobContainer();
        TaskArrivalEventContainer arrivalEventContainer = bi.arrivalInference;
        for ( AppEvent thisEvent : arrivalEventContainer.getEvents() ) {
            resultSimJobs.add( new SimJob(thisEvent.getTask(), thisEvent.getOrgBeginTimestampNs(), thisEvent.getTask().getComputationTimeNs()) );
        }
        return resultSimJobs;
    }
}
