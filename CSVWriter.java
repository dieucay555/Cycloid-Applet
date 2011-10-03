import java.io.PrintWriter;

public class CSVWriter {
    public static void writeHeader(PrintWriter writer) {}
    public static void writeTrailer(PrintWriter writer) {}
    public static void write2DPolyLine(PrintWriter writer, int n, Points points) {
        for (int i=0; i<n; i++) {
            writer.printf("%5.4f,%5.4f\n", points.getPoints().get(i).X, points.getPoints().get(i).Y);
        }
        writer.flush();
    }
}