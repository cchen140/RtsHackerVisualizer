package com.illinois.rts.simulator;

import javax.swing.*;
import java.awt.event.*;
import java.util.Date;

public class DialogSimulationProgress extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JProgressBar progressBar1;

    SimulationProgressThread simProgressThread;

    private ProgressUpdater progressUpdater = new ProgressUpdater();
    private Thread watchedSimThread;

    private Boolean isSimCanceled = false;
    private String dialogTitle = "Simulation Progress";

    public DialogSimulationProgress() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

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


        this.setTitle(dialogTitle);

        /* Start a thread to update the progress bar. */
        simProgressThread = new SimulationProgressThread();
        simProgressThread.start();

    }

    private void onCancel() {
// add your code here if necessary
        watchedSimThread.interrupt();
        simProgressThread.interrupt();
        isSimCanceled = true;
        dispose();
    }

//    public static void main(String[] args) {
//        DialogSimulationProgress dialog = new DialogSimulationProgress();
//        dialog.pack();
//        dialog.setVisible(true);
//        System.exit(0);
//    }

    public void setProgressUpdater(ProgressUpdater inProgressUpdater)
    {
        progressUpdater = inProgressUpdater;
    }

    public void setWatchedSimThread(Thread inThread)
    {
        watchedSimThread = inThread;
    }

    public Boolean isSimCanceled()
    {
        return isSimCanceled;
    }

    public void appendStringToDialogTitle(String inString)
    {
        this.setTitle(dialogTitle + " - " + inString);
    }

    class SimulationProgressThread extends Thread
    {
        public void run()
        {
            try {
                while (true) {
//                System.out.println(String.valueOf(progressUpdater.getProgressPercent()));
                    progressBar1.setValue((int) (progressUpdater.getProgressPercent() * 100));
                    appendStringToDialogTitle( String.valueOf( (int) (progressUpdater.getProgressPercent() * 100)) + "%" );

                    if (progressUpdater.isFinished() == true) {
                        isSimCanceled = false;
                        break;
                    }

                    Thread.sleep(10);
                }
            }
            catch (InterruptedException ie)
            {
                System.out.println("SimulationProgressThread is being interrupted and terminated.");
            }
            dispose();
        }
    }

}
