import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.*;

public class Main {

    private static final int TILE_SIZE = 120;   // px — image + caption box
    private static final int IMG_SIZE  = 100;   // px — thumbnail inside the tile
    private static final int GAP       = 8;

    //previous exported CSV 
    private static File lastCsvDir = null;

    public static void main(String[] args) {
        // Creates the starting frame 
        JFrame frame = new JFrame("csv analyzer");
        
        // window setup
        frame.setSize(800, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null); 
        frame.setLayout(new BorderLayout());
        frame.getContentPane().setBackground(Color.DARK_GRAY);   

        // Initializes buttons
        JButton importButton = new JButton("Import Image");
        JButton showCSVButton = new JButton("Show .CSV");
        JButton exportButton = new JButton("Export .CSV");

        JPanel contentPanel = new JPanel(new BorderLayout());
        frame.add(contentPanel, BorderLayout.CENTER);

        // Import Button functionality
        importButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            
            fileChooser.setMultiSelectionEnabled(true);
            
            // Restrict the user to selecting only image files
            fileChooser.setFileFilter(new FileNameExtensionFilter("Image Files (JPG, PNG, GIF)", "jpg", "jpeg", "png", "gif"));
            
            
            // Open file window
            if (fileChooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
                File[] selectedFile = fileChooser.getSelectedFiles();
                contentPanel.removeAll();
                JPanel imageContainer = new JPanel(new FlowLayout(FlowLayout.LEFT, GAP, GAP));

            // Load and display the images
            for(File file : selectedFile) {
            imageContainer.add(makeTile(file));

            }
            JScrollPane imageScroll = new JScrollPane(imageContainer);
            imageScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

            contentPanel.add(imageScroll, BorderLayout.CENTER);
            contentPanel.revalidate();
            contentPanel.repaint();

                
                // pass file to ImageAnalysis here

            }
        });
        
         exportButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Save CSV File");
            // Only shows .csv files 
            fileChooser.setFileFilter(new FileNameExtensionFilter("CSV Files (*.csv)", "csv"));

            if (fileChooser.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION) {
                File fileToSave = fileChooser.getSelectedFile();
                String filePath = fileToSave.getAbsolutePath();
                
                if (!filePath.toLowerCase().endsWith(".csv")) {
                    filePath += ".csv";
                }
                
                lastCsvDir = new File(filePath).getParentFile();

                CSVAnalyzer.exportToCSV(filePath);
                JOptionPane.showMessageDialog(frame, "CSV Exported Successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        //displays csv
        showCSVButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Open CSV File");
            fileChooser.setFileFilter(new FileNameExtensionFilter("CSV Files (*.csv)", "csv"));

            if (fileChooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();

            lastCsvDir = selectedFile.getParentFile();
        
            // Read data from CSVAnalyzer
            ArrayList<String[]> csvData = CSVAnalyzer.readCSV(selectedFile.getAbsolutePath());
        
            if (csvData.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "The selected file is empty or could not be read.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Separate headers from the data 
            String[] headers = csvData.get(0);
            String[][] rowData = new String[csvData.size() - 1][headers.length];
        
            for (int i = 1; i < csvData.size(); i++) {
                rowData[i - 1] = csvData.get(i);
            }

            // Create the table and load the data
            DefaultTableModel model = new DefaultTableModel(rowData, headers);
            JTable table = new JTable(model);
            table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
            JScrollPane scrollPane = new JScrollPane(table);


            JLabel preview = new JLabel("Select a row to preview label", SwingConstants.CENTER);
            preview.setPreferredSize(new Dimension(0, 220));
            preview.setBorder(BorderFactory.createTitledBorder("Label image"));
            preview.setBackground(new Color(245, 245, 245));
            preview.setOpaque(true);

            JPanel previewPanel = new JPanel(new BorderLayout());
            previewPanel.add(preview, BorderLayout.CENTER);
            previewPanel.setPreferredSize(new Dimension(0, 240));

            table.getSelectionModel().addListSelectionListener(ev -> {
                if (ev.getValueIsAdjusting()) return;
                int row = table.getSelectedRow();
                if (row < 0) {
                    preview.setIcon(null);
                    preview.setText("Select a row to preview label");
                    return;
                }
                Object idVal = table.getValueAt(row, 0); 
                String id = idVal == null ? "" : idVal.toString().replaceAll("[^a-zA-Z0-9._-]", "_");

                File scanDir = (lastCsvDir != null) ? new File(lastCsvDir, "scans") : new File("scans");
                File img = findImage(scanDir, id);

                if (img != null && img.exists()) {
                    ImageIcon ic = new ImageIcon(img.getAbsolutePath());
                    int maxW = Math.max(preview.getWidth(), 600);
                    int maxH = 200;
                    double s = Math.min((double) maxW / ic.getIconWidth(),
                                        (double) maxH / ic.getIconHeight());
                    int w = (int)(ic.getIconWidth()  * s);
                    int h = (int)(ic.getIconHeight() * s);
                    preview.setIcon(new ImageIcon(ic.getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH)));
                    preview.setText(null);
                } else {
                    preview.setIcon(null);
                    preview.setText("No image found for Sample ID " + id);
                }
            });

    
            JPanel csvView = new JPanel(new BorderLayout());
            csvView.add(scrollPane, BorderLayout.CENTER);
            csvView.add(previewPanel, BorderLayout.SOUTH);

            // clears previous image display 
            contentPanel.removeAll();
            contentPanel.add(csvView, BorderLayout.CENTER);
            contentPanel.revalidate();
            contentPanel.repaint();
            }
        });        


        // bar for the buttons
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        controlPanel.add(importButton);
        controlPanel.add(exportButton);
        controlPanel.add(showCSVButton);
        frame.add(controlPanel, BorderLayout.NORTH);

        // starts window
        frame.setVisible(true);

        
    }

    private static JPanel makeTile(File file) {
        JPanel tile = new JPanel(new BorderLayout(0, 4));
        tile.setPreferredSize(new Dimension(TILE_SIZE, TILE_SIZE + 20));
        tile.setBackground(new Color(245, 245, 245));
        tile.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));

        ImageIcon icon = new ImageIcon(file.getAbsolutePath());
        Image scaled = icon.getImage().getScaledInstance(IMG_SIZE, IMG_SIZE, Image.SCALE_SMOOTH);
        JLabel img = new JLabel(new ImageIcon(scaled));
        img.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel name = new JLabel(file.getName());
        name.setHorizontalAlignment(SwingConstants.CENTER);
        name.setFont(name.getFont().deriveFont(10f));
        name.setToolTipText(file.getName());

        tile.add(img, BorderLayout.CENTER);
        tile.add(name, BorderLayout.SOUTH);
        return tile;
    }

    // Finds a file in scans/ whose basename (minus extension) matches the Sample ID
    private static File findImage(File dir, String id) {
        if (id == null || id.isEmpty()) return null;
        File[] hits = dir.listFiles((d, n) -> {
            String base = n.contains(".") ? n.substring(0, n.lastIndexOf('.')) : n;
            return base.equals(id);
        });
        return (hits != null && hits.length > 0) ? hits[0] : null;
    }
}