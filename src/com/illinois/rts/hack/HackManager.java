package com.illinois.rts.hack;

import com.illinois.rts.visualizer.*;

import java.util.ArrayList;

/**
 * Created by CY on 7/2/2015.
 */
public class HackManager {
    EventContainer eventContainer;

    public HackManager(EventContainer inEventContainer) {
        eventContainer = inEventContainer;
    }

    public Trace buildCapturedBusyIntervalTrace() {
        return new Trace("Busy Intervals", capturedBusyIntervalsToEvents(), new TimeLine());
    }

    protected ArrayList<IntervalEvent> capturedBusyIntervalsToEvents() {
        ArrayList<IntervalEvent> resultEvents = new ArrayList<>();
        for (HackerEvent currentEvent : eventContainer.getLowHackerEvents()) {
            int beginTimeStamp = currentEvent.getOrgBeginTimestampNs();
            // TODO: The captured value from Zedboard has to be in nano seconds for consistency.
            int measuredValue = currentEvent.getRecordData()*3; // times 3 to make it in nano seconds.
            resultEvents.add(new IntervalEvent(beginTimeStamp - measuredValue, beginTimeStamp, Integer.toString(measuredValue)));
        }
        return resultEvents;
    }


}
