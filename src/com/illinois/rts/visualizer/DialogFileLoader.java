package com.illinois.rts.visualizer;

import com.sun.media.sound.InvalidDataException;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.security.InvalidParameterException;

/**
 * Created by CY on 2/16/2015.
 */
public class DialogFileLoader {
    protected String filePath = "";
    protected BufferedReader fileReader = null;

    /**
     * Open dialog for users to select a file.
     * @return 'null' if the dialog is canceled by the user, 'BufferReader' if it opens the file successfully.
     * @exception  IOException if the file is unable to be opened.
     */
    protected BufferedReader openFileFromDialog() throws IOException
    {
        filePath = openFileChooserDialog();
        if (filePath == null)
            return null;

        fileReader = openFile(filePath);
        if (fileReader == null)
            throw new IOException("Log file is incorrect.");
        else
            return fileReader;
    }

    /**
     * Open dialog for users to select a file.
     * @return The absolute path of the selected file from the dialog.
     */
    private String openFileChooserDialog()
    {
        JFileChooser fileChooser = new JFileChooser();
        //fileChooser.setFont(new Font("TimesRoman", Font.PLAIN, 32));//;setPreferredSize(new Dimension(800, 600));
        recursivelySetFonts(fileChooser, new Font("TimesRoman", Font.PLAIN, 18));
        fileChooser.setPreferredSize(new Dimension(800, 600));

        if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
        {
            return fileChooser.getSelectedFile().getAbsolutePath();
        }
        else
        {
            // User cancels the dialog.
            return null;
        }
    }

    /**
     * Load file as a BufferReader.
     * @param filePath The path of a selected log file to be opened.
     * @return a BufferedReader of the opened file.
     */
    protected BufferedReader openFile(String filePath)
    {
        try {
            BufferedReader fileReader = new BufferedReader(new FileReader(filePath));
            return fileReader;
        }
        catch (IOException x)
        {
            System.err.format("IOException @ reading file: %s%n", x);
            //throw new InvalidDataException("Can't load file.");
            return null;
        }
    }

/**
* Load file as a BufferReader.
*/
    private static void recursivelySetFonts(Component comp, Font font) {
        comp.setFont(font);
        if (comp instanceof Container) {
            Container cont = (Container) comp;
            for(int j=0, ub=cont.getComponentCount(); j<ub; ++j)
                recursivelySetFonts(cont.getComponent(j), font);
        }
    }
}
