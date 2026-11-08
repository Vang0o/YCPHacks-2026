import java.io.*;
import java.util.*;

public class FOWARD {

    static String list = "";

    public static void main(String[] args) throws Exception {
        StringBuilder input = new StringBuilder();
        File file = new File("aioutput.log");

        if (!file.exists()) {
            System.out.println("aioutput.log not found");
            return;
        }

        try (Scanner scan = new Scanner(file)) {
            while (scan.hasNextLine()) {
                input.append(scan.nextLine());
            }
        }
        list = input.toString();

        String[] listSplit = list.split("[\\(\\)]");
        ArrayList<String> listList = new ArrayList<>(Arrays.asList(listSplit));

        if (!listList.isEmpty()) {
            listList.remove(0);
        }
        listList.removeIf(s -> s.trim().matches("[,\\s]*"));
        listList.replaceAll(s -> s.replaceAll("[\"}]+$", "").trim());

        OrganizeText(listList);

        // Clear the log file
        new FileWriter("aioutput.log", false).close();
    }

    public static void OrganizeText(ArrayList<String> listList) {
        System.out.println(listList);
    }
}
