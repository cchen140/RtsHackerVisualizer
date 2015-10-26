package com.illinois.rts.simulator;

import com.illinois.rts.analysis.busyintervals.Interval;
import com.illinois.rts.framework.Task;
import com.illinois.rts.utility.GeneralUtility;
import com.illinois.rts.visualizer.ProgConfig;
import com.illinois.rts.visualizer.ProgMsg;
import com.illinois.rts.visualizer.TaskContainer;
import com.illinois.rts.visualizer.TaskSetContainer;
import com.sun.org.apache.xerces.internal.impl.xpath.regex.Match;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/**
 * Created by CY on 9/29/2015.
 * This file is modified from Man-Ki's GenInput class.
 */
public class GenerateRmTaskSet {
//    private TaskSetContainer taskSetContainer = new TaskSetContainer();

    //static int resolution;

    static int minNumTasks;
    static int maxNumTasks;

    static int minPeriod;
    static int maxPeriod;

    static int maxHyperPeriod;

    static Boolean generateFromHpDivisors;

    static int minExecTime;
    static int maxExecTime;

    static int minInitOffset;
    static int maxInitOffset;

	/* Assume Period = Deadline */

    static double minUtil;
    static double maxUtil;

    static int numTaskPerSet;
    static int numTaskSet;

    static Random rand = new Random();

    public GenerateRmTaskSet() {

        maxHyperPeriod = (1000_000_000/ProgConfig.TIMESTAMP_UNIT_NS)*3; // 3 sec

        /* When maxHyperPeriod is specified, only minPeriod will be checked while the check for maxPeriod will be skipped. */
        maxPeriod = 100_000_000/ProgConfig.TIMESTAMP_UNIT_NS; // 100 ms
        minPeriod = 10_000_000/ProgConfig.TIMESTAMP_UNIT_NS;   // 10 ms // org = 5ms

        maxExecTime = 50_000_000/ProgConfig.TIMESTAMP_UNIT_NS; // 50 ms // org=3 ms
        minExecTime = 100_000/ProgConfig.TIMESTAMP_UNIT_NS; // 0.1 ms

        maxInitOffset = 0; // 0ms //10_000_000; // 10 ms
        minInitOffset = 0; // 0 ms

        maxUtil = 1;
        minUtil = 0.9;

        numTaskPerSet = 5;
        numTaskSet = 10;

        generateFromHpDivisors = false;

    }

    public TaskSetContainer generate() {
        return generate(numTaskPerSet, numTaskSet);
    }

    public TaskSetContainer generate(int inNumTasksPerSet, int inNumTaskSet) {
        maxNumTasks = inNumTasksPerSet;
        minNumTasks = inNumTasksPerSet;

        TaskSetContainer resultTaskSetContainer = new TaskSetContainer();

        for (int i=0; i<inNumTaskSet; i++) {
            TaskContainer thisTaskContainer;
            thisTaskContainer = gen();
            if (thisTaskContainer == null) {
                i--;
                continue;
            } else {
                resultTaskSetContainer.addTaskSet(thisTaskContainer);
            }
        }

        return resultTaskSetContainer;
    }

    public static int getRandom(int min, int max) {
        return rand.nextInt(max - min + 1) + min;
    }

//    public static void main(String[] args)
//    {
//		/* TODO: Genearate input in a given range of utilization*/
//        System.out.println("--Usage--");
//        System.out.println("java GenInput [minNumTasks] [maxNumTasks] [minPeriod] [maxPeriod] [minExecTime] [maxExecTime] [inputStartNo] [inputEndNo] [minUtil] [maxUtil]");
//
//
//        int startNo = Integer.parseInt(args[6]);
//        int endNo = Integer.parseInt(args[7]);
//
//        for (int i = startNo; i <= endNo; i++) {
//            if (gen(args, i) == false) {
//                i--;
//            } else {
//                System.out.println("Inputfile #" + i + " has been generated!");
//            }
//        }
//
//    }

