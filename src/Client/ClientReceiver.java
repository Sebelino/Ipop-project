import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;


public class ClientReceiver extends Thread {
	
	
    private BufferedReader in;
    private Session s;
    
    public ClientReceiver(BufferedReader in, Session session) {
    	this.in = in;
    	s = session;
    }
    
    private void chat(String input) {
    	int state = s.getCurrentState();
		if(state == Session.STATE_HOST_GAME ||
				state == Session.STATE_PEER_GAME) {
			//extract chat message
			
			
			s.println(extractArgStr(input)); //TODO fix opponent name
		} //else do nothing
    }
    
    private void joined(String input) {
    	String[] tokens = input.split(" ");
    	int state = s.getCurrentState();
    	if(tokens.length == 4 && state == Session.STATE_HOST_WAITING) {
    		s.println("Player " +  tokens[2] + " joined your game!");
    		s.setCurrentState(Session.STATE_HOST_GAME);
    	}
    }
    
    private void exit(String input) {
    	int state = s.getCurrentState();
    	if(state == Session.STATE_HOST_GAME || state == Session.STATE_HOST_WAITING ||
    			state == Session.STATE_PEER_GAME) {
			String msg = extractArgStr(input);
    		s.println("The game was ended with the following message from server:\n" + msg);
    		s.setCurrentState(Session.STATE_LOBBY);
    	}
    }
    
    private void board(String input) {
        System.out.println(input);
    	int state = s.getCurrentState();
    	String[] tokens = input.split(" ");
    	if(state == Session.STATE_HOST_GAME || state == Session.STATE_PEER_GAME) {
			String gs = extractArgStr(input);
			s.setBoard(gs);
			String res = "New move made, current board:\n" + s.parseBoard(tokens[2]);
			if(state == Session.STATE_HOST_GAME && tokens[2].equals("r")) {
				res += "It is YOUR turn, you are red\n";
			} else if(state == Session.STATE_HOST_GAME && tokens[2].equals("w")) {
				res += "It is your opponent's turn, you are red\n";
			} else if(state == Session.STATE_PEER_GAME && tokens[2].equals("r")) {
				res += "It is your opponent's turn, you are white\n";
			} else if(state == Session.STATE_PEER_GAME && tokens[2].equals("w")) {
				res += "It is YOUR turn, you are white\n";
			}
			s.print(res);
    	} else {
    		//for debugging server
    		System.err.println("wtf, received board in state " + s.getCurrentState());
    	}
    }
    
    private String extractArgStr(String input) {
		Scanner sc = new Scanner(input);
		sc.next(); sc.next();
		
		StringBuilder sb = new StringBuilder();
		while(sc.hasNext()) {
			sb.append(sc.next() + " ");
		}
		sc.close();
		return sb.toString();
    }
    
    
    public void run() {
    	String input = "";
    	while(true) {
    		//this thread never leaves this loop, and never terminates.
    		//the program is only terminated once the other thread terminates.
    		//Or well, if connection dies I guess we can terminate from here
    		
    		try {
    			input = in.readLine().trim().toLowerCase();
    		}catch(NullPointerException e){
    			s.println("Error: Received null from the server. Terminating.");
                System.exit(1);
            }catch(Exception e){
    			s.println("Connection died or something; terminating");
    			//in.close();
    			System.exit(1);
    		}
    		
    		String[] tokens = input.split("\\s+");
    		//check type of input
    		if(tokens.length <= 1)
    			continue;
    		
    		String type = tokens[0];
    		if(type.equals("irr")) {
    			if(tokens[1].equals("chat")) {
    				//a chat message just arrived
    				chat(input);
    			} else if(tokens[1].equals("joined")) {
    				joined(input);
    			} else if(tokens[1].equals("exit")) {
    				exit(input);
    			} else if(tokens[1].equals("board")) {
    				board(input);
    			}
    			
    			
    			
    		} else if(type.equals("reg")) {
    			if(!s.getWaiting()) 
    				continue; //was not waiting for anything
    			
    			int wf = s.getWaitingFor();
    			if(tokens[1].equals("error")) {
    				String error = extractArgStr(input);
    				s.println("An error occured, message from server:\n" + error);
    				s.setWaiting(false);
    			} else if(wf == Session.WAIT_EXIT) {
    				if(tokens[1].equals("ok")) {
    					s.println("You have quit the game!");
    					s.setCurrentState(Session.STATE_LOBBY);
    					s.setWaiting(false);
    				}
    			} else if(wf == Session.WAIT_HOST) {
    				if(tokens[1].equals("ok")) {
    					s.println("Game hosted! Waiting for opponent to join.");
    					s.setCurrentState(Session.STATE_HOST_WAITING);
    					s.setWaiting(false);
    				}
    			} else if(wf == Session.WAIT_JOIN) {
    				if(tokens[1].equals("joined")) {
    					if(tokens.length == 4) {
    						s.println("Joined game " + tokens[3] + ".");
    						try {
								input = in.readLine();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								//e.printStackTrace();
								s.println("Disconnected from server, exiting");
								System.exit(0);
							}
    						s.setCurrentState(Session.STATE_PEER_GAME);
    						board(input);
    						s.setWaiting(false);
    					}
    				}
    			} else if(wf == Session.WAIT_LIST) {
    				if(tokens[1].equals("sessions")) {
    					int numSessions = Integer.parseInt(tokens[2]);
    					if(numSessions == 0) {
    						s.println("There are no active game sessions.");
    					} else {
    						for(int i = 0; i < numSessions; i++) {
    							int base = 3+i*3;
    							s.print((i+1)+": " + tokens[base] + " by " + tokens[base+1] + ". ");
    							if(tokens[base+2].equals("-")) {
    								s.println("CAN JOIN");
    							} else {
    								s.println("FULL");
    							}
    						}
    						
    					}
    					
    					s.setWaiting(false);
    				}
    			} else if(wf == Session.WAIT_MOVE) {
    				if(tokens[1].equals("board")) {
    					board(input);
    					s.setWaiting(false);
    					
    				}
    			}
    			
    			
    			
    			
    		} else {
    			//unknown, do nothing
    		}
    		
    	}
    	
    	
    }
    
    
    
    

    
    
    
}
