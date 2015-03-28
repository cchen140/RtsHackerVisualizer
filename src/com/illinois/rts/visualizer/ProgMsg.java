package com.illinois.rts.visualizer;

import javax.swing.text.*;
import java.awt.*;
import java.util.Formatter;

/**
 * Created by CY on 3/11/2015.
 */
public class ProgMsg {
    private static ProgMsg instance = null;
    private static StyledDocument outDoc = null;
    //private StyledDocument

    private ProgMsg(){}

    public static synchronized ProgMsg getInstance()
    {
        if (instance == null) {
            instance = new ProgMsg();
        }

        return instance;
    }

    public static void setDocument(StyledDocument inDoc)
    {
        outDoc = inDoc;
        sysPutLine("Log messenger initialized.");
    }

    public static void putLine(String format, Object... args)
    {
          /* Uncomment the following code to print out the method caller. */
//        Throwable t = new Throwable();
//        StackTraceElement ste = t.getStackTrace()[1];
//        inStr = "[" + ste.getMethodName() + "] " + inStr;
        colorPutLine(new Formatter().format(format, args).toString(), Color.black);
    }

    public static void errPutline(String format, Object... args)
    {
        colorPutLine(new Formatter().format(format, args).toString(), Color.red);
    }

    public static void sysPutLine(String format, Object... args)
    {
        colorPutLine(new Formatter().format(format, args).toString(), Color.blue);
    }

    private static void colorPutLine(String inStr, Color inColor)
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
