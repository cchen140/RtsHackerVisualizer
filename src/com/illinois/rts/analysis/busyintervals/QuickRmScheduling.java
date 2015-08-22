package com.illinois.rts.analysis.busyintervals;

import com.illinois.rts.framework.Task;
import com.illinois.rts.simulator.SimJob;
import com.illinois.rts.visualizer.AppEvent;
import com.illinois.rts.visualizer.ProgMsg;
import com.illinois.rts.visualizer.TaskContainer;
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
        int currentTimeStamp = 0;

        Boolean anyJobRunning = false;
        while ( true ) {

            if ( anyJobRunning == false ) {
                anyJobRunning = true;
                currentJob = jobContainer.popNextHighPriorityJob();

                if ( currentJob == null )
                    break;

                currentRunTask = currentJob.task;
                if (currentTimeStamp > (int)currentJob.releaseTime) {
                    currentJob.releaseTime = (long)currentTimeStamp;
                }

                if ( (int)currentJob.remainingExecTime == currentRunTask.getComputationTimeNs() )
                    resultSchedulingEvents.add(new AppEvent((int) currentJob.releaseTime, currentRunTask, 0, "BEGIN"));

                continue;
            } else {
                nextJob = jobContainer.popNextHigherPriorityJobByTime( currentRunTask.getPriority(), (int)(currentJob.releaseTime+currentJob.remainingExecTime) );
            }

            if ( nextJob != null ) {
                if ( nextJob.releaseTime<(currentJob.releaseTime+currentJob.remainingExecTime) ) {
                    // Next higher priority job is within current job.
                    // Current job is preempted. Create and push the updated current job.
                    TaskIntervalEvent currentJobEvent = new TaskIntervalEvent((int) currentJob.releaseTime, (int) nextJob.releaseTime, currentRunTask, "");
                    resultSchedulingEvents.add(currentJobEvent);

                    currentJob.remainingExecTime -= (nextJob.releaseTime - currentJob.releaseTime);
                    currentJob.releaseTime = nextJob.releaseTime;
                    jobContainer.add(currentJob);
                } else {
                    // No Preemption upon currentJob, thus finish this job.
                    TaskIntervalEvent currentJobEvent = new TaskIntervalEvent( (int)currentJob.releaseTime, (int)(currentJob.releaseTime+currentJob.remainingExecTime), currentRunTask, "END");
                    resultSchedulingEvents.add(currentJobEvent);
                }

                currentJob = nextJob;
                currentRunTask = currentJob.task;
                //currentTimeStamp = (int)currentJob.releaseTime;

                // Check if it is the beginning of a new job.
                if ( ((int)currentJob.remainingExecTime) == currentRunTask.getComputationTimeNs() )
                    resultSchedulingEvents.add(new AppEvent((int) currentJob.releaseTime, currentRunTask, 0, "BEGIN"));


            } else {

                // No next higher priority event, thus finish the last remaining job.
                TaskIntervalEvent currentJobEvent = new TaskIntervalEvent( (int)currentJob.releaseTime, (int)(currentJob.releaseTime+currentJob.remainingExecTime), currentRunTask, "END");
                resultSchedulingEvents.add(currentJobEvent);

                anyJobRunning = false;
                currentTimeStamp = (int)(currentJob.releaseTime+currentJob.remainingExecTime);
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
