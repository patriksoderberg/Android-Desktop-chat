
import java.net.*;
import java.util.Date;
import java.io.*;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

public class ChatClient implements Runnable {
	
	public final static int DEFAULT_PORT = 2000;
	public final static String DEFAULT_HOST_NAME = "127.0.0.1";
	
	JTextArea chatArea;
	
	private Socket socket = new Socket();
	private int port = DEFAULT_PORT;
	private InetAddress host;
	
	private Writer out;
	private BufferedReader in;
	
	private boolean readFromServer;
	private Thread t = new Thread(this);
	
	public ChatClient(InetAddress host, int port) throws IOException{
		this.host = host;
		this.port = port;
		SocketAddress address = new InetSocketAddress(host, port);
		socket.connect(address);
		out = new OutputStreamWriter(socket.getOutputStream(), "ASCII");
		}
	
	public ChatClient(InetAddress host) throws IOException{ 
		this(host, DEFAULT_PORT);
		}
	
	public ChatClient(String hostname, int port) throws UnknownHostException, IOException{
		this(InetAddress.getByName(hostname), port);
	}
	
	public ChatClient(String hostname) throws UnknownHostException, IOException {
		this(InetAddress.getByName(hostname), DEFAULT_PORT);
		}
	
	public ChatClient() throws UnknownHostException, IOException{
		this(DEFAULT_HOST_NAME, DEFAULT_PORT);
	}
	
	//Getter methods
	public InetAddress getHost(){
		return host;
	}
	
	public int getPort(){
		return port;
	}
	
	/**
	 * Makes a Message object consisting of text, ip-address and the date the message was made.
	 * @param text
	 */
	public void sendMessage(String text){
		Message msg = new Message(text, socket.getLocalAddress().toString(), new Date());
		sendMessage(msg);
	}
	
	/**
	 * Sends a message to the connected server
	 * @param msg
	 */
	private void sendMessage(Message msg){
			try{
				out.write(msg.toStringFormat());
				out.flush();
				System.out.println("Message sent");
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			
	}
	
	public void close(){
		try {
			readFromServer = false;
			out.close();
			in.close();
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	
	public void startRead(JTextArea chatArea){
		this.chatArea = chatArea;
		readFromServer = true;
		t.start();
	}
	
	/**
	 * Read incoming messages from server and put it in the installed chat box.
	 * TODO: Close sockets when client is stopped
	 */
	@Override
	public void run(){
		
		Message message;
		if( Thread.currentThread() == t){
			try{
				in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				
				while(readFromServer){
					message =  new Message(in.readLine());
					if(message.getServerCode() == ChatServer.SERVER_CLOSED ){
						close();
					}
					
					chatArea.append("[" + message.date + "] " + message.ip + ": " + message.text + "\n");
				}
			}catch(IOException e){ e.printStackTrace();}
		}
	}
	
	
	//TODO
	 public interface MessageListener{
	        public void onMessageReceived(Message message);
	    }
	 
	 public interface clientClosedEvent{
	        public void onClientClosed();
	    }
} //class