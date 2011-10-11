import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.Math;
import java.util.ArrayList;

import javax.swing.*;
import java.awt.*;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;

class Catenary extends JPanel {
    static final double PT_TO_MM = 2.8346;
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
    private boolean captionEnabled = false;
    private Metric metric;
    private PaperSize paperSize;
    private Format format;

    private Points points;
    private Points pointsFile;

    public Catenary() {
        points = new Points();
        pointsFile = new Points();
        cLength = 200.00;
        cDepth = 20.0;
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
        title = "";
    }

    public String getTitle() {
        return new String(title);
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getCatenaryLength() {
        return cLength;
    }

    public void setCatenaryLength(double length) {
        this.cLength = length;
    }

    public double getCatenaryDepth() {
        return cDepth;
    }

    public void setCatenaryDepth(double depth) {
        this.cDepth = depth;
    }

    public double getPercent() {
        return percent;
    }

    public void setPercent(double percent) {
        this.percent = percent;
    }

    public double getScaleWidth() {
        return g_xs;
    }

    public void setScaleWidth(double g_xs) {
        this.g_xs = g_xs;
    }

    public double getScaleHeight() {
        return g_ys;
    }

    public void setScaleHeight(double g_ys) {
        this.g_ys = g_ys;
    }

    public boolean isCaptionEnabled() {
        return captionEnabled;
    }

    public void setCaptionEnabled(boolean enabled) {
        captionEnabled = enabled;
    }

    public Metric getMetric() {
        return metric;
    }

    public void setMetric(Metric metric) {
        this.metric = metric;
    }

    public PaperSize getPaper() {
        return paperSize;
    }

    public void setPaper(PaperSize paperSize) {
        this.paperSize = paperSize;
    }

    public Format getFormat() {
        return format;
    }

    public void setFormat(Format format) {
        this.format = format;
    }

    /**
     * Function Catenary
     * Evaluates the catenary at a t value
     * @return x coordinate for catenary at t
     */
    double CatenaryX(double t) {
        double x = t;
        x *= g_xs;
        return x;
    }

    double CatenaryX(double a, double t) {
        // a is not used for x coord
        return CatenaryX(t);
    }

    double CatenaryY(double t) {
        double y = cLength*Math.cosh(t/cLength)-cLength;
        y *= g_ys;
        return y;
    }

    double CatenaryY(double a, double t) {
        double y = a*Math.cosh(t/a)-a;
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
            points.addPoint(new Point(x,y));
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
            double x = points.getX(i);
            double y = points.getY(i);
            x = sc*(x-minX)+5;
            y = height-(sc*(y-minY))-5;
            points.setPoint(i, new Point(x, y));
        }

        // Use Line2D since Graphics drawLine doesn't take doubles
        Graphics2D g2 = (Graphics2D) g;
        for (int i=1; i<=res; i++) {
            Line2D.Double line = new Line2D.Double(points.getX(i-1),
                                                   points.getY(i-1),
                                                   points.getX(i),
                                                   points.getY(i));
            g2.draw(line);
        }
    }

    /**
     * Given the width and depth of a catenary, compute the 'a' value.
     *
     * <p>In the following diagram of a catenary, AB is the width, BC is the
     * depth, and 'a' is the value of 'a' in the catenary equation
     * a*cosh(t/a)-a
     * A____B
     *  \   |   /
     *   \  |  /
     *    \_C_/
     * @param w width
     * @param d depth
     */
    private double computeA(double w, double d) {
        double l;
        double al, au;

        l = 1.0 * Math.cosh(w/1.0)-1.0;
        if (l == d) {
            return 1.0;
        } else if (l > d) {
            // need to increase l
            au = al = 1.0;
            while (l > d) {
                al = au;
                au *= 2.0;
                l = au*Math.cosh(w/au)-au;
            }
        } else {
            // need to decrease l
            au = al = 1.0;
            while (l < d) {
                au = al;
                al /= 2.;
                l = al*Math.cosh(w/al)-al;
            }
        }

        // now do a binary search
        while (Math.abs(l-d)>1e-12) {
            double m;
            m = (au+al)/2.;
            l = m*Math.cosh(w/m)-m;
            if (l > d) {
                al = m;
            } else {
                au = m;
            }
        }
        return au;
    }

