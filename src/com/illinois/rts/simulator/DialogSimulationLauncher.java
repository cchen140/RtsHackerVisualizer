package com.illinois.rts.simulator;

import com.illinois.rts.analysis.busyintervals.QuickRmScheduling;
import com.illinois.rts.utility.GeneralUtility;
import com.illinois.rts.utility.GuiUtility;
import com.illinois.rts.visualizer.EventContainer;
import com.illinois.rts.visualizer.ProgConfig;
import com.illinois.rts.visualizer.TaskContainer;
import com.illinois.rts.visualizer.TaskSetContainer;
import com.illinois.rts.visualizer.tasksetter.TaskConfigGroupPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

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

    private Boolean isOkClicked = false;
    private EventContainer simResultEventContainer;

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

        // Initialize taskSetterPanel
        taskSetterPanel.enableRemoveTaskBtn();
        taskSetterPanel.disablePriorityField();

        // Simulation duration.
        inputSimDuration.setText("1000");   // unit is ms

        // Set the font for entire dialog.
        GuiUtility.changeChildrenFont(this, ProgConfig.DEFAULT_CONTENT_FONT);

        this.setTitle("Simulation Launcher");
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

            // Simulation duration.
            int simDurationNs = 1000000* Integer.valueOf( inputSimDuration.getText() ); // ms to ns

            // Get task container from the panel with latest configurations.
            TaskContainer simTaskContainer = taskSetterPanel.getTaskContainerWithLatestConfigs();

            // Remove everything except app tasks.
            simTaskContainer.removeNoneAppTasks();

            if (simTaskContainer.size() > 0) {
                //RmScheduling rmScheduling = new RmScheduling();
                //rmScheduling.setTaskContainer(simTaskContainer);
                QuickRmScheduling quickRmScheduling = new QuickRmScheduling(simTaskContainer);
                if (quickRmScheduling.runSimWithProgressDialog(simDurationNs, this) == true)
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
            GenerateRmTaskSet generateRmTaskSet = new GenerateRmTaskSet();
            TaskSetContainer taskSetContainer = generateRmTaskSet.generate(GeneralUtility.getRandom(3, 6), 1);
            setTaskContainer( taskSetContainer.getTaskContainers().get(0) );
        }
    }

    public EventContainer getSimulationResultEventContainer() {
        return simResultEventContainer;
    }
}
