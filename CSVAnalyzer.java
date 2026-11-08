import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class CSVAnalyzer {

    private static final String[] CSV_HEADERS = {
        "Sample ID", "Date Sampled", "Date Received",
        "Machine", "Component", "Serial Number", "Reference Lube"
    };
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter CSV filename: ");
        String fileName = scanner.nextLine().trim();
        if (!fileName.endsWith(".csv")) { 
            fileName += ".csv"; 
        }
        exportToCSV(fileName);
        scanner.close();
    }

    public static void exportToCSV(String fileName) {
        List<String> scans = ImageAnalysis.getCurrentScans();
        if (scans.isEmpty()) {
            System.out.println("No images imported — nothing to export.");
            return;
        }
        exportToCSV(fileName, scans);
    }

    public static void exportToCSV(String fileName, List<String> rawScans) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(fileName))) {
            pw.println(String.join(",", CSV_HEADERS));

            for (String scan : rawScans) {
                LabelConfig config = LabelFormats.findFor(scan);
                if (config == null) {
                    System.out.println("Unrecognized label format: " + scan);
                    continue;
                }
                pw.println(toCsvRow(processLabel(scan, config)));
            }
            System.out.println("CSV Created Successfully!");
        } catch (IOException e) {
            System.out.println("Error writing file: " + e.getMessage());
        }
    }

    // Process user customized label
    public static String[] processLabel(String scan, LabelConfig config) {
        String sampleId = getValue(scan, config.getSampleId());
        String dateSampled = getValue(scan, config.getDate());
        String machine = getValue(scan, config.getMachine());
        String component = getValue(scan, config.getComponent());
        String serialNumber = getValue(scan, config.getSerialNumber());
        String referenceLube = getValue(scan, config.getReferenceLube());

        String dateReceived = LocalDate.now().format(
            DateTimeFormatter.ofPattern("MM/dd/yyyy")
        );
        return new String[] {
            sampleId,
            dateSampled,
            dateReceived,
            machine,
            component,
            serialNumber,
            referenceLube
        };
    }

    private static String getValue(String scan, String key) {
        if (key == null || !scan.contains(key)) {
            return "";
        }
        int start = scan.indexOf(key) + key.length();
        int end = scan.indexOf("|", start);
        if (end == -1) {
            end = scan.length();
        }
        return scan.substring(start, end).trim();
    }

    // ---- CSV escaping helpers ----

    private static String toCsvRow(String[] fields) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < fields.length; i++) {
            if (i > 0) sb.append(',');
            sb.append(csvEscape(fields[i]));
        }
        return sb.toString();
    }

    private static String csvEscape(String s) {
        if (s == null) return "";
        if (s.indexOf(',') >= 0 || s.indexOf('"') >= 0
                || s.indexOf('\n') >= 0 || s.indexOf('\r') >= 0) {
            return "\"" + s.replace("\"", "\"\"") + "\"";
        }
        return s;
    }

    private static String[] parseCsvLine(String line) {
        List<String> out = new ArrayList<>();
        StringBuilder cur = new StringBuilder();
        boolean inQuotes = false;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (inQuotes) {
                if (c == '"') {
                    if (i + 1 < line.length() && line.charAt(i + 1) == '"') {
                        cur.append('"'); i++;
                    } else {
                        inQuotes = false;
                    }
                } else cur.append(c);
            } else {
                if (c == '"') inQuotes = true;
                else if (c == ',') { out.add(cur.toString()); cur.setLength(0); }
                else cur.append(c);
            }
        }
        out.add(cur.toString());
        return out.toArray(new String[0]);
    }

    // To display CSV in the app
    public static ArrayList<String[]> readCSV(String fileName) {
        ArrayList<String[]> data = new ArrayList<>();

        try (BufferedReader render = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = render.readLine()) != null) {
                data.add(parseCsvLine(line));
            }
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }

        return data;
    }
}