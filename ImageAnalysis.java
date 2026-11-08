import java.io.File;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ImageAnalysis {

    private static final ArrayList<String> currentScans = new ArrayList<>();

    // Tells java Where ai.py lives. 
    private static final String PYTHON_SCRIPT = "ai_analysis (1).py";
    private static final String PYTHON_EXE = "/home/vang1/HackYCP/YCPHacks-2026/venv/bin/python3";  
    //private static final String PYTHON_EXE = "python3"; //change to your local python directiory if no work
    private static final String MODEL_NAME = "lfm3-omni";  // change depending on the model you want to use

    public static String analyze(File imageFile) {
        try {
            ProcessBuilder pb = new ProcessBuilder(
                PYTHON_EXE, PYTHON_SCRIPT,
                MODEL_NAME,
                "--image", imageFile.getAbsolutePath()
            );
            pb.redirectErrorStream(true); 

            Process proc = pb.start();

            // ai.py prints one JSON line per file: {"file":"...","response":"..."}
            String scanLine = null;
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(proc.getInputStream()))) {
                String line;
                while ((line = br.readLine()) != null) {
                    line = line.trim();
                    if (line.startsWith("{")) {          // the JSON result
                        System.out.println("RAW JSON LINE: " + line);
                        scanLine = extractResponse(line);
                    } else {
                        System.out.println("[ai.py] " + line);  
                    }
                }
            }

            int exit = proc.waitFor();
            if (exit != 0) {
                System.err.println("ai.py exited with " + exit + " for " + imageFile);
            }

            return scanLine;

        } catch (Exception e) {
            System.err.println("ImageAnalysis failed for " + imageFile + ": " + e.getMessage());
            return null;
        }
    }

    // JSON extractor 
    private static String extractResponse(String json) {
    // Find "response" key, skip whitespace, expect ':', skip whitespace, expect '"'
    java.util.regex.Matcher m = java.util.regex.Pattern
        .compile("\"response\"\\s*:\\s*\"((?:[^\"\\\\]|\\\\.)*)\"")
        .matcher(json);
    if (!m.find()) return null;
    String raw = m.group(1);
    // Unescape JSON string escapes
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < raw.length(); i++) {
        char c = raw.charAt(i);
        if (c == '\\' && i + 1 < raw.length()) {
            char n = raw.charAt(++i);
            switch (n) {
                case 'n': sb.append('\n'); break;
                case 't': sb.append('\t'); break;
                case 'r': sb.append('\r'); break;
                case '"': sb.append('"'); break;
                case '\\': sb.append('\\'); break;
                case '/': sb.append('/'); break;
                case 'u':
                    if (i + 4 < raw.length()) {
                        sb.append((char) Integer.parseInt(raw.substring(i+1, i+5), 16));
                        i += 4;
                    }
                    break;
                default: sb.append(n);
            }
        } else {
            sb.append(c);
            }
    }
    return sb.toString().trim();
}

    public static void analyzeAll(File[] files) {
        currentScans.clear();
        for (File f : files) {
            String scan = analyze(f);
            if (scan != null && !scan.isBlank()) {
                currentScans.add(scan);
            }
        }
    }

    public static List<String> getCurrentScans() {
        return Collections.unmodifiableList(currentScans);
    }

    public static void clear() { currentScans.clear(); }
}