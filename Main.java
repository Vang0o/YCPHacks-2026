//window
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

//window functionality
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

//file manager
import java.io.File;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

public class Main {
    public static void main(String[] args) {
        // Creates the starting frame 
        JFrame frame = new JFrame("csv analyzer");
        
        // window size
        frame.setSize(800, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null); 
        
        
        frame.setLayout(new BorderLayout());

        // Initializes Import button
        JButton importButton = new JButton("Import Image");

        // Import Button functionality
        importButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                
                // Restrict the user to selecting only image files
                FileNameExtensionFilter filter = new FileNameExtensionFilter(
                    "Image Files (JPG, PNG, GIF)", "jpg", "jpeg", "png", "gif"
                );
                
                fileChooser.setFileFilter(filter);
                
                // Open the dialog window
                int result = fileChooser.showOpenDialog(frame);
                
                // If user selected file set it to selectedFile
                if (result == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    //pass file to ImageAnalysis here
                    
                }
            }
        });

        // Add the Import button to the layout (centered)
        frame.add(importButton, BorderLayout.CENTER);

        // starts window
        frame.setVisible(true);

        
    }
}

