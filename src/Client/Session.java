import java.util.HashMap;

/**
 * This class represents a Session in the client. It is the class the receiver and the sender
 * use to communicate.
 * @author Dmitrij
 *
 */
public class Session {
	
	public static final int STATE_LOBBY = 0; //not hosted/joined a game yet
	public static final int STATE_HOST_WAITING = 1; //hosted a game, waiting for player to join
	public static final int STATE_HOST_GAME = 2; //hosted a game, playing
	public static final int STATE_PEER_GAME = 3; //joineda  game, playing
	public static final int STATE_DISCONNECTED = 4; //no connection, quit pls
	
	
	private boolean accessed = false; //semafor variable
	private boolean waiting = false; //waiting for response from server, also a semafor
	
	private int waitingFor = -1;
	
	public static final int WAIT_DONT = -1;
	public static final int WAIT_MOVE = 0;
	public static final int WAIT_LIST = 1;
	public static final int WAIT_HOST = 2;
	public static final int WAIT_JOIN = 3;
	public static final int WAIT_EXIT = 4;
	
	/*
	 * Vabiales Receiver may write to
	 */
	private int currentState;
	private String gamestate = null;
	private String ownName;
	private String oppName;
	
	

	private final SynchronizedPrinter sp;
	
	
	public Session(String name) {
		sp = new SynchronizedPrinter(System.out);
		currentState = 0;
		ownName = name;
	}
	
	public void print(String message) {
		sp.print(message);
	}
	
	public void println(String message) {
		sp.println(message);
	}
	
	/*
	 * Access state
	 */
	public int getCurrentState() {
		while(accessed){}
		accessed = true;
		int r = currentState;
		accessed = false;
		return r;
	}
	
	public void setCurrentState(int newState) {
		if(currentState == STATE_DISCONNECTED)
			return;
		
		if(newState == STATE_DISCONNECTED) {
			waiting = false;
		}
		
		
		while(accessed){}
		accessed = true;
		currentState = newState;
		accessed = false;
	}
	
	
	/*
	 * Access board
	 */
	public void setBoard(String gamestate) {
		while(accessed){}
		accessed = true;
		this.gamestate = gamestate;
		accessed = false;
	}
	
	public String getBoard() {
		while(accessed){}
		accessed = true;
		String s = gamestate;
		accessed = false;
		return s;
	}
	
	/*
	 * Get a neater representation of the board
	 */
	public String parseBoard(String boards) {
		int i = 0;
		int j = 0;
		StringBuilder board = new StringBuilder();
		board.append("   A   B   C   D   E   F   G   H   \n");
		board.append("   -----------------------------   \n");
		
		for(int row = 8; row >= 1; row--) {
			board.append(row + " | "); 
			for(int k = 0; k < 8; k++) {
				if(i%2 == 0) {
					board.append(".");
				} else {
					board.append(boards.charAt(j));
					j++;
				}
				i++;
				board.append("   ");
			}
			board.append(" | " + row + "\n");
		}
		board.append("   -----------------------------   \n");
		board.append("   A   B   C   D   E   F   G   H   \n");
		
		return board.toString();
	}
	
	/*
	 * Access waiting
	 */
	public void setWaiting(boolean w) {
		while(accessed){}
		accessed = true;
		waiting = w;
		if(w=false) {
			waitingFor = WAIT_DONT;
		}
		accessed = false;
	}
	
	public boolean getWaiting() {
		//while(accessed){}
		//accessed = true;
		Boolean b = waiting;
		//accessed = false;
		return b;
	}
	
	public int getWaitingFor() {
		return waitingFor;
	}
	
	public void setWaitingFor(int w) {
		waitingFor = w;
	}
	
	//public String getOpponentName() {
	//	return oppName;
	//}
	
	
}
