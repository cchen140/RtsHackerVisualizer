package com.illinois.rts.simulator;

import com.illinois.rts.framework.Task;
import com.illinois.rts.visualizer.ProgMsg;
import com.illinois.rts.visualizer.TaskContainer;
import com.illinois.rts.visualizer.TaskSetContainer;
import com.sun.org.apache.xerces.internal.impl.xpath.regex.Match;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by CY on 9/29/2015.
 * This file is modified from Man-Ki's GenInput class.
 */
public class GenerateRmTaskSet {
//    private TaskSetContainer taskSetContainer = new TaskSetContainer();

    static int minNumTasks;
    static int maxNumTasks;

    static int minPeriod;
    static int maxPeriod;

    static int minExecTime;
    static int maxExecTime;

    static int minInitOffset;
    static int maxInitOffset;

	/* Assume Period = Deadline */

    static double minUtil;
    static double maxUtil;

    static Random rand = new Random();

    public GenerateRmTaskSet() {

        maxPeriod = 100_000_000; // 100 ms
        minPeriod = 5_000_000;   // 5ms

        maxExecTime = 3_000_000; // 3 ms
        minExecTime = 300_000; // 0.1 ms

        maxInitOffset = 0; // 0ms //10_000_000; // 10 ms
        minInitOffset = 0; // 0 ms

        maxUtil = 1;
        minUtil = 0.9;

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

        double total_util = 0;
        for (int i = 0; i < numTasks; i++)
        {
            Task task = new Task();
            task.setId(i+1);
            task.setTitle("APP" + String.valueOf(i+1));

            int tempPeriod = (int) getRandom(minPeriod, maxPeriod);//TODO: maybe... 2^x 3^y 5^z
            //task.setPeriodNs(tempPeriod - tempPeriod % 50);

            // Round to 0.1ms (100us).
            task.setPeriodNs(tempPeriod - tempPeriod % 100_000);
            task.setDeadlineNs(task.getPeriodNs());

            int tempComputationTime;
            if (task.getPeriodNs() < maxExecTime) {
                if (minExecTime>task.getPeriodNs())
                    return null;
                tempComputationTime = (int) getRandom(minExecTime, task.getPeriodNs());
            }
            else {
                tempComputationTime = (int) getRandom(minExecTime, maxExecTime);
            }
            // Round to 0.1ms (100us).
            task.setComputationTimeNs(tempComputationTime - tempComputationTime % 100_000);

            total_util += ( task.getComputationTimeNs() / (double)(task.getPeriodNs()));

            task.setTaskType(Task.TASK_TYPE_APP);

            int tempInitialOffset = (int) getRandom(minInitOffset, Math.min(task.getPeriodNs(), maxInitOffset));
            // Round to 0.1ms (100us).
            task.setInitialOffset(tempInitialOffset - tempInitialOffset % 100_000);

            taskContainer.addTask(task);
        }

        if (total_util>1)
            return null;

        if (total_util<minUtil || total_util>=maxUtil)
            return null;

        assignPriority(taskContainer);

        if (schedulabilityTest(taskContainer) == false)
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

    public Boolean schedulabilityTest(TaskContainer taskContainer) {
        //int numTasks = taskContainer.getAppTasksAsArray().size();
        for (Task thisTask : taskContainer.getAppTasksAsArray()) {
            int thisWCRT = calc_WCRT(taskContainer, thisTask);
            if (thisWCRT > thisTask.getDeadlineNs()) {
                // unschedulable.
                //ProgMsg.errPutline("%d > %d", thisWCRT, thisTask.getDeadlineNs());
                return false;
            } else {
                //ProgMsg.sysPutLine("ok: %d < %d", thisWCRT, thisTask.getDeadlineNs());
            }
        }
        return true;
    }

    // Code modified from Man-Ki's code
    int calc_WCRT(TaskContainer taskContainer, Task task_i) {
        int numItr = 0;
        int Wi = task_i.getComputationTimeNs();
        int prev_Wi = 0;

        //int numTasks = taskContainer.getAppTasksAsArray().size();
        while (true) {
            int interference = 0;
            for (Task thisTask : taskContainer.getAppTasksAsArray()) {
                Task task_hp = thisTask;
                if (task_hp.getPriority() <= task_i.getPriority())  // Priority: the bigger the higher
                    continue;

                int Tj = task_hp.getPeriodNs();
                int Cj = task_hp.getComputationTimeNs();

                interference += (int)myCeil((double)Wi / (double)Tj) * Cj;
            }

            Wi = task_i.getComputationTimeNs() + interference;

            if (Integer.compare(Wi, prev_Wi) == 0)
                return Wi;

            prev_Wi = Wi;

            numItr++;
            if (numItr > 1000 || Wi < 0)
                return Integer.MAX_VALUE;
        }
    }


    // Code from Man-Ki
    double myCeil(double val) {
        double diff = Math.ceil(val) - val;
        if (diff > 0.99999) {
            ProgMsg.errPutline("###" + (val) + "###\t\t " + Math.ceil(val));
            System.exit(-1);
        }
        return Math.ceil(val);
    }

    // The bigger the number the higher the priority
    // When calling this function, the taskContainer should not contain idle task.
    protected void assignPriority(TaskContainer taskContainer)
    {
        ArrayList<Task> allTasks = taskContainer.getTasksAsArray();
        int numTasks = taskContainer.getAppTasksAsArray().size();

        /* Assign priorities (RM) */
        for (Task task_i : taskContainer.getAppTasksAsArray()) {
            //Task task_i = allTasks.get(i);
            int cnt = 1;    // 1 represents highest priority.
            /* Get the priority by comparing other tasks. */
            for (Task task_j : taskContainer.getAppTasksAsArray()) {
                if (task_i.equals(task_j))
                    continue;

                //Task task_j = allTasks.get(j);
                if (task_j.getPeriodNs() > task_i.getPeriodNs()
                        || (task_j.getPeriodNs() == task_i.getPeriodNs() && task_j.getId() > task_i.getId())) {
                    cnt++;
                }
            }
            task_i.setPriority(cnt);
        }
    }
}
