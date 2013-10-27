import java.io.*;
import java.util.*;

/**
 * @author Sebastian Olsson
 * @version 2013-10-27
 */
public class MasterThread extends Thread {

    private List<ATMServerThread> connections;

    /**
     * Skapar en tråd för ändring av bankens välkomstbanner.
     */
    public MasterThread() {
        connections = new ArrayList<ATMServerThread>();
    }

    /**
     * Handles the communication between clients.
     */
    public void run(){
        int clientCount = -1;
        while(true){
            if(clientCount != connections.size()){
                clientCount = connections.size();
                System.out.println("There are currently "+clientCount+" clients connected.");
            }
            Iterator<ATMServerThread> it = connections.iterator();
            while(it.hasNext()){
                ATMServerThread conn = it.next();
                if(!conn.isAlive()){
                    System.out.println("Another thread rejected!");
                    it.remove();
                }
            }
            try{Thread.sleep(500);}catch(InterruptedException e){}
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
