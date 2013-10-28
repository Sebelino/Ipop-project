import java.io.*;
import java.util.*;

/**
 * A game session. Contains information about the hosting player, joining player, and state of
 * the game.
 *
 * @author Sebastian Olsson
 * @version 2013-10-27
 */
public class Game{
    private final ATMServerThread hoster;
    private final ATMServerThread joiner;
    private int state; // byt ut denna mot ett spelbräde eller nå't
    private boolean hostersTurn;

    /**
     * Constructor.
     */
    public Game(final ATMServerThread hoster,final ATMServerThread joiner) {
        this.hoster = hoster;
        this.joiner = joiner;
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

    private class GameException extends Exception{
        public GameException(String message){
            super(message);
        }
    }
}
