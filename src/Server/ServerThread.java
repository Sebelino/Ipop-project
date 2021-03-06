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
    public Game game;

    public Socket socket;
    private BufferedReader in;
    PrintWriter out;

    public ServerThread(Socket socket) {
        super("ServerThread");
        this.socket = socket;
        game = null;
        clientName = "";	//null istället, kanske?
        requests = new LinkedList<String>();

        //Thread thread = new Thread(){ //BEHÖVS INTE; gör bara send från mastertråden
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
            //printComm("sendReg","entername");
            while(clientName.isEmpty()){
                String name = null;
                try{
                    name = receive();
                }catch(NullPointerException e){
                    printComm("receive",null);
                    System.out.println("The client sent a null pointer for the name. "+
                            "Probably because it disconnected.");
                    return;
                }
                if(name.equals("-")){
                    printComm("receive","-");
                    printComm("sendReg","error Your name cannot consist of just a hyphen.");
                }else if(name.isEmpty()){
                    printComm("receive","");
                    continue;
                }else{
                    String christenRequest = "setname "+name;
                    requests.add(christenRequest);
                    putToSleep();
                }
            }
            sendReg("ok"); // ACK the name.
            //printComm("sendReg","ok");
            boolean requestsExit = false;
            while(!requestsExit){
            	String command = "";
            	try {
                	command = receive();
                } catch (Exception e) {
                	System.out.println("A client seems to have disconnected!");
                	return;
                }
                String[] tokens = command.split("\\s+");
                if(command.isEmpty()){ // Received whitespace.
                    printComm("receive","");
                    continue;
                }else if(command.equals("exit")){
                    requests.add("exit");
                    putToSleep();
                    System.out.println("we are back!!!!!!!!!!");
                    //return; //TODO
                }else if(command.equals("list")){
                    requests.add(command);
                    putToSleep();
                }else if(tokens.length == 2 && tokens[0].equals("host")){
                    requests.add(command);
                    putToSleep();
                }else if(tokens.length == 2 && tokens[0].equals("join")){
                    requests.add(command);
                    putToSleep();
                }else if(tokens.length >= 3 && tokens[0].equals("move")){
                    requests.add(command);
                    putToSleep();
                }else if(tokens.length >= 3 && tokens[0].equals("chat")){
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
        }catch (NullPointerException e){
            e.printStackTrace();
        }
        System.out.println("Server thread signing out.");
    }

    public void printComm(String direction,String msg){
        if(!direction.equals("sendReg")
                && !direction.equals("sendIrr")
                && !direction.equals("receive")){
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
    private void send(String text){
        out.println(text);
    }

    /**
     * Sends a packet to the client, including a prepended token "reg".
     */
    public void sendReg(String text){
        send("reg "+text);
        printComm("sendReg", text);
    }

    /**
     * Sends a packet to the client, including a prepended token "irr".
     */
    public void sendIrr(String text){
        send("irr "+text);
        printComm("sendIrr", text);
    }

    /**
     * Receives a packet from the client.
     * @return The contents, trimmed.
     */
    private String receive() throws IOException{
        return in.readLine().trim();
    }

    /**
     * @return true iff the client has made a request.
     */
    public boolean hasRequest(){
        return !requests.isEmpty();
    }

	/**
     * @return true iff the client is in a game
     */
    public boolean isInGame(){
        return (game != null) ? true : false;
    }

    /**
     * @return A request from the client.
     */
    public String pollRequest(){
        return requests.poll();
    }
}
