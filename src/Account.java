import java.util.ArrayList;
/**
 * Ett konto innehållande kontonummer, lösenord och engångskoder.
 *
 * @author Sebastian Olsson
 * @version 2011-11-18
 */
public class Account{
    private int accountNo;
    private String password;
    private int balance;
    private ArrayList<Integer> oneTimeCodes;
    private String accountFile;

    /**
     * Skapar ett konto utifrån en textfil.
     */
    public Account(String accFile){
        readAccountData(accFile);
    }

    /**
     * Läser in kontodata från den givna textfilen.
     */
    private void readAccountData(String accFile){
        String[] lines = FileManager.readFile(accFile);
        String[] NPB = lines[0].split("\\s+");
        accountNo = Integer.parseInt(NPB[0]);
        password = NPB[1];
        balance = Integer.parseInt(NPB[2]);
        oneTimeCodes = new ArrayList<Integer>();
        for(int i = 1;i < lines.length;i++){
            oneTimeCodes.add(Integer.parseInt(lines[i]));
        }
        String[] pathStrings = accFile.split("/");
        accountFile = pathStrings[pathStrings.length-1];
    }

    /**
     * Returnerar det konto som har det angivna kontonumret lösenordet;
     * eller null om inget sådant konto finns.
     */
    public static Account loginAccount(ArrayList<Account> accounts,int accNo,String passwd){
        Account found = null;
        for(Account acc : accounts){
            if(accNo == acc.accountNo && passwd.equals(acc.password)){
                found = acc;
                break;
            }
        }
        return found;
    }

    /**
     * Lägg till ett belopp till saldot.
     */
    public void addBalance(int amount){
        balance += amount;
    }

    /**
     * Dra av ett belopp från saldot.
     */
    public void subtractBalance(int amount){
        addBalance(-amount);
    }

    /**
     * Ändra saldot.
     */
    public void setBalance(int amount){
        balance = amount;
    }

    /**
     * Returnera saldot.
     */
    public int getBalance(){
        return balance;
    }

    /**
     * Returnera kontonumret.
     */
    public int getAccountNo(){
        return accountNo;
    }

    /**
     * Returnera lösenordet(!).
     */
    public String getPassword(){
        return password;
    }

    /**
     * Returnera engångskoderna.
     */
    public ArrayList<Integer> getOneTimeCodes(){
        return oneTimeCodes;
    }

    /**
     * Om beloppet är negativt, returnera varningsmeddelande; annars,
     * returnera en tom sträng.
     */
    public String warningAmount(int amount){
        if(amount < 0){
            return "Beloppet måste vara icke-negativt!";
        }
        return "";
    }

    /**
     * Ta bort den första engångskoden.
     */
    public void removeFirstOTC(){
        oneTimeCodes.remove(0);
    }

    /**
     * Returnera filnamnet.
     */
    public String getFilename(){
        return accountFile;
    }
}
