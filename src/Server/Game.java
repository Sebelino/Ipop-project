import java.util.Vector;

/**
 * A game session. Contains information about the hosting player, joining player, and state of
 * the game.
 *
 * @author Sebastian Olsson
 * @version 2013-10-27
 */
public class Game{
    private final ServerThread hoster;
    private ServerThread joiner;
    public final String gameName;
    private GameState state;
    private boolean started, finished;
    private Vector<Move> lNextMoves;
    private String stateString;

    /**
     * Constructor.
     */
    public Game(final String gameName,final ServerThread hoster) {
        this.hoster = hoster;
        this.joiner = null;
        this.gameName = gameName;
        started = false;
        finished = false;
        stateString = null;
    }

	/*
	 * Initialize the Game
	 */
	private synchronized void startGame() {
		state = new GameState();
		stateString = state.toMessage();
		started = true;
		lNextMoves = new Vector<Move>();
	}

    public synchronized int makeMove(int player, Vector<Integer> poses) {
        if(state.getNextPlayer() == player) {
        	//It is indeed this player's turn
        	
        	Move wantedMove = new Move(poses);
        	
        	int nm = -1;
			for (int i = 0;i<lNextMoves.size();i++) {
				if (wantedMove.equals(lNextMoves.get(i))) {
					nm = i;
					break;
				}
			}
        	
        	if (nm >= 0) {	//Legal move!
				state.doMove(wantedMove);
				stateString = state.toMessage();
				state.findPossibleMoves(lNextMoves);
				
				//Check if the only next move is admitting draw or loss
				if (lNextMoves.size() == 0) {
					//No possible moves! You lose!
					//This shouldn't be possible anyway
					System.err.println("findPossibleMoves() ran, despite game being finished!");
				} else if (lNextMoves.size() == 1) {
					int t = lNextMoves.get(0).getType();
					if (t == Move.MoveType.MOVE_DRAW.getInternalValue() ||
		t == Move.MoveType.MOVE_RW.getInternalValue() ||
		t == Move.MoveType.MOVE_WW.getInternalValue()) 
					{
						//Only next move is draw or admit loss
						state.doMove(wantedMove);
						stateString = state.toMessage();
						finished = true;
						//return 1;	//?
					}
				}
				
				//Otherwise, indicate success!
        		return 0;
        	} else {
        		//Move not in lNextMoves, not legal!
        		//Don't update game.
        		return -1;
        	}
        } else {
        	//Not this player's turn
        	return -2;
        }
    }

    /**
     * Causes the client corresponding to the server thread to join the game.
     */
    public synchronized void join(ServerThread connection){
        joiner = connection;
        startGame();
    }
    
    /**
     * @return The name of the hosting player.
     */
    public synchronized String hosterName(){
        return hoster.clientName;
    }

	/**
     * @return The string representation of the current GameState
     */
    public synchronized String getStateString(){
        return stateString;
    }

    /**
     * @return The name of the joining player.
     * @throws RuntimeException if no one has joined yet.
     */
    public synchronized String joinerName(){
        if(!hasJoined()){
            throw new RuntimeException("No one has joined this game.");
        }
        return joiner.clientName;
    }

    /**
     * @return True iff someone has joined this game.
     */
    public synchronized boolean hasJoined(){
        return joiner != null;
    }

    /**
     * @return True iff someone has joined this game.
     */
    public synchronized boolean isFinished(){
    	return finished;
    }	

    private class GameException extends Exception{
        public GameException(String message){
            super(message);
        }
    }
}
