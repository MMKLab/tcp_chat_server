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
										 * string as parm too if we have
										 * this object already as parm
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
			
			outputToClient.println("Enter username: +\n");
			clientUsername = inputFromClient.readLine();//setting username
			
			outputToClient.println("Welcome "+clientUsername+". \nIf you want to exit, you can also type /quit");
			ChatServer.notifyAllClientsAboutANewMember(this);
			
			while(true){
				String text = inputFromClient.readLine();//reading line from client in loop
				if(text.startsWith("/quit")){
					ChatServer.notifyAllClientsAboutANewMember(this);
					break;
				}
				ChatServer.forwardMessageToAll(text);
			}
			outputToClient.println("Goodbye Mr."+clientUsername);
			clientSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		ChatServer.removeFromClientsList(this);
	}
	
	
}
