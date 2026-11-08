import java.io.*;
import java.util.*;

// Reads aioutput.log and stores it as a variable
public class FOWARD {
    

    //Variable Declaration
    static String list = "";
    String output = "";

  
    public static void main(String[] args) throws Exception{

// Declaring input
        StringBuilder input = new StringBuilder("");

//Reading aioutput.log file
        File file = new File("aioutput.log");

//scaning the file
        Scanner scan = new Scanner(file);
        
        
         while (scan.hasNextLine())
         input.append(scan.nextLine());
//save to output
         list = input.toString();
        
        scan.close();


        String[] listSplit = list.split("[\\(\\)]");

        
        
        






        ArrayList<String> listList = new ArrayList<String>(
            Arrays.asList(listSplit));
                    listList.remove(0);
                    listList.removeIf(s -> s.trim().matches("[,\\s]*"));
                    listList.replaceAll(s -> s.replaceAll("[\"}]+$", "").trim());
        OrganizeText(listList);

        
    new FileWriter("aioutput.log", false).close();  // erases the contents of the file







//run DeleteExtra and reset
   //     DeleteExtra();


    }

    public static void OrganizeText(ArrayList<String> listList) {
    System.out.println(listList);
}

    
//DeleteExtra is going to be called at the end to clear the log, then reset it with ci.autoConfig
 //   public static void DeleteExtra(){
 //  LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory(); 
 //   ContextInitializer ci = new ContextInitializer(lc); 
 //   lc.reset();
 //   ci.autoConfig();
//}
//
}