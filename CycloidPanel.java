import java.io.File;

import javax.swing.JPanel;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;

public class CycloidPanel extends JPanel {

    private JTabbedPane drawPane;
    private Cycloid cycloidPanel;
    private Catenary catenaryPanel;

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

    private boolean captionEnabled = false;
    private boolean redrawEnabled = true;
    private boolean autoFileNameEnabled = false;

    public CycloidPanel() {
        super(new BorderLayout());

        createCycloid = new JButton("Create");
        createCycloid.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser fc = new JFileChooser();
                int returnVal = fc.showSaveDialog(CycloidPanel.this);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File file = fc.getSelectedFile();
                    if (drawPane.getSelectedIndex() == 0) {
                        cycloidPanel.writeToFile(file);
                    } else if (drawPane.getSelectedIndex() == 1) {
                        catenaryPanel.writeToFile(file);
                    } else {
                        // this cannot happen
                    }
                }
            }
            });
        clearCycloid = new JButton("Clear");

        // Use JTabbedPane to display both Cycloid and Catenary under separate tabs
        drawPane = new JTabbedPane();
        cycloidPanel = new Cycloid();
        cycloidPanel.setDoubleBuffered(true);
        drawPane.addTab("Cycloid", null, cycloidPanel, "Draws Cycloid");
        catenaryPanel = new Catenary();
        catenaryPanel.setDoubleBuffered(true);
        drawPane.addTab("Catenary", null, catenaryPanel, "Draws Catenary");
        add(drawPane, BorderLayout.CENTER);

        JPanel topPanel = new JPanel(new BorderLayout());
        JPanel topLeftPanel = new JPanel(new BorderLayout());
        topLeftPanel.add(createCycloid, BorderLayout.NORTH);

        // mm/inch radio button
        mmButton = new JRadioButton("mm", true);
        mmButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cycloidPanel.setMetric(Metric.MM);
                catenaryPanel.setMetric(Metric.MM);
            }
            });
        inchButton = new JRadioButton("inch");
        inchButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cycloidPanel.setMetric(Metric.INCH);
                catenaryPanel.setMetric(Metric.INCH);
            }
            });
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
                cycloidPanel.setCycloidWidth(Double.parseDouble(widthText.getText()));
                catenaryPanel.setCatenaryLength(Double.parseDouble(widthText.getText()));
                if (redrawEnabled) {
                    cycloidPanel.repaint();
                    catenaryPanel.repaint();
                }
            }
            public void removeUpdate(DocumentEvent e) {
                // prevent NullPointerException when a user erases all text in the field
                if (widthText.getText().length() == 0) {
                    cycloidPanel.setCycloidWidth(0.00);
                    catenaryPanel.setCatenaryLength(0.00);
                } else {
                    cycloidPanel.setCycloidWidth(Double.parseDouble(widthText.getText()));
                    catenaryPanel.setCatenaryLength(Double.parseDouble(widthText.getText()));
                    if (redrawEnabled) {
                        cycloidPanel.repaint();
                        catenaryPanel.repaint();
                    }
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
                cycloidPanel.setCycloidHeight(Double.parseDouble(heightText.getText()));
                catenaryPanel.setCatenaryDepth(Double.parseDouble(heightText.getText()));
                if (redrawEnabled) {
                    cycloidPanel.repaint();
                    catenaryPanel.repaint();
                }
            }
            public void removeUpdate(DocumentEvent e) {
                if (heightText.getText().length() == 0) {
                    cycloidPanel.setCycloidHeight(0.00);
                    catenaryPanel.setCatenaryDepth(0.00);
                } else {
                    cycloidPanel.setCycloidHeight(Double.parseDouble(heightText.getText()));
                    catenaryPanel.setCatenaryDepth(Double.parseDouble(heightText.getText()));
                    if (redrawEnabled) {
                        cycloidPanel.repaint();
                        catenaryPanel.repaint();
                    }
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
        percentText.setText("110.0");
        percentText.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                cycloidPanel.setPercent(Double.parseDouble(percentText.getText()));
                catenaryPanel.setPercent(Double.parseDouble(percentText.getText()));
                if (redrawEnabled) {
                    cycloidPanel.repaint();
                    catenaryPanel.repaint();
                }
            }
            public void removeUpdate(DocumentEvent e) {
                if (percentText.getText().length() == 0) {
                    cycloidPanel.setPercent(0.00);
                    catenaryPanel.setPercent(0.00);
                } else {
                    cycloidPanel.setPercent(Double.parseDouble(percentText.getText()));
                    catenaryPanel.setPercent(Double.parseDouble(percentText.getText()));
                    if (redrawEnabled) {
                        cycloidPanel.repaint();
                        catenaryPanel.repaint();
                    }
                }
            }
            public void changedUpdate(DocumentEvent e) {}
            });
        percentPanel.add(percentText);
        topRightPanel.add(percentPanel, BorderLayout.CENTER);

        // Caption/Redraw checkboxes
        captionEnable = new JCheckBox("Caption");
        captionEnable.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                captionEnabled = e.getStateChange() == ItemEvent.SELECTED;
            }
            });
        redrawEnable = new JCheckBox("Redraw", null, true);
        redrawEnable.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                redrawEnabled = e.getStateChange() == ItemEvent.SELECTED;
                if (redrawEnabled) {
                    cycloidPanel.repaint();
                    catenaryPanel.repaint();
                }
            }
            });
        JPanel crPanel = new JPanel(new BorderLayout());
        crPanel.add(captionEnable, BorderLayout.NORTH);
        crPanel.add(redrawEnable, BorderLayout.SOUTH);
        topRightPanel.add(crPanel, BorderLayout.SOUTH);

        // auto filename checkbox and textbox and title
        autoFileNameEnable = new JCheckBox("Auto File Name");
        autoFileNameEnable.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                autoFileNameEnabled = e.getStateChange() == ItemEvent.SELECTED;
                // generate filename to use
            }
            });
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
                cycloidPanel.setTitle(titleText.getText());
                catenaryPanel.setTitle(titleText.getText());
            }
            public void removeUpdate(DocumentEvent e) {
                if (titleText.getText().length() == 0) {
                    cycloidPanel.setTitle("");
                    catenaryPanel.setTitle("");
                } else {
                    cycloidPanel.setTitle(titleText.getText());
                    catenaryPanel.setTitle(titleText.getText());
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
        letterSizeButton = new JRadioButton("Letter", true);
        letterSizeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cycloidPanel.setPaper(PaperSize.LETTER);
                catenaryPanel.setPaper(PaperSize.LETTER);
            }
            });
        legalSizeButton = new JRadioButton("Legal");
        legalSizeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cycloidPanel.setPaper(PaperSize.LEGAL);
                catenaryPanel.setPaper(PaperSize.LEGAL);
            }
            });
        a4SizeButton = new JRadioButton("A4");
        a4SizeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cycloidPanel.setPaper(PaperSize.A4);
                catenaryPanel.setPaper(PaperSize.A4);
            }
            });
        a3SizeButton = new JRadioButton("A3");
        a3SizeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cycloidPanel.setPaper(PaperSize.A3);
                catenaryPanel.setPaper(PaperSize.A3);
            }
            });
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
        pdfButton = new JRadioButton("PDF", true);
        pdfButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cycloidPanel.setFormat(Format.PDF);
                catenaryPanel.setFormat(Format.PDF);
            }
            });
        psButton = new JRadioButton("PS");
        psButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cycloidPanel.setFormat(Format.PS);
                catenaryPanel.setFormat(Format.PS);
            }
            });
        dxfButton = new JRadioButton("DXF");
        dxfButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cycloidPanel.setFormat(Format.DXF);
                catenaryPanel.setFormat(Format.DXF);
            }
            });
        csvButton = new JRadioButton("CSV");
        csvButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cycloidPanel.setFormat(Format.CSV);
                catenaryPanel.setFormat(Format.CSV);
            }
            });
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
                cycloidPanel.setScaleWidth(Double.parseDouble(scaleWidthText.getText()));
                catenaryPanel.setScaleWidth(Double.parseDouble(scaleWidthText.getText()));
                if (redrawEnabled) {
                    cycloidPanel.repaint();
                    catenaryPanel.repaint();
                }
            }
            public void removeUpdate(DocumentEvent e) {
                if (scaleWidthText.getText().length() == 0) {
                    cycloidPanel.setScaleWidth(1.00);
                    catenaryPanel.setScaleWidth(1.00);
                } else {
                    cycloidPanel.setScaleWidth(Double.parseDouble(scaleWidthText.getText()));
                    catenaryPanel.setScaleWidth(Double.parseDouble(scaleWidthText.getText()));
                    if (redrawEnabled) {
                        cycloidPanel.repaint();
                        catenaryPanel.repaint();
                    }
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
                cycloidPanel.setScaleHeight(Double.parseDouble(scaleHeightText.getText()));
                catenaryPanel.setScaleHeight(Double.parseDouble(scaleHeightText.getText()));
                if (redrawEnabled) {
                    cycloidPanel.repaint();
                    catenaryPanel.repaint();
                }
            }
            public void removeUpdate(DocumentEvent e) {
                if (scaleHeightText.getText().length() == 0) {
                    cycloidPanel.setScaleHeight(1.00);
                    catenaryPanel.setScaleHeight(1.00);
                } else {
                    cycloidPanel.setScaleHeight(Double.parseDouble(scaleHeightText.getText()));
                    catenaryPanel.setScaleHeight(Double.parseDouble(scaleHeightText.getText()));
                    if (redrawEnabled) {
                        cycloidPanel.repaint();
                        catenaryPanel.repaint();
                    }
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
}
