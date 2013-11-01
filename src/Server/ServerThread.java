import java.io.*;
import java.net.*;
import java.util.*;

/**
 * @author Sebastian Olsson
 * @version 2013-10-29
 */
public class ServerThread extends Thread {
    public String clientName;
    private Queue<String> requests;
    private Queue<String> responses;

    public Socket socket = null;
    private BufferedReader in;
    PrintWriter out;

    public ServerThread(Socket socket) {
        super("ServerThread");
        this.socket = socket;
        clientName = "";
        requests = new LinkedList<String>();

        //Thread thread = new Thread(){
        //    public void run(){
        //        try{
        //            while(responses.isEmpty()){
        //                System.out.println("yo")
        //            }
        //        }catch(InterruptedException e){
        //            e.printStackTrace();
        //        }
        //    }
        //};
    }

    /**
     * Communicates with the client.
     */
    public void run(){
        try {
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader
                (new InputStreamReader(socket.getInputStream()));

            sendReg("entername");
            printComm("sendReg","entername");
            while(clientName.isEmpty()){
                String name = null;
                try{
                    name = receiveReg();
                }catch(NullPointerException e){
                    printComm("receiveReg",null);
                    System.err.println("The client sent a null pointer for the name. "+
                            "Probably because it disconnected.");
                    return;
                }
                if(name.equals("-")){
                    printComm("receiveReg","-");
                    printComm("sendReg","error Your name cannot consist of just a hyphen.");
                }else if(name.isEmpty()){
                    printComm("receiveReg","");
                    continue;
                }else{
                    String christenRequest = "setname "+name;
                    requests.add(christenRequest);
                    putToSleep();
                }
            }
            sendReg("ok"); // ACK the name.
            printComm("sendReg","ok");
            boolean requestsExit = false;
            while(!requestsExit){
                String command = receiveReg();
                String[] tokens = command.split("\\s+");
                if(command.isEmpty()){ // Received whitespace.
                    printComm("receiveReg","");
                    continue;
                }else if(command.equals("exit")){
                    printComm("receiveReg","exit");
                    return;
                }else if(command.equals("list")){
                    requests.add(command);
                    putToSleep();
                }else if(tokens.length == 2 && tokens[0].equals("host")){
                    requests.add(command);
                    putToSleep();
                }else if(tokens.length == 2 && tokens[0].equals("join")){
                    requests.add(command);
                    putToSleep();
                }else{
                    throw new ProtocolException("Request from client was not recognized.");
                }
            }
        }catch (IOException e){
            e.printStackTrace();
        }catch(ProtocolException e){
            e.printStackTrace();
        }
        try{
            out.close();
            in.close();
            socket.close();
        }catch (IOException e){
            e.printStackTrace();
        }
        System.out.println("Server thread signing out.");
    }

    public void printComm(String direction,String msg){
        if(!direction.equals("sendReg") && !direction.equals("receiveReg")){
            throw new IllegalArgumentException("Internal printing method screwed up.");
        }
        if(msg == null){
            System.out.println(direction+" null");
        }else{
            System.out.println(direction+" \""+msg+"\"");
        }
    }

    /**
     * Makes this thread sleep until awakened.
     */
    public void putToSleep(){
        try {
            synchronized(this) {
                this.wait(); // TODO: Rykten om att den här är otillförlitlig.
            }
        }catch(InterruptedException e){
            e.printStackTrace();
        }
    }

    /**
     * Sends a packet to the client.
     */
    public void send(String text){
        out.println(text);
    }

    /**
     * Sends a packet to the client, including a prepended token "reg".
     */
    public void sendReg(String text){
        send("reg "+text);
    }

    /**
     * Receives a packet from the client.
     * @return The content except for the first token "reg".
     * @throws ProtocolException if the message does not start with "reg".
     */
    public String receiveReg() throws ProtocolException,IOException{
        String input = receive();
        String[] tokens = input.split("\\s+");
        if(!tokens[0].equals("reg")){
            throw new ProtocolException("Client missed to prepend the string with reg/irr.");
        }
        String output = "";
        for(int i = 0;i < tokens.length;i++){
            output += tokens[i];
        }
        return output.trim();
    }

    /**
     * Receives a packet from the client.
     * @return The contents, trimmed.
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
