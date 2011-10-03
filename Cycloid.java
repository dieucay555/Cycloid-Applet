import java.lang.Math;
import java.util.ArrayList;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.BufferedWriter;

import javax.swing.*;
import java.awt.*;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;

public class Cycloid extends JPanel {
    private static final double PT_TO_MM = 2.8346;
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
    private Metric metric;
    private PaperSize paperSize;
    private Format format;

    private Points points;
    private Points pointsFile;

    public Cycloid() {
        points = new Points();
        pointsFile = new Points();
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
        metric = Metric.MM;
        paperSize = PaperSize.LETTER;
        format = Format.PDF;
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

    public void setMetric(Metric metric) {
        this.metric = metric;
    }

    public void setPaper(PaperSize paperSize) {
        this.paperSize = paperSize;
    }

    public void setFormat(Format format) {
        this.format = format;
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
     * Function FilePlayfair
     * Evalues the trachoid at a t value which is stored to a file
     * Returns both X/Y coordinates as this function is a bit expensive
     */
    public void FilePlayfair(double t, Point point) throws Exception {
        double t0 = 0.0; double v0 = 0.0;
        double t1 = 0.0; double v1 = 0.0;
        double t2 = 0.0; double v2 = 0.0;
        double tt = 0.0; double v = 0.0;
        int count=0;

        t0 = R*(t-0.5);     v0 = t0-r*Math.cos(t0/R+Math.PI/2);
        t1 = R*t;           v1 = t1-r*Math.cos(t1/R+Math.PI/2);
        t2 = R*(t+0.5);     v2 = t2-r*Math.cos(t2/R+Math.PI/2);
        if (v0 < t*R && t*R < v1) {
            t2 = t1;    v2 = v1;
            t1 = t0;    v1 = v0;
        } else if (v1 < t*R && t*R < v2) {
            // do nothing
        } else {
            throw new Exception("numerical error");
        }

        while (Math.abs(v1-t*R) > 1e-6 && Math.abs(v2-t*R) > 1e-6) {
            tt = (t*R-v1)/(v2-v1)*t2 + (v2-t*R)/(v2-v1)*t1;
            v = tt-r*Math.cos(tt/R+Math.PI/2);
            if (v > t*R) {
                t2 = tt;    v2 = v;
            } else {
                t1 = tt;    v1 = v;
            }
            count++;
            if (count > 1000) {
                throw new Exception("convergence too slow");
            }
        }
        if (Math.abs(v1-t*R) > 1e-6) {
            tt = v2;
        } else {
            tt = v1;
        }

        point.X = tt-r*Math.cos(tt/R+Math.PI/2);
        point.Y = r*Math.sin(tt/R+Math.PI/2);
        point.X *= g_xs;
        point.Y *= g_ys;
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
            points.getPoints().add(new Point(x,y));
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
            double x = points.getPoints().get(i).X;
            double y = points.getPoints().get(i).Y;
            x = sc*(x-minX)+5;
            y = height-(sc*(y-minY))-5;
            points.getPoints().set(i, new Point(x,y));
        }

        // Use Line2D since Graphics drawLine doesn't take doubles
        Graphics2D g2 = (Graphics2D) g;
        for (int i=1; i<=res; i++) {
            Line2D.Double line = new Line2D.Double(points.getPoints().get(i-1).X,
                                                   points.getPoints().get(i-1).Y,
                                                   points.getPoints().get(i).X,
                                                   points.getPoints().get(i).Y);
            g2.draw(line);
        }
    }

    /**
     * Function: writing cycloid to a user specified file
     * Uses custom written CSV, DXF, PS, PDF utility to write file
     */
    public void writeToFile(File file) {
        int split = 0;
        double width = getWidth();
        double height = getHeight();
        double minXFile = PlayfairX(-1*Math.PI);
        double maxXFile = PlayfairX(Math.PI);

        if (g_xs*(maxX-minX) > (paperSize.getHeight()-30)/PT_TO_MM) {
            split = 1;
            // eps check
        }

        switch(format) {
            case CSV:
                writeToCSV(file);
                break;
            case DXF:
                writeToDXF(file);
                break;
        }
    }

    /**
     * Function: writes cycloid to CSV file
     */
    public void writeToCSV(File file) {
        int minX = (int)(-1*(getWidth()/2*percent));
        int maxX = -1*minX;
        int res = (maxX-minX);

        if (pointsFile.length() < res) {
            pointsFile.resize(res+1);
        }

        // clears previous contents
        pointsFile.clear();

        for(int i=0; i<=res; i++) {
            try {
                Point point = new Point();
                FilePlayfair(2*Math.PI*i/res-Math.PI, point);
                pointsFile.getPoints().add(new Point(point.X, point.Y));
            } catch (Exception e) {
                // print error msg and stop
                e.printStackTrace();
            }
        }

        try {
            FileWriter fw = new FileWriter(file);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter writer = new PrintWriter(bw);
            CSVWriter.writeHeader(writer);
            CSVWriter.write2DPolyLine(writer, res+1, pointsFile);
            CSVWriter.writeTrailer(writer);
            writer.close();
        } catch (Exception e) {
            // print erro msg and stop
        }
    }

    /**
     * Function: writes cycloid to DXF file
     */
    public void writeToDXF(File file) {
        int minX = (int)PlayfairX(-1*Math.PI);
        int maxX = (int)PlayfairX(Math.PI);
        int res = (int)(maxX-minX)/2;
        if (res < 20) {
            res = 50;
        }

        // clears previous contents
        pointsFile.clear();

        for (int i=0; i<=res; i++) {
            double x = PlayfairX(2*Math.PI*i/res-Math.PI);
            double y = PlayfairY(2*Math.PI*i/res-Math.PI);
            pointsFile.getPoints().add(new Point(x,y));
        }

        try {
            FileWriter fw = new FileWriter(file);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter writer = new PrintWriter(bw);
            DXFWriter.writeHeader(writer);
            DXFWriter.write2DPolyLine(writer, res+1, pointsFile);
            DXFWriter.writeTrailer(writer);
            writer.close();
        } catch (Exception e) {
            // print error msg and stop
        }
    }
}
