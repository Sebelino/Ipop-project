import java.io.PrintStream;

/**
 * This class is basically just a writer which can make sure two
 * threads will almost never print at the same time.
 * @author Dmitrij
 *
 */
public class SynchronizedPrinter {

	private PrintStream out; //the out stream, for example System.out
	private boolean printing; //semafor variable
	
	public SynchronizedPrinter(PrintStream out) {
		this.out = out;
		printing = false;
	}
	
	/*
	 * Print message. If one thread is printing while another tries to print,
	 * it will have to wait until printing is finished.
	 */
	public void print(String message) {
		while(printing){} //wait if someone else is printing
		printing = true;  //signal that we are printing
		out.print(message);
		printing = false; //signal that we stopped printing
	}
	
	/*
	 * Print message and append an '\n' to it
	 */
	public void println(String message) {
		print(message+"\n");
	}
	
}
