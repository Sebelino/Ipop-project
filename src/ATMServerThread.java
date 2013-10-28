import java.io.*;
import java.net.*;
import java.util.*;

/**
 * @author Viebrapadata
 * @author Sebastian Olsson
 * @version 2013-10-28
 */
public class ATMServerThread extends Thread {
    public String clientName;
    private Queue<String> requests;
    private boolean clientIsRunning;

    private Socket socket = null;
    private BufferedReader in;
    PrintWriter out;

    public ATMServerThread(Socket socket) {
        super("ATMServerThread");
        this.socket = socket;
        clientName = "";
        requests = new LinkedList<String>();
        clientIsRunning = true;
    }

    /**
     * Communicates with the client.
     */
    public void run(){
        try {
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader
                (new InputStreamReader(socket.getInputStream()));

            send("entername");
            while(clientName.isEmpty()){
                String name = null;
                try{
                    name = receive();
                }catch(NullPointerException e){
                    return;
                }
                if(name.equals("-")){
                    send("error Your name cannot consist of just a hyphen.");
                }else if(name.isEmpty()){
                    continue;
                }else{
                    String christenRequest = "setname "+name;
                    requests.add(christenRequest);
                    try {
                        synchronized(this) {
                            while (true) {
                                this.wait();
                            }
                        }
                    }catch(InterruptedException e){
                        e.printStackTrace();
                    }
                }
            }
            out.close();
            in.close();
            socket.close();
        }catch (IOException e){
            e.printStackTrace();
        }
        System.out.println("Server thread signing out.");
    }

    /**
     * Sends a packet to the client.
     */
    public void send(String text){
        out.println(text);
    }

    /**
     * Receives a packet from the client.
     */
    public String receive() throws IOException{
        return in.readLine().trim();
    }

    /**
     * @return true iff the client has made a request.
     */
    public boolean hasRequest(){
        return !requests.isEmpty();
    }

    /**
     * @return A request from the client.
     */
    public String pollRequest(){
        return requests.poll();
    }
}
