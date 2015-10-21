import com.illinois.rts.utility.GuiUtility;
import com.illinois.rts.visualizer.ProgConfig;
import com.illinois.rts.visualizer.ProgMsg;

import javax.swing.*;
import java.awt.event.*;

public class DialogSettings extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField textFieldTimeLineUnit;
    private JTabbedPane tabbedPane1;
    private JCheckBox checkBoxEnableSchedulerSummaryTrace;
    private JCheckBox checkBoxEnableSchedulerTaskTraces;
    private JTextField textFieldTraceHeight;
    private JTextField textFieldTraceMarginY;
    private JTextField textFieldVirtualPanelMarginY;
    private JTextField textFieldVirtualPanelMarginX;
    private JTextField textFieldHorizontalScale;
    private JTextField textFieldTimestampUnitNs;

    private static DialogSettings instance = null;

    private boolean isSettingsUpdated = false;

    private DialogSettings() {
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

        this.setTitle("Visualizer Settings");

        GuiUtility.changeChildrenFont(this, ProgConfig.DEFAULT_CONTENT_FONT);
    }

    private void onOK() {
// add your code here
        this.applySettings();
        dispose();
    }

    private void onCancel() {
// add your code here if necessary
        dispose();
    }

    public static void main(String[] args) {
        DialogSettings dialog = new DialogSettings();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }

    public static DialogSettings getInstance()
    {
        if (instance == null)
        {
            instance = new DialogSettings();
        }
        return instance;
    }

    public void updateDialog()
    {
        /* Trace Setting */
        textFieldTimestampUnitNs.setText(String.valueOf(ProgConfig.TIMESTAMP_UNIT_NS));
        textFieldTimeLineUnit.setText(String.valueOf(ProgConfig.TIME_LINE_PERIOD_NS/1000_000.0));
        textFieldHorizontalScale.setText(String.valueOf(ProgConfig.TRACE_HORIZONTAL_SCALE_FACTOR));
        checkBoxEnableSchedulerSummaryTrace.setSelected(ProgConfig.DISPLAY_SCHEDULER_SUMMARY_TRACE);
        checkBoxEnableSchedulerTaskTraces.setSelected(ProgConfig.DISPLAY_SCHEDULER_TASK_TRACES);

        /* Trace Layout */
        textFieldVirtualPanelMarginX.setText(String.valueOf(ProgConfig.VIRTUAL_PANEL_MARGIN_X));
        textFieldVirtualPanelMarginY.setText(String.valueOf(ProgConfig.VIRTUAL_PANEL_MARGIN_Y));
        textFieldTraceMarginY.setText(String.valueOf(ProgConfig.TRACE_MARGIN_Y));
        textFieldTraceHeight.setText(String.valueOf(ProgConfig.TRACE_HEIGHT));

    }

    public void applySettings()
    {
        /* Trace Setting */
        ProgConfig.setTimestampUnitNs(Integer.valueOf(textFieldTimestampUnitNs.getText()));
        ProgConfig.TIME_LINE_PERIOD_NS = (int) (Double.valueOf(textFieldTimeLineUnit.getText())*1000_000);
        ProgConfig.TRACE_HORIZONTAL_SCALE_FACTOR = Double.valueOf(textFieldHorizontalScale.getText());
        ProgConfig.DISPLAY_SCHEDULER_SUMMARY_TRACE = checkBoxEnableSchedulerSummaryTrace.isSelected();
        ProgConfig.DISPLAY_SCHEDULER_TASK_TRACES = checkBoxEnableSchedulerTaskTraces.isSelected();

        /* Trace Layout */
        ProgConfig.VIRTUAL_PANEL_MARGIN_X = Integer.valueOf(textFieldVirtualPanelMarginX.getText());
        ProgConfig.VIRTUAL_PANEL_MARGIN_Y = Integer.valueOf(textFieldVirtualPanelMarginY.getText());
        ProgConfig.TRACE_MARGIN_Y = Integer.valueOf(textFieldTraceMarginY.getText());
        ProgConfig.TRACE_HEIGHT = Integer.valueOf(textFieldTraceHeight.getText());

        isSettingsUpdated = true;
    }

    public void showDialog()
    {
        updateDialog();
        this.pack();
        this.setVisible(true);
    }

    public boolean isSettingsUpdated()
    {
        boolean result = isSettingsUpdated;
        isSettingsUpdated = false;
        return result;
    }
}
