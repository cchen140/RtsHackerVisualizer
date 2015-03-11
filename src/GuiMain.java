import javax.swing.*;

import com.illinois.rts.visualizer.*;

import java.awt.event.*;

/**
 * Created by CY on 2/10/2015.
 */
public class GuiMain implements ActionListener, MouseListener {
    private JPanel panel1;
    private JButton btnHideTaskList;
    private JButton buttonOpenFile;
    private PanelDrawer zPanel;
    private JList taskList;
    private JPanel taskListPanel;
    private JTextPane msgTextPanel;

    JFrame frame = new JFrame("RTS Hacker Visualizer");

    private static GuiMain instance = null;
    private LogLoader logLoader = new LogLoader();

    public void initGui() {

        frame.setContentPane(new GuiMain().panel1);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 600);
        //frame.pack();   // pack() will adjust the frame size according to the components it has.

        frame.setVisible(true);


    }

    private GuiMain() {

        /* Action listener for buttons */
        btnHideTaskList.addActionListener(this);
        buttonOpenFile.addActionListener(this);

        taskList.addMouseListener(this);

        ListCellRenderer rendererTaskList = new TaskListRenderer();
        taskList.setCellRenderer(rendererTaskList);

        /* Load default demo log file. */
        try {
            logLoader.loadDemoLog();
            drawPlotFromLogLoader();
        } catch (Exception ex) {
            System.err.println(ex);
            //ex.printStackTrace();
        }

    }

    //@Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnHideTaskList) {
            System.out.println("test5");
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
                    clickedTask.setBoxChecked(!clickedTask.isBoxChecked());
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

}
