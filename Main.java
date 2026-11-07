import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.awt.*;
import java.awt.event.*;
import java.io.File;

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

        // Import Button functionality
        importButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            
            // Restrict the user to selecting only image files
            fileChooser.setFileFilter(new FileNameExtensionFilter("Image Files (JPG, PNG, GIF)", "jpg", "jpeg", "png", "gif"));
            
            // Open file window
            if (fileChooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                // pass file to ImageAnalysis here

            }
        });
        
        //showCSVButton.addActionListener(null);

        //exportButton.addActionListener(null);
        
        //bar for the button
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        controlPanel.add(importButton);
        controlPanel.add(showCSVButton);
        controlPanel.add(exportButton);
        frame.add(controlPanel, BorderLayout.NORTH);

        // starts window
        frame.setVisible(true);

        
    }
}

