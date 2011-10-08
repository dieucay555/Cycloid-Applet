import java.io.PrintWriter;

class CSVWriter {
    public static void writeHeader(PrintWriter writer) {}
    public static void writeTrailer(PrintWriter writer) {}
    public static void write2DPolyLine(PrintWriter writer, int n, Points points) {
        for (int i=0; i<n; i++) {
            writer.printf("%5.4f,%5.4f\n", points.getX(i), points.getY(i));
        }
        writer.flush();
    }
}