    private TaskContainer gen()
    {

//        minNumTasks = Integer.parseInt(args[0]);
//        maxNumTasks = Integer.parseInt(args[1]);
//        minPeriod = Integer.parseInt(args[2]);
//        maxPeriod= Integer.parseInt(args[3]);
//        minExecTime = Integer.parseInt(args[4]);
//        maxExecTime = Integer.parseInt(args[5]);
//        minUtil = Double.parseDouble(args[8]);
//        maxUtil = Double.parseDouble(args[9]);

        int taskSeq = 0;

        TaskContainer taskContainer = new TaskContainer();
//        ArrayList<Task> allTasks= new ArrayList<Task>();

        int numTasks = getRandom(minNumTasks, maxNumTasks);


        // Is maxHyperPeriod enabled?
        ArrayList<Long> hyperPeriodFactors = null;
        if (generateFromHpDivisors == true) {
            hyperPeriodFactors = GeneralUtility.integerFactorization(maxHyperPeriod);
            //ProgMsg.debugPutline(hyperPeriodFactors.toString());
        }

        ArrayList<Double> utilDistribution = getRandomUtilDistribution(numTasks, minUtil+(maxUtil-minUtil)/2);

        double total_util = 0;
        for (int i = 0; i < numTasks; i++)
        {
            Task task = new Task();
            task.setId(i+1);
            task.setTitle("APP" + String.valueOf(i+1));

            if (generateFromHpDivisors == true) {
                int tempPeriod = 1;
                tempPeriod = getRandomDivisor(hyperPeriodFactors);

                if (tempPeriod<minPeriod) { // || tempPeriod>maxPeriod) {
                    i--;
                    continue;
                }

                if (taskContainer.containPeriod(tempPeriod) == true) {
                    // Skip duplicated period.
                    i--;
                    continue;
                }

                task.setPeriodNs(tempPeriod);
                task.setDeadlineNs(task.getPeriodNs());
            } else {
                int tempPeriod = (int) getRandom(minPeriod, maxPeriod);//TODO: maybe... 2^x 3^y 5^z
                //task.setPeriodNs(tempPeriod - tempPeriod % 50);

                // Round to 1ms.
                task.setPeriodNs(tempPeriod - tempPeriod % (int) (1 * ProgConfig.TIMESTAMP_MS_TO_UNIT_MULTIPLIER));
                //task.setPeriodNs(tempPeriod);
                task.setDeadlineNs(task.getPeriodNs());
            }

            int tempComputationTime;
            tempComputationTime = task.getPeriodNs();
            tempComputationTime  = (int)(((double)tempComputationTime)*utilDistribution.get(i));
            //tempComputationTime = (int)(utilDistribution.get(i)*((double)task.getPeriodNs()));
            if (tempComputationTime<minExecTime || tempComputationTime>maxExecTime) {
                i--;
                continue;
            }

//            if (minExecTime>task.getPeriodNs()) {
//                return null;
//            } else {
//                tempComputationTime = (int) getRandom(minExecTime, Math.min(task.getPeriodNs(), maxExecTime));
//            }

            // Round to 0.1ms (100us).
            //task.setComputationTimeNs(tempComputationTime - tempComputationTime % 100_000);
            task.setComputationTimeNs(tempComputationTime);

            total_util += ( task.getComputationTimeNs() / (double)(task.getPeriodNs()));

            task.setTaskType(Task.TASK_TYPE_APP);

            int tempInitialOffset = (int) getRandom(minInitOffset, Math.min(task.getPeriodNs(), maxInitOffset));
            // Round to 0.1ms (100us).
            //task.setInitialOffset(tempInitialOffset - tempInitialOffset % 100_000);
            task.setInitialOffset(tempInitialOffset);

            taskContainer.addTask(task);
        }

        if (total_util>1)
            return null;

        if (total_util<minUtil || total_util>=maxUtil)
            return null;

        taskContainer.assignPriorityRm();

        if (taskContainer.schedulabilityTest() == false)
            return null;



//        int[][] sl = new int[numTasks][numTasks];
//        for (int i=0; i<numTasks; i++)
//        {
//            for (int j=0; j<numTasks; j++)
//                sl[i][j] = 0;
//        }
//
//        for (int i=0; i<numTasks; i++)
//        {
//            for (int j=i+1; j<numTasks; j++)
//            {
//                if (rand.nextDouble()<=0.5)
//                    sl[i][j] = 1;
//            }
//        }

        return taskContainer;



        //System.out.println("Total util = " + total_util);

//        FileWriter fw = null;
//        PrintWriter pw = null;
//        try
//        {
//            new File("input").mkdir();
//            fw = new FileWriter("input/input_" + outputFileIndex + ".txt");
//            pw = new PrintWriter(fw);
//            pw.println(numTasks);
//
//            for (int i = 0; i < numTasks; i++)
//            {
//                Task task = allTasks.get(i);
//                System.out.println(task.id+ " " + task.period + " " + task.execTime + " " + task.deadline);
//                pw.println(task.id+ " " + task.period + " " + task.execTime + " " + task.deadline);
//            }
//            System.out.println("Total Util = " + total_util);
//
//            for (int i=0; i<numTasks; i++)
//            {
//                for (int j=i+1; j<numTasks; j++)
//                {
//                    if (sl[i][j] == 1)
//                    {
//                        System.out.println(i + " " + j);
//                        pw.println(i + " " + j);
//                    }
//                }
//            }
//            pw.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return true;
    }

//    static long GCD(long a, long b) {
//        long Remainder;
//
//        while (b != 0) {
//            Remainder = a % b;
//            a = b;
//            b = Remainder;
//        }
//
//        return a;
//    }
//
//    static long LCM(long a, long b) {
//        return a * b / GCD(a, b);
//    }
//
//    public long calHyperPeriod(TaskContainer taskContainer) {
//        long hyperPeriod = 1;
//        for (Task thisTask : taskContainer.getAppTasksAsArray()) {
//            hyperPeriod = LCM(hyperPeriod, thisTask.getPeriodNs());
//        }
//        return hyperPeriod;
//    }
//
//    public Boolean schedulabilityTest(TaskContainer taskContainer) {
//        //int numTasks = taskContainer.getAppTasksAsArray().size();
//        for (Task thisTask : taskContainer.getAppTasksAsArray()) {
//            int thisWCRT = calc_WCRT(taskContainer, thisTask);
//            if (thisWCRT > thisTask.getDeadlineNs()) {
//                // unschedulable.
//                //ProgMsg.errPutline("%d > %d", thisWCRT, thisTask.getDeadlineNs());
//                return false;
//            } else {
//                //ProgMsg.sysPutLine("ok: %d < %d", thisWCRT, thisTask.getDeadlineNs());
//            }
//        }
//        return true;
//    }
//
//    // Code modified from Man-Ki's code
//    int calc_WCRT(TaskContainer taskContainer, Task task_i) {
//        int numItr = 0;
//        int Wi = task_i.getComputationTimeNs();
//        int prev_Wi = 0;
//
//        //int numTasks = taskContainer.getAppTasksAsArray().size();
//        while (true) {
//            int interference = 0;
//            for (Task thisTask : taskContainer.getAppTasksAsArray()) {
//                Task task_hp = thisTask;
//                if (task_hp.getPriority() <= task_i.getPriority())  // Priority: the bigger the higher
//                    continue;
//
//                int Tj = task_hp.getPeriodNs();
//                int Cj = task_hp.getComputationTimeNs();
//
//                interference += (int)myCeil((double)Wi / (double)Tj) * Cj;
//            }
//
//            Wi = task_i.getComputationTimeNs() + interference;
//
//            if (Integer.compare(Wi, prev_Wi) == 0)
//                return Wi;
//
//            prev_Wi = Wi;
//
//            numItr++;
//            if (numItr > 1000 || Wi < 0)
//                return Integer.MAX_VALUE;
//        }
//    }
//
//
//    // Code from Man-Ki
//    double myCeil(double val) {
//        double diff = Math.ceil(val) - val;
//        if (diff > 0.99999) {
//            ProgMsg.errPutline("###" + (val) + "###\t\t " + Math.ceil(val));
//            System.exit(-1);
//        }
//        return Math.ceil(val);
//    }
//
//    // The bigger the number the higher the priority
//    // When calling this function, the taskContainer should not contain idle task.
//    protected void assignPriority(TaskContainer taskContainer)
//    {
//        ArrayList<Task> allTasks = taskContainer.getTasksAsArray();
//        int numTasks = taskContainer.getAppTasksAsArray().size();
//
//        /* Assign priorities (RM) */
//        for (Task task_i : taskContainer.getAppTasksAsArray()) {
//            //Task task_i = allTasks.get(i);
//            int cnt = 1;    // 1 represents highest priority.
//            /* Get the priority by comparing other tasks. */
//            for (Task task_j : taskContainer.getAppTasksAsArray()) {
//                if (task_i.equals(task_j))
//                    continue;
//
//                //Task task_j = allTasks.get(j);
//                if (task_j.getPeriodNs() > task_i.getPeriodNs()
//                        || (task_j.getPeriodNs() == task_i.getPeriodNs() && task_j.getId() > task_i.getId())) {
//                    cnt++;
//                }
//            }
//            task_i.setPriority(cnt);
//        }
//    }

