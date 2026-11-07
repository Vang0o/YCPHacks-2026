import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class CSVAnalyzer {
    // Keep your original main method exactly as it is for console testing
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter CSV filename: ");
        String fileName = scanner.nextLine().trim();
        if (!fileName.endsWith(".csv")) { fileName += ".csv"; }
        
        
        exportToCSV(fileName);
        scanner.close();
    }
    //added to export to Main
    public static void exportToCSV(String fileName) {
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("MM/dd/yyyy"));

        try (PrintWriter pw = new PrintWriter(new FileWriter(fileName))) {
            pw.println("Sample ID,Date Sampled,Date Received,Machine,Component,Serial Number,Reference Lube");

            //scan1 and scan2 need to be changed in someway
            String scan1 = "TYPE:LARGE | ID:53279869 | DATE:09/28/2026 | EQUIP:53279869 | POINT:420 NPF | LUBE:Adjusting side";
            String scan2 = "TYPE:SMALL | ID:111329   | DATE:09/28/2026 | RN:UP 6509     | COMP:3 + GS    | SN:SN-88321";
            
            String[] rawScans = {scan1, scan2};

            for (String scan : rawScans) {
                String sampleId = getValue(scan, "ID:");
                String dateSampled = getValue(scan, "DATE:");
                String dateReceived = today;
                String machine = "";
                String component = "";
                String serialNumber = "";
                String referenceLube = "";

                if (scan.contains("TYPE:LARGE")) {
                    machine = getValue(scan, "EQUIP:");
                    component = getValue(scan, "POINT:");
                    referenceLube = getValue(scan, "LUBE:");
                } 
                else if (scan.contains("TYPE:SMALL")) {
                    machine = getValue(scan, "RN:");
                    component = getValue(scan, "COMP:");
                    serialNumber = getValue(scan, "SN:");
                }

                pw.println(sampleId + "," + dateSampled + "," + dateReceived + "," + 
                           machine + "," + component + "," + serialNumber + "," + referenceLube);
            }
            System.out.println("CSV Created Successfully!");
        } catch (IOException e) {
            System.out.println("Error writing file: " + e.getMessage());
        }
    }

    private static String getValue(String scan, String key) {
        if (!scan.contains(key)) return "";
        int start = scan.indexOf(key) + key.length();
        int end = scan.indexOf("|", start);
        if (end == -1) end = scan.length(); 
        return scan.substring(start, end).trim();
    }
    //to display CSV in the app
    public static ArrayList<String[]> readCSV(String fileName) {
    ArrayList<String[]> data = new ArrayList<>();
    
    try (BufferedReader render = new BufferedReader(new FileReader(fileName))) {
        String line;
        while ((line = render.readLine()) != null) {
            data.add(line.split(",", -1));
        }
    } catch (IOException e) {
        System.out.println("Error reading file: " + e.getMessage());
    }
    
    return data;
}


}
