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
            System.out.println("NEXT!");
            if(clientCount != connections.size()){
                clientCount = connections.size();
                System.out.println("There are currently "+clientCount+" clients connected.");
            }
            int millisecondDelay = 250;
            try{Thread.sleep(millisecondDelay);}catch(InterruptedException e){}
        }
    }

    /**
     * Process the oldest request from the server thread.
     */
    private void processRequest(final ServerThread conn){
        String request = conn.pollRequest();
        String[] tokens = request.split("\\s+");
        if(tokens.length == 1){
            if(tokens[0].equals("list")){
                String listResponse = listResponse();
                conn.send(listResponse);
                conn.printComm("send",listResponse);
            }
        }else if(tokens.length == 2){
            if(tokens[0].equals("setname")){
                String proposedName = tokens[1];
                if(playerNameIsAvailable(proposedName)){
                    conn.clientName = proposedName;
                }else{
                    conn.send("error That name is unavailable.");
                    conn.printComm("send","error That name is unavailable.");
                }
                wake(conn);
            }else if(tokens[0].equals("host")){
                String proposedName = tokens[1];
                if(gameNameIsAvailable(proposedName)){
                    Game game = new Game(proposedName,conn);
                    games.add(game);
                    conn.send("ok");
                    conn.printComm("send","ok");
                }else{
                    conn.send("error That name is unavailable.");
                    conn.printComm("send","error That name is unavailable.");
                }
                wake(conn);
            }else if(tokens[0].equals("join")){
                String proposedName = tokens[1];
                if(gameNameIsAvailable(proposedName)){
                    Game joinedGame = null;
                    for(Game game : games){
                        if(game.gameName.equalsIgnoreCase(proposedName)){
                            joinedGame = game;
                            break;
                        }
                    }
                    if(joinedGame == null){ // There was no game with the name that the client mentions.
                        conn.send("error There is no game with that name.");
                        conn.printComm("send","error There is no game with that name.");
                    }else{
                        joinedGame.join(conn);
                        conn.send("ok");
                        conn.printComm("send","ok");
                    }
                }else{
                    conn.send("error That name is unavailable.");
                    conn.printComm("send","error That name is unavailable.");
                }
                wake(conn);
            }else{
                throw new IllegalArgumentException("Request to master thread was not recognized.");
            }
        }else{
            System.err.println("Could not parse request!");
        }
        System.out.println("exiting processReq");
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
