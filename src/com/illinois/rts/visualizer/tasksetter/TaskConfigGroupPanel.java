package com.illinois.rts.visualizer.tasksetter;

import com.illinois.rts.framework.Task;
import com.illinois.rts.visualizer.ProgConfig;
import com.illinois.rts.visualizer.ProgMsg;
import com.illinois.rts.visualizer.TaskContainer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by CY on 7/21/2015.
 */
public class TaskConfigGroupPanel extends JPanel implements ActionListener {

    HashMap<Task, TaskConfigSingleRowPanel> taskConfigPanels = new HashMap<>();
    TaskContainer taskContainer = new TaskContainer();

    Boolean removeBtnEnabled = false;   // The remove button is disabled by default.
    Boolean priorityFieldEnabled = true;

    public TaskConfigGroupPanel() {
        super();
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        /* Add 3 blank tasks as default task set. */
        this.addOneBlankTask();
        this.addOneBlankTask();
        this.addOneBlankTask();
    }

    public void addOneRow(Task inTask) {
        TaskConfigSingleRowPanel thisTaskConfigRow = new TaskConfigSingleRowPanel(inTask, removeBtnEnabled, priorityFieldEnabled);
        taskConfigPanels.put(inTask, thisTaskConfigRow);

        this.add(thisTaskConfigRow);

        repaint();
        dispatchActionEventToParent();
    }

    public void setTaskContainer(TaskContainer inTaskContainer) {

        /* Clear all existed components. */
        this.removeAll();
        taskConfigPanels.clear();

//        taskContainer = inTaskContainer.clone();
        taskContainer = inTaskContainer;

        ArrayList<Task> appTasks = taskContainer.getAppTasksAsArray();

        for (Task thisTask : appTasks) {
            addOneRow(thisTask);
        }
    }

    public void addOneBlankTask() {
        Task blankTask = taskContainer.addBlankTask();
        addOneRow(blankTask);
    }

    public void removeOneTask(Task inTask) {
        TaskConfigSingleRowPanel removingTaskPanel;
        removingTaskPanel = taskConfigPanels.get(inTask);

        // Remove the task from this display panel, panel hash map, and task container.
        this.remove(removingTaskPanel);
        taskConfigPanels.remove(inTask);
        taskContainer.removeTask(inTask);

        repaint();
        dispatchActionEventToParent();
    }

    public TaskContainer getTaskContainerWithLatestConfigs() {
        for (TaskConfigSingleRowPanel thisTaskRowPanel : taskConfigPanels.values()) {
            // The following function call makes each task update the config according to the input fields.
            thisTaskRowPanel.applySettings();
        }
        return taskContainer;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() instanceof TaskConfigSingleRowPanel) {
            // Going to delete the designated task.

            /* Check whether more than one task left. */
            if (taskConfigPanels.size() <= 1) {
                // It at least has to be 1 task, thus don't do anything if task count is one.
                return;
            }
            else {
                Task thisTask = ((TaskConfigSingleRowPanel) e.getSource()).getTask();
                removeOneTask(thisTask);
            }
        }
    }

    // This function is mainly for informing the parent the change of components.
    // The parent can capture this event to update the display (ie. pack();).
    public void dispatchActionEventToParent() {
        Component source = getParent();
        while ( (source!=null) && (source.getParent()!=null) ) {
            if (source instanceof ActionListener) {
                ((ActionListener) source).actionPerformed(new ActionEvent(this, 0, "hello"));
                break;
            }
            else {
                source = source.getParent();
            }
        }
    }

    public void enableRemoveTaskBtn() {
        this.removeBtnEnabled = true;
        for (TaskConfigSingleRowPanel thisTaskRowPanel : taskConfigPanels.values()) {
            thisTaskRowPanel.enableRemoveTaskBtn();
        }
    }

    public void disableRemoveTaskBtn() {
        this.removeBtnEnabled = false;
        for (TaskConfigSingleRowPanel thisTaskRowPanel : taskConfigPanels.values()) {
            thisTaskRowPanel.disableRemoveTaskBtn();
        }
    }

    public void disablePriorityField()
    {
        this.priorityFieldEnabled = false;
        for (TaskConfigSingleRowPanel thisTaskRowPanel : taskConfigPanels.values()) {
            thisTaskRowPanel.disablePriorityField();
        }
    }
}
