package com.illinois.rts.analysis.busyintervals.autotest;

import com.illinois.rts.utility.GuiUtility;
import com.illinois.rts.visualizer.DataExporter;
import com.illinois.rts.visualizer.DialogFileHandler;
import com.illinois.rts.visualizer.ProgConfig;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

public class DialogLogOutput extends JDialog {
    private JPanel contentPane;
    private JButton btnClose;
    private JButton btnExport;
    private JTextArea textAreaLog;

    public DialogLogOutput() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(btnClose);

        btnClose.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onClose();
            }
        });

        btnExport.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onExport();
            }
        });

// call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onClose();
            }
        });

// call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onClose();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        this.setTitle("Log Output");
        textAreaLog.setEditable(false);

        // Set the font for entire dialog.
        GuiUtility.changeChildrenFont(this, ProgConfig.DEFAULT_CONTENT_FONT);

        this.setMinimumSize(new Dimension(800, 400));

    }

    public JTextArea getTextAreaLog() {
        return textAreaLog;
    }

    private void onExport() {
// add your code here
        DataExporter dataExporter = new DataExporter();
        try {
            dataExporter.exportStringToFileDialog(textAreaLog.getText());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        dispose();
    }

    private void onClose() {
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

    public void put(String format, Object... args) {
        textAreaLog.append(String.format(format, args));
    }

    public void putLine(String format, Object... args) {
        put(format + "\r\n", args);
    }

    public void clear() {
        textAreaLog.setText("");
    }
}
