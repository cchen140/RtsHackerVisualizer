package com.illinois.rts.analysis.busyintervals.ArrivalTimeWindow;

import com.illinois.rts.analysis.busyintervals.BusyInterval;
import com.illinois.rts.analysis.busyintervals.BusyIntervalContainer;
import com.illinois.rts.analysis.busyintervals.Interval;
import com.illinois.rts.framework.Task;
import com.illinois.rts.visualizer.ProgMsg;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by CY on 10/31/2015.
 */
public class ArrivalSegmentsContainer {
    /* inputs from constructor */
    private BusyIntervalContainer biContainer;
    private Task task;

    private ArrayList<ArrivalSegment> arrivalSegments = new ArrayList<>();
    private ArrayList<Interval> arrivalIntersections = new ArrayList<>();

//    private long periodBeginTimeStamp;
//    private long period;

    private Interval finalArrivalTimeWindow;
//    private ArrivalSegment baseArrivalSegment;

    public ArrivalSegmentsContainer(Task inTask, BusyIntervalContainer inBiContainer){
        biContainer = inBiContainer;
        task = inTask;
    }

    public Boolean calculateFinalArrivalTimeWindow() {

        convertBusyIntervalsToArrivalSegments();

//        ArrivalSegment baseArrivalSegment;// = new ArrivalSegment( getFirstOneArrivalSegment() );
//        if (getFirstOneArrivalSegment() == null) {
//            // If there is no one-segment, then just get one from anything you have.
//            ProgMsg.errPutline("%s doesn't have 1-seg now.", task.getTitle());
//            baseArrivalSegment = new ArrivalSegment( arrivalSegments.get(0) );
//        } else {
//            baseArrivalSegment = new ArrivalSegment( getFirstOneArrivalSegment() );
//        }

        //periodBeginTimeStamp = baseArrivalSegment.getBegin();

        arrivalIntersections.clear();
        //arrivalIntersections.add(baseArrivalSegment);

        createArrivalIntersectionsByPeriod();

        /* Compute intersections */
        //updateArrivalIntersectionsByOneSegments();
        //updateArrivalIntersectionsByZeroOneSegments();

        if (arrivalIntersections.size() == 1) {
            // Move the window to around zero point.
            finalArrivalTimeWindow = arrivalIntersections.get(0);
            finalArrivalTimeWindow.shift(-(finalArrivalTimeWindow.getBegin() / task.getPeriodNs()) * task.getPeriodNs());

            return true;
        } else {
            ProgMsg.errPutline("%s still has %d possible arrival windows after computation.", task.getTitle(), arrivalIntersections.size());
//            ProgMsg.errPutline("\t" + arrivalIntersections.get(0).getBegin() + ":" + arrivalIntersections.get(0).getEnd());
//            ProgMsg.errPutline("\t" + arrivalIntersections.get(1).getBegin() + ":" + arrivalIntersections.get(1).getEnd());
//
//            finalArrivalTimeWindow = arrivalIntersections.get(1);
//            finalArrivalTimeWindow.shift(-(finalArrivalTimeWindow.getBegin() / task.getPeriodNs()) * task.getPeriodNs());
            //return true;

            return false;
        }
    }

    private Boolean convertBusyIntervalsToArrivalSegments() {
        if (biContainer == null)
            return false;

        // Reset related variables.
        arrivalSegments.clear();

        /* Partitions busy intervals into arrival segments (1-seg and 0-1-seg) */
        for (BusyInterval thisBi : biContainer.getBusyIntervals()) {
            arrivalSegments.addAll(getArrivalSegmentsInBusyIntervalForTask(thisBi));
        }

        return true;
    }

