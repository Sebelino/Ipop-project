import java.io.*;
import java.net.*;
import java.util.ArrayList;

/**
 * @author Viebrapadata
 * @author Sebastian Olsson
 * @version 2011-11-18
 */
public class ATMServerThread extends Thread {
    private static final String ACCOUNTS_PATH = "server/accounts/";
    private static final String LANG_PATH = "server/languages/";
    private static final String SWEDISH = "svenska.txt";
    private static final String ENGLISH = "english.txt";
    private static final String ACC1 = "acc1.txt";
    private static final String ACC2 = "acc2.txt";
    private static final String ACC3 = "acc3.txt";
    static String banner = null;

    private Socket socket = null;
    private BufferedReader in;
    PrintWriter out;
    private ArrayList<Account> accounts;
    private Language[] langs;

    public ATMServerThread(Socket socket) {
        super("ATMServerThread");
        this.socket = socket;
        loadAccounts();
        loadLangs();
    }

    /**
     * Laddar in konton.
     */
    private void loadAccounts(){
        accounts = new ArrayList<Account>();
        String[] files = new String[]{ACC1,ACC2,ACC3};
        for(String file : files){
            accounts.add(new Account(ACCOUNTS_PATH+file));
        }
    }

    /**
     * Laddar in sprak.
     */
    private void loadLangs(){
        String[] files = new String[]{ENGLISH,SWEDISH};
        langs = new Language[files.length];
        for(int i = 0;i < files.length;i++){
            langs[i] = new Language(LANG_PATH+files[i]);
        }
    }

    /**
     * Läs in textrad från servern.
     */
    private String readLine() throws IOException {
        String str = in.readLine();
        //System.out.println(""  + socket + " : " + str);
        return str;
    }

    /**
     * Låter klienten skriva in kontonummer och lösenord.
     */
    private Account login(Language lang) throws IOException{
        Account acc;
        boolean invalid = false;
        int accNo;
        do{
            if(invalid){
                send(lang.errorInvalidLogin);
            }
            send(lang.enterAccNo);
            accNo = Integer.parseInt(receive());
            send(lang.enterPassword);
            String passwd = receive();
            acc = Account.loginAccount(accounts,accNo,passwd);
            invalid = true;
        }while(acc == null);
        send("");
        return acc;
    }

    /**
     * Later klienten skriva in engangskod anda tills den matar in ratt kod.
     */
    private void enterOTC(Account acc,Language lang) throws IOException{
        boolean invalid = false;
        int otc;
        do{
            if(invalid){
                send(lang.errorInvalidOTC);
            }
            send(lang.enterOTC);
            otc = Integer.parseInt(receive());
            invalid = true;
        }while(otc != acc.getOneTimeCodes().get(0));
        acc.removeFirstOTC();
    }

    /**
     * Skriver ut menyn for klienten.
     */
    private void printMenu(Language lang) throws IOException{
        send(lang.menuBalance);
        send(lang.menuWithdrawal);
        send(lang.menuDesposit);
        send(lang.menuLanguage);
        send(lang.menuExit);
    }

    /**
     * Kommunicerar med klienten.
     */
    public void run(){
         
        try {
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader
                (new InputStreamReader(socket.getInputStream()));

            String inputLine, outputLine;

            Language lang = langs[1];
            if(banner == null){
                banner = lang.welcome2;
            }
            Account account = login(lang);

            int value;
            int balance = account.getBalance();

            lang.welcome2 = banner;
            send(lang.welcome1); 
            send(lang.welcome2); 
            send(lang.welcome3); 
            printMenu(lang);

            inputLine = receive();
            int choice = Integer.parseInt(inputLine);
            while (choice != 5) {
                int deposit = 1;
                switch (choice) {
                case 2:
                    deposit = -1;
                case 3:
                    enterOTC(account,lang);
                    send(lang.enterAmount);	
                    inputLine = receive();
                    value = Integer.parseInt(inputLine);
                    balance += deposit * value;
                    account.setBalance(balance);

                case 1:
                    send(lang.currentBalance+" "+balance+" "+lang.dollars);
                    printMenu(lang);
                    inputLine = receive();
                    choice = Integer.parseInt(inputLine);
                    break;
                case 4:
                    lang = changeLanguage(lang);
                    printMenu(lang);
                    inputLine = receive();
                    choice = Integer.parseInt(inputLine);
                    break;
                case 5:
                    break;
                default: 
                    send(lang.errorInvalidOption);
                    printMenu(lang);
                    inputLine = receive();
                    choice = Integer.parseInt(inputLine);
                    break;
                }
            }
            saveAccount(account);
            send(lang.bye);
            out.close();
            in.close();
            socket.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    /**
     * Skickar en textsträng till klienten; vid behov delas strängen upp i
     * mindre datapaket som skickas en i taget.
     */
    private void send(String text){
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
     * Tar emot en serie av datapaket från klienten och bygger upp dem
     * till en fullständig textsträng.
     */
    private String receive() throws IOException{
        String received = readLine();
        String completeString = "";
        while(!received.endsWith("¤")){
            completeString += received;
            received = readLine();
        }
        completeString += received;
        return completeString.substring(0,completeString.length()-1);
    }

    /**
     * Skriver ut en lista med språkalternativ och låter klienten välja
     * språk.
     */
    private Language changeLanguage(Language lang){
        send(lang.chooseLang);
        send(lang.english);
        send(lang.swedish);
        boolean invalid = false;
        int option = -1;
        try{
            do{
                if(invalid){
                    send(lang.errorInvalidLang);
                }
                option = Integer.parseInt(receive());
                invalid = true;
            }while(option <= 0 || option >= 3);
        }catch (IOException e){
            e.printStackTrace();
        }
        return langs[option-1];
    }

    /**
     * Sparar kontoändringarna i lämplig textfil.
     */
    private void saveAccount(Account account){
        int balance = account.getBalance();
        int accountNo = account.getAccountNo();
        String password = account.getPassword();
        String firstLine = accountNo+" "+password+" "+balance+"\n";
        String filename = account.getFilename();
        ArrayList<Integer> otcs = account.getOneTimeCodes();
        try{
            BufferedWriter bw = new BufferedWriter(new FileWriter(ACCOUNTS_PATH+filename));
            bw.write(firstLine);
            for(Integer otc : otcs){
                bw.write(otc+"\n");
            }
            bw.close();
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }

}
