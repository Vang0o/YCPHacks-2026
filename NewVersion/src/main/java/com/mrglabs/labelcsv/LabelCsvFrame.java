package com.mrglabs.labelcsv;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;

/**
 * The main application window (Swing).
 *
 * <p>User flow:
 * <ol>
 *   <li>Choose Small or Large tube</li>
 *   <li>Pick / "scan" a label image (file chooser — phone photos work too)</li>
 *   <li>Read with the <b>local</b> AI (or load demo fields)</li>
 *   <li>Edit the seven fields if needed</li>
 *   <li>Add to batch → save a user-named CSV</li>
 * </ol>
 *
 * <p>Beginner tip: a {@link JFrame} is the window; panels inside hold buttons,
 * labels, and text fields. Event listeners ({@code addActionListener}) run
 * when the user clicks.
 */
public class LabelCsvFrame extends JFrame {

    private final LocalVisionClient vision = new LocalVisionClient();
    private final FieldMapper mapper = new FieldMapper();
    private final CsvWriter csvWriter = new CsvWriter();

    private LabelSize labelSize = LabelSize.SMALL;
    private File selectedImage;

    private final JLabel statusLabel = new JLabel("Checking local AI…");
    private final JLabel imageLabel = new JLabel("No image selected.");
    private final JTextArea rawAiArea = new JTextArea(4, 40);
    private final JTextField[] fields = new JTextField[CsvColumns.COUNT];
    private final JLabel[] helpLabels = new JLabel[CsvColumns.COUNT];
    private final DefaultTableModel tableModel =
            new DefaultTableModel(CsvColumns.NAMES, 0);
    private final JRadioButton smallRadio = new JRadioButton("Small tube", true);
    private final JRadioButton largeRadio = new JRadioButton("Large tube");

    /** Build the whole UI. */
    public LabelCsvFrame() {
        super("MRG Label → CSV · Schneider Prize 2026");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(780, 720);
        setLocationByPlatform(true);

        rawAiArea.setEditable(false);
        rawAiArea.setLineWrap(true);
        rawAiArea.setWrapStyleWord(true);

        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        root.add(buildTop(), BorderLayout.NORTH);
        root.add(buildCenter(), BorderLayout.CENTER);
        root.add(buildBottom(), BorderLayout.SOUTH);
        setContentPane(root);

        updateHelps();
        loadDemoIntoFields(); // so Save always has something to start from
        refreshAiStatus();
    }

