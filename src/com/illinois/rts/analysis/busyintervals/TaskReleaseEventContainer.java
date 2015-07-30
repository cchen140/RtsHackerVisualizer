package com.illinois.rts.analysis.busyintervals;

import com.illinois.rts.framework.Task;
import com.illinois.rts.visualizer.AppEvent;

import java.util.ArrayList;

/**
 * Created by CY on 7/29/2015.
 */
public class TaskReleaseEventContainer {
    ArrayList<AppEvent> taskReleaseEvents = new ArrayList<>();

    public TaskReleaseEventContainer()
    {

    }

    public AppEvent add(int releaseTime, Task inTask)
    {
        AppEvent thisEvent = new AppEvent( releaseTime, inTask, 0, "" );

        taskReleaseEvents.add(thisEvent);
        sortTaskReleaseEventsByTime();

        return thisEvent;
    }

    public void add( AppEvent inEvent )
    {
        taskReleaseEvents.add( inEvent );
        sortTaskReleaseEventsByTime();
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
}
