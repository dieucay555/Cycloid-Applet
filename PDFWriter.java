import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.Math;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

/**
 * A PDF file writer for Cycloid applet
 */
class PDFWriter {
    private static final int MAX_XREFS = 2000;
    private final RandomAccessFile raf;

    /**
     * Internal struct used for constructing PDF
     */
    class PDFFile {
        public int objs;
        public final long [] xrefs;
        public long xref;
        public int width, height;
        public long streamlenpos1;
        public long streamlenpos2;
        public long streamlenpos3;

        public PDFFile() {
            objs = 0;
            xrefs = new long[MAX_XREFS];
            xref = 0;
            width = 0;
            height = 0;
            streamlenpos1 = 0;
            streamlenpos2 = 0;
            streamlenpos3 = 0;
        }
    }

    private final PDFFile pdfInfo;

    /**
     * Handles creating PDF file from ccloid/catenary user specifid.
     *
     * @param file filename the user specified
     * @throws FileNotFoundException if the file exists but is a directory, or cannot be opened or created
     * for any other reason
     */
    public PDFWriter(File file) throws FileNotFoundException {
        raf = new RandomAccessFile(file, "rwd");
        pdfInfo = new PDFFile();
    }

    /**
     * Opens initializes PDFInfo struct and begins writing file.
     *
     * @throws IOException if an I/O error occurs
     */
    public void openPDF() throws IOException {
        pdfInfo.objs = 1;
        for (int i=0; i<MAX_XREFS; i++) {
            pdfInfo.xrefs[i] = 0;
        }
        raf.writeBytes("%%PDF-1.0\n");
        if (pdfInfo.width == 0) {
            pdfInfo.width = 612;
        }
        if (pdfInfo.height == 0) {
            pdfInfo.height = 792;
        }
    }

