package com.illinois.rts.analysis.busyintervals;

import com.illinois.rts.framework.Task;
import com.illinois.rts.simulator.DialogSimulationProgress;
import com.illinois.rts.simulator.GenerateRmTaskSet;
import com.illinois.rts.simulator.ProgressUpdater;
import com.illinois.rts.simulator.SimJob;
import com.illinois.rts.visualizer.*;

import java.awt.*;
import java.util.ArrayList;

/**
 * Created by CY on 8/18/2015.
 */
public class QuickRmScheduling {
    private TaskContainer taskContainer;
    private EventContainer simEventContainer = new EventContainer();
    public ProgressUpdater progressUpdater = new ProgressUpdater();

    public QuickRmScheduling( TaskContainer inTaskContainer )
    {
        setTaskContainer(inTaskContainer);
    }

    /* This function is only used by Amir's algorithm to reconstruct schedules. */
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

                // TODO: If the schedule is not continuous, we still plot the intervals that will not be in the same busy interval.
//                if ( currentJob == null ) {
//                    break;
//                }
                if ((currentJob == null) && (jobContainer.size() == 0)) {
                    break;
                } else if (currentJob == null) {
                    currentJob = jobContainer.popNextEarliestHighestPriorityJob();
                    currentTimeStamp = (int)currentJob.releaseTime;
                }

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

    /* This function is only used by Amir's algorithm to reconstruct schedules. */
    public static QuickRmSimJobContainer arrivalWindowsToSimJobs( BusyInterval bi )
    {
        QuickRmSimJobContainer resultSimJobs = new QuickRmSimJobContainer();
        TaskArrivalEventContainer arrivalEventContainer = bi.arrivalInference;
        for ( AppEvent thisEvent : arrivalEventContainer.getEvents() ) {
            resultSimJobs.add( new SimJob(thisEvent.getTask(), thisEvent.getOrgBeginTimestampNs(), thisEvent.getTask().getComputationTimeNs()) );
        }
        return resultSimJobs;
    }


    public EventContainer getSimEventContainer() {
        return simEventContainer;
    }


    public void runSim(int tickLimit) {
        if (taskContainer.schedulabilityTest() == true)
            ProgMsg.debugPutline("schedulable.");
        else
            ProgMsg.errPutline("not schedulable.");

        // Pre-schedule, turn tasks into jobs
        QuickRmSimJobContainer simJobContainer = preSchedule(tickLimit);

        // Start simulating, the output schedule will be stored in
        simJobs(simJobContainer);

    }

    /**
     * Turn task set into jobs within designated end time.
     * @param tickLimit designated end time
     * @return a container that stores jobs to be simulated.
     */
    public QuickRmSimJobContainer preSchedule(int tickLimit) {
        QuickRmSimJobContainer resultSimJobs = new QuickRmSimJobContainer();
        for (Task thisTask : taskContainer.getTasksAsArray()) {
            if (thisTask.getTaskType() == Task.TASK_TYPE_IDLE)
                continue;

            int thisPeriod = thisTask.getPeriodNs();
            int thisOffset = thisTask.getInitialOffset();
            for (int tick=thisOffset; tick<tickLimit; tick+=thisPeriod) {
                resultSimJobs.add( new SimJob(thisTask, tick, thisTask.getComputationTimeNs()) );
            }
        }
        return resultSimJobs;
    }

    public void setTaskContainer(TaskContainer inTaskContainer)
    {
        taskContainer = inTaskContainer;

        // Remove current idle task if there is any because the simulator will create one later.
        taskContainer.removeIdleTask();
        taskContainer.clearSimData();

        assignPriority(); // It doesn't (shouldn't) include the idle task.
        taskContainer.addTask(Task.IDLE_TASK_ID, "IDLE", Task.TASK_TYPE_IDLE, 0, 0, 0);

        // Clear previous event container if any
        simEventContainer.clearAll();
        simEventContainer.setTaskContainer(taskContainer);
    }

