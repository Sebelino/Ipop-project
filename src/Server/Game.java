import java.util.Vector;

/**
 * A game session. Contains information about the hosting player, joining player, and state of
 * the game.
 *
 * @author Sebastian Olsson
 * @version 2013-10-27
 */
public class Game{
    public final ServerThread hoster;
    public ServerThread joiner;
    public final String gameName;
    private GameState state;
    private boolean started;

    /**
     * Constructor.
     */
    public Game(final String gameName,final ServerThread hoster) {
        this.hoster = hoster;
        this.joiner = null;
        this.gameName = gameName;
        started = false;
        //state = new GameState();
    }

	/*
	 * Initialize the Game
	 */
	public void startGame() {
		state = new GameState();
		started = true;
	}

	public void makeMove(String playerName,String move) throws GameException{
		//Most likely obsolete in the future
		//makeMove(Constants.CELL_RED, 1, 3);
	}
	
    public void makeMove(int player, Vector<Integer> poses) throws GameException{
        if(state.getNextPlayer() == player) {
        	//It is indeed this player's turn
        	
        	
        	
        	
        } else {
        	//Not this player's turn
        	throw new GameException("It is not your turn.");
        }
    }

    /**
     * @return The name of the hosting player.
     */
    public String hosterName(){
        return hoster.clientName;
    }

    /**
     * Causes the client corresponding to the server thread to join the game.
     */
    public void join(ServerThread connection){
        joiner = connection;
    }

    /**
     * @return The name of the joining player.
     * @throws RuntimeException if no one has joined yet.
     */
    public String joinerName(){
        if(!hasJoined()){
            throw new RuntimeException("No one has joined this game.");
        }
        return joiner.clientName;
    }

    /**
     * @return True iff someone has joined this game.
     */
    public boolean hasJoined(){
        return joiner != null;
    }

    private class GameException extends Exception{
        public GameException(String message){
            super(message);
        }
    }
}
