import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import javax.swing.JOptionPane;

/**
 * A CSV file writer for Cycloid applet
 */
class CSVWriter {
    private final PrintWriter writer; // this will never change for each instance

    /**
     * Handles creating CSV file from cycloid/catenary user specified.
     *
     * @param file filename the user specified
     */
    public CSVWriter(File file) throws IOException {
        writer = new PrintWriter(new BufferedWriter(new FileWriter(file)));
    }

    /**
     * Closes writer used after done.
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
     * Writes 2D polyline's coordinates to CSV file.
     *
     * @param points points with X/Y coords representing cycloid/catenary
     */
    public void write2DPolyLine(final Points points) {
        for (Point point : points) {
            writer.printf("%5.4f,%5.4f\n", point.X, point.Y);
        }
        writer.flush();
    }
}
