import java.lang.Math;
import java.util.ArrayList;

import javax.swing.*;
import java.awt.*;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;

public class Cycloid extends JPanel {
    // contains user specified parameters from Applet GUI as its member vars
    private String title;
    private double cWidth; // cycloid width
    private double cHeight; // cycloid height
    private double R;
    private double r;
    private double percent;
    private double g_xs; // scale width
    private double g_ys; // scale height
    private double minX;
    private double maxX;
    private double minY;
    private double maxY;
    private double sc;

    private Points points;

    // class for storing points to draw cycloid
    class Points {
        private ArrayList<Double> x;
        private ArrayList<Double> y;

        public Points() {
            // default size to 1024 points to minimize resizing the list
            x = new ArrayList<Double>(1024);
            y = new ArrayList<Double>(1024);
        }

        // assumes that no multiple threads will access this obj
        public ArrayList<Double> getX() {
            return x;
        }

        public ArrayList<Double> getY() {
            return y;
        }

        // attempt to minimize resizing of the list
        public void resize(int size) {
            x.ensureCapacity(size*2);
            y.ensureCapacity(size*2);
        }

        public int length() {
            return x.size();
        }

        public void clear() {
            x.clear();
            y.clear();
        }
    }

    public Cycloid() {
        points = new Points();
        cWidth = 200.00;
        cHeight = 20.0;
        R = 0.0;
        r = 0.0;
        percent = 110.0;
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

    public void setCycloidWidth(double width) {
        this.cWidth = width;
        this.R = width/2/Math.PI;
    }

    public void setCycloidHeight(double height) {
        this.cHeight = height;
        this.r = height/2;
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
     * Function PlayfairX/Y
     * Evalues the trachoid at a t value
     */
    public double PlayfairX(double t) {
        // Estimate extent of curve
        double percent = (this.percent > 100.0 ? this.percent/100.0 : 1.0);
        double x = R*t-r*Math.cos(t*percent+Math.PI/2);
        x *= g_xs;
        return x;
    }

    public double PlayfairY(double t) {
        // Estimate extent of curve
        double percent = (this.percent > 100.0 ? this.percent/100.0 : 1.0);
        double y = r*Math.sin(t*percent+Math.PI/2)+r;
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
        minX = PlayfairX(-1*Math.PI);
        maxX = PlayfairX(Math.PI);
        minY = 0;
        maxY = PlayfairY(0.0);

        int res = (int)(width/2);

        if (points.length() < res) {
            points.resize(res+1);
        }

        // clears previous contents
        points.clear();

        for (int i=0; i<=res; i++) {
            double x = PlayfairX(2*Math.PI*i/res-Math.PI);
            double y = PlayfairY(2*Math.PI*i/res-Math.PI);
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
