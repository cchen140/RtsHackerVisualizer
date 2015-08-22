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

    public SimJob popNextHigherPriorityJobByTime( int inPriority, int timeStamp )
    {
        if ( jobs.size() == 0 )
            return null;

        SimJob nextHighJob = null;
        Boolean firstLoop = true;
        for ( SimJob thisJob : jobs ) {
            // Skip the job that is later than the designated time.
            if ( (thisJob.releaseTime>timeStamp) || (thisJob.task.getPriority()<inPriority) )
                continue;

            if ( firstLoop == true ) {
                firstLoop = false;
                nextHighJob = thisJob;
                continue;
            }

            // Note that bigger value in priority means higher priority.
            if ( (thisJob.releaseTime<=nextHighJob.releaseTime) && (thisJob.task.getPriority()>nextHighJob.task.getPriority()) ) {
                nextHighJob = thisJob;
            }
        }
        jobs.remove( nextHighJob );
        return nextHighJob;
    }
}
