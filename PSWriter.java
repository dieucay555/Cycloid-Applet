import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.Math;
import javax.swing.JOptionPane;

/**
 * A PS file writer for Cycloid applet
 */
class PSWriter {
    private final PrintWriter writer; // this will never change for each instance
    private int page = 0;
    private int top = 0;
    private int eps = 0; // not sure if this is still used

    /**
     * Handles creating PS file from cycloid/catenary user specified.
     *
     * @param file filename the user specified
     */
    public PSWriter(File file) throws IOException {
        writer = new PrintWriter(new BufferedWriter(new FileWriter(file)));
    }

    /**
     * Prints header for the PS file being constructed.
     */
    public void writeHeader() {
        writer.printf("%%!PS-Adobe-2.0\n");
        writer.printf("%%%%Title: Trachoids\n");
        writer.printf("%%%%DocumentFonts: Times-Roman\n");
        writer.printf("%%%%Orientation: Landscape\n");
        writer.printf("%%%%Pages: (at end)\n");
        writer.printf("%%%%EndComments\n");
        writer.flush();
    }

    /**
     * Prints the header for the EPS file.
     */
    // TODO Implement this     

    /**
     * Prints page header for the PS file being constructed.
     *
     * @param scale current scale for the drawing
     * @param width width of cycloid/length of catenary
     * @param height height of cycloid/depth of catenary
     * @param g_xs horizontal scale
     * @param g_ys vertical scale
     * @param title title of the figure
     */
    private void writePageHeader(int scale, int width, int height
                                , double g_xs, double g_ys, String title)
    {
        writer.printf("%%%%Page: %d %d\n", page, page);
        // TODO: I am not sure why this is needed
        if (scale != 1.0) {
            writer.printf("%4.2f %4.2f scale\n", scale, scale);
        }
        writer.printf("gsave\n");
        writer.printf("90 rotate\n");
        writer.printf("0 -%d translate\n", width);
        writer.printf("%5.4f %5.4f scale\n", Cycloid.PT_TO_MM*g_xs, Cycloid.PT_TO_MM*g_ys);
        writer.printf("/Times-Roman findfont 5 scalefont setfont\n");
        writer.printf("%g %g moveto (%s) dup stringwidth pop 2 div neg 0 rmoveto show\n"
                        , height/2.0/Cycloid.PT_TO_MM, 200.0/g_ys, title);
        writer.flush();
    }

    /**
     * Prints trailer for the PS file being constructed.
     */
    public void writeTrailer() {
        if (top == 1) {
            writer.printf("grestore\n");
            writer.printf("showpage\n");
        }
        if (eps == 0) {
            writer.printf("%%%%Pages: %d\n", page);
        }
        writer.printf("%%%%EOF\n");
        writer.flush();
    }

