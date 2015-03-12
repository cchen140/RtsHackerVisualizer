package com.illinois.rts.visualizer;

import javax.swing.text.*;
import java.awt.*;

/**
 * Created by CY on 3/11/2015.
 */
public class ProgramLogMessenger {
    private static ProgramLogMessenger instance = null;
    private static StyledDocument outDoc = null;
    //private StyledDocument

    private ProgramLogMessenger(){}

    public static synchronized ProgramLogMessenger getInstance()
    {
        if (instance == null) {
            instance = new ProgramLogMessenger();
        }

        return instance;
    }

    public void setDocument(StyledDocument inDoc)
    {
        outDoc = inDoc;
        this.sysPutLine("Log messenger initialized.");
    }

    public void putLine(String inStr)
    {
        /* Uncomment the following code to print out the method caller. */
//        Throwable t = new Throwable();
//        StackTraceElement ste = t.getStackTrace()[1];
//        inStr = "[" + ste.getMethodName() + "] " + inStr;

        colorPutLine(inStr, Color.black);
    }

    public void errPutline(String inStr)
    {
        colorPutLine(inStr, Color.red);
    }

    public void sysPutLine(String inStr)
    {
        colorPutLine(inStr, Color.blue);
    }

    public void colorPutLine(String inStr, Color inColor)
    {
        if (outDoc == null)
        {
            System.err.println("Error: outDoc in ProgramLogMessenger has not been initialized.");
            return;
        }

        try {
            outDoc.insertString(outDoc.getLength(), inStr + "\n", null);

            Style style = outDoc.addStyle("newStyle", null);
//            StyleConstants.setFontFamily(errStyle, "monospaced");
            StyleConstants.setForeground(style, inColor);

//            outDoc.setParagraphAttributes(0, 1, errStyle, false);
            outDoc.setCharacterAttributes(outDoc.getLength()-inStr.length()-1, inStr.length(), style, false);

        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }
}
