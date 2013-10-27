import java.io.*;
import java.util.*;

/**
 * @author Sebastian Olsson
 * @version 2011-11-18
 */
public class BannerThread extends Thread {

    private List<ATMServerThread> connections;

    /**
     * Skapar en tråd för ändring av bankens välkomstbanner.
     */
    public BannerThread() {
        connections = new ArrayList<ATMServerThread>();
    }

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

    /**
     * Starts a connection and keeps track of it.
     */
    public void addConnection(ATMServerThread connection){
        connection.start();
        connections.add(connection);
    }
}
