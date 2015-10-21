package com.illinois.rts.analysis.busyintervals.autotest;

import com.illinois.rts.analysis.busyintervals.AmirDecomposition;
import com.illinois.rts.analysis.busyintervals.BusyIntervalContainer;
import com.illinois.rts.analysis.busyintervals.QuickRmScheduling;
import com.illinois.rts.framework.Task;
import com.illinois.rts.simulator.*;
import com.illinois.rts.utility.GuiUtility;
import com.illinois.rts.visualizer.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.ArrayList;

public class DialogAutoTestWizard extends JDialog implements ActionListener {
    private static int TEXTFIELD_COLUMN_SIZE = 5;
    private static int TABLE_COLUMN_WIDTH = 120;
    private static int TABLE_ROW_HEIGHT = 50;

    private ProgressUpdater progressUpdater;    // It's initialized every time.

    private JPanel contentPane;
    private JButton btnStartAutoTest;
    private JButton buttonCancel;
    private JTable tableTaskSets;
    private JButton btnGenerateTaskSets;
    private JButton btnExportTaskSets;
    private JButton btnImportTaskSets;
    private JTextField inputNumOfTasks;
    private JTextField inputNumOfTaskSets;
    private JScrollPane tableTaskSetsScroll;
    private JTextField inputSimDuration;

    private Boolean startBtnClicked = false;
    private TaskSetContainer taskSetContainer = new TaskSetContainer();

//    private DialogLogOutput dialogLogOutput = new DialogLogOutput();
    String logBuffer = "";

    public DialogAutoTestWizard() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(btnStartAutoTest);

        btnStartAutoTest.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onStart();
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

        this.setTitle("Busy Interval Analysis AutoTest Wizard");

        inputNumOfTasks.setColumns(TEXTFIELD_COLUMN_SIZE);
        inputNumOfTaskSets.setColumns(TEXTFIELD_COLUMN_SIZE);

//        String tableHeader[] = {"Task Set #", "Task 1", "Task 2", "Task 3"};
        ArrayList<String> tableHeader = new ArrayList<String>();
//        tableHeader.addAll({"Task Se t #", "Task 1", "Task 2", "Task 3"});
        //new DefaultTableModel(tableHeader);
        tableHeader.add("Task Set #");
        tableHeader.add("Task 1");
        tableHeader.add("Task 2");
        tableHeader.add("Task 3");
        DefaultTableModel defaultTableModel = new DefaultTableModel(tableHeader.toArray(), 10);
        tableTaskSets.setModel(defaultTableModel);//createDefaultColumnsFromModel(1,1);//.setTableHeader(new JTableHeader(tableHeader));//.createDefaultColumnsFromModel(tableHeader);//.setTableHeader(new JTableHeader());
//        tableTaskSets.addColumn(new TableColumn(2, 3));
//        tableTaskSets = new JTable(10, 5);


        /* Configure tableTaskSetsScroll size. */
        tableTaskSetsScroll.setMaximumSize(new Dimension(800, 400));

        /* Add action listener of buttons. */
        btnStartAutoTest.addActionListener(this);
        btnImportTaskSets.addActionListener(this);
        btnExportTaskSets.addActionListener(this);
        btnGenerateTaskSets.addActionListener(this);

        // Simulation duration.
        inputSimDuration.setText("1000");   // unit is ms

        inputNumOfTaskSets.setText("5");
        inputNumOfTasks.setText("3");

        // Set the font for entire dialog.
        GuiUtility.changeChildrenFont(this, ProgConfig.DEFAULT_CONTENT_FONT);

        this.setResizable(false);

