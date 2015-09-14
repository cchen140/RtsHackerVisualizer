package com.illinois.rts.visualizer;

import java.util.ArrayList;

/**
 * Created by jjs on 9/3/15.
 */
public class TaskSetContainer {
    ArrayList<TaskContainer> taskContainers = new ArrayList<>();

    public TaskSetContainer() {
    }

    public TaskSetContainer(ArrayList<TaskContainer> taskContainers) {
        this.taskContainers = taskContainers;
    }

    public ArrayList<TaskContainer> getTaskContainers() {
        return taskContainers;
    }

    public void addTaskSet(TaskContainer inTaskSet) {
        taskContainers.add(inTaskSet);
    }

    public void setTaskContainers(ArrayList<TaskContainer> inTaskContainers) {
        taskContainers.clear();
        taskContainers.addAll( inTaskContainers );
    }

    public int getMostTaskCount() {
        int mostTaskCount = 0;
        for (TaskContainer thisTasks : taskContainers) {
            mostTaskCount = (thisTasks.size()>mostTaskCount) ? thisTasks.size() : mostTaskCount;
        }
        return mostTaskCount;
    }

    public int size() {
        return taskContainers.size();
    }
}