    int getRandomDivisor(ArrayList<Long> inFactors) {
        int resultDivisor = 1;
        int numOfFactors = inFactors.size();
        int randomLoopNum = getRandom(1, numOfFactors);
        ArrayList<Integer> factorHistory = new ArrayList<>();
        for (int i=0; i<randomLoopNum; i++) {
            int thisIndex = getRandom(0, numOfFactors-1);
            if (factorHistory.contains(thisIndex) == true) {
                // This item has been chosen, try again.
                i--;
                continue;
            } else {
                // This index has not yet been chosen, so it's clear.
                resultDivisor = resultDivisor * inFactors.get(thisIndex).intValue();
                factorHistory.add(thisIndex);
            }
        }
        return resultDivisor;
    }

    ArrayList<Double> getRandomUtilDistribution(int inMaxTaskNum, double inMaxUtil) {
        ArrayList<Double> resultUtilArray = new ArrayList<>();

        // Initialize the array with evenly divided utilization.
        for (int i=0; i<inMaxTaskNum; i++) {
            resultUtilArray.add(inMaxUtil/(double)inMaxTaskNum);
        }

        double randUnit = inMaxUtil/100.0;
        for (int i=0; i<100; i++) {
            int indexA, indexB;
            indexA = getRandom(0, inMaxTaskNum-1);
            indexB = getRandom(0, inMaxTaskNum-1);

            if (indexA==indexB || resultUtilArray.get(indexB)<0.01) {
                i--;
                continue;
            } else {
                resultUtilArray.set(indexA, resultUtilArray.get(indexA) + randUnit);
                resultUtilArray.set(indexB, resultUtilArray.get(indexB) - randUnit);
            }
        }

        return  resultUtilArray;
    }


