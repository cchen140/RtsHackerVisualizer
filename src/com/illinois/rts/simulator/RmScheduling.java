package com.illinois.rts.simulator;

/**
 * Created by CY on 5/26/2015.
 */

import com.illinois.rts.framework.Task;
import com.illinois.rts.visualizer.Event;
import com.illinois.rts.visualizer.EventContainer;
import com.illinois.rts.visualizer.TaskContainer;

import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;
import java.util.StringTokenizer;

public class RmScheduling {
    public ProgressUpdater progressUpdater = new ProgressUpdater();

    TaskContainer simTaskContainer = null;
    ArrayList<Task> allTasks = null;
    int numTasks = 0;
    double totalUtil = 0;
    ArrayList<SimJob> readyQueue = new ArrayList<SimJob>();
    ArrayList<SimJob> activeQueue = new ArrayList<>();
    // jobs that were released but haven't finished.
    long LCM = 1;
    int maximumInitialOffset = Integer.MIN_VALUE;
    long tick = 0;
    SimJob currentJob = null;
    SimJob lastJob = null;
    String inputFileName = null;
    long NUM_INVOC = 0;
    static boolean DEBUG = false; // ////////////////////////////////////////////////////////////////////////
    static boolean DEBUG_SCHLOG = false;// Added by CY
    static Random random = new Random();

    private EventContainer simEventContainer = new EventContainer();


    /* Comment added by CY on 2015/05/13
    * Argument format:
    * One line indicates to one task.
    * In each line, specify task's values as follows:
    *   taskID, period, executionTime, deadline
    * */
//    public static void main(String[] args) {
//        if (args.length < 2) {
//            System.out.println("Usage: java RM inputfile tickLimit");
//            return;
//        }
//        RM rmfp = new RM();
//        rmfp.run(args[0], Long.parseLong(args[1]));
//    }

    public void setTaskContainer(TaskContainer inTaskContainer)
    {
        simTaskContainer = inTaskContainer;

        // Remove current idle task if there is any because the simulator will create one later.
        simTaskContainer.removeIdleTask();
        simTaskContainer.clearSimData();
        allTasks = simTaskContainer.getTasksAsArray();

        /* Initialize initial offset. (By assigning it to release time.) */
        for (Task thisTask : allTasks) {
            thisTask.nextReleaseTime = thisTask.initialOffset;
        }

        numTasks = allTasks.size(); // It doesn't include the idle task.
        simTaskContainer.addTask(Task.IDLE_TASK_ID, "IDLE", Task.TASK_TYPE_IDLE, 0, 0, 0);
//        allTasks.add(new Task(99, "IDLE", 1, 0, 0, 0, 0));
        simEventContainer.setTaskContainer(simTaskContainer);
        assignPriority();
        // totalUtil?
    }