    private ArrayList<ArrivalSegment> getArrivalSegmentsInBusyIntervalForTask(BusyInterval inBusyInterval)
    {
        ArrayList<ArrivalSegment> resultArrivalSegments = new ArrayList<>();

        int taskP = task.getPeriodNs();
        int taskC = task.getComputationTimeNs();

        /* Deal with the certain part first. */
        for (int i=0; i<inBusyInterval.getMinNkValueOfTask(task); i++) {
            /* Create the arrival segment for every period in this busy interval (except an extra one later). */
            int resultBeginTime = inBusyInterval.getBeginTimeStampNs() + taskP*i;
            int resultEndTime = Math.min(inBusyInterval.getEndTimeStampNs() - taskC,
                    inBusyInterval.getBeginTimeStampNs() + taskP*(i+1) - taskC);
            resultArrivalSegments.add(new ArrivalSegment(resultBeginTime, resultEndTime, ArrivalSegment.ONE_ARRIVAL_SEGMENT));
        }

        if (inBusyInterval.getNkValuesOfTask(task).size() > 1) {
            /* Create the arrival segment (0-1-segment) for this extra one. */
            int nthPeriod = inBusyInterval.getMaxNkValueOfTask(task);

            int resultBeginTime = inBusyInterval.getBeginTimeStampNs() + taskP*(nthPeriod-1); // Note that nthPeriod is always >=1
            int resultEndTime = Math.min(inBusyInterval.getEndTimeStampNs() - taskC,
                    inBusyInterval.getBeginTimeStampNs() + taskP*(nthPeriod) - taskC);
            resultArrivalSegments.add(new ArrivalSegment(resultBeginTime, resultEndTime, ArrivalSegment.ZERO_ONE_ARRIVAL_SEGMENT));
        }

        return resultArrivalSegments;
    }

    private ArrivalSegment getFirstOneArrivalSegment() {
        for (ArrivalSegment thisArrivalSegment : arrivalSegments) {
            if (thisArrivalSegment.getSegmentType() == ArrivalSegment.ONE_ARRIVAL_SEGMENT) {
                return thisArrivalSegment;
            }
        }
        return null;
    }

    /* The result is the intersections. */
//    private ArrayList<Interval> intersectArrivalIntersections(ArrivalSegment inSegment) {
//        ArrayList<Interval> newArrivalIntersections = new ArrayList<>();
//
//        for (Interval thisWindow : arrivalIntersections) {
//            newArrivalIntersections.addAll( inSegment.getIntersectionWithPeriodShift(thisWindow, task.getPeriodNs()) );
//        }
//        return newArrivalIntersections;
//    }
//
//    /* This is specific for 0-1-segment.
//     * If no intersection happens, then nothing would be changed. */
//    // TODO: potential bug if 0-1-segment is actually 0?
//    private ArrayList<Interval> narrowExistingArrivalIntersections(ArrivalSegment inSegment) {
//        ArrayList<Interval> newArrivalIntersections = new ArrayList<>();
//
//        for (Interval thisWindow : arrivalIntersections) {
//            ArrayList<Interval> thisIntersections = inSegment.getIntersectionWithPeriodShift(thisWindow, task.getPeriodNs());
//            if (thisIntersections.size() == 0) {
//                newArrivalIntersections.add(thisWindow);
//            } else {
//                newArrivalIntersections.addAll(thisIntersections);
//            }
//        }
//        return newArrivalIntersections;
//    }

//    private void updateArrivalIntersectionsByOneSegments() {
//        for (ArrivalSegment thisSegment : arrivalSegments) {
//            if (thisSegment.getSegmentType() == ArrivalSegment.ONE_ARRIVAL_SEGMENT) {
//                arrivalIntersections = intersectArrivalIntersections(thisSegment);
//            }
//        }
//    }

//    private void updateArrivalIntersectionsByZeroOneSegments() {
//        for (ArrivalSegment thisSegment : arrivalSegments) {
//            if (thisSegment.getSegmentType() == ArrivalSegment.ZERO_ONE_ARRIVAL_SEGMENT) {
//                arrivalIntersections = narrowExistingArrivalIntersections(thisSegment);
//            }
//        }
//    }

