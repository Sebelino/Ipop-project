import java.io.*;
import java.util.*;

/**
 * @author Sebastian Olsson
 * @version 2013-10-27
 */
public class MasterThread extends Thread {

    private Set<ServerThread> connections;
    private Set<Game> games;

    /**
     * Skapar en tråd för ändring av bankens välkomstbanner.
     */
    public MasterThread() {
        connections = new HashSet<ServerThread>();
        games = new HashSet<Game>();
    }

    /**
     * Handles the communication between clients.
     */
    public void run(){
        int clientCount = -1;
        while(true){
            Iterator<ServerThread> it = connections.iterator();
            while(it.hasNext()){
                ServerThread conn = it.next();
                if(!conn.isAlive()){
                    it.remove();
                    continue;
                }
                if(conn.hasRequest()){
                    processRequest(conn);
                }
            }
            if(clientCount != connections.size()){
                clientCount = connections.size();
                System.out.println("There are currently "+clientCount+" clients connected.");
            }
            int millisecondDelay = 50;
            try{Thread.sleep(millisecondDelay);}catch(InterruptedException e){}
        }
    }

    /**
     * Process the oldest request from the server thread.
     */
    private void processRequest(final ServerThread conn){
        String request = conn.pollRequest();
        String processedReq = request;
        if(hasNextToken(processedReq)){
            String token = nextToken(processedReq); processedReq = removeToken(processedReq);
            if(token.equals("list")){
                String listResponse = listResponse();
                conn.sendReg(listResponse);
                if(hasNextToken(processedReq)){
                    System.err.println("Could not parse request!");
                }
            }else if(token.equals("exit")){
                tryQuittingGame(conn.clientName);
            }else if(token.equals("setname")){
                token = nextToken(processedReq); processedReq = removeToken(processedReq);
                String proposedName = token;
                if(playerNameIsAvailable(proposedName)){
                    conn.clientName = proposedName;
                }else{
                    conn.sendReg("error That name is unavailable.");
                }
            }else if(token.equals("host")){
                token = nextToken(processedReq); processedReq = removeToken(processedReq);
                String proposedName = token;
                if(gameNameIsAvailable(proposedName)){
                    Game game = new Game(proposedName,conn);
                    games.add(game);
                    conn.sendReg("ok");
                }else{
                    conn.sendReg("error That name is unavailable.");
                }
            }else if(token.equals("join")){
                token = nextToken(processedReq); processedReq = removeToken(processedReq);
                String proposedName = token;
                Game joinedGame = null;
                for(Game game : games){
                    if(game.gameName.equalsIgnoreCase(proposedName)){
                        joinedGame = game;
                        break;
                    }
                }
                if(joinedGame == null){
                    String msg = "error There is no game with that name.";
                    conn.sendReg(msg);
                }else{
                    joinedGame.join(conn);
                    // Send messages to joiner.
                    String joinMessage = "joined "+joinedGame.joinerName()+" "+joinedGame.gameName;
                    String boardMessage = "board "+joinedGame.getStateString();
                    conn.sendReg(joinMessage);
                    conn.sendReg(boardMessage);
                    // Send messages to hoster.
                    joinedGame.hoster.sendIrr(joinMessage);
                    joinedGame.hoster.sendIrr(boardMessage);
                }
            }else if(token.equals("move")){
                //TODO
                
                //Check if user is in a game. If no: do nothing.
                //If yes, parse the parameters to an integer vector.
                Vector<Integer> positions = new Vector<Integer>();
                int player;
                //player should be either 1 or 10 (Constants.CELL_RED or
                //Constants.CELL_WHITE) depending on what player requested the move.
                try {
                    while(hasNextToken(processedReq)){
                        token = nextToken(processedReq); processedReq = removeToken(processedReq);
		            	positions.add(Integer.parseInt(token));
                    }
                } catch (Exception e) {
                	//Probably because the arguments were not valid integers.
                	//Notify player somehow
                	conn.sendReg("error Illegal command or parameters");
                }
                
                
                //Find the correct game and call:
                //correctGame.makeMove(player, positions);
            }else if(token.equals("chat")){
                token = nextToken(processedReq); processedReq = removeToken(processedReq);
                //TODO
                
                //Check if user is in a game .
                //If yes, send the message to the correct opponent.
                //If not in a game: do nothing for now.
                //Might implement lobby chat in the future, though.
                
            }else{
                System.err.println("Could not parse request...!");
            }
        }else{
            System.err.println("Could not parse request!");
        }
        wake(conn);
        System.out.println("exiting processReq");
    }

    private boolean hasNextToken(String request){
        return !request.trim().isEmpty();
    }

    private String nextToken(String request){
        return request.split("\\s+")[0];
    }

    private String removeToken(String request){
        String[] tokens = request.split("\\s+");
        String result = "";
        for(int i = 1;i < tokens.length;i++){
            result += tokens[i];
        }
        return result;
    }

    /**
     * Make the player identified by the parameter quit a game, if s/he is part of one.
     */
    private void tryQuittingGame(String clientName){
        Iterator<Game> it = games.iterator();
        while(it.hasNext()){
            Game game = it.next();
            if(game.hosterName().equals(clientName)){
                wake(game.hoster);
                game.hoster.sendReg("ok");
                //game.hoster.printComm("sendReg","ok");
                it.remove();
                break;
            }else if(game.joinerName().equals(clientName)){
                wake(game.joiner);
                game.joiner.sendReg("ok");
                //game.hoster.printComm("sendReg","ok");	//Huh??, hoster?
                it.remove();
                break;
            }
        }
    }

    /**
     * @return A response representing a list of all available hosted games.
     */
    private String listResponse(){
        int numSessions = games.size();
        String list = "sessions "+numSessions;
        for(Game game : games){
            list += " "+game.gameName+" "+game.hosterName()+" "+game.joinerName();
        }
        return list;
    }

    /**
     * Wake the thread up.
     */
    private void wake(ServerThread conn){
        synchronized(conn){
            conn.notify();
        }
    }

    /**
     * @return true if no other client uses that name.
     */
    private boolean playerNameIsAvailable(String name){
        for(ServerThread conn : connections){
            if(conn.clientName.equalsIgnoreCase(name)){
                return false;
            }
        }
        return true;
    }

    /**
     * @return true if no other game session uses that name.
     */
    private boolean gameNameIsAvailable(String name){
        for(Game game : games){
            if(game.gameName.equalsIgnoreCase(name)){
                return false;
            }
        }
        return true;
    }

    /**
     * Starts a connection and keeps track of it.
     */
    public void addConnection(ServerThread connection){
        connection.start();
        connections.add(connection);
    }
}
