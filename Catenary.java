import java.lang.Math;
import java.util.ArrayList;

import javax.swing.*;
import java.awt.*;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;

public class Catenary extends JPanel {
    // contains user specified parameters from Applet GUI as its member vars
    private String title;
    private double cLength;
    private double cDepth;
    private double percent;
    private double g_xs; // scale width
    private double g_ys; // scale height
    private double minX;
    private double maxX;
    private double minY;
    private double maxY;
    private double sc;

    private Points points;

    public Catenary() {
        points = new Points();
        cLength = 200.00;
        cDepth = 20.0;
        percent = 100.0;
        g_xs = 1.0;
        g_ys = 1.0;
        minX = 0.0;
        maxX = 0.0;
        minY = 0.0;
        maxY = 0.0;
        sc = 0.0;
        title = new String("");
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setCatenaryLength(double length) {
        this.cLength = length;
    }

    public void setCatenaryDepth(double depth) {
        this.cDepth = depth;
    }

    public void setPercent(double percent) {
        this.percent = percent;
    }

    public void setScaleWidth(double g_xs) {
        this.g_xs = g_xs;
    }

    public void setScaleHeight(double g_ys) {
        this.g_ys = g_ys;
    }

    /**
     * Function Catenary
     * Evaluates the catenary at a t value
     */
    public double CatenaryX(double t) {
        double x = t;
        x *= g_xs;
        return x;
    }

    public double CatenaryY(double t) {
        double y = cLength*Math.cosh(t/cLength)-cLength;
        y *= g_ys;
        return y;
    }

    /**
     * Function: draw
     * Receives Graphics object from JComponent to draw itself
     */
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        double width = getWidth();
        double height = getHeight();
        double tmpPercent = (this.percent > 100.0 ? this.percent/100.0 : 1.0); // used for Catenary calculation
        minX = CatenaryX(-1*cDepth*tmpPercent);
        maxX = CatenaryX(cDepth*tmpPercent);
        minY = 0;
        maxY = CatenaryY(0.0);

        int res = (int)(width/2);

        if (points.length() < res) {
            points.resize(res+1);
        }

        // clears previous contents
        points.clear();

        for (int i=0; i<=res; i++) {
            double t = 2*cDepth*i/res-cDepth;
            t *= tmpPercent; // not sure but need to double check
            double x = CatenaryX(t);
            double y = CatenaryY(t);
            if (x < minX) minX = x;
            if (x > maxX) maxX = x;
            if (y < minY) minY = y;
            if (y > maxY) maxY = y;
            points.getX().add(new Double(x));
            points.getY().add(new Double(y));
        }

        double sx = (width-10)/(maxX-minX);
        double sy = (height-10)/(maxY-minY);
        if (sx < sy) {
            sc = sx;
        } else {
            sc = sy;
            // ignore offset X/Y for now
        }
        for (int i=0; i<=res; i++) {
            double x = points.getX().get(i);
            double y = points.getY().get(i);
            points.getX().set(i, new Double(sc*(x-minX)+5));
            points.getY().set(i, new Double(height-(sc*(y-minY))-5));
        }

        // Use Line2D since Graphics drawLine doesn't take doubles
        Graphics2D g2 = (Graphics2D) g;
        for (int i=1; i<=res; i++) {
            Line2D.Double line = new Line2D.Double(points.getX().get(i-1),
                                                   points.getY().get(i-1),
                                                   points.getX().get(i),
                                                   points.getY().get(i));
            g2.draw(line);
        }
    }
}
