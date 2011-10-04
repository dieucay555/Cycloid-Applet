import java.lang.Math;
import java.util.ArrayList;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.BufferedWriter;

import javax.swing.*;
import java.awt.*;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;

public class Cycloid extends JPanel {
    static final double PT_TO_MM = 2.8346;
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
    private boolean captionEnabled = false;
    private Metric metric;
    private PaperSize paperSize;
    private Format format;

    private final Points points;
    private final Points pointsFile;

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

    public void setCaptionEnabled(boolean enable) {
        captionEnabled = enable;
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
    double PlayfairX(double t) {
        // Estimate extent of curve
        double percent = (this.percent > 100.0 ? this.percent/100.0 : 1.0);
        double x = R*t-r*Math.cos(t*percent+Math.PI/2);
        x *= g_xs;
        return x;
    }

    double PlayfairX(double t, double percent) {
        // Estimate extent of curve
        double x = R*t-r*Math.cos(t*percent+Math.PI/2);
        x *= g_xs;
        return x;
    }

    double PlayfairY(double t) {
        // Estimate extent of curve
        double percent = (this.percent > 100.0 ? this.percent/100.0 : 1.0);
        double y = r*Math.sin(t*percent+Math.PI/2)+r;
        y *= g_ys;
        return y;
    }

    public double PlayfairY(double t, double percent) {
        double y = r*Math.sin(t*percent+Math.PI/2)+r;
        y *= g_ys;
        return y;
    }

    /**
     * Function FilePlayfair
     * Evalues the trachoid at a t value which is stored to a file
     * Returns both X/Y coordinates as this function is a bit expensive
     */
    void FilePlayfair(double t, Point point) throws Exception {
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
        double minXFile = PlayfairX(-1*Math.PI);
        double maxXFile = PlayfairX(Math.PI);

        if (g_xs*(maxXFile-minXFile) > (paperSize.getHeight()-30)/PT_TO_MM) {
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
            case PDF:
                writeToPDF(file, split==1);
                break;
            case PS:
                writeToPS(file, split==1);
                break;
        }
    }

    /**
     * Function: writes cycloid to CSV file
     */
    void writeToCSV(File file) {
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
    void writeToDXF(File file) {
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

    /**
     * Function: creates PDF graph
     * flag - 0 if whole curve, -1 for negative half, 1 for positive half
     */
    private void makePDFGraph(PDFWriter writer, int flag) throws IOException {
        double minX = PlayfairX(-1*Math.PI);
        double maxX = PlayfairX(Math.PI);

        // PrintTitle
        writer.writeLine("BT\n");
        writer.writeLine("/F1 12 Tf\n");
        writer.writeLine(String.format("%g %g Td (%s) Tj\n",
                                (float)(paperSize.getHeight()/2-5*title.length()/2),
                                (float)(paperSize.getWidth()-60.0),
                                title));
        writer.writeLine("ET\n");

        if (captionEnabled) {
            writer.writeLine("BT\n");
            writer.writeLine("/F1 12 Tf\n");
            if (metric == Metric.MM) {
                writer.writeLine(String.format("%g 60 Td (W=%4.2f,  h=%4.2f  scale=%4.3f %4.3f) Tj\n",
                                (float)(paperSize.getHeight()/2-5*40/2), cWidth, cHeight, g_xs, g_ys));
            } else {
                writer.writeLine(String.format("%g 60 Td (W=%4.2f,  h=%4.2f  scale=%4.3f %4.3f) Tj\n",
                                (float)(paperSize.getHeight()/2-5*40/2), cWidth/25.4, cHeight/25.4, g_xs, g_ys));
            }
            writer.writeLine("ET\n");
        }

        writer.writeLine(String.format("%5.4f 0 0 %5.4f 0 0 cm\n", PT_TO_MM, PT_TO_MM));
        double scale = 1.0f;
        writer.writeLine(String.format("%g 0 0 %g %g 50 cm\n", scale, scale, paperSize.getHeight()/2.0/PT_TO_MM));

        if (flag == -1) {
            writer.writeLine(String.format("1 0 0 1 %6.3f 20 cm\n", -1*minX/2));
            maxX = 0.0;
        } else if (flag == 1) {
            writer.writeLine(String.format("1 0 0 1 %6.3f 20 cm\n", minX/2));
            minX = 0.0;
        }

        // Plot the grid
        writer.writeLine("q\n");
        writer.writeLine(".35 w\n");
        writer.writeLine(".5 G\n");
        writer.writeLine(String.format("%6.3f 0 m %6.3f 0 l S\n", minX, maxX));
        writer.writeLine("Q\n");

        // Plot the curve
        writer.writeLine("q\n");
        writer.writeLine(".1 w\n");
        plotPDFCurve(writer, (int)(maxX-minX), flag);
        writer.writeLine("Q\n");
    }

    /**
     * Function: creates PS graph
     * flag - 0 if whole curve, -1 for negative half, 1 for positive half
     */
    private void makePSGraph(PrintWriter writer, Integer page) throws IOException {
        double minX = PlayfairX(-1*Math.PI);
        double maxX = PlayfairX(Math.PI);

        if (maxX-minX > (paperSize.getHeight()-30)/PT_TO_MM) {
            // makeSplitGraph
            makeSplitPSGraph(writer, minX, maxX, page);
            return;
        }

        page = new Integer(page+1);

        // PrintPageHeader
        PSWriter.writePageHeader(writer, page /*page*/,
                                         1/*scale*/,
                                         paperSize.getWidth(),
                                         paperSize.getHeight(),
                                         g_xs,
                                         g_ys,
                                         title);
        writer.printf("%g 20 translate\n", paperSize.getHeight()/2.0/PT_TO_MM);

        if (captionEnabled) {
            writer.printf("/Times-Roman findfont 5 scalefont setfont\n");
            if (cWidth > 0 && cHeight > 0) {
                if (metric == Metric.MM) {
                    writer.printf("0 -8 moveto (W=%4.2f,  h=%4.2f  scale=%4.3f %4.3f) dup stringwidth pop 2 div neg 0 rmoveto show \n",
                                    cWidth, cHeight, g_xs, g_ys);
                } else {
                    writer.printf("0 -8 moveto (W=%4.2f,  h=%4.2f  scale=%4.3f %4.3f) dup stringwidth pop 2 div neg 0 rmoveto show \n",
                                    cWidth/25.4, cHeight/25.4, g_xs, g_ys);
                }
            } else {
                if (metric == Metric.MM) {
                    writer.printf("0 -8 moveto (R=%4.2f,  r=%4.2f) dup stringwidth pop 2 div neg 0 rmoveto show \n", R, r);
                } else {
                    writer.printf("0 -8 moveto (R=%4.2f,  r=%4.2f) dup stringwidth pop 2 div neg 0 rmoveto show\n", R/25.4, r/25.4);
                }
            }
        }

        writer.printf("gsave\n");
        writer.printf(".35 setlinewidth\n");
        writer.printf(".5 setgray\n");
        writer.printf("%6.3f 0 moveto %6.3f 0 rlineto stroke\n", minX, maxX-minX);
        writer.printf("%6.3f %6.3f moveto %6.3f 0 rlineto stroke\n", minX, cHeight/2, maxX-minX);
        writer.printf("%6.3f %6.3f moveto %6.3f 0 rlineto stroke\n", minX, cHeight, maxX-minX);
        writer.printf("grestore\n");

        // Plot the curve
        writer.printf("gsave\n");
        writer.printf(".1 setlinewidth\n");
        plotPSCurve(writer, (int)(maxX-minX), 0);
        writer.printf("grestore\n");
    }

    private void makeSplitPSGraph(PrintWriter writer, double minX, double maxX, Integer page) throws IOException {
        page = new Integer(page+1);
        PSWriter.writePageHeader(writer, page, 1/*scale*/, paperSize.getWidth(), paperSize.getHeight(), g_xs, g_ys, title);
        writer.printf("%6.3f 20 translate\n", -1*minX+20);
        if (captionEnabled) {
            writer.printf("/Times-Roman findfont 5 scalefont setfont\n");
            if (cWidth > 0 && cHeight > 0) {
                if (metric == Metric.MM) {
                    writer.printf("0 -8 moveto (W=%4.2f,  h=%4.2f  scale=%4.3f %4.3f) dup stringwidth pop 2 div neg 0 rmoveto show \n",
                            cWidth, cHeight, g_xs, g_ys);
                } else {
                    writer.printf("0 -8 moveto (W=%4.2f,  h=%4.2f  scale=%4.3f %4.3f) dup stringwidth pop 2 div neg 0 rmoveto show \n",
                            cWidth/25.4, cHeight/25.4, g_xs, g_ys);
                }
            } else {
                if (metric == Metric.MM) {
                    writer.printf("0 -8 moveto (R=%4.2f,  r=%4.2f) dup stringwidth pop 2 div neg 0 rmoveto show \n", R, r);
                } else {
                    writer.printf("0 -8 moveto (R=%4.2f,  r=%4.2f) dup stringwidth pop 2 div neg 0 rmoveto show \n", R/25.4, r/25.4);
                }
            }
        }
        writer.printf("gsave\n");
        writer.printf(".1 setlinewidth\n");
        plotPSCurve(writer, (int)(maxX-minX)/2, -1);
        writer.printf("grestore\n");

        writer.printf("grestore\n");
        writer.printf("showpage\n");

        // Print the second page
        page = new Integer(page+1);
        PSWriter.writePageHeader(writer, page, 1/*scale*/, paperSize.getWidth(), paperSize.getHeight(), g_xs, g_ys, title);
        writer.printf("20 20 translate\n");

        if (captionEnabled) {
            writer.printf("/Times-Roman findfont 5 scalefont setfont\n");
            if (cWidth > 0 && cHeight > 0) {
                if (metric == Metric.MM) {
                    writer.printf("0 -8 moveto (W=%4.2f,  h=%4.2f  scale=%4.3f %4.3f) dup stringwidth pop 2 div neg 0 rmoveto show \n",
                            cWidth, cHeight, g_xs, g_ys);
                } else {
                    writer.printf("0 -8 moveto (W=%4.2f,  h=%4.2f  scale=%4.3f %4.3f) dup stringwidth pop 2 div neg 0 rmoveto show \n",
                            cWidth/25.4, cHeight/25.4, g_xs, g_ys);
                }
            } else {
                writer.printf("0 -8 moveto (R=%4.2f,  r=%4.2f) dup stringwidth pop 2 div neg 0 rmoveto show \n", R, r);
            }
        }
        writer.printf("gsave\n");
        writer.printf(".35 setlinewidth\n");
        writer.printf(".5 setgray\n");
        writer.printf("%6.3f 0 moveto %6.3f 0 rlineto stroke\n", minX, maxX-minX);
        writer.printf("%6.3f 10 moveto %6.3f 0 rlineto stroke\n", minX, maxX-minX);
        writer.printf("%6.3f 20 moveto %6.3f 0 rlineto stroke\n", minX, maxX-minX);
        writer.printf("grestore\n");

        // plot the curve
        writer.printf("gsave\n");
        writer.printf(".1 setlinewidth\n");
        plotPSCurve(writer, (int)(maxX-minX)/2, 1);
        writer.printf("grestore\n");

        writer.printf("grestore\n");
        writer.printf("showpage\n");
    }

    void plotPDFCurve(PDFWriter writer, double res, int flag) throws IOException {
        if (res < 20) {
            res = 50;
        }

        Point point = new Point();
        if (flag <= 0) {
            point.X = PlayfairX(-1*Math.PI);
            point.Y = PlayfairY(-1*Math.PI);
        } else {
            point.X = PlayfairX(0.0);
            point.Y = PlayfairY(0.0);
        }

        writer.writeLine(String.format("%6.3f %6.3f m\n", point.X, point.Y));
        for (int i=1; i<=res; i++) {
            if (flag == -1) {
                point.X = PlayfairX(Math.PI*i/res-Math.PI);
                point.Y = PlayfairY(Math.PI*i/res-Math.PI);
            } else if (flag == 0) {
                point.X = PlayfairX(2*Math.PI*i/res-Math.PI);
                point.Y = PlayfairY(2*Math.PI*i/res-Math.PI);
            } else {
                point.X = PlayfairX(Math.PI*i/res);
                point.Y = PlayfairY(Math.PI*i/res);
            }
            writer.writeLine(String.format("%6.3f %6.3f l\n", point.X, point.Y));
            if (i%10 == 0) {
                writer.writeLine("S\n");
                writer.writeLine(String.format("%6.3f %6.3f m\n", point.X, point.Y));
            }
        }
        writer.writeLine("S\n");
        // flush
        point.X = PlayfairX(0.0);
        point.Y = 0;
        writer.writeLine(String.format("%6.3f %6.3f m\n", point.X, point.Y));
        point.Y = 2*r+10;
        writer.writeLine(String.format("%6.3f %6.3f l\n", point.X, point.Y));

        if (flag <= 0) {
            point.X = PlayfairX(-1*Math.PI, 1.0f);
            point.Y = 0;
            writer.writeLine(String.format("%6.3f %6.3f m\n", point.X, point.Y));
            point.Y = 2*r+10;
            writer.writeLine(String.format("%6.3f %6.3f l\n", point.X, point.Y));
        }

        if (flag >= 0) {
            point.X = PlayfairX(Math.PI, 1.0f);
            point.Y = 0;
            writer.writeLine(String.format("%6.3f %6.3f m\n", point.X, point.Y));
            point.Y = 2*r+10;
            writer.writeLine(String.format("%6.3f %6.3f l\n", point.X, point.Y));
        }

        writer.writeLine("S\n");
    }

    private void plotPSCurve(PrintWriter writer, double res, int flag) throws IOException {
        if (res < 20) {
            res = 50;
        }

        Point point = new Point();
        if (flag <= 0) {
            point.X = PlayfairX(-1*Math.PI);
            point.Y = PlayfairY(-1*Math.PI);
        } else {
            point.X = PlayfairX(0.0);
            point.Y = PlayfairY(0.0);
        }

        writer.printf("%6.3f %6.3f moveto\n", point.X, point.Y);
        for (int i=1; i<=res; i++) {
            if (flag == -1) {
                point.X = PlayfairX(Math.PI*i/res-Math.PI);
                point.Y = PlayfairY(Math.PI*i/res-Math.PI);
            } else if (flag == 0) {
                point.X = PlayfairX(2*Math.PI*i/res-Math.PI);
                point.Y = PlayfairY(2*Math.PI*i/res-Math.PI);
            } else {
                point.X = PlayfairX(Math.PI*i/res);
                point.Y = PlayfairY(Math.PI*i/res);
            }
            writer.printf("%6.3f %6.3f lineto\n", point.X, point.Y);
            if (i%10 == 0) {
                writer.printf("stroke\n");
                writer.printf("%6.3f %6.3f moveto\n", point.X, point.Y);
            }
        }
        writer.printf("stroke\n");
        writer.flush();
        point.X = PlayfairX(0.0);
        point.Y = 0;
        writer.printf("%6.3f %6.3f moveto\n", point.X, point.Y);
        point.Y = 2*r+10;
        writer.printf("%6.3f %6.3f lineto\n", point.X, point.Y);

        if (flag <= 0) {
            point.X = PlayfairX(-1*Math.PI, 1.0f);
            point.Y = 0;
            writer.printf("%6.3f %6.3f moveto\n", point.X, point.Y);
            point.Y = 2*r+10;
            writer.printf("%6.3f %6.3f lineto\n", point.X, point.Y);
        }

        if (flag >= 0) {
            point.X = PlayfairX(Math.PI, 1.0f);
            point.Y = 0;
            writer.printf("%6.3f %6.3f moveto\n", point.X, point.Y);
            point.Y = 2*r+10;
            writer.printf("%6.3f %6.3f lineto\n", point.X, point.Y);
        }

        writer.printf("stroke\n");
    }

    /**
     * Function: writes cycloid to PDF file
     */
    void writeToPDF(File file, boolean split) {
        try {
            PDFWriter writer = new PDFWriter(file);
            writer.openPDF();
            writer.setPageSize(paperSize.getHeight(), paperSize.getWidth());

            if (!split) {
                writer.simpleHeaders();
                writer.beginStreamObj(7);
                makePDFGraph(writer, 0);
                writer.endStreamObj();
            } else {
                int [] pa = new int[2];
                writer.multiPageHeaders(2, pa);
                writer.beginStreamObj(pa[0]);
                makePDFGraph(writer, -1);
                writer.endStreamObj();

                writer.beginStreamObj(pa[1]);
                makePDFGraph(writer, 1);
                writer.endStreamObj();
            }

            writer.writeXrefs();
            writer.closePDF();
        } catch (Exception e) {
            // print error
        }
    }

    /**
     * Function: writes cycloid to PS file
     */
    void writeToPS(File file, boolean split) {
        try {
            Integer page = new Integer(0);
            FileWriter fw = new FileWriter(file);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter writer = new PrintWriter(bw);
            PSWriter.writeHeader(writer);
            makePSGraph(writer, page);
            // TODO: Not sure of top or eps
            writer.printf("%%%%EOF\n");
            //PSWriter.writeTrailer(0);
            writer.close();
        } catch (Exception e) {
            // print erro msg and stop
        }
    }
}