    /* The following section is the automatically generated setters and getters. */

    public int getMinNumTasks() {
        return minNumTasks;
    }

    public void setMinNumTasks(int minNumTasks) {
        GenerateRmTaskSet.minNumTasks = minNumTasks;
    }

    public int getMaxNumTasks() {
        return maxNumTasks;
    }

    public void setMaxNumTasks(int maxNumTasks) {
        GenerateRmTaskSet.maxNumTasks = maxNumTasks;
    }

    public int getMinPeriod() {
        return minPeriod;
    }

    public void setMinPeriod(int minPeriod) {
        GenerateRmTaskSet.minPeriod = minPeriod;
    }

    public int getMaxPeriod() {
        return maxPeriod;
    }

    public void setMaxPeriod(int maxPeriod) {
        GenerateRmTaskSet.maxPeriod = maxPeriod;
    }

    public int getMaxHyperPeriod() {
        return maxHyperPeriod;
    }

    public void setMaxHyperPeriod(int maxHyperPeriod) {
        GenerateRmTaskSet.maxHyperPeriod = maxHyperPeriod;
    }

    public int getMinExecTime() {
        return minExecTime;
    }

    public void setMinExecTime(int minExecTime) {
        GenerateRmTaskSet.minExecTime = minExecTime;
    }

