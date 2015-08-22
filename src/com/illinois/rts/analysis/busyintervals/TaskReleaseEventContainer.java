package com.illinois.rts.analysis.busyintervals;

import com.illinois.rts.framework.Task;
import com.illinois.rts.visualizer.AppEvent;

import java.util.ArrayList;

/**
 * Created by CY on 7/29/2015.
 */
public class TaskReleaseEventContainer {
    ArrayList<AppEvent> taskReleaseEvents = new ArrayList<>();

    public TaskReleaseEventContainer() {}

    public TaskReleaseEventContainer( TaskReleaseEventContainer inContainer )
    {
        // replicate the event array.
        taskReleaseEvents.addAll( inContainer.taskReleaseEvents );
    }

    public void add( AppEvent inEvent )
    {
        taskReleaseEvents.add( inEvent );
        sortTaskReleaseEventsByTime();
    }

    public AppEvent add(int releaseTime, Task inTask)
    {
        AppEvent thisEvent = new AppEvent( releaseTime, inTask, 0, "" );

        this.add(thisEvent);

        return thisEvent;
    }

    public void addAll( ArrayList<AppEvent> inEvents )
    {
        taskReleaseEvents.addAll(inEvents);
    }

    public void sortTaskReleaseEventsByTime()
    {
        ArrayList<AppEvent> sortedEvents = new ArrayList<>();
        for ( AppEvent thisEvent : taskReleaseEvents ) {

            Boolean firstLoop = true;
            for ( AppEvent thisSortedEvent : sortedEvents ) {
                if ( firstLoop == true ) {
                    firstLoop = false;
                    sortedEvents.add( thisEvent );
                    continue;
                }

                // If the time is smaller (earlier), then insert to that
                if ( thisEvent.getOrgBeginTimestampNs() < thisSortedEvent.getOrgBeginTimestampNs() ) {
                    sortedEvents.add( sortedEvents.indexOf( thisSortedEvent ), thisEvent );
                    break; // Found place, so insert the event and break the loop to process next event.
                }
            }
        }
    }

    public int size()
    {
        return taskReleaseEvents.size();
    }

    public ArrayList<Task> getTasksOfEvents()
    {
        ArrayList<Task> resultTasks = new ArrayList<>();
        for ( AppEvent thisEvent : taskReleaseEvents ) {
            resultTasks.add(thisEvent.getTask());
        }
        return resultTasks;
    }

    public AppEvent get(int index)
    {
        return taskReleaseEvents.get( index );
    }

    public void clear()
    {
        taskReleaseEvents.clear();
    }


    /* Find the first event after the designated time stamp. */
    public AppEvent getNextEvent( int inTimeStamp )
    {
        for ( AppEvent thisEvent : taskReleaseEvents ) {
            if ( thisEvent.getOrgBeginTimestampNs() >= inTimeStamp )
                return thisEvent;
        }

        // If no event is after the designated time, then return null.
        return null;
    }

    public ArrayList<AppEvent> getEvents()
    {
        return taskReleaseEvents;
    }

}
