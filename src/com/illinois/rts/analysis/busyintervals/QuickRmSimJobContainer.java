package com.illinois.rts.analysis.busyintervals;

import com.illinois.rts.simulator.SimJob;

import java.util.ArrayList;

/**
 * Created by CY on 8/19/2015.
 */
public class QuickRmSimJobContainer {
    ArrayList<SimJob> jobs = new ArrayList<>();

    public void add( SimJob inJob )
    {
        jobs.add( inJob );
    }


    /* This method pops the earliest available job.
    * Use popNextHighestPriorityJobByTime() instead to determine reference time stamp. */
    public SimJob popNextHighPriorityJob()
    {
        if ( jobs.size() == 0 )
            return null;

        SimJob nextHighJob = null;
        Boolean firstLoop = true;
        for ( SimJob thisJob : jobs ) {
            if ( firstLoop == true ) {
                firstLoop = false;
                nextHighJob = thisJob;
                continue;
            }

            // Note that bigger value in priority means higher priority.
            if ( (thisJob.releaseTime<nextHighJob.releaseTime)
                    || ( (thisJob.releaseTime==nextHighJob.releaseTime) && (thisJob.task.getPriority()>=nextHighJob.task.getPriority()) ) ) {
                nextHighJob = thisJob;
            }
        }
        jobs.remove(nextHighJob);
        return nextHighJob;
    }

    public SimJob popNextEarliestHigherPriorityJobByTime(int inPriority, int timeStamp)
    {
        if ( jobs.size() == 0 )
            return null;

        SimJob nextHighJob = null;
        Boolean firstLoop = true;
        for ( SimJob thisJob : jobs ) {
            // Skip the job that is later than the designated time.
            if ( (thisJob.releaseTime>timeStamp) || (thisJob.task.getPriority()<=inPriority) )
                continue;

            if ( firstLoop == true ) {
                firstLoop = false;
                nextHighJob = thisJob;
                continue;
            }

            // Among those jobs that have higher priority, find the earliest one.
            if ( thisJob.releaseTime<=nextHighJob.releaseTime ) {
                nextHighJob = thisJob;
            }
        }
        jobs.remove( nextHighJob );
        return nextHighJob;
    }

    public SimJob popNextHighestPriorityJobByTime( int timeStamp )
    {
        if ( jobs.size() == 0 )
            return null;

        SimJob nextHighJob = null;
        Boolean firstLoop = true;
        for ( SimJob thisJob : jobs ) {
            // Skip the job that is later than the designated time.
            if ( thisJob.releaseTime>timeStamp )
                continue;

            if ( firstLoop == true ) {
                firstLoop = false;
                nextHighJob = thisJob;
                continue;
            }

            // Note that bigger value in priority means higher priority.
            if ( thisJob.task.getPriority() > nextHighJob.task.getPriority() ) {
                nextHighJob = thisJob;
            }
        }
        jobs.remove( nextHighJob );
        return nextHighJob;
    }

    public int size() {
        return jobs.size();
    }
}
