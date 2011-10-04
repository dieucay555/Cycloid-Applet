import java.io.PrintWriter;

class PSWriter {
    public static void writeHeader(PrintWriter writer) {
        writer.printf("%%!PS-Adobe-2.0\n");
        writer.printf("%%%%Title: Trachoids\n");
        writer.printf("%%%%DocumentFonts: Times-Roman\n");
        writer.printf("%%%%Orientation: Landscape\n");
        writer.printf("%%%%Pages: (at end)\n");
        writer.printf("%%%%EndComments\n");
    }
    public static void writePageHeader(PrintWriter writer,
                                       Integer page,
                                       int scale,
                                       int width,
                                       int height,
                                       double g_xs,
                                       double g_ys,
                                       String title)
    {
        writer.printf("%%%%Page: %d %d\n", page, page);
        if (scale != 1.0) {
            writer.printf("%4.2f %4.2f scale\n", scale, scale);
        }
        writer.printf("gsave\n");
        writer.printf("90 rotate\n");
        writer.printf("0 -%d translate\n", width);
        writer.printf("%5.4f %.4f scale\n", Cycloid.PT_TO_MM*g_xs, Cycloid.PT_TO_MM*g_ys);
        writer.printf("/Times-Roman findfont 5 scalefont setfont\n");
        writer.printf("%g 200 moveto (%s) dup stringwidth pop 2 div neg 0 rmoveto show\n", height/2.0/Cycloid.PT_TO_MM, title);
    }
}
