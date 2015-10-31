package com.illinois.rts.simulator;

import com.illinois.rts.utility.GuiUtility;
import com.illinois.rts.visualizer.ProgConfig;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class DialogTaskSetGeneratorSetter extends JDialog implements ActionListener, FocusListener{
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JPanel panelMain;
    private JTextField inputUtilMin;
    private JTextField inputUtilMax;
    private JTextField inputNumTasks;
    private JCheckBox checkUseHyperPeriodUpperBound;
    private JTextField inputHyperPeriodUpperBound;
    private JTextField inputPeriodMin;
    private JTextField inputPeriodMax;
    private JTextField inputExecutionMin;
    private JTextField inputExecutionMax;
    private JTextField inputOffsetMin;
    private JTextField inputOffsetMax;
    private JCheckBox checkNonHarmonicOnly;

    GenerateRmTaskSet taskSetGenerator = null;

    public DialogTaskSetGeneratorSetter() {
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


        /* Action Listener */
        checkUseHyperPeriodUpperBound.addActionListener(this);
        checkNonHarmonicOnly.addActionListener(this);

        /* Focus listener */
        inputUtilMax.addFocusListener(this);
        inputUtilMin.addFocusListener(this);
        inputPeriodMax.addFocusListener(this);
        inputPeriodMin.addFocusListener(this);
        inputExecutionMax.addFocusListener(this);
        inputExecutionMin.addFocusListener(this);
        inputOffsetMax.addFocusListener(this);
        inputOffsetMin.addFocusListener(this);
        inputNumTasks.addFocusListener(this);
        inputHyperPeriodUpperBound.addFocusListener(this);

        // Set the font for entire dialog.
        GuiUtility.changeChildrenFont(this, ProgConfig.DEFAULT_CONTENT_FONT);

        this.setTitle("Task Set Generator Setter");

        this.setResizable(false);

    }

    private void onOK() {
// add your code here
        applyNewSettings();
        dispose();
    }

    private void onCancel() {
// add your code here if necessary
        dispose();
    }

    public void showDialog(Component locationReference) {
        updateDisplayValues();
        this.pack();

        // If the parent frame is assigned, then set this dialog to show at the center.
        if (locationReference != null) {
            this.setLocationRelativeTo(locationReference);
        }
        this.setVisible(true);
    }

    public void setTaskSetGenerator(GenerateRmTaskSet inGenerator) {
        taskSetGenerator = inGenerator;
    }

    private void updateDisplayValues() {
        if (taskSetGenerator == null) {
            return;
        }

        inputNumTasks.setText(String.valueOf(taskSetGenerator.getNumTaskPerSet()));

        inputUtilMax.setText(String.valueOf(taskSetGenerator.getMaxUtil()*100));
        inputUtilMin.setText(String.valueOf(taskSetGenerator.getMinUtil()*100));

        inputPeriodMax.setText(String.valueOf(taskSetGenerator.getMaxPeriod()*ProgConfig.TIMESTAMP_UNIT_TO_MS_MULTIPLIER));
        inputPeriodMin.setText(String.valueOf(taskSetGenerator.getMinPeriod()*ProgConfig.TIMESTAMP_UNIT_TO_MS_MULTIPLIER));

        inputExecutionMax.setText(String.valueOf(taskSetGenerator.getMaxExecTime()*ProgConfig.TIMESTAMP_UNIT_TO_MS_MULTIPLIER));
        inputExecutionMin.setText(String.valueOf(taskSetGenerator.getMinExecTime()*ProgConfig.TIMESTAMP_UNIT_TO_MS_MULTIPLIER));

        inputOffsetMax.setText(String.valueOf(taskSetGenerator.getMaxInitOffset()*ProgConfig.TIMESTAMP_UNIT_TO_MS_MULTIPLIER));
        inputOffsetMin.setText(String.valueOf(taskSetGenerator.getMinInitOffset()*ProgConfig.TIMESTAMP_UNIT_TO_MS_MULTIPLIER));

        inputHyperPeriodUpperBound.setText(String.valueOf(taskSetGenerator.getMaxHyperPeriod()*ProgConfig.TIMESTAMP_UNIT_TO_MS_MULTIPLIER));

        if (taskSetGenerator.getGenerateFromHpDivisors() == true) {
            inputHyperPeriodUpperBound.setEnabled(true);
            checkUseHyperPeriodUpperBound.setSelected(true);
            inputPeriodMax.setEnabled(false);
        } else {
            inputHyperPeriodUpperBound.setEnabled(false);
            checkUseHyperPeriodUpperBound.setSelected(false);
            inputPeriodMax.setEnabled(true);
        }

        checkNonHarmonicOnly.setSelected(taskSetGenerator.getNonHarmonicOnly());
    }

    private void applyNewSettings() {
        if (taskSetGenerator == null) {
            return;
        }

        taskSetGenerator.setNumTaskPerSet(Integer.valueOf(inputNumTasks.getText()));

        taskSetGenerator.setMaxUtil(Double.valueOf(inputUtilMax.getText()) / 100.0);
        taskSetGenerator.setMinUtil(Double.valueOf(inputUtilMin.getText()) / 100.0);

        taskSetGenerator.setMaxPeriod((int) (Double.valueOf(inputPeriodMax.getText()) * ProgConfig.TIMESTAMP_MS_TO_UNIT_MULTIPLIER));
        taskSetGenerator.setMinPeriod((int) (Double.valueOf(inputPeriodMin.getText()) * ProgConfig.TIMESTAMP_MS_TO_UNIT_MULTIPLIER));

        taskSetGenerator.setMaxExecTime((int) (Double.valueOf(inputExecutionMax.getText()) * ProgConfig.TIMESTAMP_MS_TO_UNIT_MULTIPLIER));
        taskSetGenerator.setMinExecTime((int) (Double.valueOf(inputExecutionMin.getText()) * ProgConfig.TIMESTAMP_MS_TO_UNIT_MULTIPLIER));

        taskSetGenerator.setMaxInitOffset((int) (Double.valueOf(inputOffsetMax.getText()) * ProgConfig.TIMESTAMP_MS_TO_UNIT_MULTIPLIER));
        taskSetGenerator.setMinInitOffset((int) (Double.valueOf(inputOffsetMin.getText()) * ProgConfig.TIMESTAMP_MS_TO_UNIT_MULTIPLIER));

        taskSetGenerator.setMaxHyperPeriod((int) (Double.valueOf(inputHyperPeriodUpperBound.getText()) * ProgConfig.TIMESTAMP_MS_TO_UNIT_MULTIPLIER));

        taskSetGenerator.setGenerateFromHpDivisors(checkUseHyperPeriodUpperBound.isSelected() );
        taskSetGenerator.setNonHarmonicOnly( checkNonHarmonicOnly.isSelected() );
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == checkUseHyperPeriodUpperBound) {
            if (checkUseHyperPeriodUpperBound.isSelected() == true) {
                inputHyperPeriodUpperBound.setEnabled(true);
                checkUseHyperPeriodUpperBound.setSelected(true);
                inputPeriodMax.setEnabled(false);
            } else {
                inputHyperPeriodUpperBound.setEnabled(false);
                checkUseHyperPeriodUpperBound.setSelected(false);
                inputPeriodMax.setEnabled(true);
            }
        } else if (e.getSource() == checkNonHarmonicOnly) {
            // Do nothing here.
        }
    }

    @Override
    public void focusGained(FocusEvent e) {
        if ( JTextField.class.isInstance(e.getSource()) ) {
            ((JTextField) e.getSource()).selectAll();
        }
    }

    @Override
    public void focusLost(FocusEvent e) {
        if ( JTextField.class.isInstance(e.getSource()) ) {
            ((JTextField) e.getSource()).select(0, 0);
        }
    }
}