        importDemoTaskSet();
    }

    private void onStart() {
// add your code here

        // Clear the log buffer for a new test.
        logBuffer = "";

        /* Put task set info in logBuffer */
        TaskSetFileHandler taskSetFileHandler = new TaskSetFileHandler();
        logBuffer += taskSetFileHandler.generateProgConfigLines();
        logBuffer += taskSetFileHandler.generateTaskParamsLines();
        logBuffer += taskSetFileHandler.generateTaskSetContainerLines(taskSetContainer);
        logBuffer += "@\r\n";

        Boolean isNotCancel = startAutoTest();
        if (isNotCancel == true) {
            DialogLogOutput dialogLogOutput = new DialogLogOutput();
            dialogLogOutput.put(logBuffer);
            dialogLogOutput.showDialog(this);
        }

        //dispose();
    }

    private void onCancel() {
// add your code here if necessary
        dispose();
    }

    public void showDialog(Component locationReference) {
        this.pack();

        // If the parent frame is assigned, then set this dialog to show at the center.
        if (locationReference != null) {
            this.setLocationRelativeTo(locationReference);
        }

        this.setVisible(true);

    }

    public Boolean importDemoTaskSet() {
        TaskSetFileHandler taskSetFileHandler = new TaskSetFileHandler();

        try {
            taskSetContainer.setTaskContainers(taskSetFileHandler.loadMultipleTaskSetsFromPath("./log/AutoTestDemo_TaskSet_5Tx10.txt"));
        } catch (IOException ex) {
            ex.printStackTrace();
            return false;
        }

        if (taskSetContainer.size() > 0) {
            buildTableFromTaskContainers();
        } // else, nothing loaded thus do nothing.

        return true;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnImportTaskSets) {
            TaskSetFileHandler taskSetFileHandler = new TaskSetFileHandler();

            try {
                taskSetContainer.setTaskContainers( taskSetFileHandler.loadMultipleTaskSetsFromDialog() );
            } catch (Exception ex) {
                ProgMsg.errPutline("Error occurs while importing the task set file.");
                ex.printStackTrace();
                return;
            }

            if (taskSetContainer.size() > 0) {
                buildTableFromTaskContainers();
            } // else, nothing loaded thus do nothing.

        } else if (e.getSource() == btnExportTaskSets) {
            if (taskSetContainer.size() == 0) {
                // No task set exists.
                return;
            }

            TaskSetFileHandler taskSetFileHandler = new TaskSetFileHandler();

            try {
                taskSetFileHandler.exportTaskSetsByDialog(taskSetContainer);
            } catch (Exception ex) {
                ProgMsg.errPutline("Error occurs while exporting the task set file.");
                ex.printStackTrace();
                return;
            }

        } else if (e.getSource() == btnGenerateTaskSets) {
            GenerateRmTaskSet generateRmTaskSet = new GenerateRmTaskSet();
            taskSetContainer = generateRmTaskSet.generate(Integer.valueOf(inputNumOfTasks.getText()), Integer.valueOf(inputNumOfTaskSets.getText()));

            if (taskSetContainer.size() > 0) {
                buildTableFromTaskContainers();
            } // else, nothing loaded thus do nothing.

        } else if (e.getSource() == btnStartAutoTest) {

        }
    }

    private void buildTableFromTaskContainers() {
        /* Configure the table. */

        /* Table's header */
        ArrayList<String> tableHeader = new ArrayList<>();
        tableHeader.add("TaskSet #");

        int mostTaskCount = taskSetContainer.getMostTaskCount();
        for (int loop=1; loop<=mostTaskCount; loop++) {
            tableHeader.add("Task " + loop);
        }
        /* End - Table's header */

        // Set the number of row and column according to the number of task sets and the most task count.
        DefaultTableModel defaultTableModel = new DefaultTableModel(tableHeader.toArray(), taskSetContainer.size());
        tableTaskSets.setModel(defaultTableModel);

        /* Configure width of each column. */
        // mostTaskCount = taskSetContainer.getMostTaskCount();
        tableTaskSets.getColumnModel().getColumn(0).setMinWidth(TABLE_COLUMN_WIDTH / 2);
        for (int loop=1; loop<=mostTaskCount; loop++) {
            tableTaskSets.getColumnModel().getColumn(loop).setMinWidth(TABLE_COLUMN_WIDTH);
        }

        // Configure height of rows.
        tableTaskSets.setRowHeight(TABLE_ROW_HEIGHT);

        /* Fill in the table with task set values. */
        int rowIndex = 0;
        for (TaskContainer thisTaskContainer : taskSetContainer.getTaskContainers()) {
            // Fill the task set sequence number into the first column.
            tableTaskSets.setValueAt("#" + String.valueOf(rowIndex + 1), rowIndex, 0);

            int taskIndex = 1;
            for (Task thisTask : thisTaskContainer.getAppTaskAsArraySortedByPeriod()) {
                tableTaskSets.setValueAt(thisTask.getComputationTimeNs()*ProgConfig.TIMESTAMP_UNIT_NS/1000_000.0 + "ms/" + thisTask.getPeriodNs()*ProgConfig.TIMESTAMP_UNIT_NS/1000_000.0 + "ms", rowIndex, taskIndex);
                taskIndex++;
            }
            rowIndex++;
        }
        /* End - filling table. */

        /* Adjust the size of the scroll panel according to the size of the table and it's pre-designed maximum value. */
        int tablePreferredWidth = (int) (TABLE_COLUMN_WIDTH * (mostTaskCount + 0.7));
        int tablePreferredHeight = TABLE_ROW_HEIGHT * (taskSetContainer.size() + 1);
        if ( tablePreferredHeight > tableTaskSetsScroll.getMaximumSize().getHeight() ) {
            tablePreferredHeight = (int)tableTaskSetsScroll.getMaximumSize().getHeight();
        }
        if ( tablePreferredWidth > tableTaskSetsScroll.getMaximumSize().getWidth() ) {
            tablePreferredWidth = (int)tableTaskSetsScroll.getMaximumSize().getWidth();
            // Since content width is over the scroll panel's maximum display width, thus the scrolling is enabled.
            // (Thus everything will be fit into the scroll panel and no need to adjust column, nothing will be too small.)
            tableTaskSets.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        } else {
            // If it doesn't over the maximum display size of scroll panel, the content could be too small and there
            // will be some space in the last column, thus enable to adjust the last column automatically.
            tableTaskSets.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
        }

        tableTaskSetsScroll.setMinimumSize(new Dimension(tablePreferredWidth, tablePreferredHeight));
        tableTaskSetsScroll.setPreferredSize(new Dimension(tablePreferredWidth, tablePreferredHeight));

        this.pack();
    }

    // With progress dialog
    private Boolean startAutoTest() {
        progressUpdater = new ProgressUpdater();
        DialogSimulationProgress dialogSimulationProgress = new DialogSimulationProgress();
        dialogSimulationProgress.setProgressUpdater(progressUpdater);

        MainTestThread mainTestThread = new MainTestThread();
        dialogSimulationProgress.setWatchedSimThread(mainTestThread);
        mainTestThread.start();

        dialogSimulationProgress.pack();

        dialogSimulationProgress.setLocationRelativeTo(this);
        dialogSimulationProgress.setVisible(true);

        if ( (dialogSimulationProgress.isSimCanceled()==false) )//&&(rmSimThread.getSimResult()==true))
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    private Boolean mainTest() {
        progressUpdater.setIsFinished(false);
        progressUpdater.setIsStarted(true);
        if (taskSetContainer.size() == 0) {
            progressUpdater.setIsFinished(true);
            return false;
        }

        int taskSetIndex = 1;
        int failureCount = 0;
        progressUpdater.setProgressPercent(0.0);
        final int numOfTaskSet = taskSetContainer.getTaskContainers().size();
        for (TaskContainer thisTaskContainer : taskSetContainer.getTaskContainers()) {
            /* Start RM scheduling simulation */
            EventContainer thisEventContainer = null;

            int simDurationNs = (int)(Double.valueOf( inputSimDuration.getText() ) * ProgConfig.TIMESTAMP_MS_TO_UNIT_MULTIPLIER);

            // Get task container from the panel with latest configurations.
            TaskContainer simTaskContainer = thisTaskContainer;

            // Remove everything except app tasks.
            simTaskContainer.removeNoneAppTasks();

            if (simTaskContainer.size() <= 0) {
                continue;
            }

            QuickRmScheduling quickRmScheduling = new QuickRmScheduling(simTaskContainer);
            quickRmScheduling.runSim(simDurationNs);
            thisEventContainer = quickRmScheduling.getSimEventContainer();

            if (thisEventContainer == null) {
                ProgMsg.errPutline("Got empty result from RM scheduling simulation.");
                continue;
            }

            // Build busy intervals

            // Analyze
            BusyIntervalContainer busyIntervalContainer = new BusyIntervalContainer();
            busyIntervalContainer.createBusyIntervalsFromEvents(thisEventContainer);

                /* Analyze busy intervals. The result will be written back to busy intervals. */
            AmirDecomposition amirDecomposition = new AmirDecomposition(thisEventContainer.getTaskContainer(), busyIntervalContainer);
            //Decomposition decomposition = new Decomposition(thisEventContainer.getTaskContainer());
            //decomposition.runAmirDecomposition(busyIntervalContainer);
            try {
                amirDecomposition.runDecomposition();
            } catch (RuntimeException rex) {
                //rex.printStackTrace();
                putLineLogBuffer("#%d TkSet: TERMINATE - " + rex.getMessage(), taskSetIndex);
                failureCount++;
                continue;
            }

            if ( amirDecomposition.verifySchedulingInference() == true ) {
                putLineLogBuffer("#%d TkSet: SUCCESS", taskSetIndex);
            } else {
                putLineLogBuffer("#%d TkSet: FAILED", taskSetIndex);
                failureCount++;
            }

            /* This block of code is for outputting the busy intervals. */
//                DataExporter dataExporter = new DataExporter();
//                try {
//                    dataExporter.exportBusyIntervalsToFileDialog(busyIntervalContainer);
//                } catch (IOException ex) {
//                    ex.printStackTrace();
//                }

                /* Build a trace to show result of busy interval analysis. */
//                TraceGroup decompositionTraceGroup = new TraceGroup();
//                decompositionTraceGroup.setTitle("Decomposition");
////                Trace decompositionInferenceTrace = decomposition.BuildInferenceTrace(busyIntervalContainer);
////                decompositionTraceGroup.addTrace(decomposition.buildAmirDecompositionStep1ResultTrace());
//                decomposition.runAmirDecompositionStep2();
//                decomposition.runAmirDecompositionStep3();
//                decompositionTraceGroup.addTraces(decomposition.buildAmirDecompositionResultTraces());
//                zPanel.getTraceGroupContainer().addTraceGroup(decompositionTraceGroup);
//                applyNewSettingsAndRePaint();

            taskSetIndex++;
            progressUpdater.setProgressPercent((double)taskSetIndex / (double)numOfTaskSet);
        }

        putLineLogBuffer("Summary: %d / %d failures.", failureCount, numOfTaskSet);

        progressUpdater.setIsFinished(true);
        return true;
    }

    void putLineLogBuffer(String format, Object... args) {
        logBuffer += String.format(format + "\r\n", args);
    }

    class MainTestThread extends Thread
    {
        Boolean simResult = false;

        public MainTestThread()
        {
            super();
        }

        public void run()
        {
            mainTest();
            //dispose();
        }

        @Override
        public void interrupt() {
            super.interrupt();
            System.out.println("Interrupted.");
        }

        //        public Boolean getSimResult()
//        {
//            return simResult;
//        }
    }
}