    public boolean runSim(long tickLimit) {
        if (allTasks == null)
        {
            return false;
        }
        else if (allTasks.size() == 0)
        {
            return false;
        }

        /* Set up tasks' parameters */
//        if (readModel(inputFileName) == false)
//            return false;

        /* Simulation starts. */
        progressUpdater.setIsStarted(true);
        while (true) {
            if (release() == false)
                return false;
            if (schedule() == true)
                tick++;

            progressUpdater.setProgressPercent(((double)tick/(double)tickLimit));

            if (tick == tickLimit) {
                progressUpdater.setIsFinished(true);
                break;
            }

            if (Thread.currentThread().isInterrupted() == true)
            {
                return false;
            }
        }

        for (int i = 0; i < numTasks; i++) {
            Task task_i = allTasks.get(i);
            if (task_i.WCRT < 0) {
                return false;
            }
        }

        /* Output the simulation result. */
//        try {
//            System.out.println("Total Util = " + totalUtil);
//            for (int i = 0; i < numTasks; i++) {
//                SimTask task_i = allTasks.get(i);
//                System.out.println(task_i + ", # response times = "
//                        + task_i.responseTimeHistory.size() + ",\t WCRT = "
//                        + task_i.WCRT);
//                File f = new File("output\\resp_times\\" + inputFileName);
//                f.mkdirs();
//                f = new File("output\\interarrival_times\\" + inputFileName);
//                f.mkdirs();
//                f = new File("output\\exec_times\\" + inputFileName);
//                f.mkdirs();
//                f = new File("output\\all\\" + inputFileName);
//                f.mkdirs();
//
//                PrintWriter pw = new PrintWriter(new File(
//                        "output\\resp_times\\" + inputFileName
//                                + "\\RespTime.Task_" + task_i.id + ".txt"));
//                Iterator<Long> itr = task_i.responseTimeHistory.iterator();
//                while (itr.hasNext())
//                    pw.println(itr.next());
//                pw.close();
//
//                pw = new PrintWriter(new File("output\\interarrival_times\\"
//                        + inputFileName + "\\InterarrivalTime.Task_"
//                        + task_i.id + ".txt"));
//                itr = task_i.interarrivalTImeHistory.iterator();
//                while (itr.hasNext())
//                    pw.println(itr.next());
//                pw.close();
//
//                pw = new PrintWriter(new File("output\\exec_times\\"
//                        + inputFileName + "\\ExecTime.Task_" + task_i.id
//                        + ".txt"));
//                Iterator<Long> itr2 = task_i.execTimeHistory.iterator();
//                while (itr2.hasNext())
//                    pw.println(itr2.next());
//                pw.close();
//
//                pw = new PrintWriter(new File("output\\all\\" + inputFileName
//                        + "\\all.Task_" + task_i.id + ".txt"));
//                int numHist = task_i.responseTimeHistory.size();
//                if (numHist > 0) {
//                    pw.println("," + task_i.execTimeHistory.get(0) + ","
//                            + task_i.responseTimeHistory.get(0));
//                }
//                for (int j = 1; j < numHist; j++) {
//                    pw.println(task_i.interarrivalTImeHistory.get(j - 1) + ","
//                            + task_i.execTimeHistory.get(j) + ","
//                            + task_i.responseTimeHistory.get(j));
//                }
//                pw.close();
//
//                pw = new PrintWriter(new File("output\\all\\" + inputFileName
//                        + "\\resp_distribution.Task_" + task_i.id + ".txt"));
//                double[][] distribution_resp_times = getDistribution(task_i.responseTimeHistory);
//                for (int k=0; k<distribution_resp_times.length; k++) {
//                    pw.println(distribution_resp_times[k][0] + "," + distribution_resp_times[k][1]);
//                }
//                pw.close();
//
//                pw = new PrintWriter(new File("output\\all\\" + inputFileName
//                        + "\\exectime_distribution.Task_" + task_i.id + ".txt"));
//                double[][] distribution_exec_times = getDistribution(task_i.execTimeHistory);
//                for (int k=0; k<distribution_exec_times.length; k++) {
//                    pw.println(distribution_exec_times[k][0] + "," + distribution_exec_times[k][1]);
//                }
//                pw.close();
//
//                pw = new PrintWriter(new File("output\\all\\" + inputFileName
//                        + "\\interarrival_distribution.Task_" + task_i.id + ".txt"));
//                double[][] distribution_interarrival = getDistribution(task_i.interarrivalTImeHistory);
//                for (int k=0; k<distribution_interarrival.length; k++) {
//                    pw.println(distribution_interarrival[k][0] + "," + distribution_interarrival[k][1]);
//                }
//                pw.close();
//
//            }
//            return true;
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        return true;
    }

    public boolean runSimWithProgressDialog(long tickLimit, Component locationReference)
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

        if ( (dialogSimulationProgress.isSimCanceled()==false)&&(rmSimThread.getSimResult()==true))
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
        long simTickLength = 0;
        Boolean simResult = false;

