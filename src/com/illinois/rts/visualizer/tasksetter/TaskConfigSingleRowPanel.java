package com.illinois.rts.visualizer.tasksetter;

import com.illinois.rts.framework.Task;
import com.illinois.rts.visualizer.ProgConfig;
import sun.rmi.server.InactiveGroupException;

import javax.swing.*;
import javax.swing.border.Border;
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

    JTextField inputName = new JTextField();
    JTextField inputPeriod = new JTextField();
    JTextField inputComputation = new JTextField();
    JTextField inputInitialOffset = new JTextField();
    JButton inputColor = new JButton();
    JButton removeBtn = new JButton();

    public TaskConfigSingleRowPanel() {
        super();
    }

    public TaskConfigSingleRowPanel(Task inTask) {
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

        inputPeriod.setText(String.valueOf(task.getPeriodNs()));
        inputPeriod.setColumns(TEXTFIELD_COLUMN_SIZE);
        this.add(inputPeriod);
        addJLabelComponent("ns");

        /* Computation time */
        addSpaceComponent(COMPONENT_SPACE_LENGTH);
        addJLabelComponent("Computation:");

        inputComputation.setText(String.valueOf(task.getComputationTimeNs()));
        inputComputation.setColumns(TEXTFIELD_COLUMN_SIZE);
        this.add(inputComputation);
        addJLabelComponent("ns");

        /* Initial offset */
        addSpaceComponent(COMPONENT_SPACE_LENGTH);
        addJLabelComponent("Initial Offset:");

        inputInitialOffset.setText(String.valueOf(task.initialOffset));
        inputInitialOffset.setColumns(TEXTFIELD_COLUMN_SIZE);
        this.add(inputInitialOffset);
        addJLabelComponent("ns");

        /* Remove button */
        addSpaceComponent(COMPONENT_SPACE_LENGTH);
        removeBtn.setText("X");
        removeBtn.setPreferredSize(new Dimension(45, 30));
        removeBtn.setBackground(Color.GRAY);
        this.add(removeBtn);
        removeBtn.addActionListener(this);
        removeBtn.setVisible(removeBtnEnabled);

        changeFont(this, ProgConfig.DEFAULT_CONTENT_FONT);

    }

    private void addSpaceComponent(int inWidth) {
        this.add(Box.createRigidArea(new Dimension(inWidth, 0)));    // Space
    }

    private void addJLabelComponent(String inString) {
        JLabel label = new JLabel(inString);
        this.add(label);
    }


    /* This code is from StackOverFlow at the following link:
     * http://stackoverflow.com/questions/12730230/set-the-same-font-for-all-component-java
     */
    public static void changeFont ( Component component, Font font )
    {
        component.setFont(font);
        if ( component instanceof Container )
        {
            for ( Component child : ( ( Container ) component ).getComponents () )
            {
                changeFont ( child, font );
            }
        }
    }

    public void applySettings() {
        task.setTitle( inputName.getText() );
        task.setPeriodNs(Integer.valueOf(inputPeriod.getText()));
        task.setComputationTimeNs(Integer.valueOf(inputComputation.getText()));
        task.initialOffset = Integer.valueOf(inputInitialOffset.getText());
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
}
