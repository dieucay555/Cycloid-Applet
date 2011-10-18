/**
 * Use constructor enabled enum
 * Specified default width/height are assigned to defined enum type
 * constructor is private because cannot be called outside of enum
 */
public enum PaperSize {
    LETTER(612, 792),
    LEGAL(612, 1008),
    A4(595, 842),
    A3(842, 1190);

    private PaperSize(int w, int h) {
        width = w;
        height = h;
    }
    final int getWidth() {
        return width;
    }
    final int getHeight() {
        return height;
    }
    private final int width;
    private final int height;
}
