import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.*;

public class Main {
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
            
            // Restrict the user to selecting only image files
            fileChooser.setFileFilter(new FileNameExtensionFilter("Image Files (JPG, PNG, GIF)", "jpg", "jpeg", "png", "gif"));
            
            // Open file window
            if (fileChooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();

            // Load and display the image
            ImageIcon imageIcon = new ImageIcon(selectedFile.getAbsolutePath());
            Image scaledImage = imageIcon.getImage().getScaledInstance(-1, -1, Image.SCALE_SMOOTH);

            JLabel imageLabel = new JLabel(imageIcon);
            imageLabel.setHorizontalAlignment(JLabel.CENTER);

            JScrollPane imageScroll = new JScrollPane(imageLabel);

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
            
            // clears any previous table display so they don't stack up
            frame.getContentPane().add(scrollPane, BorderLayout.CENTER);
            frame.revalidate();
            frame.repaint();
            //clear image
            contentPanel.removeAll();
            contentPanel.add(scrollPane, BorderLayout.CENTER);
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
}