    public void simJobs(QuickRmSimJobContainer jobContainer) {
        progressUpdater.setIsStarted(true);
        int orgNumOfJobsToSim = jobContainer.size();

        Task idleTask = taskContainer.getTaskById(Task.IDLE_TASK_ID);
        Task currentRunTask = null;
        SimJob currentJob = null;
        SimJob nextJob;
        int currentTimeStamp = 0;

        Boolean anyJobRunning = false;
        while ( true ) {
            progressUpdater.setProgressPercent(1-((double)jobContainer.size()/(double)orgNumOfJobsToSim));

            if ( anyJobRunning == false ) {
                anyJobRunning = true;
                currentJob = jobContainer.popNextHighestPriorityJobByTime(currentTimeStamp);

                if ( (currentJob == null) && (jobContainer.size() == 0) ) {
                        break;
                } else if (currentJob == null) {
                    currentJob = jobContainer.popNextEarliestHighestPriorityJob();
//                        simEventContainer.add(EventContainer.SCHEDULER_EVENT, (int) tick, 0, Task.IDLE_TASK_ID, "IDLE");
                    TaskIntervalEvent currentIdleEvent = new TaskIntervalEvent(currentTimeStamp, (int)currentJob.releaseTime, idleTask, "");
                    simEventContainer.addCompleteSchedulerEvent(currentIdleEvent);

                    currentTimeStamp = (int)currentJob.releaseTime;
                }


                currentRunTask = currentJob.task;
                if (currentTimeStamp > (int)currentJob.releaseTime) {
                    currentJob.releaseTime = (long)currentTimeStamp;
                }

                if ( (int)currentJob.remainingExecTime == currentRunTask.getComputationTimeNs() ) {
                    AppEvent thisReleaseEvent = new AppEvent((int) currentJob.releaseTime, currentRunTask, 0, "BEGIN");
                    simEventContainer.addAppEvent(thisReleaseEvent);
                    //resultSchedulingEvents.add(thisReleaseEvent);
                    //bi.startTimesInference.add(thisReleaseEvent);
                }

                continue;
            }

            /* There is a job running. */

            // Get the job that will preempt current job before within the remaining computation time.
            nextJob = jobContainer.popNextEarliestHigherPriorityJobByTime(currentRunTask.getPriority(), (int) (currentTimeStamp + currentJob.remainingExecTime));

            if ( nextJob != null ) {
                // Current job is being preempted. Create and push the updated current job.
                TaskIntervalEvent currentJobEvent = new TaskIntervalEvent((int) currentJob.releaseTime, (int) nextJob.releaseTime, currentRunTask, "");
                simEventContainer.addCompleteSchedulerEvent(currentJobEvent);
                //resultSchedulingEvents.add(currentJobEvent);

                currentJob.remainingExecTime -= (nextJob.releaseTime - currentTimeStamp);
                currentJob.releaseTime = nextJob.releaseTime;
                jobContainer.add(currentJob);

                currentJob = nextJob;
                currentRunTask = currentJob.task;
                currentTimeStamp = (int)currentJob.releaseTime;

                // Check if it is the beginning of a new job.
                if ( ((int)currentJob.remainingExecTime) == currentRunTask.getComputationTimeNs() ) {
                    AppEvent thisReleaseEvent = new AppEvent((int) currentJob.releaseTime, currentRunTask, 0, "BEGIN");
                    simEventContainer.addAppEvent(thisReleaseEvent);
                    //resultSchedulingEvents.add(thisReleaseEvent);
                    //bi.startTimesInference.add(thisReleaseEvent);
                }


            } else {

                // No next higher priority event, thus finish the last remaining job.
                TaskIntervalEvent currentJobEvent = new TaskIntervalEvent( currentTimeStamp, (int)(currentTimeStamp+currentJob.remainingExecTime), currentRunTask, "END");
                simEventContainer.addCompleteSchedulerEvent(currentJobEvent);
                //resultSchedulingEvents.add(currentJobEvent);

                anyJobRunning = false;
                currentTimeStamp = (int)(currentTimeStamp+currentJob.remainingExecTime);
            }

            continue;
        }

        progressUpdater.setIsFinished(true);
    }

    // The bigger the number the higher the priority
    // When calling this function, the taskContainer should not contain idle task.
    protected void assignPriority()
    {
        ArrayList<Task> allTasks = taskContainer.getTasksAsArray();
        int numTasks = taskContainer.size();

        /* Assign priorities (RM) */
        for (int i = 0; i < numTasks; i++) {
            Task task_i = allTasks.get(i);
            int cnt = 1;    // 1 represents highest priority.
            /* Get the priority by comparing other tasks. */
            for (int j = 0; j < numTasks; j++) {
                if (i == j)
                    continue;

                Task task_j = allTasks.get(j);
                if (task_j.getPeriodNs() > task_i.getPeriodNs()
                        || (task_j.getPeriodNs() == task_i.getPeriodNs() && task_j.getId() > task_i.getId())) {
                    cnt++;
                }
            }
            task_i.setPriority(cnt);
        }
    }

    public boolean runSimWithProgressDialog(int tickLimit, Component locationReference)
    {
        DialogSimulationProgress dialogSimulationProgress = new DialogSimulationProgress();
        dialogSimulationProgress.setProgressUpdater(progressUpdater);

        SimThread rmSimThread = new SimThread(tickLimit);
        dialogSimulationProgress.setWatchedSimThread(rmSimThread);
        rmSimThread.start();

        dialogSimulationProgress.pack();
        if (locationReference != null)
            dialogSimulationProgress.setLocationRelativeTo(locationReference);
        dialogSimulationProgress.setVisible(true);

        if ( (dialogSimulationProgress.isSimCanceled()==false) )//&&(rmSimThread.getSimResult()==true))
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    class SimThread extends Thread
    {
        int simTickLength = 0;
        Boolean simResult = false;

        public SimThread(int inSimTickLength)
        {
            super();
            simTickLength = inSimTickLength;
        }

        public void run()
        {
            runSim(simTickLength);
        }

//        public Boolean getSimResult()
//        {
//            return simResult;
//        }
    }
}
