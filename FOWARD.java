import java.io.*;
import java.util.Scanner;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.ContextInitializer;
import org.slf4j.LoggerFactory;

// Reads aioutput.log and stores it as a variable
public class FOWARD {
    

    //list for later and output for next stage
    static String list = "";
    String output = "";

  
    public static void main(String[] args) throws FileNotFoundException{

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





//run DeleteExtra and reset
        DeleteExtra();


    }
    
//Organizes the text and puts it into output
    public static OrganizeText(){
//not yet implemented
    }

    
//DeleteExtra is going to be called at the end to clear the log, then reset it with ci.autoConfig
    public static void DeleteExtra(){
    LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory(); 
    ContextInitializer ci = new ContextInitializer(lc); 
    lc.reset();
    ci.autoConfig();
}

}