    private boolean isSplit(double a, double w) {
        double minxX, maxX;
        double percent = (this.percent > 100.0 ? this.percent/100.0 : 1.0);

        // determines x extent of curve
        minX = CatenaryX(-1*w*percent);
        maxX = CatenaryX(w*percent);

        if (g_xs * (maxX-minX) > (paperSize.getHeight() - 30)/PT_TO_MM) {
            return true;
        }
        return false;
    }

    public void writeToFile(File file) {
        // need to adjust width/height for inch
        boolean ldAdjusted = false;
        double prevCLength = cLength;
        double prevCDepth = cDepth;
        if (metric != Metric.MM && format != Format.DXF) {
            ldAdjusted = true;
            setCatenaryLength(cLength*25.4);
            setCatenaryDepth(cDepth*25.4);
        }
        double a = computeA(cLength/2., cDepth);
        double length = cLength/2.;
        boolean split = false;

        if (isSplit(a, length)) {
            split = true;
            // error check for EPS
        }

        // Need to scale w/d for inch
        try {
            switch(format) {
                case CSV:
                    // disabled!!!
                    break;
                case DXF:
                    writeToDXF(file, a, length);
                    break;
                case PDF:
                    writeToPDF(file, a, length, split);
                    break;
                case PS:
                    writeToPS(file, a, length, split);
                    break;
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        } finally {
            if (ldAdjusted) {
                setCatenaryLength(prevCLength);
                setCatenaryDepth(prevCDepth);
            }
        }
    }

    /**
     * Writes catenary to DXF file
     *
     * @param file filename
     * @param a 'a' for catenary curve
     * @param length half of cLength
     */
    void writeToDXF(File file, double a, double length) throws IOException {
        double percent = (this.percent > 100.0 ? this.percent/100.0 : 1.0);
        double minX = CatenaryX(-1*length*percent);
        double maxX = CatenaryX(length*percent);

        int res = (int)(maxX-minX)/2;
        if (res < 20) {
            res = 50;
        }

        // clears previous contents
        pointsFile.clear();

        for (int i=0; i<=res; ++i) {
            double x = CatenaryX(2*length*percent*i/res-length*percent);
            double y = CatenaryY(2*length*percent*i/res-length*percent);
            pointsFile.addPoint(new Point(x,y));
        }

        DXFWriter writer = new DXFWriter(file);
        try {
            writer.writeHeader();
            writer.write2DPolyLine(pointsFile);
            writer.writeTrailer();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        } finally {
            if (writer != null) {
                writer.closeFile(); // does not throw exception
            }
        }
    }

    /**
     * Writes catenary to PDF file
     *
     * @param file filename
     * @param a 'a' for catenary
     * @param length half of cLength
     * @param split true if split into 2 pages, false otherwise
     */
    void writeToPDF(File file, double a, double length, boolean split) throws FileNotFoundException {
        PDFWriter writer = new PDFWriter(file);
        try {
            writer.openPDF();
            writer.setPageSize(paperSize.getHeight(), paperSize.getWidth());

            if (!split) {
                writer.simpleHeaders();
                writer.beginStreamObj(7);
                writer.makeCatenaryGraph(a, length, 0, this);
                writer.endStreamObj();
            } else {
                writer.multiPageHeaders(2);

                writer.beginStreamObj(8/*pc+6*/);
                writer.makeCatenaryGraph(a, length, -1, this);
                writer.endStreamObj();

                writer.beginStreamObj(9/*pc+6+1*/);
                writer.makeCatenaryGraph(a, length, 1, this);
                writer.endStreamObj();
            }
            writer.writeXrefs();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        } finally {
            if (writer != null) {
                writer.closePDF(); // will not throw exception here
            }
        }
    }

    /**
     * Writes catenary to PS file
     *
     * @param file filename
     * @param a 'a' for catenary
     * @param length half of cLength
     * @param split true if split into 2 pages, false otherwise
     */
    void writeToPS(File file, double a, double length, boolean split) throws IOException {
        PSWriter writer = new PSWriter(file, split);
        try {
            writer.writeHeader();
            writer.makeCatenaryGraph(a, length, this);
            writer.writeTrailer();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        } finally {
            if (writer != null) {
                writer.closeFile(); // will not throw exception here
            }
        }
    }
}