    /** Banner + tube-size radios + AI status. */
    private JPanel buildTop() {
        JPanel top = new JPanel();
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("MRG Laboratories — Label scanner");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 18f));
        top.add(title);
        top.add(Box.createVerticalStrut(4));
        top.add(new JLabel("Local AI only (Ollama · moondream). No cloud OCR."));

        ButtonGroup group = new ButtonGroup();
        group.add(smallRadio);
        group.add(largeRadio);
        smallRadio.addActionListener(e -> {
            labelSize = LabelSize.SMALL;
            updateHelps();
        });
        largeRadio.addActionListener(e -> {
            labelSize = LabelSize.LARGE;
            updateHelps();
        });
        JPanel radios = new JPanel(new FlowLayout(FlowLayout.LEFT));
        radios.add(smallRadio);
        radios.add(largeRadio);
        top.add(radios);

        statusLabel.setBorder(BorderFactory.createEmptyBorder(4, 0, 4, 0));
        top.add(statusLabel);
        return top;
    }

    /** Image picker, field editors, raw AI text. */
    private JPanel buildCenter() {
        JPanel center = new JPanel(new BorderLayout(8, 8));

        // --- capture / upload row ---
        JButton pickBtn = new JButton("1. Pick / upload label image…");
        pickBtn.addActionListener(e -> pickImage());
        JButton readBtn = new JButton("2. Read with local AI");
        readBtn.addActionListener(e -> readWithLocalAi());
        JButton demoBtn = new JButton("Load demo fields");
        demoBtn.addActionListener(e -> {
            loadDemoIntoFields();
            rawAiArea.setText("(demo — local AI not used)");
        });
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT));
        actions.add(pickBtn);
        actions.add(readBtn);
        actions.add(demoBtn);

        JPanel north = new JPanel(new BorderLayout());
        north.add(actions, BorderLayout.NORTH);
        north.add(imageLabel, BorderLayout.CENTER);
        center.add(north, BorderLayout.NORTH);

        // --- seven editable fields ---
        JPanel form = new JPanel(new GridLayout(CsvColumns.COUNT, 1, 4, 4));
        for (int i = 0; i < CsvColumns.COUNT; i++) {
            JPanel row = new JPanel(new BorderLayout(6, 0));
            JPanel labels = new JPanel(new GridLayout(2, 1));
            labels.add(new JLabel(CsvColumns.NAMES[i]));
            helpLabels[i] = new JLabel(" ");
            helpLabels[i].setFont(helpLabels[i].getFont().deriveFont(Font.ITALIC, 11f));
            labels.add(helpLabels[i]);
            fields[i] = new JTextField();
            row.add(labels, BorderLayout.WEST);
            row.add(fields[i], BorderLayout.CENTER);
            form.add(row);
        }
        center.add(form, BorderLayout.CENTER);

        JPanel raw = new JPanel(new BorderLayout());
        raw.add(new JLabel("Raw local-AI text (for debugging)"), BorderLayout.NORTH);
        raw.add(new JScrollPane(rawAiArea), BorderLayout.CENTER);
        center.add(raw, BorderLayout.SOUTH);
        return center;
    }

    /** Batch table + add/save/clear. */
    private JPanel buildBottom() {
        JPanel bottom = new JPanel(new BorderLayout(8, 8));

        JButton addBtn = new JButton("3. Add row to batch");
        addBtn.addActionListener(e -> addRow());
        JButton saveBtn = new JButton("4. Save CSV…");
        saveBtn.addActionListener(e -> saveCsv());
        JButton clearBtn = new JButton("Clear batch");
        clearBtn.addActionListener(e -> {
            csvWriter.clear();
            tableModel.setRowCount(0);
        });
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttons.add(addBtn);
        buttons.add(saveBtn);
        buttons.add(clearBtn);

        JTable table = new JTable(tableModel);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        bottom.add(buttons, BorderLayout.NORTH);
        bottom.add(new JScrollPane(table), BorderLayout.CENTER);
        bottom.setPreferredSize(new Dimension(760, 200));
        return bottom;
    }

    /** Update the italic hints under each field name (Small vs Large). */
    private void updateHelps() {
        if (labelSize == LabelSize.SMALL) {
            helpLabels[0].setText("Circled Num + GS/NGS");
            helpLabels[1].setText("Date on label");
            helpLabels[2].setText("Today (auto)");
            helpLabels[3].setText("Equip ID");
            helpLabels[4].setText("Sample Point");
            helpLabels[5].setText("Leave blank");
            helpLabels[6].setText("Ref Lube");
        } else {
            helpLabels[0].setText("Barcode or Num");
            helpLabels[1].setText("Date on label");
            helpLabels[2].setText("Today (auto)");
            helpLabels[3].setText("SN");
            helpLabels[4].setText("Leave blank");
            helpLabels[5].setText("Leave blank");
            helpLabels[6].setText("RN");
        }
    }

    /** File chooser = "scan/upload" for a desktop app. */
    private void pickImage() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Choose a sample-tube label photo");
        chooser.setFileFilter(new FileNameExtensionFilter(
                "Images", "png", "jpg", "jpeg", "gif", "bmp", "webp"));
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            selectedImage = chooser.getSelectedFile();
            imageLabel.setText("Image: " + selectedImage.getAbsolutePath());
        }
    }

    /**
     * Call Ollama on a background thread so the UI does not freeze.
     * SwingWorker = "do slow work off the EDT, then update the UI".
     */
    private void readWithLocalAi() {
        if (selectedImage == null) {
            JOptionPane.showMessageDialog(this,
                    "Pick an image first.", "No image", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!vision.isReachable()) {
            JOptionPane.showMessageDialog(this,
                    "Local Ollama is not reachable at 127.0.0.1:11434.\n"
                            + "Start it and run: ollama pull moondream\n"
                            + "Or click \"Load demo fields\".",
                    "Local AI offline",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        statusLabel.setText("Reading label with local " + vision.getModel() + "…");
        // Capture for the worker thread (must be final/effectively final)
        File image = selectedImage;
        LabelSize size = labelSize;

        new SwingWorker<OptionalResult, Void>() {
            @Override
            protected OptionalResult doInBackground() {
                // This runs OFF the UI thread
                var text = vision.readLabel(image, size);
                if (text.isEmpty()) {
                    return new OptionalResult(false, "", mapper.demo(size),
                            "Local AI returned no text — demo fields loaded.");
                }
                SampleRow row = mapper.map(text.get(), size);
                return new OptionalResult(true, text.get(), row, "OK from " + vision.describe());
            }

            @Override
            protected void done() {
                // Back on the UI thread
                try {
                    OptionalResult r = get();
                    fillFields(r.row);
                    rawAiArea.setText(r.rawText.isBlank() ? "(empty)" : r.rawText);
                    statusLabel.setText(r.message);
                    if (!r.ok) {
                        JOptionPane.showMessageDialog(LabelCsvFrame.this, r.message,
                                "Local AI", JOptionPane.INFORMATION_MESSAGE);
                    }
                } catch (Exception ex) {
                    statusLabel.setText("Read failed: " + ex.getMessage());
                }
                refreshAiStatus();
            }
        }.execute();
    }

    private void loadDemoIntoFields() {
        fillFields(mapper.demo(labelSize));
        statusLabel.setText("Demo fields loaded (" + labelSize + "). Edit freely, then Add → Save.");
    }

    private void fillFields(SampleRow row) {
        String[] values = row.toArray();
        for (int i = 0; i < fields.length; i++) {
            fields[i].setText(values[i]);
        }
    }

    private SampleRow readFields() {
        SampleRow row = new SampleRow();
        String[] values = new String[CsvColumns.COUNT];
        for (int i = 0; i < fields.length; i++) {
            values[i] = fields[i].getText();
        }
        row.fromArray(values);
        return row;
    }

    private void addRow() {
        SampleRow row = readFields();
        csvWriter.add(row);
        tableModel.addRow(row.toArray());
    }

    /** Ask for a filename, then write the CSV. */
    private void saveCsv() {
        if (csvWriter.size() == 0) {
            JOptionPane.showMessageDialog(this,
                    "Add at least one row to the batch first.",
                    "Empty batch", JOptionPane.WARNING_MESSAGE);
            return;
        }
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Save CSV as… (choose the file name)");
        chooser.setSelectedFile(new File("mrg-samples.csv"));
        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        File out = chooser.getSelectedFile();
        if (!out.getName().toLowerCase().endsWith(".csv")) {
            out = new File(out.getParentFile(), out.getName() + ".csv");
        }
        try {
            csvWriter.write(out);
            JOptionPane.showMessageDialog(this, "Saved:\n" + out.getAbsolutePath(),
                    "CSV saved", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Save failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refreshAiStatus() {
        if (vision.isReachable()) {
            statusLabel.setText("Local AI ready · " + vision.describe()
                    + " (runs on this laptop, ~1.7GB model)");
        } else {
            statusLabel.setText("Local AI offline · start Ollama + `ollama pull moondream` · demo still works");
        }
    }

    /**
     * Tiny helper object returned from the background OCR worker.
     * (A private record is a short immutable data carrier — Java 16+.)
     */
    private record OptionalResult(boolean ok, String rawText, SampleRow row, String message) {
    }
}
