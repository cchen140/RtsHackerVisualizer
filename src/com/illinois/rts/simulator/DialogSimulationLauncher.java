package com.illinois.rts.simulator;

import com.illinois.rts.analysis.busyintervals.QuickRmScheduling;
import com.illinois.rts.utility.GeneralUtility;
import com.illinois.rts.utility.GuiUtility;
import com.illinois.rts.visualizer.*;
import com.illinois.rts.visualizer.tasksetter.TaskConfigGroupPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

public class DialogSimulationLauncher extends JDialog implements ActionListener {
    private static DialogSimulationLauncher instance = null;

    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField inputSimDuration;
    private JButton btnImportTaskConfig;
    private JButton btnExportTaskConfig;
    private JButton btnAddNewTask;
    private TaskConfigGroupPanel taskSetterPanel;
    private JButton btnRandomTasks;
    private JButton btnConfigureTaskSetGenerator;
    private JRadioButton checkSimDuration;
    private JTextField inputHyperPeriodScale;
    private JRadioButton checkSimAutoHyperPeriodScale;

    private Boolean isOkClicked = false;
    private EventContainer simResultEventContainer;

    private GenerateRmTaskSet taskSEtGenerator = new GenerateRmTaskSet();

    private DialogSimulationLauncher() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

// call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

// call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        /* Assign current class to catch the button events. */
        btnAddNewTask.addActionListener(this);
        btnImportTaskConfig.addActionListener(this);
        btnExportTaskConfig.addActionListener(this);
        btnRandomTasks.addActionListener(this);
        btnConfigureTaskSetGenerator.addActionListener(this);
        checkSimDuration.addActionListener(this);
        checkSimAutoHyperPeriodScale.addActionListener(this);

        // Initialize taskSetterPanel
        taskSetterPanel.enableRemoveTaskBtn();
        taskSetterPanel.disablePriorityField();

        // Simulation duration.
        inputSimDuration.setText("1000");   // unit is ms

        checkSimDuration.setSelected(true);
        inputHyperPeriodScale.setEnabled(false);

        // Set the font for entire dialog.
        GuiUtility.changeChildrenFont(this, ProgConfig.DEFAULT_CONTENT_FONT);

        this.setTitle("Simulation Launcher");

        importDemoTaskSet();
    }

    public static DialogSimulationLauncher getInstance()
    {
        if (instance == null)
        {
            instance = new DialogSimulationLauncher();
        }
        return instance;
    }

    private void onOK() {
// add your code here
        isOkClicked = true;
        dispose();
    }

    private void onCancel() {
// add your code here if necessary
        dispose();
    }

    public static void main(String[] args) {
        DialogSimulationLauncher dialog = new DialogSimulationLauncher();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }

    public void setTaskContainer(TaskContainer inTaskContainer) {
        ProgMsg.debugPutline("HyperPeriod = " + String.valueOf((inTaskContainer.calHyperPeriod() / 1000_000.0) * (double) ProgConfig.TIMESTAMP_UNIT_NS) + " ms");
        taskSetterPanel.setTaskContainer(inTaskContainer);
    }

    public Boolean runLauncher(Component locationReference)
    {
        isOkClicked = false;
        this.pack();
        this.setResizable(false);

        // If the parent frame is assigned, then set this dialog to show at the center.
        if (locationReference != null) {
            this.setLocationRelativeTo(locationReference);
        }

        this.setVisible(true);

        if ( isOkClicked == true ) {
            /* Run simulation */

            // Get task container from the panel with latest configurations.
            TaskContainer simTaskContainer = taskSetterPanel.getTaskContainerWithLatestConfigs();

            // Remove everything except app tasks.
            simTaskContainer.removeNoneAppTasks();

            if (simTaskContainer.size() > 0) {
                //RmScheduling rmScheduling = new RmScheduling();
                //rmScheduling.setTaskContainer(simTaskContainer);

                // Simulation duration.
                int simDuration;
                if (checkSimAutoHyperPeriodScale.isSelected() == true) {
                    simDuration = (int) ((double)simTaskContainer.calHyperPeriod() * Double.valueOf(inputHyperPeriodScale.getText()));
                } else {
                    simDuration = (int) (Double.valueOf(inputSimDuration.getText()) * ProgConfig.TIMESTAMP_MS_TO_UNIT_MULTIPLIER); // ms to default unit
                }

                QuickRmScheduling quickRmScheduling = new QuickRmScheduling(simTaskContainer);
                if (quickRmScheduling.runSimWithProgressDialog(simDuration, this) == true)
                {
                    simResultEventContainer = quickRmScheduling.getSimEventContainer();
                    if ( simResultEventContainer != null ) {
                        return true;
                    }
                }
            }
        }

        // If cancel button is clicked or simulation is terminated, then return false to indicate the incompletion of simulation.
        return false;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if ( e.getSource() == taskSetterPanel ) {
            this.pack();
        } else if (e.getSource() == btnAddNewTask) {
            taskSetterPanel.addOneBlankTask();
        } else if ( e.getSource() == btnImportTaskConfig ) {

            TaskSetFileHandler taskSetFileHandler = new TaskSetFileHandler();
            TaskContainer simTaskContainer;

            try {
                simTaskContainer = taskSetFileHandler.loadSingleTaskSetFromDialog();
            } catch (Exception ex) {
                System.out.println("Error occurs while loading the task config file.");
                ex.printStackTrace();
                return;
            }

            if (simTaskContainer != null) {
                setTaskContainer(simTaskContainer);
            }
        } else if ( e.getSource() == btnExportTaskConfig ) {
            TaskSetFileHandler taskConfigExporter = new TaskSetFileHandler();
            taskConfigExporter.exportSingleTaskSetByDialog(taskSetterPanel.getTaskContainerWithLatestConfigs());
        } else if ( e.getSource() == btnRandomTasks ) {
            taskSEtGenerator.setNumTaskSet(1);  // Generate only 1 task set.
            TaskSetContainer taskSetContainer = taskSEtGenerator.generate();//GeneralUtility.getRandom(3, 10), 1);
            setTaskContainer( taskSetContainer.getTaskContainers().get(0) );
        } else if ( e.getSource() == btnConfigureTaskSetGenerator) {
            DialogTaskSetGeneratorSetter dialogTaskSetGeneratorSetter = new DialogTaskSetGeneratorSetter();
            dialogTaskSetGeneratorSetter.setTaskSetGenerator(taskSEtGenerator);
            dialogTaskSetGeneratorSetter.showDialog(this);
            // It's automatically updates values to taskSetGenerator variable if Ok button is clicked.
        } else if ( e.getSource() == checkSimAutoHyperPeriodScale ) {
            inputSimDuration.setEnabled(false);
            inputHyperPeriodScale.setEnabled(true);
        } else if ( e.getSource() == checkSimDuration ) {
            inputSimDuration.setEnabled(true);
            inputHyperPeriodScale.setEnabled(false);
        }
    }

    public EventContainer getSimulationResultEventContainer() {
        return simResultEventContainer;
    }

    public Boolean importDemoTaskSet() {
        TaskSetFileHandler taskSetFileHandler = new TaskSetFileHandler();

        try {
            setTaskContainer(taskSetFileHandler.loadMultipleTaskSetsFromPath("./log/RmSimulator_TaskSet_5Tx1.txt").get(0));
        } catch (IOException ex) {
            ex.printStackTrace();
            return false;
        }
        return true;
    }
}
