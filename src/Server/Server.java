import java.net.*;
import java.io.*;
import java.util.*;

/**
   @author Viebrapadata
*/
public class Server {

    private static int connectionPort = 8989;

    private static MasterThread gamingThread;

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = null;
       
        boolean listening = true;
        
        try {
            serverSocket = new ServerSocket(connectionPort); 
        } catch (IOException e) {
            System.out.println("Could not listen on port: " + connectionPort);
            System.exit(1);
        }
	
        System.err.println("Creating master thread.");
        gamingThread = new MasterThread();
        gamingThread.start();

        System.out.println("Game server started listening on port: " + connectionPort);
        while(listening){
            System.out.println("im listening");
            ServerThread connection = new ServerThread(serverSocket.accept());
            gamingThread.addConnection(connection);
            //connection.start();
            System.err.println("Client thread created.");
        }

        System.err.println("closing socket");
        serverSocket.close();
    }
}