    private void createArrivalIntersectionsByPeriod() {
        int beginTimeStamp = findEarliestArrivalSegmentBeginTime();
        int endTimeStamp = findLeastArrivalSegmentEndTime();

        int taskP = task.getPeriodNs();

        // Make initial window as whole period.
        arrivalIntersections.add(new Interval(beginTimeStamp, beginTimeStamp+taskP));

        for (int i=0; (beginTimeStamp+taskP*(i+1)) < endTimeStamp; i++) {// i+1 is to skip the last period since it may not be complete
            ArrayList<Interval> newArrivalIntersections = new ArrayList<>();
            ArrayList<ArrivalSegment> thisPeriodSegments = findArrivalSegmentsBetweenTimes(beginTimeStamp+taskP*i, beginTimeStamp+taskP*(i+1)-1);

            for (ArrivalSegment thisSegment : thisPeriodSegments) {
                ArrayList<Interval> thisSegmentResult = new ArrayList<>();

                for (Interval thisWindow : arrivalIntersections) {
                    Interval result = thisSegment.intersect(thisWindow);
                    if (result != null) {
                        // Shift thisWindow to next period.
                        result.shift(taskP);
                        thisSegmentResult.add(result);
                    }
                }

                /* Check whether this ONE-segment is completely within this period. */
                if (thisSegment.getSegmentType() == ArrivalSegment.ONE_ARRIVAL_SEGMENT) {
                    if (thisSegment.within(new Interval(beginTimeStamp + taskP * i, beginTimeStamp + taskP * (i + 1) - 1))) {
                        // This is the KEY segment which is completely within this period. Do this one is enough.
                        newArrivalIntersections = thisSegmentResult;

                        for (ArrivalSegment secLoopSegment : thisPeriodSegments) {
                            if (secLoopSegment == thisSegment)
                                continue;

                            if (secLoopSegment.getSegmentType() == ArrivalSegment.ONE_ARRIVAL_SEGMENT) {
                                if (secLoopSegment.getBegin() > thisSegment.getBegin()) {
                                    secLoopSegment.setBegin(beginTimeStamp+taskP*(i+1));
                                }
                            }
                        }

                        break;
                    }
                }

                newArrivalIntersections.addAll(thisSegmentResult);
                continue;

            }

//            for (Interval thisWindow : arrivalIntersections) {
//                for (ArrivalSegment thisSegment : thisPeriodSegments) {
//                    Interval result = thisSegment.intersect(thisWindow);
//                    if (result != null) {
//                        // Shift thisWindow to next period.
//                        result.shift(taskP);
//                        newArrivalIntersections.add(result);
//                    }
//                }
//            }

            if (newArrivalIntersections.size() == 0) {
                ProgMsg.errPutline("%s intersection becomes null. It should never happen!!", task.getTitle());
            }

            arrivalIntersections = newArrivalIntersections;
        }
    }

    private int findEarliestArrivalSegmentBeginTime() {
        int earliestTimeStamp = 0;
        Boolean firstLoop = true;
        for (ArrivalSegment thisSegment : arrivalSegments) {
            if (firstLoop == true) {
                earliestTimeStamp = thisSegment.getBegin();
                firstLoop = false;
            }
            earliestTimeStamp = thisSegment.getBegin() < earliestTimeStamp ? thisSegment.getBegin() : earliestTimeStamp;
        }
        return earliestTimeStamp;
    }

    private int findLeastArrivalSegmentEndTime() {
        int leastEndTime = 0;
        for (ArrivalSegment thisSegment : arrivalSegments) {
            leastEndTime = thisSegment.getEnd() > leastEndTime ? thisSegment.getEnd() : leastEndTime;
        }
        return leastEndTime;
    }

    private ArrayList<ArrivalSegment> findArrivalSegmentsBetweenTimes(int begin, int end) {
        Interval range = new Interval(begin, end);
        ArrayList<ArrivalSegment> resultSegments = new ArrayList<>();
        for (ArrivalSegment thisSegment : arrivalSegments) {
            if (range.intersect(thisSegment) != null) {
                resultSegments.add(thisSegment);
            }
        }
        return resultSegments;
    }

    public Interval getFinalArrivalTimeWindow() {
        return finalArrivalTimeWindow;
    }
}
