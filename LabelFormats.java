import java.io.*;
import java.nio.file.*;
import java.util.*;

public class LabelFormats {

    private static final String FILE = "label_formats.txt";
    private static List<LabelConfig> formats = new ArrayList<>();

    static { load(); }

    public static List<LabelConfig> getAll() { return formats; }

    public static LabelConfig findFor(String scan) {
        for (LabelConfig c : formats)
            if (c.getType() != null && scan.contains(c.getType())) return c;
        return null;    
    }

    public static void load() {
        formats.clear();
        try {
            File f = new File(FILE);
            if (!f.exists()) {
                Files.writeString(f.toPath(),
                    "Large label,TYPE:LARGE,ID:,DATE:,EQUIP:,POINT:,,LUBE:\n" +
                    "Small label,TYPE:SMALL,ID:,DATE:,RN:,COMP:,SN:,\n");
            }
            for (String line : Files.readAllLines(f.toPath())) {
                if (line.isBlank() || line.startsWith("#")) continue;
                String[] p = line.split(",", -1);
                if (p.length < 8) continue;
                formats.add(new LabelConfig(p[0].trim(), blank(p[1]), blank(p[2]), blank(p[3]),
                    blank(p[4]), blank(p[5]), blank(p[6]), blank(p[7])));
            }
        } catch (IOException e) {
            System.err.println("Label config error: " + e.getMessage());
        }
    }

    public static void save(String text) throws IOException {
        Files.writeString(Paths.get(FILE), text);
        load();
    }

    private static String blank(String s) {
        return (s == null || s.isBlank()) ? null : s.trim();
    }
}