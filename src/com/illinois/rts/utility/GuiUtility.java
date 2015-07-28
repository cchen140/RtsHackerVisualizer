package com.illinois.rts.utility;

import javax.swing.*;
import java.awt.*;

/**
 * Created by CY on 7/25/2015.
 */
public class GuiUtility {
    /* This code is from StackOverFlow at the following link:
    * http://stackoverflow.com/questions/12730230/set-the-same-font-for-all-component-java
    */
    public static void changeChildrenFont ( Component component, Font font )
    {
        component.setFont(font);
        if ( component instanceof Container )
        {
            for ( Component child : ( ( Container ) component ).getComponents () )
            {
                changeChildrenFont ( child, font );
            }
        }
    }

    public static void resizeJButtonByString(JButton btn)
    {
        int strHeight, strWidth;

        FontMetrics fontMetrics = btn.getFontMetrics( btn.getFont() );
        strWidth = fontMetrics.stringWidth(btn.getText());
        strHeight = fontMetrics.getHeight();
        btn.setPreferredSize( new Dimension(strWidth+40, strHeight+7) );
        btn.setBounds( new Rectangle( btn.getLocation(), btn.getPreferredSize()));
    }

}
