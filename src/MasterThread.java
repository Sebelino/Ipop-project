import java.io.*;
import java.util.*;

/**
 * @author Sebastian Olsson
 * @version 2013-10-27
 */
public class MasterThread extends Thread {

    private Set<ATMServerThread> connections;
    private Set<Game> games;

    /**
     * Skapar en tråd för ändring av bankens välkomstbanner.
     */
    public MasterThread() {
        connections = new HashSet<ATMServerThread>();
    }

    /**
     * Handles the communication between clients.
     */
    public void run(){
        int clientCount = -1;
        while(true){
            Iterator<ATMServerThread> it = connections.iterator();
            while(it.hasNext()){
                ATMServerThread conn = it.next();
                if(!conn.isAlive()){
                    it.remove();
                    continue;
                }
                if(conn.hasRequest()){
                    processRequest(conn);
                }
            }
            System.out.println("NEXT!");
            if(clientCount != connections.size()){
                clientCount = connections.size();
                System.out.println("There are currently "+clientCount+" clients connected.");
            }
            int millisecondDelay = 250;
            try{Thread.sleep(millisecondDelay);}catch(InterruptedException e){}
        }
    }

    private void processRequest(final ATMServerThread conn){
        String request = conn.pollRequest();
        String[] tokens = request.split("\\s+");
        if(tokens.length == 2){
            if(tokens[0].equals("setname")){
                String proposedName = tokens[1];
                if(isAvailable(proposedName)){
                    conn.clientName = proposedName;
                    synchronized(conn){
                        conn.notify();
                    }
                    System.out.println("notified");
                }else{
                    conn.send("error That name is unavailable.");
                    synchronized(conn){
                        conn.notify();
                    }
                }
            }
        }else{
            System.err.println("Could not parse request!");
        }
        System.out.println("exiting processReq");
    }

    /**
     * @return true if no other client uses that name.
     */
    private boolean isAvailable(String name){
        for(ATMServerThread conn : connections){
            if(conn.clientName.equalsIgnoreCase(name)){
                return false;
            }
        }
        return true;
    }

    /**
     * Starts a connection and keeps track of it.
     */
    public void addConnection(ATMServerThread connection){
        connection.start();
        connections.add(connection);
    }
}