        public SimThread(long inSimTickLength)
        {
            super();
            simTickLength = inSimTickLength;
        }

        public void run()
        {
            simResult = runSim(simTickLength);
        }

        public Boolean getSimResult()
        {
            return simResult;
        }
    }

    long getNextInterarrivalTime(Task task_i) {
        // http://en.wikipedia.org/wiki/Poisson_distribution#Generating_Poisson-distributed_random_variables
        // Example: poisson
                /*
                 * double lambda = task_i.period; double L =
                 * Math.exp(-(1/(double)lambda)); double p = 1.0; int k = 0; do { k++; p
                 * *= Math.random(); } while (p > L); return k - 1;
                 */
                /*
                 * //Case of exponential double lambda = 1/(double)task_i.period; double
                 * u=Math.random(); return (long)(Math.log(1-u)/(double)(-lambda));
                 */
        // Case of Fixed
        return task_i.getPeriodNs();

        // Case of Gaussian
        // double stddev = 0.2;
        // return (long) (random.nextGaussian() * (task_i.period * stddev) +
        // task_i.period);
        // Case of Uniform Distribution
                /*
                 * double ScaleFactor = 0.2; return (long)( ((Math.random()-0.5)*2) *
                 * (ScaleFactor*task_i.period) + task_i.period);
                 */
    }

    long newExecutionTime(Task task_i) {
        // Case of Fixed
        // return task_i.execTime;
        // Case of Gaussian
        double stddev = 0.2;
        return (long) (random.nextGaussian() * (task_i.getComputationTimeNs() * stddev) + task_i.getComputationTimeNs());
    }

    boolean release() {
        for (int i = 0; i < numTasks; i++) {
            Task task_i = allTasks.get(i);
            if ((tick == task_i.nextReleaseTime))
            { // time to release
                long nextInterarrivalTime = getNextInterarrivalTime(task_i);
                task_i.nextReleaseTime = tick + nextInterarrivalTime;
                if (DEBUG)
                    System.out.println("[" + tick + "] Task " + task_i.getId() + "_"
                            + task_i.jobSeqNo
                            + " is released. Next interarrival time = "
                            + nextInterarrivalTime + ", Next release time = "
                            + task_i.nextReleaseTime);
                SimJob job = new SimJob();
                job.task = task_i;
                job.seqNo = task_i.jobSeqNo;
                job.releaseTime = tick;
                //job.remainingExecTime = newExecutionTime(task_i); // Commented out by CY
                job.remainingExecTime = task_i.getComputationTimeNs();    // Added by CY for removing deviations.

                task_i.execTimeHistory.add(job.remainingExecTime);
                task_i.interarrivalTImeHistory.add(nextInterarrivalTime);

                insertToReadyQueue(job);
                task_i.jobSeqNo++;
                activeQueue.add(job);
                if (task_i.lastFinishTime < task_i.lastReleaseTime) {
                    // TODOTODO
                    if (currentJob != null && currentJob.task == task_i
                            && currentJob.remainingExecTime == 1) {
                        // this is fine. it's about to finish.
                    } else {

                        if (DEBUG) {
                                                        /*
                                                         * System.out.println(
                                                         * "task_i.lastFinishTime < task_i.lastReleaseTime"
                                                         * ); System.out.println(task_i);
                                                         * System.out.println(task_i.lastFinishTime);
                                                         * System.out.println(task_i.lastReleaseTime);
                                                         */
                            System.out.println("Unschedulable!!! --> Task "
                                    + task_i.getId() + " has an unfinished job");
                        }
                        // return false;
                        if (DEBUG)
                            System.out.println("but continue...");
                    }
                }
                task_i.lastReleaseTime = tick;
            }
        }
        return true;
    }

    boolean schedule() {
        if (currentJob == null)
        { /* No job is running */
            if (readyQueue.size() == 0) {
                /* No ready job */
                return true;
            }

            /* Pick the highest priority job */
            SimJob job_i = readyQueue.get(0);
            currentJob = job_i;
            readyQueue.remove(0);
            if (DEBUG)
                System.out.println("\t[" + tick + "] Job " + job_i.task.getId()
                        + "_" + job_i.seqNo + " begins execution.");

            if (DEBUG_SCHLOG) {
                if (job_i.remainingExecTime == job_i.task.getComputationTimeNs()) {
                    System.out.println("@SchedulerLog");
                    System.out.println(tick + ", 0, " + job_i.task.getId() + ", \"BEGIN\"");
                    System.out.println("@AppLog");
                    System.out.println(tick + ", " + job_i.task.getId() + ", 0, \"BEGIN\"");
                }
                else {
                    System.out.println("@SchedulerLog");
                    System.out.println(tick + ", 0, " + job_i.task.getId() + ", \"RESUME\"");
                }
            }

            /* Create events. */
            if (job_i.remainingExecTime == job_i.task.getComputationTimeNs()) {
                //TODO: tick is in long(64-bit) while timestamp is in int(32-bit), inconsistent.
                simEventContainer.add(EventContainer.SCHEDULER_EVENT, (int) tick, 0, job_i.task.getId(), "BEGIN");
                simEventContainer.add(EventContainer.APP_EVENT, (int) tick, job_i.task.getId(), 0, "BEGIN");
            }
            else {
                simEventContainer.add(EventContainer.SCHEDULER_EVENT, (int) tick, 0, job_i.task.getId(), "RESUME");
            }


            return true;
        } else {
            /* There is a job running. */
            currentJob.remainingExecTime--;

            if (currentJob.remainingExecTime == 0) {
                /* if the job is finished. */
                currentJob.responseTime = tick - currentJob.releaseTime;
                if (DEBUG)
                    System.out
                            .println("\t\t["
                                    + tick
                                    + "] Job "
                                    + currentJob.task.getId()
                                    + "_"
                                    + currentJob.seqNo
                                    + " finished.  RespTime = "
                                    + currentJob.responseTime
                                    + ", anyjobReady?="
                                    + !readyQueue.isEmpty()
                                    + ", "
                                    + (!readyQueue.isEmpty() ? readyQueue
                                    .get(0).task.getId() : ""));
                currentJob.task.lastFinishTime = tick;
                if (currentJob.task.WCRT < currentJob.responseTime) {
                    currentJob.task.WCRT = currentJob.responseTime;
                }
                currentJob.task.responseTimeHistory
                        .add(currentJob.responseTime);
                if (currentJob.task.WCRT > currentJob.task.getDeadlineNs()) {
                    if (DEBUG)
                        System.out.println("Task " + currentJob.task.getId()
                                + " is not schedulable. WCRT = "
                                + currentJob.task.WCRT + ", deadline = "
                                + currentJob.task.getDeadlineNs());
                    // return false;
                    if (DEBUG)
                        System.out.println("..but continue..");
                }
                activeQueue.remove(currentJob);
                lastJob = currentJob;
                currentJob = null;
                if (readyQueue.size() == 0) {
                    if (DEBUG_SCHLOG) {
                        System.out.println("@SchedulerLog");
                        System.out.println(tick + ", 0, IDLE_TASK_ID, \"IDLE\"");
                    }
                    simEventContainer.add(EventContainer.SCHEDULER_EVENT, (int) tick, 0, Task.IDLE_TASK_ID, "IDLE");
                    return true;
                }
                else {
                    return false;
                    // Time will proceed when picking the highest priority job
                }
            } else {
                /* The current job is not finished yet, thus check whether there is higher priority task preempting it. */
                if (readyQueue.size() == 0) {
                    // No any other job is ready to run, clear!
                    return true;
                }

                SimJob job_i = readyQueue.get(0);
                if (currentJob.task.getPriority() < job_i.task.getPriority()) {
                    // Has a higher priority job, thus start preemption.
                    if (DEBUG)
                        System.out.println("\t\t\t[" + tick + "] Job "
                                + job_i.task.getId() + "_" + job_i.seqNo
                                + " ##preempts## Job " + currentJob.task.getId()
                                + "_" + currentJob.seqNo);

                    if (DEBUG_SCHLOG) {
                        System.out.println("@SchedulerLog");
                        System.out.println(tick + ", 0, " + job_i.task.getId() + ", \"BEGIN\"");
                        System.out.println("@AppLog");
                        System.out.println(tick + ", " + job_i.task.getId() + ", 0, \"BEGIN\"");
                    }

                    simEventContainer.add(EventContainer.SCHEDULER_EVENT, (int) tick, 0, job_i.task.getId(), "BEGIN");
                    simEventContainer.add(EventContainer.APP_EVENT, (int) tick, job_i.task.getId(), 0, "BEGIN");

                    insertToReadyQueue(currentJob);
                    readyQueue.remove(0);
                    currentJob = job_i;
                }
                return true;
            }
        }
    }

    // Insert the job to ready queue according to the priority. (the bigger the higher)
    void insertToReadyQueue(SimJob job_i) {
        int idxToInsert = -1;
        for (int j = 0; j < readyQueue.size(); j++) {
            SimJob job_j = readyQueue.get(j);
            if (job_i.task.getPriority() > job_j.task.getPriority()
                    || (job_i.task.getId() == job_j.task.getId() && job_i.seqNo > job_j.seqNo)) {
                idxToInsert = j;
                break;
            }
        }
        if (idxToInsert > -1)
            readyQueue.add(idxToInsert, job_i);
        else
            readyQueue.add(job_i);
    }

