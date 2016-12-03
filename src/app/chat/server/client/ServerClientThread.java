package app.chat.server.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

import app.chat.server.startup.ChatServer;

public class ServerClientThread extends Thread{
	Socket clientSocket = null;
	BufferedReader inputFromClient = null;
	PrintStream outputToClient = null;
	public String clientUsername = null; /*
										 * we made this public just to ease
										 * things up so we don't need to pass
										 * string as parm too if we have this
										 * object already as parm, better
										 * alternative to this is to create
										 * getter
										 */

	public ServerClientThread(Socket clientSocket) {
		this.clientSocket = clientSocket;
	}
	
	public void sendStringToClient(String text){
		outputToClient.println(text);
	}


	@Override
	public void run() {
		try {
			inputFromClient = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));//init inputStream
			outputToClient = new PrintStream(clientSocket.getOutputStream());//init outputStream
			
			outputToClient.println("Enter username: \n"); //notifying client to send server his user name
			clientUsername = inputFromClient.readLine();//setting username
			
			ChatServer.notifyAllClientsAboutANewMember(this); //method from ChatServer(look there for explanation)
			
			while(true){ //endless loop until client notifies us he wants to quit
				String text = inputFromClient.readLine();//reading line from client in loop
				if(text.startsWith("/quit")){
					ChatServer.notifyAllClientsAboutALeavingMember(this);; //method from ChatServer(look there for explanation)
					break;
				}
				ChatServer.forwardMessageToAll(text, this); //method from ChatServer(look there for explanation)
			}
			clientSocket.close(); //closing socket with client
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
}
