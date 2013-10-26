import java.util.ArrayList;
public class Pakettest{
    private static int index;

    public static void main(String[] args){
        index = 0;
        send("Hejsan");
        send("Hejsan, jag heter Sebastian Olsson och jag är en lång textsträng!!!111oneone");
        System.out.println();
        System.out.println(receive());
    }

    private static String stringGenerator(){
        String[] strs = new String[]{
        "Hejsan, ja",
        "g heter Se",
        "bastian Ol",
        "sson och j",
        "ag är en l",
        "ång textst",
        "räng!!!111",
        "oneone¤"
        };
        return strs[index];
    }

    /**
     * Skickar en textsträng till klienten; vid behov delas strängen upp i
     * mindre datapaket som skickas en i taget.
     */
    private static void send(String text){
        ArrayList<String> packets = new ArrayList<String>();
        String str = text;
        while(str.length() > 9){
            packets.add(str.substring(0,10));
            str = str.substring(10,str.length());
        }
        packets.add(str+'¤');
        for(String packet : packets){
            System.out.print(packet);
for(long i = 0;i < 2000000000;i++);
        }
        System.out.println();
    }

    /**
     * Tar emot en serie av datapaket från klienten och bygger upp dem
     * till en fullständig textsträng.
     */
    private static String receive(){
        String received = stringGenerator();
        String completeString = "";
        while(!received.endsWith("¤")){
            completeString += received;
        System.out.println(completeString);
for(long i = 0;i < 2000000000;i++);
            index++;
            received = stringGenerator();
        }
        completeString += received;
        System.out.println(completeString);
for(long i = 0;i < 2000000000;i++);
        return completeString.substring(0,completeString.length()-1);
    }
}