//    boolean readModel(String inputFileName) {
//        allTasks = new ArrayList<SimTask>();
//
//        BufferedReader br = null;
//        String line;
//        StringTokenizer st = null;
//
//        try {
//            br = new BufferedReader(new FileReader(inputFileName));
//
//            while ((line = br.readLine()) != null) {
//                numTasks++;
//                st = new StringTokenizer(line);
//
//                Task task = new Task();
//                task.id = Integer.parseInt(st.nextToken());
//                task.period = Integer.parseInt(st.nextToken());
//                task.execTime = Integer.parseInt(st.nextToken());
//                task.deadline = Integer.parseInt(st.nextToken());
//                task.priority = -1;
//
//                totalUtil += (task.execTime / (double) task.period);
//
//                if (st.hasMoreTokens())
//                    task.initialOffset = Integer.parseInt(st.nextToken());
//                else
//                    task.initialOffset = 0;
//
//                task.nextReleaseTime = task.initialOffset;
//
//                if (task.initialOffset > maximumInitialOffset)
//                    maximumInitialOffset = task.initialOffset;
//
//                task.WCRT = Long.MIN_VALUE;
//                task.jobSeqNo = 0;
//                task.lastReleaseTime = -1;
//                task.lastFinishTime = 0;
//
//                LCM = LCM(LCM, task.period);
//
//                allTasks.add(task);
//            }
//
//                        /* Assign priorities (RM) */
//            for (int i = 0; i < numTasks; i++) {
//                Task task_i = allTasks.get(i);
//                int cnt = 0;
//                for (int j = 0; j < numTasks; j++) {
//                    if (i == j)
//                        continue;
//
//                    Task task_j = allTasks.get(j);
//                    if (task_j.period < task_i.period
//                            || (task_j.period == task_i.period && task_j.id < task_i.id)) {
//                        cnt++;
//                    }
//                }
//                task_i.priority = cnt;
//                if (DEBUG)
//                    System.out.println(task_i);
//            }
//
//
//            for (int i = 0; i < numTasks; i++) {
//                NUM_INVOC += (LCM / allTasks.get(i).period);
//            }
//
//            br.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//            return false;
//        }
//
//        return true;
//    }

    static long GCD(long a, long b) {
        long Remainder;

        while (b != 0) {
            Remainder = a % b;
            a = b;
            b = Remainder;
        }

        return a;
    }

    static long LCM(long a, long b) {
        return a * b / GCD(a, b);
    }

    public static int[][] multiply(int a[][], int b[][]) {
        int aRows = a.length, aColumns = a[0].length, bRows = b.length, bColumns = b[0].length;

        if (aColumns != bRows) {
            throw new IllegalArgumentException("A:Rows: " + aColumns
                    + " did not match B:Columns " + bRows + ".");
        }

        int[][] resultant = new int[aRows][bColumns];

        for (int i = 0; i < aRows; i++) { // aRow
            for (int j = 0; j < bColumns; j++) { // bColumn
                for (int k = 0; k < aColumns; k++) { // aColumn
                    resultant[i][j] += a[i][k] * b[k][j];
                }
            }
        }

        return resultant;
    }

    double calc_WCRT(Task task_i) {
        int numItr = 0;
        double Wi = task_i.getComputationTimeNs();
        double prev_Wi = 0;

        while (true) {
            double interference = 0;
            for (int i = 0; i < numTasks; i++) {
                Task task_hp = allTasks.get(i);
                if (task_hp.getPriority() >= task_i.getPriority())
                    continue;

                double Tj = task_hp.getPeriodNs();
                double Cj = task_hp.getComputationTimeNs();

                interference += myCeil(Wi / Tj) * Cj;
            }

            Wi = task_i.getComputationTimeNs() + interference;

            if (Double.compare(Wi, prev_Wi) == 0)
                return Wi;

            prev_Wi = Wi;

            numItr++;
            if (numItr > 1000 || Double.isInfinite(Wi) || Wi < 0)
                return Double.MAX_VALUE;
        }
    }

    static double myCeil(double val) {
        double diff = Math.ceil(val) - val;
        if (diff > 0.99999) {
            System.out.println("###" + (val) + "###\t\t " + Math.ceil(val));
            System.exit(-1);
        }
        return Math.ceil(val);
    }


    static double[][] getDistribution(LinkedList<Long> list) {
        double[][] distribution = null;
        Collections.sort(list);

        long lowest = list.get(0);
        long highest = list.get(list.size()-1);
        int numDistinct = (int) (highest - lowest + 1);
        int numAll = list.size();

        distribution = new double[numDistinct][numAll];
        for (int i=0; i<distribution.length; i++) {
            distribution[i][0] = lowest + i;
            distribution[i][1] = 0;
        }
        Iterator<Long> itr = list.iterator();
        while(itr.hasNext()) {
            long number = itr.next();
            int idx = (int)(number - lowest);
            distribution[idx][1]++;
        }

        for (int i=0; i<distribution.length; i++) {
            distribution[i][1] /= (double)numAll;
        }

        return distribution;
    }

    // The bigger the number the higher the priority
    protected void assignPriority()
    {
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
            if (DEBUG)
                System.out.println(task_i);
        }
    }

    public EventContainer getSimEventContainer()
    {
        return simEventContainer;
    }
}



//class Task {
//    public int id;
//    public int execTime;
//    public int period;
//    public int deadline;
//    public int priority;
//
//    public int initialOffset;
//
//    public long WCRT;
//
//    public long jobSeqNo;
//    public long lastReleaseTime;
//    public long lastFinishTime;
//
//    public long nextReleaseTime;
//
//    @Override
//    public String toString() {
//        return "[Task " + id + "] p = " + period + ", e = " + execTime
//                + ", d = " + deadline + ", prio = " + priority;
//    }
//
//    public LinkedList<Long> responseTimeHistory = new LinkedList<>();
//    public LinkedList<Long> interarrivalTImeHistory = new LinkedList<>();
//    public LinkedList<Long> execTimeHistory = new LinkedList<>();
//}