    /**
     * Closes writer stream.
     */
    public void closeFile() {
        try {
            writer.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,
                e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Creates PS graph for cycloid
     *
     * @param cycloid declared as final to only give access to its public methods
     */
    public void makeCycloidGraph(final Cycloid cycloid) {
        double minX = cycloid.PlayfairX(-1*Math.PI);
        double maxX = cycloid.PlayfairY(Math.PI);

        if (maxX-minX > (cycloid.getPaper().getHeight()-30)/Cycloid.PT_TO_MM) {
            if (top == 1) {
                writer.printf("grestore\n");
                writer.printf("showpage\n");
            }
            // makeSplitGraph
            top = 0;
            makeSplitCycloidGraph(minX, maxX, cycloid);
            return;
        }

        if (top == 0) {
            page += 1;

            writePageHeader(1/* scale */,
                            cycloid.getPaper().getWidth(),
                            cycloid.getPaper().getHeight(),
                            cycloid.getScaleWidth(),
                            cycloid.getScaleHeight(),
                            cycloid.getTitle());
            writer.printf("%g 20 translate\n", cycloid.getPaper().getHeight()/2.0/Cycloid.PT_TO_MM);
        } else {
            writer.printf("0 125 translate\n");
        }

        if (cycloid.isCaptionEnabled()) {
            writer.printf("/Times-Roman findfont 5 scalefont setfont\n");
            if (cycloid.getCycloidWidth() > 0 && cycloid.getCycloidHeight() > 0) {
                if (cycloid.getMetric() == Metric.MM) {
                    writer.printf("0 -8 moveto (W=%4.2f,  h=%4.2f  scale=%4.3f %4.3f) dup "
                                    + "stringwidth pop 2 div neg 0 rmoveto show \n",
                                    cycloid.getCycloidWidth(),
                                    cycloid.getCycloidHeight(),
                                    cycloid.getScaleWidth(),
                                    cycloid.getScaleHeight());
                } else {
                    writer.printf("0 -8 moveto (W=%4.2f,  h=%4.2f  scale=%4.3f %4.3f) dup "
                                    + "stringwidth pop 2 div neg 0 rmoveto show \n",
                                    cycloid.getCycloidWidth()/25.4,
                                    cycloid.getCycloidHeight()/25.4,
                                    cycloid.getScaleWidth(),
                                    cycloid.getScaleHeight());
                }
            } else {
                if (cycloid.getMetric() == Metric.MM) {
                    writer.printf("0 -8 moveto (R=%4.2f,  r=%4.2f) dup stringwidth pop 2 "
                                    + "div neg 0 rmoveto show \n", cycloid.getR(), cycloid.getr());
                } else {
                    writer.printf("0 -8 moveto (R=%4.2f,  r=%4.2f) dup stringwidth pop 2 "
                                    + "div neg 0 rmoveto show \n", cycloid.getR()/25.4, cycloid.getr()/25.4);
                }
            }
        }

        writer.printf("gsave\n");
        writer.printf(".35 setlinewidth\n");
        writer.printf(".5 setgray\n");
        writer.printf("%6.3f 0 moveto %6.3f 0 rlineto stroke\n", minX, maxX-minX);
        writer.printf("%6.3f %6.3f moveto %6.3f 0 rlineto stroke\n", minX, cycloid.getCycloidHeight()/2, maxX-minX);
        writer.printf("%6.3f %6.3f moveto %6.3f 0 rlineto stroke\n", minX, cycloid.getCycloidHeight(), maxX-minX);
        writer.printf("grestore\n");

        // finally plotting the curve
        writer.printf("gsave\n");
        writer.printf(".1 setlinewidth\n");
        plotCycloidCurve((int)(maxX-minX), 0, cycloid);
        writer.printf("grestore\n");

        if (top == 1) {
            writer.printf("grestore\n");
            writer.printf("showpage\n");
        }
        writer.flush();

        top = (top+1)%2;
    }

    /**
     * Draws split graph if it is too large to fit in a single page
     *
     * @param minX min x coord
     * @param maxX max x coord
     * @param cycloid cycloid instance (only public methods are accessible)
     */
    private void makeSplitCycloidGraph(double minX, double maxX, final Cycloid cycloid) {
        if (maxX-minX > 500) {
            JOptionPane.showMessageDialog(null,
                "Graph too large. Ends may be chopped",
                "Warning",
                JOptionPane.WARNING_MESSAGE);
        }

        page += 1;

        writePageHeader(1/* scale */,
                        cycloid.getPaper().getWidth(),
                        cycloid.getPaper().getHeight(),
                        cycloid.getScaleWidth(),
                        cycloid.getScaleHeight(),
                        cycloid.getTitle());
        writer.printf("%6.3f 20 translate\n", -1*minX+20);
        if (cycloid.isCaptionEnabled()) {
            writer.printf("/Times-Roman findfont 5 scalefont setfont\n");
            if (cycloid.getCycloidWidth() > 0 && cycloid.getCycloidHeight() > 0) {
                if (cycloid.getMetric() == Metric.MM) {
                    writer.printf("0 -8 moveto (W=%4.2f,  h=%4.2f  scale=%4.3f %4.3f) dup stringwidth "
                                    + "pop 2 div neg 0 removeto show \n",
                                    cycloid.getCycloidWidth(), cycloid.getCycloidHeight(),
                                    cycloid.getScaleWidth(), cycloid.getScaleHeight());
                } else {
                    writer.printf("0 -8 moveto (W=%4.2f,  h=%4.2f  scale=%4.3f %4.3f) dup stringwidth "
                                    + "pop 2 div neg 0 removeto show \n",
                                    cycloid.getCycloidWidth()/25.4, cycloid.getCycloidHeight()/25.4,
                                    cycloid.getScaleWidth(), cycloid.getScaleHeight());
                }
            } else {
                if (cycloid.getMetric() == Metric.MM) {
                    writer.printf("0 -8 moveto (R=%4.2f,  r=%4.2f) dup stringwidth pop 2 div neg 0 "
                                    + "rmoveto show \n", cycloid.getR(), cycloid.getr());
                } else {
                    writer.printf("0 -8 moveto (R=%4.2f,  r=%4.2f) dup stringwidth pop 2 div neg 0 "
                                    + "rmoveto show \n", cycloid.getR()/25.4, cycloid.getr()/25.4);
                }
            }
        }

        writer.printf("gsave\n");
        writer.printf(".35 setlinewidth\n");
        writer.printf(".5 setgray\n");
        writer.printf("%6.3f 0 moveto %6.3f 0 rlineto stroke\n", minX, maxX-minX);
        writer.printf("%6.3f 10 moveto %6.3f 0 rlineto stroke\n", minX, maxX-minX);
        writer.printf("%6.3f 20 moveto %6.3f 0 rlineto stroke\n", minX, maxX-minX);
        writer.printf("grestore\n");

        // finally plotting the curve
        writer.printf("gsave\n");
        writer.printf(".1 setlinewidth\n");
        plotCycloidCurve((int)(maxX-minX)/2, -1, cycloid);
        writer.printf("grestore\n");

        writer.printf("grestore\n");
        writer.printf("showpage\n");

        // print the second page
        page += 1;
        writePageHeader(1/* scale */,
                        cycloid.getPaper().getWidth(),
                        cycloid.getPaper().getHeight(),
                        cycloid.getScaleWidth(),
                        cycloid.getScaleHeight(),
                        cycloid.getTitle());
        writer.printf("20 20 translate\n");

        if (cycloid.isCaptionEnabled()) {
            writer.printf("/Times-Roman findfont 5 scalefont setfont\n");
            if (cycloid.getCycloidWidth() > 0 && cycloid.getCycloidHeight() > 0) {
                if (cycloid.getMetric() == Metric.MM) {
                    writer.printf("0 -8 moveto (W=%4.2f,  h=%4.2f  scale=%4.3f %4.3f) dup stringwidth "
                                    + "pop 2 div neg 0 rmoveto show \n",
                                    cycloid.getCycloidWidth(), cycloid.getCycloidHeight(),
                                    cycloid.getScaleWidth(), cycloid.getScaleHeight());
                } else {
                    writer.printf("0 -8 moveto (W=%4.2f,  h=%4.2f  scale=%4.3f %4.3f) dup stringwidth "
                                    + "pop 2 div neg 0 rmoveto show \n",
                                    cycloid.getCycloidWidth()/25.4, cycloid.getCycloidHeight()/25.4,
                                    cycloid.getScaleWidth(), cycloid.getScaleHeight());
                }
            } else {
                writer.printf("0 -8 moveto (R=%4.2f,  r=%4.2f) dup stringwidth pop 2 div neg 0 rmoveto show \n",
                                cycloid.getR(), cycloid.getr());
            }
        }
        writer.printf("gsave\n");
        writer.printf(".35 setlinewidth\n");
        writer.printf(".5 setgray\n");
        writer.printf("%6.3f 0 moveto %6.3f 0 rlineto stroke\n", minX, maxX-minX);
        writer.printf("%6.3f 10 moveto %6.3f 0 rlineto stroke\n", minX, maxX-minX);
        writer.printf("%6.3f 20 moveto %6.3f 0 rlineto stroke\n", minX, maxX-minX);
        writer.printf("grestore\n");

        // plotting the curve again
        writer.printf("gsave\n");
        writer.printf(".1 setlinewidth\n");
        plotCycloidCurve((int)(maxX-minX)/2, 1, cycloid);
        writer.printf("grestore\n");

        writer.printf("grestore\n");
        writer.printf("showpage\n");
        writer.flush();
    }

    /**
     * Plots the cycloid curve on PS file
     *
     * @param res resolution
     * @param flag -1 negative half, 0 whole curve, 1 positive half
     */
    private void plotCycloidCurve(double res, int flag, final Cycloid cycloid) {
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

        writer.printf("%6.3f %6.3f moveto\n", x, y);
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
            writer.printf("%6.3f %6.3f lineto\n", x, y);
            if (i%10 == 0) {
                writer.printf("stroke\n");
                writer.printf("%6.3f %6.3f moveto\n", x, y);
            }
        }
        writer.printf("stroke\n");
        writer.flush();
        x = cycloid.PlayfairX(0.0);
        y = 0;
        writer.printf("%6.3f %6.3f moveto\n", x, y);
        y = 2*cycloid.getr()+10;
        writer.printf("%6.3f %6.3f lineto\n", x, y);

        if (flag <= 0) {
            x = cycloid.PlayfairX(-1*Math.PI, 1.0f);
            y = 0;
            writer.printf("%6.3f %6.3f moveto\n", x, y);
            y = 2*cycloid.getr()+10;
            writer.printf("%6.3f %6.3f lineto\n", x, y);
        }
        if (flag >= 0) {
            x = cycloid.PlayfairX(Math.PI, 1.0f);
            y = 0;
            writer.printf("%6.3f %6.3f moveto\n", x, y);
            y = 2*cycloid.getr()+10;
            writer.printf("%6.3f %6.3f lineto\n", x, y);
        }
        writer.printf("stroke\n");
        writer.flush();
    }
}
