package app.chat.server.startup;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;

import app.chat.server.client.ServerClientThread;

public class ChatServer {
	
	/*
	 * creates list of instances ServerClientThred, so we can always pinpoint and access those instances
	 */
	public static LinkedList<ServerClientThread> listOfClients = new LinkedList<ServerClientThread>();
	
	public static void main(String[] args) {
		
		int portNumber = 8090; //port which server will use for listening!
		
		/*
		 * If the main method has a port number as a parameter, it will use that number instead of 8090
		 */
		if(args.length > 0){
			portNumber = Integer.parseInt(args[0]);
		}
		
		
		try {
			ServerSocket serverSocketForChatApp =  new ServerSocket(portNumber); //server relying on TCP starts
			while(true){//infinite loop because server must always in state of listening 
				Socket clientSocket = serverSocketForChatApp.accept(); //the method on right returns socket when client makes contact with server
				ServerClientThread newClient = new ServerClientThread(clientSocket);
				listOfClients.add(newClient);
				newClient.start(); //starting a Thread(the run method within class) for parallel tasking 
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/*
	 * method for notifying clients about a new member
	 */
	public synchronized static void notifyAllClientsAboutANewMember(ServerClientThread newClient){
		for (ServerClientThread client : listOfClients) {
			if(newClient == client)
				continue;
			client.sendStringToClient("*** NEW USER: "+newClient.clientUsername+" has entered the chat room!!!***");
		}
	}
	/*
	 * method for passing message to all clients
	 */
	public synchronized static void forwardMessageToAll(String message, ServerClientThread clientSender){
		for (ServerClientThread client : listOfClients) {
			client.sendStringToClient("<"+clientSender.clientUsername+"> "+message);
		}
	}
	/*
	 * method for notifying clients about a leaving member
	 */
	public synchronized static void notifyAllClientsAboutALeavingMember(ServerClientThread leavingClient){
		for (ServerClientThread client : listOfClients) {
			if(leavingClient == client)
				continue;
			client.sendStringToClient("*** USER: "+leavingClient.clientUsername+" has left the chat room!!!***");
		}
	}
	/*
	 * method for removing passed client as parameter 
	 */
	public synchronized static void removeFromClientsList(ServerClientThread client){
		listOfClients.remove(client);
	}
}
