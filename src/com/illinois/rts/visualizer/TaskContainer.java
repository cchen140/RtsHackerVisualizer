package com.illinois.rts.visualizer;

import com.illinois.rts.framework.Task;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

// For sorting ArrayList
import java.util.Collections;


/**
 * Created by CY on 2/17/2015.
 */
public class TaskContainer {
    //private ArrayList<Task> tasks = new ArrayList<Task>();
    public HashMap<Integer, Task> tasks = new HashMap<Integer, Task>();
    private ArrayList<Color> colorList = new ArrayList<Color>();

    public TaskContainer()
    {
        initColorList();
    }


    public Boolean addTask(int taskId, String taskTitle, int taskType, int taskPeriod, int taskDeadline, int taskComputationTime, int taskPriority)
    {
        String reTaskTitle = taskTitle.toLowerCase().trim();
        if (tasks.containsKey(reTaskTitle))
        {
            return false;
        }

        tasks.put(taskId, new Task(taskId, taskTitle, taskType, taskPeriod, taskComputationTime, taskPriority, taskDeadline));

        if (taskTitle.equalsIgnoreCase("IDLE"))
        {
            tasks.get(taskId).setColor(ProgConfig.IDLE_TASK_COLOR);
        }
        else
        {
            tasks.get(taskId).setColor(getColorByIndex(taskId));
        }

        return true;
    }

    public Boolean addTask(int taskId, String taskTitle, int taskType, int taskPeriod, int taskComputationTime, int taskPriority)
    {
        // When adding this task, assign deadline as equals period.
        return addTask(taskId, taskTitle, taskType, taskPeriod, taskPeriod, taskComputationTime, taskPriority);
    }

    public Boolean addTask(Task inTask) {
        String reTaskTitle = inTask.getTitle().toLowerCase().trim();
        if (tasks.containsKey(reTaskTitle))
        {
            return false;
        }

        tasks.put(inTask.getId(), inTask);

        if (inTask.getTitle().equalsIgnoreCase("IDLE"))
        {
            inTask.setColor(ProgConfig.IDLE_TASK_COLOR);
        }
        else
        {
            inTask.setColor(getColorByIndex(inTask.getId()));
        }

        return true;
    }

    public Task addBlankTask() {
        int maxId = 0;

        /* Search for the largest ID number. */
        for (int thisId : tasks.keySet()) {
            maxId = (maxId>thisId) ? maxId : thisId;
        }
        maxId++;

        addTask(maxId, "Task" + maxId, Task.TASK_TYPE_APP, 10*ProgConfig.TIMESTAMP_MS_TO_UNIT_MULTIPLIER, 10*ProgConfig.TIMESTAMP_MS_TO_UNIT_MULTIPLIER, 1*ProgConfig.TIMESTAMP_MS_TO_UNIT_MULTIPLIER, 0);
        return getTaskById(maxId);
    }

    public Boolean removeTask(Task inTask) {
        if (inTask == null)
            return false;

        int thisTaskId = inTask.getId();
        if (getTaskById(thisTaskId) == null) {
            return false;
        }
        else {
            tasks.remove(thisTaskId);
            return true;
        }
    }

    public Task getTaskById(int searchId)
    {
        return tasks.get(searchId);
    }

    public ArrayList<Task> getTasksAsArray()
    {
        ArrayList<Task> resultTaskList = new ArrayList<Task>();
        ArrayList<Integer> taskIdList = new ArrayList<Integer>(tasks.keySet());
        Collections.sort(taskIdList);
        for (int thisTaskId : taskIdList)
        {
            resultTaskList.add(tasks.get(thisTaskId));
        }

//        return resultTaskList.toArray();
        return resultTaskList;
    }

    public ArrayList<Task> getAppTaskAsArraySortedByComputationTime()
    {
        // This method will return a new task array.
        return SortTasksByComputationTime(getAppTasksAsArray());
    }

    public ArrayList<Task> getAppTaskAsArraySortedByPeriod() {
        // This method will return a new task array.
        return SortTasksByPeriod(getAppTasksAsArray());
    }

    private ArrayList<Task> SortTasksByComputationTime(ArrayList<Task> inTaskArray)
    {
        if (inTaskArray.size() <= 1)
        { // If only one task is left in the array, then just return it.
            return new ArrayList<Task>(inTaskArray);
        }

        /* Find the task that has largest computation time. */
        Task LargestComputationTimeTask = null;
        Boolean firstLoop = true;
        for (Task thisTask : inTaskArray)
        {
            if (firstLoop == true)
            {
                LargestComputationTimeTask = thisTask;
                firstLoop = false;
                continue;
            }
            else
            {
                if (thisTask.getComputationTimeNs() > LargestComputationTimeTask.getComputationTimeNs())
                {
                    LargestComputationTimeTask = thisTask;
                }
            }
        }

        // Clone the input task array and pass it into next layer of recursive function (with largest task removed).
        ArrayList processingTaskArray = new ArrayList<Task>(inTaskArray);
        processingTaskArray.remove(LargestComputationTimeTask);

        // Get the rest of tasks sorted in the array.
        ArrayList<Task> resultTaskArray = SortTasksByComputationTime(processingTaskArray);

        // Add the largest computation time task in the array so that it is in ascending order.
        resultTaskArray.add(LargestComputationTimeTask);
        return resultTaskArray;

    }