    public int getMaxExecTime() {
        return maxExecTime;
    }

    public void setMaxExecTime(int maxExecTime) {
        GenerateRmTaskSet.maxExecTime = maxExecTime;
    }

    public int getMinInitOffset() {
        return minInitOffset;
    }

    public void setMinInitOffset(int minInitOffset) {
        GenerateRmTaskSet.minInitOffset = minInitOffset;
    }

    public int getMaxInitOffset() {
        return maxInitOffset;
    }

    public void setMaxInitOffset(int maxInitOffset) {
        GenerateRmTaskSet.maxInitOffset = maxInitOffset;
    }

    public double getMinUtil() {
        return minUtil;
    }

    public void setMinUtil(double minUtil) {
        GenerateRmTaskSet.minUtil = minUtil;
    }

    public double getMaxUtil() {
        return maxUtil;
    }

    public void setMaxUtil(double maxUtil) {
        GenerateRmTaskSet.maxUtil = maxUtil;
    }

    public int getNumTaskPerSet() {
        return numTaskPerSet;
    }

    public void setNumTaskPerSet(int numTaskPerSet) {
        GenerateRmTaskSet.numTaskPerSet = numTaskPerSet;
    }

    public int getNumTaskSet() {
        return numTaskSet;
    }

    public void setNumTaskSet(int numTaskSet) {
        GenerateRmTaskSet.numTaskSet = numTaskSet;
    }

    public static Boolean getGenerateFromHpDivisors() {
        return generateFromHpDivisors;
    }

    public static void setGenerateFromHpDivisors(Boolean generateFromHpDivisors) {
        GenerateRmTaskSet.generateFromHpDivisors = generateFromHpDivisors;
    }

    public String toCommentString() {
        String outputStr = "";

        outputStr += "## Task set parameters:\r\n";
        outputStr += "# num of tasks per set = " + numTaskPerSet + "\r\n";
        outputStr += "# util = " + minUtil*100 + "%% - " + maxUtil*100 + "%%\r\n";
        outputStr += "# exe = " + minExecTime*ProgConfig.TIMESTAMP_UNIT_TO_MS_MULTIPLIER + "ms - " + maxExecTime*ProgConfig.TIMESTAMP_UNIT_TO_MS_MULTIPLIER + "ms\r\n";
        outputStr += "# offset = " + minInitOffset*ProgConfig.TIMESTAMP_UNIT_TO_MS_MULTIPLIER + "ms - " + maxInitOffset*ProgConfig.TIMESTAMP_UNIT_TO_MS_MULTIPLIER + "ms\r\n";
        outputStr += "# period = " + minPeriod*ProgConfig.TIMESTAMP_UNIT_TO_MS_MULTIPLIER + "ms - " + maxPeriod*ProgConfig.TIMESTAMP_UNIT_TO_MS_MULTIPLIER + "ms\r\n";
        outputStr += "#  - Is tasks generated based on HP upper bound? " + generateFromHpDivisors + "\r\n";
        outputStr += "#  --- If yes, hyper-period upper bound = " + maxHyperPeriod*ProgConfig.TIMESTAMP_UNIT_TO_MS_MULTIPLIER + "ms \r\n";

        return outputStr;
    }
}
