import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.*;

public class CSVAnalyzer {

    private static final String[] CSV_HEADERS = {
        "Sample ID",
        "Date Sampled",
        "Date Received",
        "Machine",
        "Component",
        "Serial Number",
        "Reference Lube",
        "Label Type"
    };

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
                System.out.println("RAW SCAN: " + scan);
                String[] row = mapToCsvRow(scan);
                if (row != null) {
                    pw.println(toCsvRow(row));
                } else {
                    System.out.println("Unrecognized label format: " + scan);
                }
            }
            System.out.println("CSV Created Successfully!");
        } catch (IOException e) {
            System.out.println("Error writing file: " + e.getMessage());
        }
    }

    private static String[] mapToCsvRow(String scan) {
        if (scan == null || scan.isBlank()) return null;

        boolean isLarge = isLargeLabel(scan);
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("MM/dd/yyyy"));

        String sampleId    = extract(scan, "SampleID", "Sample ID");
        String dateSampled = extract(scan, "Date Sampled", "Date");

        String machine;
        String component;
        String serialNumber;
        String referenceLube;

        if (isLarge) {
            machine       = extractNested(scan, "Machine", "Equip ID");
            component     = extract(scan, "Sample Point", "Point");
            serialNumber  = "";
            referenceLube = extract(scan, "Reference Lube", "Lube");
        } else {
            machine       = extractNested(scan, "Machine", "RN");
            component     = extract(scan, "Reference Lube", "GS", "NGS");
            serialNumber  = extract(scan, "Serial Number", "SN");
            referenceLube = "";
        }

        if (serialNumber.toUpperCase().startsWith("SN:")) {
            serialNumber = serialNumber.substring(3).trim();
        }

        return new String[] {
            sampleId,
            dateSampled,
            today,
            machine,
            component,
            serialNumber,
            referenceLube,
            isLarge ? "Large" : "Small"
        };
    }

    private static boolean isLargeLabel(String scan) {
        String lower = scan.toLowerCase();
        return lower.contains("equip id");
    }

    private static String extractNested(String text, String outer, String inner) {
        Pattern p = Pattern.compile(
            "\\(" + Pattern.quote(outer) + "\\s*:\\s*" + Pattern.quote(inner) + "\\s*:\\s*([^)]+)\\)",
            Pattern.CASE_INSENSITIVE
        );
        Matcher m = p.matcher(text);
        if (m.find()) return m.group(1).trim();
        return extract(text, outer);
    }

    private static String extract(String text, String... keys) {
        for (String key : keys) {
            Pattern p = Pattern.compile(
                "\\(" + Pattern.quote(key) + "\\s*:\\s*([^)]+)\\)",
                Pattern.CASE_INSENSITIVE
            );
            Matcher m = p.matcher(text);
            if (m.find()) return m.group(1).trim();
        }
        return "";
    }

    private static String toCsvRow(String[] fields) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < fields.length; i++) {
            if (i > 0) sb.append(',');
            sb.append(csvEscape(fields[i] == null ? "" : fields[i]));
        }
        return sb.toString();
    }

    private static String csvEscape(String s) {
        if (s == null) return "";
        if (s.indexOf(',') >= 0 || s.indexOf('"') >= 0 || s.indexOf('\n') >= 0 || s.indexOf('\r') >= 0) {
            return "\"" + s.replace("\"", "\"\"") + "\"";
        }
        return s;
    }

    public static ArrayList<String[]> readCSV(String fileName) {
        ArrayList<String[]> data = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                data.add(parseCsvLine(line));
            }
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
        return data;
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
}