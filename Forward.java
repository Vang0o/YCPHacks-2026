import java.io.*;
import java.util.Scanner;

public class Forward {

    static String list = "";
    static String output = "";

    public static void main(String[] args) throws FileNotFoundException {
        StringBuilder input = new StringBuilder();
        File file = new File("aioutput.log");

        try (Scanner scan = new Scanner(file)) {
            while (scan.hasNextLine()) {
                input.append(scan.nextLine()).append("\n");
            }
        }
        list = input.toString();

        organizeText();
        deleteExtra();
    }

    public static void organizeText() {
        if (list == null || list.isEmpty()) {
            output = "";
            return;
        }

        String[] lines = list.split("\\R");
        StringBuilder organized = new StringBuilder();

        for (String line : lines) {
            String trimmed = line.trim();
            if (!trimmed.isEmpty()) {
                organized.append(trimmed).append("\n");
            }
        }

        output = organized.toString();
    }

    public static void deleteExtra() {
        try (PrintWriter writer = new PrintWriter("aioutput.log")) {
            writer.print(output);
        } catch (FileNotFoundException e) {
            System.err.println("Unable to write to aioutput.log: " + e.getMessage());
        }
    }
}