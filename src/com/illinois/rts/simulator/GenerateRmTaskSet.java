package com.illinois.rts.simulator;

import com.illinois.rts.framework.Task;
import com.illinois.rts.visualizer.TaskContainer;
import com.illinois.rts.visualizer.TaskSetContainer;

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

        maxInitOffset = 10_000_000; // 10 ms
        minInitOffset = 0; // 0 ms

        maxUtil = 0.7;
        minUtil = 0.1;

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
            task.setPeriodNs(tempPeriod - tempPeriod % 50);


            if (task.getPeriodNs() < maxExecTime)
            {
                if (minExecTime>task.getPeriodNs())
                    return null;
                task.setComputationTimeNs( (int) getRandom(minExecTime, task.getPeriodNs()) );
            }
            else
                task.setComputationTimeNs( (int) getRandom(minExecTime, maxExecTime) );

            task.setDeadlineNs( task.getPeriodNs() );

            total_util += ( task.getComputationTimeNs() / (double)(task.getPeriodNs()));

            task.setTaskType(Task.TASK_TYPE_APP);

            task.setInitialOffset((int) getRandom(minInitOffset, task.getPeriodNs()));

//            allTasks.add(task);
            taskContainer.addTask(task);

        }

        if (total_util>1)
            return null;

        if (total_util<minUtil || total_util>=maxUtil)
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
}
