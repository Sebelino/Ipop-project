/**
 * Provides convenient methods regarding handling strings.
 * 
 * @author Sebastian Olsson
 * @version 2011-08-05
 */
public class StringManager{
	/**
	 * Returns a string with the same contents as the text argument
	 * save for the first occurrence of the string argument comment
	 * and everything after that.
	 * @param text Input text
	 * @param comment Delimiter string
	 * @return A string with comments removed, if any
	 */
	public static String removeComments(String text,String comment){
		int location = text.indexOf(comment);
		if(location < 0){
			return text;
		}
		return text.substring(0,location);
	}
}
