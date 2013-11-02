import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
/**
 * Manages files.
 *
 * @author Sebastian Olsson
 * @version 2011-08-05
 */
public class FileManager{
	/**
	 * Reads a file and returns an array of strings, each corresponding
	 * to a line in the file.
	 * @param file The name of the file
	 * @return An array containing the file contents
	 */
	public static String[] readFile(String file){
		ArrayList<String> lines = new ArrayList<String>();
		try{
			FileInputStream fis = new FileInputStream(file);
			InputStreamReader ir = new InputStreamReader(fis);
			BufferedReader reader = new BufferedReader(ir);
			try{
				String line = null;
				while((line = reader.readLine()) != null){
					lines.add(line);
				}
			}
			finally{
				reader.close();
			}
		}
		catch(FileNotFoundException e){
			System.out.println("The file: "+file+" was not found.");
		}
		catch(IOException e){
			e.printStackTrace();
		}
		return lines.toArray(new String[lines.size()]);
	}

	/**
	 * Reads a file and returns an array of strings, each corresponding
	 * to a line in the file. Text written after every occurrence of
	 * the string argument omittance is not read.
	 * @param file The name of the file
	 * @param omittance Text written after this string is ignored
	 * @return An array containing the file contents
	 */
	public static String[] readFileIgnoreComments(String file,String omittance){
		String[] lines = readFile(file);
		for(int i = 0;i < lines.length;i++){
			lines[i] = StringManager.removeComments(lines[i],omittance);
		}
		return lines;
	}
}