    private ArrayList<Task> SortTasksByPeriod(ArrayList<Task> inTaskArray)
    {
        if (inTaskArray.size() <= 1)
        { // If only one task is left in the array, then just return it.
            return new ArrayList<Task>(inTaskArray);
        }

        /* Find the task that has largest period. */
        Task LargestPeriodTask = null;
        Boolean firstLoop = true;
        for (Task thisTask : inTaskArray)
        {
            if (firstLoop == true)
            {
                LargestPeriodTask = thisTask;
                firstLoop = false;
                continue;
            }
            else
            {
                if (thisTask.getPeriodNs() > LargestPeriodTask.getPeriodNs())
                {
                    LargestPeriodTask = thisTask;
                }
            }
        }

        // Clone the input task array and pass it into next layer of recursive function (with largest task removed).
        ArrayList processingTaskArray = new ArrayList<Task>(inTaskArray);
        processingTaskArray.remove(LargestPeriodTask);

        // Get the rest of tasks sorted in the array.
        ArrayList<Task> resultTaskArray = SortTasksByPeriod(processingTaskArray);

        // Add the largest period task in the array so that it is in ascending order.
        resultTaskArray.add(LargestPeriodTask);
        return resultTaskArray;

    }

    public ArrayList<Task> getAppTasksAsArray()
    {
        ArrayList<Task> appTasks = new ArrayList<Task>();
        for (Task thisTask: tasks.values())
        {
            if (thisTask.getTaskType() == Task.TASK_TYPE_APP)
            {
                appTasks.add(thisTask);
            }
        }
        return appTasks;
    }

    public Color getColorByIndex(int index)
    {
        // ArrayList index starts from 0.
        return colorList.get(index);
    }

    private void initColorList()
    {
        final int center = 128;
        final int width = 127;
        final double frequency = 2.4;
        colorList.clear();
        generateColorList(frequency, frequency, frequency, 0, 2, 4, center, width, 50);
    }

    /* Source from: http://krazydad.com/tutorials/makecolors.php */
    private void generateColorList(double frequency1, double frequency2, double frequency3,
                               int phase1, int phase2, int phase3,
                               int center, int width, int len)
    {
        if (center ==0)   center = 128;
        if (width == 0)    width = 127;
        if (len == 0)      len = 50;

        for (int i = 0; i < len; ++i)
        {
            int red = (int) (Math.sin(frequency1*i + phase1) * width + center);
            int grn = (int) (Math.sin(frequency2*i + phase2) * width + center);
            int blu = (int) (Math.sin(frequency3*i + phase3) * width + center);
            colorList.add(new Color(red, grn, blu));
            //System.out.println(colorList.get(i).toString());
        }
    }

    public void clear()
    {
        tasks.clear();
    }

    public int size() { return tasks.size(); }

    /**
     * Caution!!
     * This clone method does not do deep copy.
     * It only creates new hash map and new array list for tasks and colors.
     * Task instances in both original and new hash maps are the same instances.
     * In other words, modifying variables in any task instance would change the original instance.
     *
     * @return TaskContainer a new instance of TaskContainer that has new task HashMap with the same task instances.
     */
    public TaskContainer clone() {
        TaskContainer cloneTaskContainer = new TaskContainer();
        cloneTaskContainer.tasks = (HashMap<Integer, Task>) this.tasks.clone();
        cloneTaskContainer.colorList = (ArrayList<Color>) this.colorList.clone();
        return cloneTaskContainer;
    }

    public Task getTaskByName( String inName ) {
        for (Task thisTask : getTasksAsArray()) {
            if ( thisTask.getTitle().equalsIgnoreCase(inName) == true ) {
                return thisTask;
            }
        }

        // No task has been found.
        return null;
    }

    public void removeIdleTask() {
        Task idleTask = getTaskByName("IDLE");
        if (idleTask != null) {
            removeTask(idleTask);
        }
    }

    public void clearSimData() {
        for (Task thisTask : getTasksAsArray()) {
            thisTask.clearSimData();
        }
    }

    public void removeNoneAppTasks()
    {
        int taskCount = tasks.size();
        int loop;
        for (loop=0; loop<taskCount; loop++) {
            for (Task thisTask : getTasksAsArray()) {
                if (thisTask.getTaskType() != Task.TASK_TYPE_APP) {
                    removeTask(thisTask);
                    break;
                }
            }
        }
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

    public long calHyperPeriod() {
        long hyperPeriod = 1;
        for (Task thisTask : getAppTasksAsArray()) {
            hyperPeriod = LCM(hyperPeriod, thisTask.getPeriodNs());
        }
        return hyperPeriod;
    }

    public Boolean schedulabilityTest() {
        //int numTasks = taskContainer.getAppTasksAsArray().size();
        for (Task thisTask : getAppTasksAsArray()) {
            int thisWCRT = calc_WCRT(thisTask);
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
    int calc_WCRT(Task task_i) {
        int numItr = 0;
        int Wi = task_i.getComputationTimeNs();
        int prev_Wi = 0;

        //int numTasks = taskContainer.getAppTasksAsArray().size();
        while (true) {
            int interference = 0;
            for (Task thisTask : getAppTasksAsArray()) {
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
    public void assignPriorityRm()
    {
        ArrayList<Task> allTasks = getTasksAsArray();
        int numTasks = getAppTasksAsArray().size();

        /* Assign priorities (RM) */
        for (Task task_i : getAppTasksAsArray()) {
            //Task task_i = allTasks.get(i);
            int cnt = 1;    // 1 represents highest priority.
            /* Get the priority by comparing other tasks. */
            for (Task task_j : getAppTasksAsArray()) {
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
