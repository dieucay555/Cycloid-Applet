import javax.swing.JPanel;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;

public class CycloidPanel extends JPanel
    implements ActionListener {

    private Cycloid drawPanel;

    private JButton createCycloid;
    private JButton clearCycloid;
    private JRadioButton mmButton;
    private JRadioButton inchButton;
    private JTextField widthText;
    private JTextField heightText;
    private JTextField percentText;
    private JCheckBox captionEnable;
    private JCheckBox redrawEnable;
    private JCheckBox autoFileNameEnable;
    private JTextField fileNameText;
    private JTextField titleText;
    private JRadioButton letterSizeButton;
    private JRadioButton legalSizeButton;
    private JRadioButton a4SizeButton;
    private JRadioButton a3SizeButton;
    private JRadioButton pdfButton;
    private JRadioButton psButton;
    private JRadioButton dxfButton;
    private JRadioButton csvButton;
    private JTextField scaleWidthText;
    private JTextField scaleHeightText;

    private static String CREATE_COMMAND = "create";
    private static String CLEAR_COMMAND = "clear";
    private static String MM_COMMAND = "mm";
    private static String INCH_COMMAND = "inch";
    private static String LETTER_COMMAND = "letter";
    private static String LEGAL_COMMAND = "legal";
    private static String A4_COMMAND = "a4";
    private static String A3_COMMAND = "a3";
    private static String PDF_COMMAND = "pdf";
    private static String PS_COMMAND = "ps";
    private static String DXF_COMMAND = "dxf";
    private static String CSV_COMMAND = "csv";

    private enum Metric {
        MM, INCH
    }

    // default select metric to mm
    private Metric metricSelected = Metric.MM;

    private enum PaperSize {
        LETTER, LEGAL, A4, A3
    }

    // default select paper size to metric
    private PaperSize paperSelected = PaperSize.LETTER;

    private enum Format {
        PDF, PS, DXF, CSV
    }

    // default select format to PDF
    private Format formatSelected = Format.PDF;

    private boolean captionEnabled = false;
    private boolean redrawEnabled = false;
    private boolean autoFileNameEnabled = false;

    // set to false when stable
    private boolean DEBUG = true;

    public CycloidPanel() {
        super(new BorderLayout());

        createCycloid = new JButton("Create");
        createCycloid.setActionCommand(CREATE_COMMAND);
        createCycloid.addActionListener(this);

        clearCycloid = new JButton("Clear");
        clearCycloid.setActionCommand(CLEAR_COMMAND);
        clearCycloid.addActionListener(this);

        // Set top panel for drawing
        drawPanel = new Cycloid();
        drawPanel.setDoubleBuffered(true);
//        drawPanel.setSize(600, 400);
        add(drawPanel, BorderLayout.CENTER);

        JPanel topPanel = new JPanel(new BorderLayout());
        JPanel topLeftPanel = new JPanel(new BorderLayout());
        topLeftPanel.add(createCycloid, BorderLayout.NORTH);

        // mm/inch radio button
        mmButton = new JRadioButton("mm", true);
        mmButton.setActionCommand(MM_COMMAND);
        mmButton.addActionListener(this);
        inchButton = new JRadioButton("inch");
        inchButton.setActionCommand(INCH_COMMAND);
        inchButton.addActionListener(this);
        ButtonGroup mmInchGroup = new ButtonGroup();
        mmInchGroup.add(mmButton);
        mmInchGroup.add(inchButton);
        topLeftPanel.add(mmButton, BorderLayout.WEST);
        topLeftPanel.add(inchButton, BorderLayout.EAST);

        // width/height specifiers
        JLabel widthLabel = new JLabel("Width");
        JLabel heightLabel = new JLabel("Height");
        JPanel widthPanel = new JPanel(new FlowLayout());
        widthPanel.add(widthLabel);
        widthText = new JTextField(6);
        widthText.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                drawPanel.setCycloidWidth(Double.parseDouble(widthText.getText()));
                drawPanel.repaint();
            }
            public void removeUpdate(DocumentEvent e) {
                // prevent NullPointerException when a user erases all text in the field
                if (widthText.getText().length() == 0) {
                    drawPanel.setCycloidWidth(0.00);
                } else {
                    drawPanel.setCycloidWidth(Double.parseDouble(widthText.getText()));
                    drawPanel.repaint();
                }
            }
            public void changedUpdate(DocumentEvent e) {}
            });
        widthText.setText("200.00");
        widthPanel.add(widthText);

        JPanel heightPanel = new JPanel(new FlowLayout());
        heightPanel.add(heightLabel);
        heightText = new JTextField(6);
        heightText.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                drawPanel.setCycloidHeight(Double.parseDouble(heightText.getText()));
                drawPanel.repaint();
            }
            public void removeUpdate(DocumentEvent e) {
                if (heightText.getText().length() == 0) {
                    drawPanel.setCycloidHeight(0.00);
                } else {
                    drawPanel.setCycloidHeight(Double.parseDouble(heightText.getText()));
                    drawPanel.repaint();
                }
            }
            public void changedUpdate(DocumentEvent e) {}
            });
        heightText.setText("20.00");
        heightPanel.add(heightText);

        JPanel whPanel = new JPanel(new BorderLayout());
        whPanel.add(widthPanel, BorderLayout.NORTH);
        whPanel.add(heightPanel, BorderLayout.SOUTH);

        topLeftPanel.add(whPanel, BorderLayout.SOUTH);

        JPanel topRightPanel = new JPanel(new BorderLayout());
        topRightPanel.add(clearCycloid, BorderLayout.NORTH);

        // percent text box
        JLabel percentLabel = new JLabel("Percent");
        JPanel percentPanel = new JPanel(new FlowLayout());
        percentPanel.add(percentLabel);
        percentText = new JTextField(6);
        percentText.setText("100.0");
        percentText.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                drawPanel.setPercent(Double.parseDouble(percentText.getText()));
                drawPanel.repaint();
            }
            public void removeUpdate(DocumentEvent e) {
                if (percentText.getText().length() == 0) {
                    drawPanel.setPercent(0.00);
                } else {
                    drawPanel.setPercent(Double.parseDouble(percentText.getText()));
                    drawPanel.repaint();
                }
            }
            public void changedUpdate(DocumentEvent e) {}
            });
        percentPanel.add(percentText);
        topRightPanel.add(percentPanel, BorderLayout.CENTER);

        // Caption/Redraw checkboxes
        captionEnable = new JCheckBox("Caption");
        redrawEnable = new JCheckBox("Redraw");
        JPanel crPanel = new JPanel(new BorderLayout());
        crPanel.add(captionEnable, BorderLayout.NORTH);
        crPanel.add(redrawEnable, BorderLayout.SOUTH);
        topRightPanel.add(crPanel, BorderLayout.SOUTH);

        // auto filename checkbox and textbox and title
        autoFileNameEnable = new JCheckBox("Auto File Name");
        JLabel fileNameLabel = new JLabel("File name");
        fileNameText = new JTextField(20);
        JPanel nameLeftPanel = new JPanel(new FlowLayout());
        nameLeftPanel.add(autoFileNameEnable);
        JPanel nameRightPanel = new JPanel(new FlowLayout());
        nameRightPanel.add(fileNameLabel);
        nameRightPanel.add(fileNameText);
        // container for auto filename checkbox/textbox
        JPanel namePanel = new JPanel(new BorderLayout());
        namePanel.add(nameLeftPanel, BorderLayout.WEST);
        namePanel.add(nameRightPanel, BorderLayout.EAST);
        JPanel filePanel = new JPanel(new BorderLayout());
        filePanel.add(namePanel, BorderLayout.NORTH);

        // title
        JLabel titleLabel = new JLabel("Title");
        titleText = new JTextField(40);
        titleText.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                drawPanel.setTitle(titleText.getText());
            }
            public void removeUpdate(DocumentEvent e) {
                if (titleText.getText().length() == 0) {
                    drawPanel.setTitle("");
                } else {
                    drawPanel.setTitle(titleText.getText());
                }
            }
            public void changedUpdate(DocumentEvent e) {}
            });
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.add(titleLabel, BorderLayout.WEST);
        titlePanel.add(titleText, BorderLayout.EAST);
        filePanel.add(titlePanel, BorderLayout.CENTER);

        // size panel
        JPanel sizePanel = new JPanel(new BorderLayout());
        JPanel paperSizePanel = new JPanel(new FlowLayout());
        letterSizeButton = new JRadioButton("Letter");
        letterSizeButton.setActionCommand(LETTER_COMMAND);
        letterSizeButton.addActionListener(this);
        legalSizeButton = new JRadioButton("Legal");
        legalSizeButton.setActionCommand(LEGAL_COMMAND);
        legalSizeButton.addActionListener(this);
        a4SizeButton = new JRadioButton("A4");
        a4SizeButton.setActionCommand(A4_COMMAND);
        a4SizeButton.addActionListener(this);
        a3SizeButton = new JRadioButton("A3");
        a3SizeButton.setActionCommand(A3_COMMAND);
        a3SizeButton.addActionListener(this);

        ButtonGroup paperSizeGroup = new ButtonGroup();
        paperSizeGroup.add(letterSizeButton);
        paperSizeGroup.add(legalSizeButton);
        paperSizeGroup.add(a4SizeButton);
        paperSizeGroup.add(a3SizeButton);

        paperSizePanel.add(letterSizeButton);
        paperSizePanel.add(legalSizeButton);
        paperSizePanel.add(a4SizeButton);
        paperSizePanel.add(a3SizeButton);
        sizePanel.add(paperSizePanel, BorderLayout.WEST);

        // file format panel
        JPanel formatPanel = new JPanel(new FlowLayout());
        pdfButton = new JRadioButton("PDF");
        pdfButton.setActionCommand(PDF_COMMAND);
        pdfButton.addActionListener(this);
        psButton = new JRadioButton("PS");
        psButton.addActionListener(this);
        psButton.setActionCommand(PS_COMMAND);
        dxfButton = new JRadioButton("DXF");
        dxfButton.setActionCommand(DXF_COMMAND);
        dxfButton.addActionListener(this);
        csvButton = new JRadioButton("CSV");
        csvButton.setActionCommand(CSV_COMMAND);
        csvButton.addActionListener(this);

        ButtonGroup formatGroup = new ButtonGroup();
        formatGroup.add(pdfButton);
        formatGroup.add(psButton);
        formatGroup.add(dxfButton);
        formatGroup.add(csvButton);

        formatPanel.add(pdfButton);
        formatPanel.add(psButton);
        formatPanel.add(dxfButton);
        formatPanel.add(csvButton);
        sizePanel.add(formatPanel, BorderLayout.EAST);

        filePanel.add(sizePanel, BorderLayout.SOUTH);

        // scale panel
        JPanel scalePanel = new JPanel(new BorderLayout());
        JPanel scaleWidthPanel = new JPanel(new FlowLayout());
        JPanel scaleHeightPanel = new JPanel(new FlowLayout());

        JLabel scaleWidthLabel = new JLabel("Scale width:");
        scaleWidthText = new JTextField(6);
        scaleWidthText.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                drawPanel.setScaleWidth(Double.parseDouble(scaleWidthText.getText()));
                drawPanel.repaint();
            }
            public void removeUpdate(DocumentEvent e) {
                if (scaleWidthText.getText().length() == 0) {
                    drawPanel.setScaleWidth(1.00);
                } else {
                    drawPanel.setScaleWidth(Double.parseDouble(scaleWidthText.getText()));
                    drawPanel.repaint();
                }
            }
            public void changedUpdate(DocumentEvent e) {}
            });
        scaleWidthText.setText("1.000");
        scaleWidthPanel.add(scaleWidthLabel);
        scaleWidthPanel.add(scaleWidthText);

        JLabel scaleHeightLabel = new JLabel("Scale height:");
        scaleHeightText = new JTextField(6);
        scaleHeightText.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                drawPanel.setScaleHeight(Double.parseDouble(scaleHeightText.getText()));
                drawPanel.repaint();
            }
            public void removeUpdate(DocumentEvent e) {
                if (scaleHeightText.getText().length() == 0) {
                    drawPanel.setScaleHeight(1.00);
                } else {
                    drawPanel.setScaleHeight(Double.parseDouble(scaleHeightText.getText()));
                    drawPanel.repaint();
                }
            }
            public void changedUpdate(DocumentEvent e) {}
            });
        scaleHeightText.setText("1.000");
        scaleHeightPanel.add(scaleHeightLabel);
        scaleHeightPanel.add(scaleHeightText);

        scalePanel.add(scaleWidthPanel, BorderLayout.WEST);
        scalePanel.add(scaleHeightPanel, BorderLayout.EAST);

        // settings panel -> file/scale panel
        JPanel settingsPanel = new JPanel(new BorderLayout());
        settingsPanel.add(filePanel, BorderLayout.NORTH);
        settingsPanel.add(scalePanel, BorderLayout.SOUTH);

        // top panel -> topleft/topright/settings panels
        topPanel.add(topLeftPanel, BorderLayout.WEST);
        topPanel.add(topRightPanel, BorderLayout.EAST);
        topPanel.add(settingsPanel, BorderLayout.SOUTH);
        add(topPanel, BorderLayout.SOUTH);
    }

    public void itemStateChanged(ItemEvent e) {
        Object source = e.getItemSelectable();
        boolean selected = (e.getStateChange() == ItemEvent.SELECTED);

        if (source == captionEnable) {
            captionEnabled = selected;
            if (DEBUG) {
                drawPanel.add(new JLabel("Caption enabled"));
            }
        } else if (source == redrawEnable) {
            redrawEnabled = selected;
            if (DEBUG) {
                drawPanel.add(new JLabel("Redraw enabled"));
            }
        } else if (source == autoFileNameEnable) {
            autoFileNameEnabled = selected;
            if (DEBUG) {
                drawPanel.add(new JLabel("AutoFileName enabled"));
            }
        }
    }

    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();

        if (CREATE_COMMAND.equals(command)) {
            if (DEBUG) {
                drawPanel.add(new JLabel("Creating cycloid..."));
            }
        } else if (CLEAR_COMMAND.equals(command)) {
            if (DEBUG) {
                drawPanel.removeAll();
            }
        } else if (MM_COMMAND.equals(command)) {
            metricSelected = Metric.MM;
            if (DEBUG) {
                drawPanel.add(new JLabel("MM selected"));
            }
            // redraw
        } else if (INCH_COMMAND.equals(command)) {
            metricSelected = Metric.INCH;
            if (DEBUG) {
                drawPanel.add(new JLabel("INCH selected"));
            }
            // redraw
        } else if (LETTER_COMMAND.equals(command)) {
            paperSelected = PaperSize.LETTER;
            if (DEBUG) {
                drawPanel.add(new JLabel("LETTER selected"));
            }
            // redraw
        } else if (LEGAL_COMMAND.equals(command)) {
            paperSelected = PaperSize.LEGAL;
            if (DEBUG) {
                drawPanel.add(new JLabel("LEGAL selected"));
            }
            // redraw
        } else if (A4_COMMAND.equals(command)) {
            paperSelected = PaperSize.A4;
            if (DEBUG) {
                drawPanel.add(new JLabel("A4 seleted"));
            }
            // redraw
        } else if (A3_COMMAND.equals(command)) {
            paperSelected = PaperSize.A3;
            if (DEBUG) {
                drawPanel.add(new JLabel("A3 selected"));
            }
            // redraw
        } else if (PDF_COMMAND.equals(command)) {
            formatSelected = Format.PDF;
            if (DEBUG) {
                drawPanel.add(new JLabel("PDF selected"));
            }
            // redraw
        } else if (PS_COMMAND.equals(command)) {
            formatSelected = Format.PS;
            if (DEBUG) {
                drawPanel.add(new JLabel("PS selected"));
            }
            // redraw
        } else if (DXF_COMMAND.equals(command)) {
            formatSelected = Format.DXF;
            if (DEBUG) {
                drawPanel.add(new JLabel("DXF selected"));
            }
            // redraw
        } else if (CSV_COMMAND.equals(command)) {
            formatSelected = Format.CSV;
            if (DEBUG) {
                drawPanel.add(new JLabel("CSV selected"));
            }
            // redraw
        }
        // debug only
        validate();
    }
}
