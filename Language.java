import java.util.ArrayList;
/**
 * Textstrangar som servern anvander vid kontakt med klienten.
 *
 * @author Sebastian Olsson
 * @version 2011-11-18
 */
public class Language{
    String welcome1;
    String welcome2;
    String welcome3;
    String menuBalance;
    String menuWithdrawal;
    String menuDesposit;
    String menuLanguage;
    String menuExit;
    String currentBalance;
    String dollars;
    String enterAmount;
    String enterOTC;
    String errorInvalidOTC;
    String enterAccNo;
    String enterPassword;
    String errorInvalidLogin;
    String chooseLang;
    String errorInvalidLang;
    String english;
    String swedish;
    String errorInvalidOption;
    String bye;
    private String accountFile;

    public Language(String accFile){
        setLanguage(accFile);
    }

    public void setLanguage(String accFile){
        String[] lines = FileManager.readFile(accFile);
        welcome1 = lines[0];
        welcome2 = lines[1];
        welcome3 = lines[2];
        menuBalance = lines[3];
        menuWithdrawal = lines[4];
        menuDesposit = lines[5];
        menuLanguage = lines[6];
        menuExit = lines[7];
        currentBalance = lines[8];
        dollars = lines[9];
        enterAmount = lines[10];
        enterOTC = lines[11];
        errorInvalidOTC = lines[12];
        enterAccNo = lines[13];
        enterPassword = lines[14];
        errorInvalidLogin = lines[15];
        chooseLang = lines[16];
        errorInvalidLang = lines[17];
        english = lines[18];
        swedish = lines[19];
        errorInvalidOption = lines[20];
        bye = lines[21];
        String[] pathStrings = accFile.split("/");
        accountFile = pathStrings[pathStrings.length-1];
    }

    /**
     * Returnera filnamnet.
     */
    public String getFilename(){
        return accountFile;
    }
}
