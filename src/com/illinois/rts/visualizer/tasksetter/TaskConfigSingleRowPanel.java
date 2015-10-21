package com.illinois.rts.visualizer.tasksetter;

import com.illinois.rts.framework.Task;
import com.illinois.rts.utility.GeneralUtility;
import com.illinois.rts.utility.GuiUtility;
import com.illinois.rts.visualizer.ProgConfig;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by CY on 7/21/2015.
 */
public class TaskConfigSingleRowPanel extends JPanel implements ActionListener {
    private static int COMPONENT_SPACE_LENGTH = 15;
    private static int TEXTFIELD_COLUMN_SIZE = 10;

    Task task;
    Boolean removeBtnEnabled = false;   // The remove button is disabled by default.
    Boolean priorityFieldEnabled = true;

    JTextField inputName = new JTextField();
    JTextField inputPeriod = new JTextField();
    JTextField inputComputation = new JTextField();
    JTextField inputInitialOffset = new JTextField();
    JTextField inputPriority = new JTextField();
    JButton inputColor = new JButton();
    JButton removeBtn = new JButton();

    public TaskConfigSingleRowPanel() {
        super();
    }

    public TaskConfigSingleRowPanel(Task inTask, Boolean inEnableRemoveBtn, Boolean inEnablePriorityField) {
        super();

        task = inTask;

        /* Task display color */
        inputColor.setPreferredSize(new Dimension(20, 20));
        inputColor.setBackground(task.getTaskColor());
        inputColor.addActionListener(this);
        this.add(inputColor);
        addSpaceComponent(COMPONENT_SPACE_LENGTH);

        /* Task name */
        addJLabelComponent("Name:");

        inputName.setText(task.getTitle());
        inputName.setColumns(TEXTFIELD_COLUMN_SIZE);
//        inputName.setPreferredSize(new Dimension(100, ProgConfig.DEFAULT_CONTENT_FONT.getStyle()));
        this.add(inputName);

        /* Period */
        addSpaceComponent(COMPONENT_SPACE_LENGTH);
        addJLabelComponent("Period:");

        inputPeriod.setText(GeneralUtility.nanoIntToMilliString((long)task.getPeriodNs() * (long)ProgConfig.TIMESTAMP_UNIT_NS));
        inputPeriod.setColumns(TEXTFIELD_COLUMN_SIZE);
        this.add(inputPeriod);
        addJLabelComponent("ms");

        /* Computation time */
        addSpaceComponent(COMPONENT_SPACE_LENGTH);
        addJLabelComponent("Computation:");

        inputComputation.setText(GeneralUtility.nanoIntToMilliString((long)task.getComputationTimeNs() * (long)ProgConfig.TIMESTAMP_UNIT_NS));
        inputComputation.setColumns(TEXTFIELD_COLUMN_SIZE);
        this.add(inputComputation);
        addJLabelComponent("ms");

        /* Initial offset */
        addSpaceComponent(COMPONENT_SPACE_LENGTH);
        addJLabelComponent("Initial Offset:");

        inputInitialOffset.setText(GeneralUtility.nanoIntToMilliString((long)task.initialOffset * (long)ProgConfig.TIMESTAMP_UNIT_NS));
        inputInitialOffset.setColumns(TEXTFIELD_COLUMN_SIZE);
        this.add(inputInitialOffset);
        addJLabelComponent("ms");

        /* Priority */
        addSpaceComponent(COMPONENT_SPACE_LENGTH);
        addJLabelComponent("Priority:");

        inputPriority.setText(String.valueOf(task.getPriority()));
        inputPriority.setColumns(TEXTFIELD_COLUMN_SIZE);
        this.add(inputPriority);
        priorityFieldEnabled = inEnablePriorityField;
        inputPriority.setEnabled(priorityFieldEnabled);

        /* Remove button */
        addSpaceComponent(COMPONENT_SPACE_LENGTH);
        removeBtn.setText("X");
        removeBtn.setPreferredSize(new Dimension(45, 30));
        removeBtn.setBackground(Color.GRAY);
        this.add(removeBtn);
        removeBtn.addActionListener(this);
        removeBtnEnabled = inEnableRemoveBtn;
        removeBtn.setVisible(removeBtnEnabled);

        GuiUtility.changeChildrenFont(this, ProgConfig.DEFAULT_CONTENT_FONT);

    }

    private void addSpaceComponent(int inWidth) {
        this.add(Box.createRigidArea(new Dimension(inWidth, 0)));    // Space
    }

    private void addJLabelComponent(String inString) {
        JLabel label = new JLabel(inString);
        this.add(label);
    }

    public void applySettings() {
        task.setTitle(inputName.getText());
        task.setPeriodNs((int) (Double.valueOf(inputPeriod.getText()) * (double)ProgConfig.TIMESTAMP_MS_TO_UNIT_MULTIPLIER));
        task.setComputationTimeNs((int) (Double.valueOf(inputComputation.getText()) * (double)ProgConfig.TIMESTAMP_MS_TO_UNIT_MULTIPLIER));
        task.initialOffset = (int) (Double.valueOf(inputInitialOffset.getText()) * (double)ProgConfig.TIMESTAMP_MS_TO_UNIT_MULTIPLIER);
        task.setPriority( Integer.valueOf(inputPriority.getText()) );
        task.setColor(inputColor.getBackground());
    }

    public Task getTask() {
        return task;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == inputColor) {
            inputColor.setBackground(JColorChooser.showDialog(this, "Task Color", inputColor.getBackground()));
        }
        else {
            Component source = getParent();
            if (source instanceof ActionListener) {
                ((ActionListener) source).actionPerformed(new ActionEvent(this, 0, "hello"));
            }
        }
    }

    public void enableRemoveTaskBtn() {
        this.removeBtnEnabled = true;
        removeBtn.setVisible(true);
    }

    public void disableRemoveTaskBtn() {
        this.removeBtnEnabled = false;
        removeBtn.setVisible(false);
    }

    public void enablePriorityField()
    {
        priorityFieldEnabled = true;
        inputPriority.setEnabled(priorityFieldEnabled);
    }

    public void disablePriorityField()
    {
        priorityFieldEnabled = false;
        inputPriority.setEnabled(priorityFieldEnabled);
    }
}
