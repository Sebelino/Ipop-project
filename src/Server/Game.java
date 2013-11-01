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
    private int state; // byt ut denna mot ett spelbräde eller nå't
    private boolean hostersTurn;

    /**
     * Constructor.
     */
    public Game(final String gameName,final ServerThread hoster) {
        this.hoster = hoster;
        this.joiner = null;
        this.gameName = gameName;
        state = 0;
        hostersTurn = true;
    }

    public void makeMove(String playerName,String move) throws GameException{
        if(playerName.equals(hoster.clientName)){
            if(hostersTurn){
                state++;
            }else{
                throw new GameException("It is not your turn.");
            }
        }else if(playerName.equals(joiner.clientName)){
        }else{
            throw new IllegalArgumentException();
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
