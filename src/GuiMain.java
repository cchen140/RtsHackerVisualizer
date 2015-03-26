import javax.swing.*;
import javax.swing.border.Border;

import com.illinois.rts.visualizer.*;

import java.awt.*;
import java.awt.event.*;

/**
 * Created by CY on 2/10/2015.
 */
public class GuiMain implements ActionListener, MouseListener, AdjustmentListener {
    private JPanel panel1;
    private JButton btnHideTaskList;
    private JButton buttonOpenFile;
    private PanelDrawer zPanel;
    private JList taskList;
    private JPanel taskListPanel;
    private JTextPane msgTextPane;
    private JButton buttonSettings;
    private JList zPanelList;
    private JScrollBar zPanelScrollBarHorizontal;
    private JScrollPane zPanelScrollHorizontal;
    private JScrollPane zPanelScrollVertical;
    private TimeLinePanel zPanelTimeLine;
    private JScrollPane zPanelTimeLineScrollHorizontal;

    JFrame frame = new JFrame("RTS Hacker Visualizer");

    private static GuiMain instance = null;
    private LogLoader logLoader = new LogLoader();
    public ProgramLogMessenger progMsger = null;

    public void initGui() {

        frame.setContentPane(new GuiMain().panel1);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1200, 800);
        //frame.pack();   // pack() will adjust the frame size according to the components it has.

        frame.setVisible(true);

        /*
        *  Don't initialize any variables here! Put them in GuiMain constructor.
        *  Variables initialized here will remain null for other functions. Why?
        */

    }

    private GuiMain() {

        /* Action listener for buttons */
        btnHideTaskList.addActionListener(this);
        buttonOpenFile.addActionListener(this);
        buttonSettings.addActionListener(this);

        taskList.addMouseListener(this);

        zPanelScrollBarHorizontal.addAdjustmentListener(this);

        ListCellRenderer rendererTaskList = new TaskListRenderer();
        taskList.setCellRenderer(rendererTaskList);

        ListCellRenderer rendererTraceList = new TraceListRenderer();
        zPanelList.setCellRenderer(rendererTraceList);
        zPanelList.setFixedCellHeight(ProgConfig.TRACE_HEIGHT + ProgConfig.TRACE_GAP_Y);

//        zPanelTimeLine
        zPanel.setTraceList(zPanelList);
        zPanel.setTimeLinePanel(zPanelTimeLine);

        /* Load default demo log file. */
        try {
            logLoader.loadDemoLog();
            drawPlotFromLogLoader();
        } catch (Exception ex) {
            System.err.println(ex);
            //ex.printStackTrace();
        }

        // Set up program log messenger handler.
        progMsger = ProgramLogMessenger.getInstance();
        progMsger.setDocument(msgTextPane.getStyledDocument());
        msgTextPane.setFont(new Font("TimesRoman", Font.PLAIN, 16));

        // Make zPanel visible.
        /* For fixing the problem that the zPanel cannot be displayed
         * correctly in form designer. Set it invisible can temporarily
         * make the display normal in the form designer. */
        zPanel.setVisible(true);

        zPanelScrollVertical.getHorizontalScrollBar().setPreferredSize(new Dimension(20, -1));

    }

    //@Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnHideTaskList) {
            //System.out.println("test5");
            //zPanel.toggleDoNotDraw();

            if (taskListPanel.isVisible() == true)
            {
                taskListPanel.setVisible(false);
                btnHideTaskList.setText("Show List");
            }
            else
            {
                taskListPanel.setVisible(true);
                btnHideTaskList.setText("Hide List");
            }

        } else if (e.getSource() == buttonOpenFile) {

            try {
                if (logLoader.loadLogFromDialog() != null) {
                /* TODO: you may want to do something after loading the log file. */
                    drawPlotFromLogLoader();
                }
            } catch (Exception ex) {

            }
        } else if (e.getSource() == buttonSettings) {
//            ProgramLogMessenger.getInstance().putLine("settings button clicked.");
//            System.out.println("setting button clicked.");
//            GuiSettings guiSettings = GuiSettings.getInstance();
//            guiSettings.initGui();
            DialogSettings dialogSettings = DialogSettings.getInstance();
            //dialogSettings.pack();
            //dialogSettings.setVisible(true);
            dialogSettings.showDialog();
//            System.out.println("exit dialog.");
            zPanel.repaint();
        }


    }


    public static GuiMain getInstance() {
        if (instance == null) {
            instance = new GuiMain();
        }
        return instance;
    }

    private void drawPlotFromLogLoader()
    {
        zPanel.setEventContainer(logLoader.getEventContainer());
        // zPanel (PanelDrawer) will update the content automatically, periodically.

        taskList.setListData(logLoader.getEventContainer().getTaskContainer().getTasksAsArray());
    }


    public void mousePressed(MouseEvent e) {
    }

    public void mouseReleased(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public void mouseClicked(MouseEvent e) {
        if (e.getSource() == taskList) {
            JList clickedList = (JList) e.getSource();
            int index = taskList.locationToIndex(e.getPoint());

            if (e.getClickCount() == 1) {
//                System.out.format("Single clicked on %d\n", index);
//                System.out.println(e.getPoint().toString());
                Task clickedTask = (Task) taskList.getModel().getElementAt(index);
                if (e.getPoint().getX() <= 40)
                { // Clicking occurs within the color icon.
                    clickedTask.setDisplayBoxChecked(!clickedTask.isDisplayBoxChecked());
//                    System.out.format("Icon clicked.\n");
                }
                /* Force drawing panel and list to update the appearance*/
                zPanel.repaint();
                taskList.repaint();
            }
            else if (e.getClickCount() == 2) {
                System.out.format("Double clicked on %d\n", index);
                Task o = (Task) clickedList.getModel().getElementAt(index);
                System.out.println(o.getTitle());
                // (TaskListRenderer)clickedList.getCellRenderer()
            }
        }
    }

    @Override
    public void adjustmentValueChanged(AdjustmentEvent e) {
        if (e.getSource() == zPanelScrollBarHorizontal)
        {
            //System.out.println("moving");
            /* Scroll zPanelScrollHorizontal panel according to zPanelScrollBarHorizontal */
            zPanelScrollBarHorizontal.setMaximum(zPanelScrollHorizontal.getHorizontalScrollBar().getMaximum());
            zPanelScrollBarHorizontal.setUnitIncrement(zPanelScrollHorizontal.getHorizontalScrollBar().getUnitIncrement());
            zPanelScrollHorizontal.getHorizontalScrollBar().setValue(zPanelScrollBarHorizontal.getValue());
            zPanelTimeLineScrollHorizontal.getHorizontalScrollBar().setValue(zPanelScrollBarHorizontal.getValue());
        }
    }
}
