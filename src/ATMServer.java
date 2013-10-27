import java.net.*;
import java.io.*;
import java.util.*;

/**
   @author Viebrapadata
*/
public class ATMServer {

    private static int connectionPort = 8989;
    private static boolean bannerThreadOn = true;

    //private static Map<String,ATMServerThread> connections;
    //private static List<ATMServerThread> connections;
    private static MasterThread gamingThread;

    public static void main(String[] args) throws IOException {
        System.err.println("entering main");
        
        ServerSocket serverSocket = null;
       
        boolean listening = true;
        
        try {
            serverSocket = new ServerSocket(connectionPort); 
        } catch (IOException e) {
            System.out.println("Could not listen on port: " + connectionPort);
            System.exit(1);
        }
	
        if(bannerThreadOn){
            System.err.println("creating banner thread");
            gamingThread = new MasterThread();
            gamingThread.start();
            bannerThreadOn = false;
        }
        System.out.println("Game server started listening on port: " + connectionPort);
        while(listening){
            System.out.println("im listening");
            ATMServerThread connection = new ATMServerThread(serverSocket.accept());
            gamingThread.addConnection(connection);
            //connection.start();
            System.err.println("Client thread created.");
        }

        System.err.println("closing socket");
        serverSocket.close();
    }
}
