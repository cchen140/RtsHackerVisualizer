package com.illinois.rts.utility;

import java.awt.*;
import java.awt.geom.AffineTransform;

/**
 * Created by CY on 7/25/2015.
 */
public class GraphicUtility {
    public static void drawArrow(Graphics g1, int x1, int y1, int x2, int y2) {
        int ARR_SIZE = 7;
        Graphics2D g = (Graphics2D) g1.create();

        double dx = x2 - x1, dy = y2 - y1;
        double angle = Math.atan2(dy, dx);
        int len = (int) Math.sqrt(dx*dx + dy*dy);
        AffineTransform at = AffineTransform.getTranslateInstance(x1, y1);
        at.concatenate(AffineTransform.getRotateInstance(angle));
        g.transform(at);

        // Draw horizontal arrow starting in (0, 0)
        g.drawLine(0, 0, len, 0);
        g.fillPolygon(new int[] {len, len-ARR_SIZE, len-ARR_SIZE, len},
                new int[] {0, -ARR_SIZE, ARR_SIZE, 0}, 4);
    }

    public static int getGraphicStringWidth( Graphics2D g, String inString ) {
        FontMetrics fontMetrics = g.getFontMetrics();
        return fontMetrics.stringWidth( inString );
    }

    public static int getGraphicFontHeight( Graphics2D g) {
        FontMetrics fontMetrics = g.getFontMetrics();
        return fontMetrics.getHeight();
    }
}
