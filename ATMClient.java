import java.io.*;   
import java.net.*;  
import java.util.Scanner;
import java.util.ArrayList;

/**
 * @author Snilledata
 * @author Sebastian Olsson
 * @version 2011-11-18
 */
public class ATMClient{
    private static int connectionPort = 8989;

    /**
     * Läser in menyn från servern och skriver ut den.
     */
    private static void printMenu(BufferedReader in){ 
        try{
            System.out.println(receive(in));
            System.out.println(receive(in));
            System.out.println(receive(in));
            System.out.println(receive(in));
            System.out.println(receive(in));
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    /**
     * Läser in nuvarande kontosaldot från servern och skriver ut den.
     */
    private static void printBalance(BufferedReader in){ 
        try{
            System.out.println(receive(in));
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    /**
     * Läser in meddelanden från servern om att klienten ska skriva in
     * kontonummer och lösenord.
     */
    private static void printLogin(BufferedReader in,PrintWriter out,Scanner scanner){ 
        String response = null;
        try{
            int accNo;
            response = receive(in);
            boolean invalid = false;
            do{
                if(invalid){
                    System.out.println(response); //Error!
                    response = receive(in);
                }
                System.out.println(response); //Enter account number:
                printPrompt();
                accNo = scanner.nextInt();
                send(""+accNo,out);
                response = receive(in);
                System.out.println(response);
                printPrompt();
                String passwd = scanner.nextLine();
                passwd = scanner.nextLine();
                send(passwd,out);
                response = receive(in);
                invalid = true;
            }while(response.startsWith("Error"));
        }catch(IOException e){
            e.printStackTrace();
        }
        System.out.println(response);
    }

    /**
     * Läser in meddelanden från servern om att klienten ska skriva in
     * engångskoden, och returnerar serverns respons.
     */
    private static void printOTC(BufferedReader in,PrintWriter out,Scanner scanner){ 
        String response = null;
        try{
            int otc;
            response = receive(in);
            boolean invalid = false;
            do{
                if(invalid){
                    System.out.println(response); //Error!
                    response = receive(in);
                }

                System.out.println(response); //Enter one-time-code:
                printPrompt();
                otc = scanner.nextInt();
                send(""+otc,out);
                response = receive(in);
                invalid = true;
            }while(response.startsWith("Error"));
        }catch(IOException e){
            e.printStackTrace();
        }
        System.out.println(response); //Enter amount:
        printPrompt();
        send(""+scanner.nextInt(),out);
    }

    /**
     * Byter språk.
     */
    private static void changeLanguage(BufferedReader in,PrintWriter out,Scanner scanner){ 
        try{
            System.out.println(receive(in)); // Choose lang:
            System.out.println(receive(in)); // (1) English
            System.out.println(receive(in)); // (2) Swedish
            printPrompt();
            send(""+scanner.nextInt(),out);
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    /**
     * Skriver ut inmatningsmarkor.
     */
    private static void printPrompt(){ 
        System.out.print("> ");
    }

    public static void main(String[] args) throws IOException {
        
        Socket ATMSocket = null;
        PrintWriter out = null;
        BufferedReader in = null;
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

        System.out.println("Contacting bank ... ");
        Scanner scanner = new Scanner(System.in);
        printLogin(in,out,scanner);

        System.out.println(receive(in)); //Welcome banner
        System.out.println(receive(in));
        System.out.println(receive(in));
        printMenu(in);

        printPrompt();
        int menuOption = scanner.nextInt();
        int userInput;
        send(""+menuOption,out);
        while(menuOption != 5) {
            if(menuOption == 1) {
                    printBalance(in);
                    printMenu(in);
                    printPrompt();
                    menuOption = scanner.nextInt();
                    send(""+menuOption,out);           
            } else if (menuOption == 4) {
                changeLanguage(in,out,scanner);
                printMenu(in);
                printPrompt();
                menuOption = scanner.nextInt();
                send(""+menuOption,out);           
            } else if (menuOption == 5) { }	
            else if(menuOption == 2 || menuOption == 3){
                printOTC(in,out,scanner); //Enter one-time code:

                System.out.println(receive(in)); //Current balance is
                printMenu(in);
                printPrompt();
                menuOption = scanner.nextInt();
                send(""+menuOption,out);
            }else{
                //System.out.println(in.readLine());
                System.out.println(receive(in));
                printMenu(in);
                printPrompt();
                menuOption = scanner.nextInt();
                send(""+menuOption,out);
            }
        }		
        System.out.println(receive(in)); 
        out.close();
        in.close();
        ATMSocket.close();
    }

    /**
     * Skickar en textsträng till servern; vid behov delas strängen upp i
     * mindre datapaket som skickas en i taget.
     */
    private static void send(String text,PrintWriter out){
        ArrayList<String> packets = new ArrayList<String>();
        String str = text;
        while(str.length() > 8){
            packets.add(str.substring(0,9));
            str = str.substring(9,str.length());
        }
        packets.add(str+'¤');
        for(String packet : packets){
            out.print(packet);
        }
        out.println();
    }

    /**
     * Tar emot en serie av datapaket från servern och använder dessa för
     * att bygga upp en fullständig textsträng.
     */
    private static String receive(BufferedReader in) throws IOException{
        String received = in.readLine();
        String completeString = "";
        while(!received.endsWith("¤")){
            completeString += received;
            received = in.readLine();
        }
        completeString += received;
        return completeString.substring(0,completeString.length()-1);
    }
}   