    /**
     * Begins writing a PDF object.
     *
     * @param obj object number
     * @throws IOException if an I/O error occurs
     */
    private void beginObj(int obj) throws IOException {
        if (obj >= MAX_XREFS) {
            // bails out in case obj is too large
            JOptionPane.showMessageDialog(null,
                "PDFBeginObj: object " + obj + " too large.",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (pdfInfo.xrefs[obj] != 0 || obj == 0) {
            // bails out in case obj is already defined
            JOptionPane.showMessageDialog(null,
                "PDFBeginObj: object " + obj + " already defined!",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        pdfInfo.objs++;
        pdfInfo.xrefs[obj] = raf.getFilePointer();
        raf.writeBytes(String.format("%d 0 obj\n", obj));
    }

    /**
     * Ends a PDF object.
     *
     * @throws IOException if an I/O error occurs
     */
    private void endObj() throws IOException {
        raf.writeBytes("endobj\n\n");
    }

    /**
     * Begins a PDF stream object.
     *
     * @param obj stream object
     * @throws IOException if an I/O error occurs
     */
    public void beginStreamObj(int obj) throws IOException {
        if (obj >= MAX_XREFS) {
            JOptionPane.showMessageDialog(null,
                "PDFBeginStreamObj: object " + obj + " too large.",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (pdfInfo.xrefs[obj] != 0 || obj == 0) {
            JOptionPane.showMessageDialog(null,
                "PDFBeginStreamObj: object " + obj + " already defined!",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        pdfInfo.objs++;
        pdfInfo.xrefs[obj] = raf.getFilePointer();
        raf.writeBytes(String.format("%d 0 obj\n", obj));
        raf.writeBytes("<< /Length ");
        pdfInfo.streamlenpos1 = raf.getFilePointer();
        raf.writeBytes("00000 >>\n");
        raf.writeBytes("stream\n");
        pdfInfo.streamlenpos2 = raf.getFilePointer();
    }

    /**
     * Ends PDF stream object.
     *
     * @throws IOException if an I/O error occurs
     */
    public void endStreamObj() throws IOException {
        pdfInfo.streamlenpos3 = raf.getFilePointer();
        raf.seek(pdfInfo.streamlenpos1);
        raf.writeBytes(String.format("%05d", pdfInfo.streamlenpos3-pdfInfo.streamlenpos2));
        raf.seek(raf.length()); // attempting fseek(0, SEEK_END)
        raf.writeBytes("endstream\n");
        raf.writeBytes("endobj\n\n");
    }

    /**
     * Writes XREFS for PDF.
     *
     * @throws IOException if an I/O error occurs
     */
    public void writeXrefs() throws IOException {
        pdfInfo.xref = raf.getFilePointer();
        raf.writeBytes("xref\n");
        raf.writeBytes(String.format("0 %d\n", pdfInfo.objs));

        raf.writeBytes("0000000000 65535 f\n");
        for (int i=1; i<MAX_XREFS; i++) {
            if (pdfInfo.xrefs[i] != 0) {
                raf.writeBytes(String.format("%010d 00000 n\n", pdfInfo.xrefs[i]));
            }
        }
        raf.writeBytes("trailer\n");
        raf.writeBytes("<<\n");
        raf.writeBytes(String.format("/Size %d\n", pdfInfo.objs));
        raf.writeBytes("/Root 1 0 R\n");
        raf.writeBytes(">>\n");
        raf.writeBytes("startxref\n");
        raf.writeBytes(String.format("%d\n", pdfInfo.xref));
    }

    /**
     * Closes PDF file.
     */
    public void closePDF() {
        try {
            raf.writeBytes("%%%%EOF\n");
            raf.close();
            pdfInfo.objs = 0;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,
                e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Sets PDF page size.
     *
     * @param width width
     * @param height height
     */
    public void setPageSize(int width, int height) {
        pdfInfo.width = width;
        pdfInfo.height = height;
    }

    /**
     * Writes simple PDF headers.
     *
     * @throws IOException if an I/O error occurs
     */
    public void simpleHeaders() throws IOException {
        beginObj(1);
        raf.writeBytes("<<\n");
        raf.writeBytes("/Type /Catalog\n");
        raf.writeBytes("/Pages 3 0 R\n");
        raf.writeBytes("/Outlines 2 0 R\n");
        raf.writeBytes(">>\n");
        endObj();

        beginObj(2);
        raf.writeBytes("<<\n");
        raf.writeBytes("/Type /Outlines\n");
        raf.writeBytes("/Count 0\n");
        raf.writeBytes(">>\n");
        endObj();

        beginObj(3);
        raf.writeBytes("<<\n");
        raf.writeBytes("/Type /Pages\n");
        raf.writeBytes("/Count 1\n");
        raf.writeBytes("/Kids [ 4 0 R ]\n");
        raf.writeBytes(">>\n");
        endObj();

        beginObj(4);
        raf.writeBytes("<<\n");
        raf.writeBytes("/Type /Page\n");
        raf.writeBytes("/Parent 3 0 R\n");
        raf.writeBytes("/Resources << /Font << /F1 5 0 R >> /ProcSet 6 0 R >>\n");
        raf.writeBytes(String.format("/MediaBox [ 0 0 %d %d ]\n", pdfInfo.width, pdfInfo.height));
        raf.writeBytes("/Contents 7 0 R\n");
        raf.writeBytes(">>\n");
        endObj();

        beginObj(5);
        raf.writeBytes("<<\n");
        raf.writeBytes("/Type /Font\n");
        raf.writeBytes("/Subtype /Type1\n");
        raf.writeBytes("/Name /F1\n");
        raf.writeBytes("/BaseFont /Helvetica\n");
        raf.writeBytes("/Encoding /MacRomanEncoding\n");
        raf.writeBytes(">> \n");
        endObj();

        beginObj(6);
        raf.writeBytes("[\n");
        raf.writeBytes("/PDF /Text\n");
        raf.writeBytes("] \n");
        endObj();
    }

    /**
     * Writes multiple PDF page headers.
     *
     * @param pc page count
     * @throws IOException if an I/O error occurs
     */
    public void multiPageHeaders(int pc/*, int on[] can be easily calculated*/) throws IOException {
        beginObj(1);
        raf.writeBytes("<<\n");
        raf.writeBytes("/Type /Catalog\n");
        raf.writeBytes("/Pages 3 0 R\n");
        raf.writeBytes("/Outlines 2 0 R\n");
        raf.writeBytes(">>\n");
        endObj();

        beginObj(2);
        raf.writeBytes("<<\n");
        raf.writeBytes("/Type /Outlines\n");
        raf.writeBytes("/Count 0\n");
        raf.writeBytes(">>\n");
        endObj();

        beginObj(3);
        raf.writeBytes("<<\n");
        raf.writeBytes("/Type /Pages\n");
        raf.writeBytes(String.format("/Count %d\n", pc));
        raf.writeBytes("/Kids [ ");
        for (int i=0; i<pc; i++) {
            raf.writeBytes(String.format("        %d 0 R\n", i+6));
        }
        raf.writeBytes("      ]\n");
        raf.writeBytes(">>\n");
        endObj();

        beginObj(4);
        raf.writeBytes("<<\n");
        raf.writeBytes("/Type /Font\n");
        raf.writeBytes("/Subtype /Type1\n");
        raf.writeBytes("/Name /F1\n");
        raf.writeBytes("/BaseFont /Helvetica\n");
        raf.writeBytes("/Encoding /MacRomanEncoding\n");
        raf.writeBytes(">> \n");
        endObj();

        beginObj(5);
        raf.writeBytes("[\n");
        raf.writeBytes("/PDF /Text\n");
        raf.writeBytes("] \n");
        endObj();

        for (int i=0; i<pc; i++) {
            beginObj(i+6);
            raf.writeBytes("<<\n");
            raf.writeBytes("/Type /Page\n");
            raf.writeBytes("/Parent 3 0 R\n");
            raf.writeBytes("/Resources << /Font << /F1 4 0 R >> /ProcSet 5 0 R >>\n");
            raf.writeBytes(String.format("/MediaBox [ 0 0 %d %d ]\n", pdfInfo.width, pdfInfo.height));
            raf.writeBytes(String.format("/Contents %d 0 R\n", i+pc+6));
            //on[i] = 6+pc+i;
            raf.writeBytes(">>\n");
            endObj();
        }
    }

    /**
     * Draws a polyline from points array.
     *
     * @param points points
     * @throws IOException if an I/O error occurs
     */
    public void drawPolyLine(Points points) throws IOException {
        // Using for-each loop for subList of points array to avoid i>0 && i%20==0
        raf.writeBytes(String.format("%6.3f %6.3f m\n", points.getX(0), points.getY(0)));
        int i = 1;
        List<Point> subPoints = points.subList(1, points.length());
        for (Point point : subPoints) {
            if (i%20 == 0) {
                raf.writeBytes(String.format("%6.3f %6.3f l\n", point.X, point.Y));
                raf.writeBytes("S\n");
                raf.writeBytes(String.format("%6.3f %6.3f m\n", point.X, point.Y));
            } else {
                raf.writeBytes(String.format("%6.3f %6.3f l\n", point.X, point.Y));
            }
            i++;
        }
        raf.writeBytes("S\n");
    }

    /**
     * Creates a PDF graph for cycloid
     *
     * @param flag -1 if negative half; 0 whole graph; 1 positive half
     * @param cycloid declared as final to only give access to its public methods
     */
    public void makeCycloidGraph(int flag, final Cycloid cycloid) throws IOException {
        double minX = cycloid.PlayfairX(-1*Math.PI);
        double maxX = cycloid.PlayfairX(Math.PI);

        // prints title
        raf.writeBytes("BT\n");
        raf.writeBytes("/F1 12 Tf\n");
        raf.writeBytes(String.format("%g %g Td (%s) Tj\n",
                        (float)(cycloid.getPaper().getHeight()/2-5*cycloid.getTitle().length()/2),
                        (float)(cycloid.getPaper().getWidth()-60.0),
                        cycloid.getTitle()));
        raf.writeBytes("ET\n");
        if (cycloid.isCaptionEnabled()) {
            raf.writeBytes("BT\n");
            raf.writeBytes("/F1 12 Tf\n");
            if (cycloid.getMetric() == Metric.MM) {
                raf.writeBytes(String.format("%g 60 Td (W=%4.2f,  h=%4.2f  scale=%4.3f %4.3f) Tj\n",
                        (float)(cycloid.getPaper().getHeight()/2-5*40/2),
                        cycloid.getCycloidWidth(), cycloid.getCycloidHeight(),
                        cycloid.getScaleWidth(), cycloid.getScaleHeight()));
            } else {
                raf.writeBytes(String.format("%g 60 Td (W=%4.2f,  h=%4.2f  scale=%4.3f %4.3f) Tj\n",
                        (float)(cycloid.getPaper().getHeight()/2-5*40/2),
                        cycloid.getCycloidWidth()/25.4, cycloid.getCycloidHeight()/25.4,
                        cycloid.getScaleWidth(), cycloid.getScaleHeight()));
            }
            raf.writeBytes("ET\n");
        }

        raf.writeBytes(String.format("%5.4f 0 0 %5.4f 0 0 cm\n", Cycloid.PT_TO_MM, Cycloid.PT_TO_MM));
        raf.writeBytes(String.format("%g 0 0 %g %g 50 cm\n", cycloid.getScaleWidth(), cycloid.getScaleHeight(),
                        cycloid.getPaper().getHeight()/2.0/Cycloid.PT_TO_MM));

        if (flag == -1) {
            raf.writeBytes(String.format("1 0 0 1 %6.3f 20 cm\n", -1*minX/2));
            maxX = 0.0;
        } else if (flag == 1) {
            raf.writeBytes(String.format("1 0 0 1 %6.3f 20 cm\n", minX/2));
            minX = 0.0;
        }

        // plots the grid
        raf.writeBytes("q\n");
        raf.writeBytes(".35 w\n");
        raf.writeBytes(".5 G\n");
        raf.writeBytes(String.format("%6.3f 0 m %6.3f 0 l S\n", minX, maxX));
        raf.writeBytes("Q\n");

        // plots the curve
        raf.writeBytes("q\n");
        raf.writeBytes(".1 w\n");
        plotCycloidCurve((int)(maxX-minX), flag, cycloid);
        raf.writeBytes("Q\n");
    }

    /**
     * Plots the cycloid curve on PDF file
     *
     * @param res resolution
     * @param flag -1 negative half, 0 whole curve, 1 positive half
     */
    private void plotCycloidCurve(double res, int flag, final Cycloid cycloid) throws IOException {
        if (res < 20) {
            res = 50;
        }

        double x = 0.0;
        double y = 0.0;
        if (flag <= 0) {
            x = cycloid.PlayfairX(-1*Math.PI);
            y = cycloid.PlayfairY(-1*Math.PI);
        } else {
            x = cycloid.PlayfairX(0.0);
            y = cycloid.PlayfairY(0.0);
        }

        raf.writeBytes(String.format("%6.3f %6.3f m\n", x, y));
        for (int i=1; i<=res; ++i) {
            if (flag == -1) {
                x = cycloid.PlayfairX(Math.PI*i/res-Math.PI);
                y = cycloid.PlayfairY(Math.PI*i/res-Math.PI);
            } else if (flag == 0) {
                x = cycloid.PlayfairX(2*Math.PI*i/res-Math.PI);
                y = cycloid.PlayfairY(2*Math.PI*i/res-Math.PI);
            } else {
                x = cycloid.PlayfairX(Math.PI*i/res);
                y = cycloid.PlayfairY(Math.PI*i/res);
            }
            raf.writeBytes(String.format("%6.3f %6.3f l\n", x, y));
            if (i%10 == 0) {
                raf.writeBytes("S\n");
                raf.writeBytes(String.format("%6.3f %6.3f m\n", x, y));
            }
        }
        raf.writeBytes("S\n");

        x = cycloid.PlayfairX(0.0);
        y = 0.0;
        raf.writeBytes(String.format("%6.3f %6.3f m\n", x, y));
        y = 2*cycloid.getr()+10;
        raf.writeBytes(String.format("%6.3f %6.3f l\n", x, y));

        if (flag <= 0) {
            x = cycloid.PlayfairX(-1*Math.PI, 1.0);
            y = 0.0;
            raf.writeBytes(String.format("%6.3f %6.3f m\n", x, y));
            y = 2*cycloid.getr()+10;
            raf.writeBytes(String.format("%6.3f %6.3f l\n", x, y));
        }
        if (flag >= 0) {
            x = cycloid.PlayfairX(Math.PI, 1.0);
            y = 0.0;
            raf.writeBytes(String.format("%6.3f %6.3f m\n", x, y));
            y = 2*cycloid.getr()+10;
            raf.writeBytes(String.format("%6.3f %6.3f l\n", x, y));
        }
        raf.writeBytes("S\n");
    }
}
