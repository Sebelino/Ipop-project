import java.io.*;
import java.util.Scanner;

/**
 * @author Sebastian Olsson
 * @version 2011-11-18
 */
public class BannerThread extends Thread {

    /**
     * Skapar en tråd för ändring av bankens välkomstbanner.
     */
    public BannerThread() { }

    /**
     * Kör igång tråden.
     */
    public void run(){
        Scanner scanner = new Scanner(System.in);
        while(true){
            System.out.println("Change banner to:");
            System.out.print("> ");
            String banner = scanner.nextLine();
            ATMServerThread.banner = banner;
        }
    }
}
