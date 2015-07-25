package com.illinois.rts.visualizer.tasksetter;

import com.illinois.rts.framework.Task;
import com.illinois.rts.visualizer.ProgMsg;
import com.illinois.rts.visualizer.TaskContainer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class DialogTaskSetter extends JDialog implements ActionListener{
    private static DialogTaskSetter instance = null;

    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private TaskConfigGroupPanel mainPanel;
    private JButton buttonAddBlankTask;

    private Boolean isOkClicked = false;

    private DialogTaskSetter() {
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

        this.setTitle("Task Setter");
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
        DialogTaskSetter dialog = new DialogTaskSetter();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }

    public void loadTaskContainer(TaskContainer inTaskContainer) {
        mainPanel.loadTaskContainer(inTaskContainer);
    }

    public static DialogTaskSetter getInstance()
    {
        if (instance == null)
        {
            instance = new DialogTaskSetter();
        }
        return instance;
    }

    public Boolean showDialog(Component locationReference)
    {
        isOkClicked = false;
        this.pack();
        this.setLocationRelativeTo(locationReference);
        this.setResizable(false);
        this.setVisible(true);

        return (isOkClicked==true) ? true : false;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == mainPanel) {
            this.pack();
        }
    }

    public TaskContainer getTaskContainer() {
        return mainPanel.getTaskContainerWithLatestConfigs();
    }

}
