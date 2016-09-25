
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.BindException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;

import javax.swing.JFrame;
import javax.swing.JTextArea;

/**
 * Sets up a chat server on port 2000 if no start variable is given. The server waits for incoming connection requests from clients. 
 * When a client request has been sent, the server sets up a connection and puts the client on a unique thread. 
 * This thread will listen to incoming messages from the client and broadcast it to all clients on the server.
 * @author Patrik
 *
 */
public class ChatServer{
	public final static int DEFAULT_PORT = 2000;
	public final static int SERVER_CLOSED = 1;
	
	private static ServerSocket server;
	
	private static HashSet<ClientThread> connections = new HashSet<ClientThread>();
	private static boolean listen = true; //listen for client connections
	
	private static JFrame window;
	private static String windowTitle;
	private static JTextArea msgArea = new JTextArea();
	
	public static void main(String[]args){
		int port;
		if(args.length > 0)
			port = Integer.parseInt(args[0]);
		else
			port = DEFAULT_PORT;
		
		try{
			server = new ServerSocket(port);
			windowTitle = "Host: " + server.getInetAddress().getLocalHost().getHostName() + " PORT: " + port + "- Clients Connected: ";
			window = new JFrame(windowTitle + connections.size());
			window.add(msgArea);
			msgArea.setEditable(false);
			window.setVisible(true);
			window.addWindowListener(new ExitListener());
			
			while(listen){
				Socket connection = server.accept();
				ClientThread t = new ClientThread(connection);
				
				//Message about the newly connected client
				String connectionMsg = "CLIENT CONNECTED(" + connection.getInetAddress().getHostName() +")";
				broadcast(new Message(connectionMsg, "FROM SERVER", new Date()));
				
				connections.add(t); 
				t.start();
				window.setTitle(windowTitle + connections.size());
			}
			
		}catch(IOException ex){
			if(ex instanceof BindException)
				System.out.println("Port busy. Could not start server");
		}
		finally{
			try {
				if(server != null)
					server.close();
			} catch (IOException e) { System.out.println("Could not close server");}
		}
	}
	
	/**
	 * Sends a Message to all clients on the server and to the server textbox.
	 * @param message The text to be sent.
	 * @param serverText Set to true to send the same string to the server's console text box.
	 */
	private static synchronized void broadcast(Message message){
		for(ClientThread t: connections){
			try{
				t.out.write(message.toStringFormat());
				t.out.flush(); //#NEEDED?
			}catch(IOException e){}
		}
		msgArea.append("[" +message.date + "] " + message.ip + ": " + message.text + "\n");
	}
	
	/**
	 * Removes the client thread from the server's datastructure and kills the thread. Broadcasts a message that the client has been disconnected and finally updates the window title.
	 * @param t The thread to be removed and killed.
	 */
	private static synchronized void removeThread(ClientThread t){
		
		String disconnectMsg = "CLIENT DISCONNECTED(" + t.socket.getInetAddress().getHostName() + ")";
		broadcast(new Message(disconnectMsg, "FROM SERVER: ", new Date()));
		
		
		t.kill();
		connections.remove(t);
		window.setTitle(windowTitle + connections.size());
	}
	
	/**
	 * Kills all client threads on the server.
	 */
	private static synchronized void killActiveConnections(){
		for(ClientThread t: connections){
			 removeThread(t);
		 }
	}
	
	/**
	 * Kills all client threads, disposes the server window and closes the server socket. 
	 * @author Patrik
	 *
	 */
	private static class ExitListener extends WindowAdapter{
		 public void windowClosing(WindowEvent ev) {
			 System.out.println("CLOSING SERVER...");
			 listen = false;
			 
			 //Notify all clients that the server has closed
			 Message serverMsg = new Message("Server has been closed", server.getLocalSocketAddress().toString(), new Date(), SERVER_CLOSED);
			 broadcast(serverMsg);
			 
			 killActiveConnections();
			 window.dispose();
			 
			 try {server.close();} catch (IOException e) {}
			 
			 System.out.println("SERVER CLOSED");
		 }
	}
	
	/**
	 * Continuously listens to incoming messages from it's connections ( clients ) and broadcasts this message to all clients on the server.
	 * @author Patrik
	 *
	 */
	private static class ClientThread extends Thread{
		Socket socket;
		Writer out;
		Boolean active;
		
		public ClientThread(Socket connection){
			this.socket = connection;
			try {
				out = new PrintWriter(socket.getOutputStream());
			} catch (IOException e) {
				e.printStackTrace();
			}
			active = true;
		}
		@Override
		public void run(){
			String strMsg;
			try {
				BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				//#
				//Listen for new messages from client and broadcast the message
				while((strMsg = reader.readLine()) != null && active){
					Message msg = new Message(strMsg);
					broadcast(msg);
				}
				reader.close();
				socket.close();	
					
			} catch (IOException e) {}
			
			removeThread(this); 
		}
		
		/**
		 * Ends the run loop.
		 */
		public void kill(){
			active = false;
		}
		
	}
}
