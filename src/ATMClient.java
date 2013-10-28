import java.io.*;   
import java.net.*;  
import java.util.Scanner;
import java.util.ArrayList;

/**
 * @author Snilledata
 * @author Sebastian Olsson
 * @version 2013-10-28
 */
public class ATMClient{
    private static int connectionPort = 8989;
    private static PrintWriter out;
    private static BufferedReader in;

    /**
     * Skriver ut inmatningsmarkor.
     */
    private static void printPrompt(){ 
        System.out.print("> ");
    }

    public static void main(String[] args) throws IOException {
        Socket ATMSocket = null;
        out = null;
        in = null;
        String adress = "";

        try {
            //adress = args[0];
            adress = "127.0.0.1";
        } catch (ArrayIndexOutOfBoundsException e) {
            System.err.println("Missing argument ip-adress");
            System.exit(1);
        }
        try {
            ATMSocket = new Socket(adress, connectionPort); 
            out = new PrintWriter(ATMSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader
                                    (ATMSocket.getInputStream()));
        } catch (UnknownHostException e) {
            System.err.println("Unknown host: " +adress);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't open connection to " + adress);
            System.exit(1);
        }

        System.out.println("Contacting game server ... ");
        Scanner scanner = new Scanner(System.in);

        final String welcome = receive();
        System.out.println(welcome);
        for(int i = 0;i < 5;i++){
            printPrompt();
            String input = scanner.nextLine();
            send(input);
            String received = receive();
            System.out.println("Servertråden skickade: "+received);
        }
        scanner.close();
        out.close();
        in.close();
        ATMSocket.close();
        System.out.println("Client signing out.");
    }

    /**
     * Sends a string to the other party.
     */
    private static void send(String text){
        out.println(text);
    }

    /**
     * Receives a string from the other party.
     */
    private static String receive() throws IOException{
        return in.readLine();
    }
}   
