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
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;

import static com.illinois.rts.utility.Sys.createFolder;

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
    private JTextField inputNumOfTaskSets;
    private JScrollPane tableTaskSetsScroll;
    private JTextField inputSimDuration;
    private JRadioButton radioBtnHyperPeriod;
    private JRadioButton radioBtnCustomDuration;
    private JTextField inputHyperPeriodScale;
    private JButton btnConfigTaskSetGenerator;
    private JCheckBox checkAutoLogEnable;
    private JButton btnAutoLogFolderPath;
    private JButton btnMassTestStart;

    private Boolean startBtnClicked = false;
    private TaskSetContainer taskSetContainer = new TaskSetContainer();
    private GenerateRmTaskSet taskSetGenerator = new GenerateRmTaskSet();

    private int globalFailureCount = 0; // Caution!! This is a cross function variable.
    private double globalPrecisionRatioAverage = 0.0; // Caution!! This is a cross function variable.
    private ArrayList<Double> globalPrecisionRatioRecords = new ArrayList<>();
    private ArrayList<Double> globalPrecisionRatioFullHpRecords = new ArrayList<>();

//    private DialogLogOutput dialogLogOutput = new DialogLogOutput();
    String logBuffer = "";
    String logRawDataBuffer = "";
    String autoLogFileName = "";
    String autoLogFileNamePrefix = "";

    String rootAutoLogPath = "";
    String currentAutoLogPath = "";

    private static DialogAutoTestWizard instance;

    public static DialogAutoTestWizard getInstance()
    {
        if (instance == null)
        {
            instance = new DialogAutoTestWizard();
        }
        return instance;
    }

    private DialogAutoTestWizard() {
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

        //inputNumOfTasks.setColumns(TEXTFIELD_COLUMN_SIZE);
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
        btnConfigTaskSetGenerator.addActionListener(this);
        btnAutoLogFolderPath.addActionListener(this);
        btnMassTestStart.addActionListener(this);
        radioBtnCustomDuration.addActionListener(this);
        radioBtnHyperPeriod.addActionListener(this);
        checkAutoLogEnable.addActionListener(this);


        // Select custom simulation duration by default.
        radioBtnCustomDuration.setSelected(true);
        inputHyperPeriodScale.setEnabled(false);

        // Uncheck auto log
        checkAutoLogEnable.setSelected(false);
        btnAutoLogFolderPath.setEnabled(false);

        // Simulation duration and hyper-period scale.
        inputSimDuration.setText("1000");   // unit is ms
        inputHyperPeriodScale.setText("4.5");

        inputNumOfTaskSets.setText("5");

        // Set the font for entire dialog.
        GuiUtility.changeChildrenFont(this, ProgConfig.DEFAULT_CONTENT_FONT);

        this.setResizable(false);

        importDemoTaskSet();
    }

    private void onStart() {

        long startTime = System.currentTimeMillis();

        // Clear the log buffer for a new test.
        logBuffer = "";
        autoLogFileName = "";
        autoLogFileNamePrefix = "";

        /* Initialize file name prefix. */
        autoLogFileName += "[" + (new DecimalFormat("##.##").format(taskSetGenerator.getMinUtil()));
        autoLogFileName += "," + (new DecimalFormat("##.##").format(taskSetGenerator.getMaxUtil())) + "]_";
        autoLogFileName += taskSetGenerator.getNumTaskPerSet() + "Tx";
        autoLogFileName += taskSetGenerator.getNumTaskSet() + "_";
        if (radioBtnCustomDuration.isSelected() == true) {
            autoLogFileName += "custom" + inputSimDuration.getText() + "ms_";
        } else {
            autoLogFileName += "autoHPx" + inputHyperPeriodScale.getText() + "_";
        }
        if (taskSetGenerator.getGenerateFromHpDivisors() == true) {
            autoLogFileName += taskSetGenerator.getMaxHyperPeriod()*ProgConfig.TIMESTAMP_UNIT_TO_MS_MULTIPLIER + "msHPUpperBound_";
        } else {
            autoLogFileName += "[" + taskSetGenerator.getMinPeriod()*ProgConfig.TIMESTAMP_UNIT_TO_MS_MULTIPLIER;
            autoLogFileName += "," + taskSetGenerator.getMaxPeriod()*ProgConfig.TIMESTAMP_UNIT_TO_MS_MULTIPLIER + "]_";
        }
        if (taskSetGenerator.getMaxInitOffset() != 0) {
            autoLogFileName += "hasOffset_";
        }
        if (taskSetGenerator.getNonHarmonicOnly() == true) {
            autoLogFileName += "nonHarmonic_";
        }


        /* Put note for the settings of task set generator. */
        logBuffer += taskSetGenerator.toCommentString();

        /* Put task set info in logBuffer */
        TaskSetFileHandler taskSetFileHandler = new TaskSetFileHandler();
        logBuffer += taskSetFileHandler.generateProgConfigLines();
        if (radioBtnCustomDuration.isSelected() == true) {
            logBuffer += "#sim duration: " + inputSimDuration.getText() + " ms\r\n";
        } else {
            logBuffer += "#sim duration: " + inputHyperPeriodScale.getText() + "xHP\r\n";
        }

        logBuffer += taskSetFileHandler.generateTaskParamsLines();
        logBuffer += taskSetFileHandler.generateTaskSetContainerLines(taskSetContainer);
        logBuffer += "@\r\n";

        // Start test!!
        Boolean isNotCancel = startAutoTest();

        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;
        ProgMsg.sysPutLine("Exp Total Computation Time: " + elapsedTime/1000.0 + "s");

        if (isNotCancel == true) {
            if (checkAutoLogEnable.isSelected() == true) {
                DataExporter autoLogExporter = new DataExporter();
                try {
                    autoLogExporter.exportStringToFilePath(currentAutoLogPath +"raw_"+autoLogFileName+".txt", "test");
                    autoLogExporter.exportStringToFilePath(currentAutoLogPath +"log_"+autoLogFileName+".txt", logBuffer);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

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
            //GenerateRmTaskSet generateRmTaskSet = new GenerateRmTaskSet();
            taskSetGenerator.setNumTaskSet(Integer.valueOf(inputNumOfTaskSets.getText()));
            taskSetContainer = taskSetGenerator.generate();

            if (taskSetContainer.size() > 0) {
                buildTableFromTaskContainers();
            } // else, nothing loaded thus do nothing.

        } else if (e.getSource() == radioBtnHyperPeriod) {
            inputSimDuration.setEnabled(false);
            inputHyperPeriodScale.setEnabled(true);

        } else if (e.getSource() == radioBtnCustomDuration) {
            inputSimDuration.setEnabled(true);
            inputHyperPeriodScale.setEnabled(false);
        } else if (e.getSource() == btnConfigTaskSetGenerator) {
            DialogTaskSetGeneratorSetter dialogTaskSetGeneratorSetter = new DialogTaskSetGeneratorSetter();
            dialogTaskSetGeneratorSetter.setTaskSetGenerator(taskSetGenerator);
            dialogTaskSetGeneratorSetter.showDialog(this);
        } else if (e.getSource() == btnStartAutoTest) {
            // The function is handled by onStart()
        } else if (e.getSource() == checkAutoLogEnable) {
            if (checkAutoLogEnable.isSelected() == true) {
                btnAutoLogFolderPath.setEnabled(true);
            } else {
                btnAutoLogFolderPath.setEnabled(false);
            }
        } else if (e.getSource() == btnAutoLogFolderPath) {
            DataExporter dataExporter = new DataExporter();
            rootAutoLogPath = dataExporter.getFolderPathFromDialog(rootAutoLogPath) + "\\";
            ProgMsg.debugPutline(rootAutoLogPath);
        } else if (e.getSource() == btnMassTestStart) {
            if (checkAutoLogEnable.isSelected() == true) {
                try {
                    startMassTest();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
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
                tableTaskSets.setValueAt(thisTask.getComputationTimeNs()*ProgConfig.TIMESTAMP_UNIT_TO_MS_MULTIPLIER + "ms/" + thisTask.getPeriodNs()*ProgConfig.TIMESTAMP_UNIT_TO_MS_MULTIPLIER + "ms", rowIndex, taskIndex);
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

        String logFileName = "";


        int taskSetIndex = 1;
        int failureCount = 0;
        double sumOfPrecisionRatio = 0.0;
        int sumOfPrecisionRatioCount = 0;
        progressUpdater.setProgressPercent(0.0);
        final int numOfTaskSet = taskSetContainer.getTaskContainers().size();
        for (TaskContainer thisTaskContainer : taskSetContainer.getTaskContainers()) {
            System.out.println("");
            ProgMsg.debugPutline("#%d", taskSetIndex);

            /* Start RM scheduling simulation */
            EventContainer thisEventContainer = null;
            EventContainer thisFullEventContainer = null;

            // Get task container from the panel with latest configurations.
            TaskContainer simTaskContainer = thisTaskContainer;

            // Remove everything except app tasks.
            simTaskContainer.removeNoneAppTasks();

            if (simTaskContainer.size() <= 0) {
                continue;
            }

            /* Determine simulation duration. */
            int simDurationNs;
            int fullSimDurationNs;
            int hyperPeriod = 0;
            if (radioBtnCustomDuration.isSelected() == true) {
                simDurationNs = (int) (Double.valueOf(inputSimDuration.getText()) * ProgConfig.TIMESTAMP_MS_TO_UNIT_MULTIPLIER);
            } else {
                // Duration is at least one hyper-period
                // TODO: need to make sure the hyper-period doesn't exceed integer limit.
                hyperPeriod = (int) simTaskContainer.calHyperPeriod();
                ProgMsg.debugPutline("HP = %d", hyperPeriod);
                simDurationNs = (int) (hyperPeriod* Double.valueOf(inputHyperPeriodScale.getText()));
                ProgMsg.debugPutline("scaled HP = " + String.valueOf(simDurationNs*ProgConfig.TIMESTAMP_UNIT_TO_MS_MULTIPLIER) + " ms");
            }


            QuickRmScheduling quickRmScheduling = new QuickRmScheduling(simTaskContainer);
            quickRmScheduling.runSim(simDurationNs);
            thisEventContainer = quickRmScheduling.getSimEventContainer();

            fullSimDurationNs = (int) (hyperPeriod * 2);
            quickRmScheduling = new QuickRmScheduling(simTaskContainer);
            quickRmScheduling.runSim(fullSimDurationNs);
            thisFullEventContainer = quickRmScheduling.getSimEventContainer();


            if (thisEventContainer == null) {
                ProgMsg.errPutline("Got empty result from RM scheduling simulation.");
                continue;
            }

            // Build busy intervals
            BusyIntervalContainer busyIntervalContainer = new BusyIntervalContainer();
            busyIntervalContainer.createBusyIntervalsFromEvents(thisEventContainer);

            // For computing precision ratio over full HP (3.5 HP)
            BusyIntervalContainer fullHpBusyIntervalContainer = new BusyIntervalContainer();
            fullHpBusyIntervalContainer.createBusyIntervalsFromEvents(thisFullEventContainer);

            /* Analyze busy intervals. The result will be written back to busy intervals. */
            AmirDecomposition amirDecomposition = new AmirDecomposition(thisEventContainer.getTaskContainer(), busyIntervalContainer);
            //Decomposition decomposition = new Decomposition(thisEventContainer.getTaskContainer());
            //decomposition.runAmirDecomposition(busyIntervalContainer);
            try {
                amirDecomposition.runDecomposition();
                //amirDecomposition.runZeroDecomposition();
                //amirDecomposition.runRandomDecomposition();
                double precisionRatioGmSd = amirDecomposition.computeInferencePrecisionRatioGeometricMeanByTaskStandardDeviation();
                //double precisionRatioGm = amirDecomposition.computeInferencePrecisionRatioGeometricMean();
                //putLineLogBuffer("#%d TkSet: SUCCESS (harmonic?%s)", taskSetIndex, isHarmonic.toString());

                double meanPrFullHp = amirDecomposition.computeMeanPrecisionRatioFromEventContainer(fullHpBusyIntervalContainer);
                globalPrecisionRatioFullHpRecords.add(meanPrFullHp);

                globalPrecisionRatioRecords.add(precisionRatioGmSd);

                Boolean isHarmonic = simTaskContainer.hasHarmonicPeriods();
                //if ( amirDecomposition.verifySchedulingInference() == true ) {
                if (precisionRatioGmSd == 1.0) {
                    putLineLogBuffer("#%d TkSet: SUCCESS (H?-%s) SdGm=%s;", taskSetIndex, isHarmonic.toString(), Double.toString(precisionRatioGmSd));
                } else {
                    putLineLogBuffer("#%d TkSet: FAILED (H?-%s) SdGm=%s;", taskSetIndex, isHarmonic.toString(), Double.toString(precisionRatioGmSd));
                    failureCount++;
                }

                if (precisionRatioGmSd >= 0) {
                    sumOfPrecisionRatio += precisionRatioGmSd;
                    sumOfPrecisionRatioCount++;
                }

                logRawDataBuffer += taskSetIndex;
                logRawDataBuffer += "\t" + (new DecimalFormat("##.##").format(thisTaskContainer.getUtilization()));
                logRawDataBuffer += "\t" + (new DecimalFormat("##.####").format(precisionRatioGmSd));
                logRawDataBuffer += "\r\n";

            } catch (RuntimeException rex) {
                //rex.printStackTrace();
                putLineLogBuffer("#%d TkSet: TERMINATE - " + rex.getMessage(), taskSetIndex);
                ProgMsg.errPutline("#%d TkSet: TERMINATE - " + rex.getMessage(), taskSetIndex);
                rex.printStackTrace();
                failureCount++;
                taskSetIndex++;
                progressUpdater.setProgressPercent((double)taskSetIndex / (double)numOfTaskSet);
                continue;
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
        autoLogFileName = autoLogFileName + failureCount + "Failure";

        globalFailureCount = failureCount;

        if (sumOfPrecisionRatioCount == 0) {
            globalPrecisionRatioAverage = -0.1;
            ProgMsg.errPutline("This should not happen (but may happen...)");
        } else {
            globalPrecisionRatioAverage = sumOfPrecisionRatio / (double) sumOfPrecisionRatioCount;
        }

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

    void startMassTest() throws IOException{

        String columnTitle = "X" +
                "\t\"[0.0,0.1]\"" +
                "\t\"[0.1,0.2]\"" +
                "\t\"[0.2,0.3]\"" +
                "\t\"[0.3,0.4]\"" +
                "\t\"[0.4,0.5]\"" +
                "\t\"[0.5,0.6]\"" +
                "\t\"[0.6,0.7]\"" +
                "\t\"[0.7,0.8]\"" +
                "\t\"[0.8,0.9]\"" +
                "\t\"[0.9,1.0]\"" +
                "\r\n";

        String fullPrOverHpFilePath = rootAutoLogPath + "PrOverHp.txt";
        DataExporter prOverHpLogFile = new DataExporter();
        prOverHpLogFile.exportStringToFilePath(fullPrOverHpFilePath, columnTitle);

        String fullPrSdOverHpFilePath = rootAutoLogPath + "PrSdOverHp.txt";
        DataExporter prSdOverHpLogFile = new DataExporter();
        prSdOverHpLogFile.exportStringToFilePath(fullPrSdOverHpFilePath, columnTitle);

        DataExporter prOverHpFullHpLogFile = new DataExporter(rootAutoLogPath + "PrOverHpFullHp.txt");
        prOverHpFullHpLogFile.appendString(columnTitle);

        DataExporter prSdOverHpFullHpLogFile = new DataExporter(rootAutoLogPath + "PrSdOverHpFullHp.txt");
        prSdOverHpFullHpLogFile.appendString(columnTitle);

        // Hyper period loop
        //for (double hp=3.5; hp<=3.5; hp+=0.1) {
        for (double hp=1.1; hp<=3.1; hp+=0.1) {
        //for (double hp=2.6; hp<=2.6; hp+=0.1) {

            // Set current hyper-period.
            inputHyperPeriodScale.setText(String.valueOf(hp));

            /* Create a dedicated folder. */
            createFolder(rootAutoLogPath + (new DecimalFormat("##.#").format(hp)));
            currentAutoLogPath = rootAutoLogPath + (new DecimalFormat("##.#").format(hp)) + File.separator;

            String fullFailureCountFilePath = currentAutoLogPath + "lowPrecisionCount.txt";
            DataExporter failureCountLogFile = new DataExporter();
            failureCountLogFile.exportStringToFilePath(fullFailureCountFilePath, "X\t10\t11\t12\t13\t14\t15\r\n");

            String fullPrecisionRatioFilePath = currentAutoLogPath + "precisionRatio.txt";
            DataExporter precisionRatioLogFile = new DataExporter();
            precisionRatioLogFile.exportStringToFilePath(fullPrecisionRatioFilePath, "X\t10\t11\t12\t13\t14\t15\r\n");

            // row title
            prOverHpLogFile.appendStringToFilePath(fullPrOverHpFilePath, (new DecimalFormat("##.#").format(hp)));
            prSdOverHpLogFile.appendStringToFilePath(fullPrSdOverHpFilePath, (new DecimalFormat("##.#").format(hp)));

            prOverHpFullHpLogFile.appendString(new DecimalFormat("##.#").format(hp));
            prSdOverHpFullHpLogFile.appendString(new DecimalFormat("##.#").format(hp));

            // utilization loop
            for (double util = 0.001; util < 1; util += 0.1) {
                taskSetGenerator.setMinUtil(util);
                taskSetGenerator.setMaxUtil(util + 0.1);

                // row title
                String rowTitle = "\"" + utilizationRangeString(util, util + 0.1) + "\"";
                failureCountLogFile.appendStringToFilePath(fullFailureCountFilePath, rowTitle);
                precisionRatioLogFile.appendStringToFilePath(fullPrecisionRatioFilePath, rowTitle);

                globalPrecisionRatioFullHpRecords.clear();
                globalPrecisionRatioRecords.clear();

                double sumOfPrThisUtil = 0.0;
                for (int taskPerSet = 10; taskPerSet <= 15; taskPerSet++) {
                //for (int taskPerSet = 1; taskPerSet <= 9; taskPerSet++) {
                    taskSetGenerator.setNumTaskPerSet(taskPerSet);

                    //GenerateRmTaskSet generateRmTaskSet = new GenerateRmTaskSet();
                    taskSetGenerator.setNumTaskSet(Integer.valueOf(inputNumOfTaskSets.getText()));
                    taskSetContainer = taskSetGenerator.generate();

                    /* Make computation time as 80% of current WCET. */
                    for (TaskContainer thisTaskSet : taskSetContainer.getTaskContainers()) {
                        for (Task thisTask : thisTaskSet.getAppTasksAsArray()) {
                            int oldComputationTime = thisTask.getComputationTimeNs();
                            thisTask.setWcet(oldComputationTime);
                            thisTask.setComputationTimeNs((int)(oldComputationTime*0.8));
                        }
                    }

                    if (taskSetContainer.size() > 0) {
                        ProgMsg.debugPutline("Start mass test: %dT [%s,%s]", taskPerSet,
                                (new DecimalFormat("##.##").format(taskSetGenerator.getMinUtil())),
                                (new DecimalFormat("##.##").format(taskSetGenerator.getMaxUtil())));
                        startUnitTest();
                        //failureCount += globalFailureCount;
                    } else {
                        ProgMsg.errPutline("No task set to be tested. Return to issue caution.");
                        return;
                    }

                    failureCountLogFile.appendStringToFilePath(fullFailureCountFilePath, String.format("\t%d", globalFailureCount));
                    precisionRatioLogFile.appendStringToFilePath(fullPrecisionRatioFilePath, String.format("\t%s", (new DecimalFormat("##.###").format(globalPrecisionRatioAverage))));

                    sumOfPrThisUtil += globalPrecisionRatioAverage;
                }

                double sd = computeStandardDeviationFromDoubleArrayList(globalPrecisionRatioRecords);
                prSdOverHpLogFile.appendStringToFilePath(fullPrSdOverHpFilePath, String.format("\t%s", (new DecimalFormat("##.###").format(sd))));

                failureCountLogFile.appendStringToFilePath(fullFailureCountFilePath, "\r\n");
                precisionRatioLogFile.appendStringToFilePath(fullPrecisionRatioFilePath, "\r\n");

                prOverHpLogFile.appendStringToFilePath(fullPrOverHpFilePath, String.format("\t%s", (new DecimalFormat("##.###").format(sumOfPrThisUtil/6.0))));

                double meanHullHp = computeMeanFromDoubleArrayList(globalPrecisionRatioFullHpRecords);
                double sdFullHp = computeStandardDeviationFromDoubleArrayList(globalPrecisionRatioFullHpRecords);
                prOverHpFullHpLogFile.appendString(String.format("\t%s", (new DecimalFormat("##.###").format(meanHullHp))));
                prSdOverHpFullHpLogFile.appendString(String.format("\t%s", (new DecimalFormat("##.###").format(sdFullHp))));
            }

            prOverHpLogFile.appendStringToFilePath(fullPrOverHpFilePath, "\r\n");
            prSdOverHpLogFile.appendStringToFilePath(fullPrSdOverHpFilePath, "\r\n");

            prOverHpFullHpLogFile.appendString("\r\n");
            prSdOverHpFullHpLogFile.appendString("\r\n");
        }

    }

    void startUnitTest() {
        logBuffer = "";
        logRawDataBuffer = "";
        autoLogFileName = "";
        autoLogFileNamePrefix = "";

        /* Initialize file name prefix. */
        autoLogFileName += "[" + (new DecimalFormat("##.##").format(taskSetGenerator.getMinUtil()));
        autoLogFileName += "," + (new DecimalFormat("##.##").format(taskSetGenerator.getMaxUtil())) + "]_";
        autoLogFileName += taskSetGenerator.getNumTaskPerSet() + "Tx";
        autoLogFileName += taskSetGenerator.getNumTaskSet() + "_";
        if (radioBtnCustomDuration.isSelected() == true) {
            autoLogFileName += "custom" + inputSimDuration.getText() + "ms_";
        } else {
            autoLogFileName += "autoHPx" + inputHyperPeriodScale.getText() + "_";
        }
        if (taskSetGenerator.getGenerateFromHpDivisors() == true) {
            autoLogFileName += taskSetGenerator.getMaxHyperPeriod() * ProgConfig.TIMESTAMP_UNIT_TO_MS_MULTIPLIER + "msHPUpperBound_";
        } else {
            autoLogFileName += "[" + taskSetGenerator.getMinPeriod() * ProgConfig.TIMESTAMP_UNIT_TO_MS_MULTIPLIER;
            autoLogFileName += "," + taskSetGenerator.getMaxPeriod() * ProgConfig.TIMESTAMP_UNIT_TO_MS_MULTIPLIER + "]_";
        }
        if (taskSetGenerator.getMaxInitOffset() != 0) {
            autoLogFileName += "hasOffset_";
        }
        if (taskSetGenerator.getNonHarmonicOnly() == true) {
            autoLogFileName += "nonHarmonic_";
        }


        /* Put note for the settings of task set generator. */
        logBuffer += taskSetGenerator.toCommentString();

        /* Put task set info in logBuffer */
        TaskSetFileHandler taskSetFileHandler = new TaskSetFileHandler();
        logBuffer += taskSetFileHandler.generateProgConfigLines();
        if (radioBtnCustomDuration.isSelected() == true) {
            logBuffer += "#sim duration: " + inputSimDuration.getText() + " ms\r\n";
        } else {
            logBuffer += "#sim duration: " + inputHyperPeriodScale.getText() + "xHP\r\n";
        }

        logBuffer += taskSetFileHandler.generateTaskParamsLines();
        logBuffer += taskSetFileHandler.generateTaskSetContainerLines(taskSetContainer);
        logBuffer += "@\r\n";

        // Start test!!
        Boolean isNotCancel = startAutoTest();

        if (isNotCancel == true) {
            if (checkAutoLogEnable.isSelected() == true) {
                DataExporter autoLogExporter = new DataExporter();
                try {
                    autoLogExporter.exportStringToFilePath(currentAutoLogPath + "raw_" + autoLogFileName + ".txt", logRawDataBuffer);
                    autoLogExporter.exportStringToFilePath(currentAutoLogPath + "log_" + autoLogFileName + ".txt", logBuffer);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        //dispose();
    }

    String utilizationRangeString(double inMin, double inMax) {
        String resultString = "";
        resultString += "[" + (new DecimalFormat("##.##").format(inMin));
        resultString += "," + (new DecimalFormat("##.##").format(inMax)) + "]";
        return resultString;
    }

    double computeMeanFromDoubleArrayList(ArrayList<Double> inList) {
        double sum = 0;
        for (Double thisVal : inList) {
            sum += thisVal;
        }
        return sum/inList.size();
    }

    double computeStandardDeviationFromDoubleArrayList(ArrayList<Double> inList) {
        double mean = computeMeanFromDoubleArrayList(inList);
        double sumOfSquare = 0;
        for (Double thisVal : inList) {
             sumOfSquare += Math.pow(mean-thisVal, 2);
        }
        return Math.pow(sumOfSquare/inList.size(), 0.5);
    }
}
