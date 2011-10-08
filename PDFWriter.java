import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

class PDFWriter {
    private static final int MAX_XREFS = 2000;
    private RandomAccessFile raf;

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

    private PDFFile pdfInfo;

    public PDFWriter(File file) throws FileNotFoundException {
        raf = new RandomAccessFile(file, "rwd");
        pdfInfo = new PDFFile();

    }

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

    void beginObj(int obj) throws IOException {
        if (obj >= MAX_XREFS) {
            // print error
            return;
        }
        if (pdfInfo.xrefs[obj] != 0 || obj == 0) {
            // print error
            return;
        }
        pdfInfo.objs++;
        pdfInfo.xrefs[obj] = raf.getFilePointer();
        raf.writeBytes(String.format("%d 0 obj\n", obj));
    }

    void endObj() throws IOException {
        raf.writeBytes(String.format("endobj\n\n"));
    }

    public void beginStreamObj(int obj) throws IOException {
        if (obj >= MAX_XREFS) {
            // print error
            return;
        }
        if (pdfInfo.xrefs[obj] != 0 || obj == 0) {
            // print error
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

    public void endStreamObj() throws IOException {
        pdfInfo.streamlenpos3 = raf.getFilePointer();
        raf.seek(pdfInfo.streamlenpos1);
        raf.writeBytes(String.format("%05d", pdfInfo.streamlenpos3-pdfInfo.streamlenpos2));
        raf.seek(raf.length()); // attempting fseek(0, SEEK_END)
        raf.writeBytes("endstream\n");
        raf.writeBytes("endobj\n\n");
    }

    public void writeLine(String line) throws IOException {
        raf.writeBytes(line);
    }

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

    public void closePDF() throws IOException {
        raf.writeBytes("%%%%EOF\n");
        raf.close();
        pdfInfo.objs = 0;
    }

    public void setPageSize(int width, int height) {
        pdfInfo.width = width;
        pdfInfo.height = height;
    }

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
        raf.writeBytes("/Resources << /Font << /F1 5 0 R >> /ProcSet 6 0 R >> \n");
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

    public void multiPageHeaders(int pc, int on[]) throws IOException {
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
            on[i] = 6+pc+i;
            raf.writeBytes(">>\n");
            endObj();
        }
    }

    public void drawPolyLine(int n, Points points) throws IOException {
        int i;
        raf.writeBytes(String.format("%6.3f %6.3f m\n", points.getX(0), points.getY(0)));
        for (i=1; i<n; i++) {
            if (i%20 == 0) {
                raf.writeBytes(String.format("%6.3f %6.3f l\n", points.getX(i), points.getY(i)));
                raf.writeBytes("S\n");
                if (i != n) {
                    raf.writeBytes(String.format("%6.3f %6.3f m\n", points.getX(i), points.getY(i)));
                }
            } else {
                raf.writeBytes(String.format("%6.3f %6.3f l\n", points.getX(i), points.getY(i)));
            }
        }
        if (i != n+1) {
            raf.writeBytes("S\n");
        }
    }
}
