

import java.awt.EventQueue;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

import javax.swing.JOptionPane;

public class StartClient {
	
	public static void main(String[]args){
		String host ="127.0.0.1";
		int port = 2000;
		
		if (args.length > 0) {
			host = args[0];
			if(args.length > 1)
				port = Integer.parseInt(args[1]);
		}
		
		try {
			ChatClient c = new ChatClient(host,port);
			ChatGUI chatGUI = new ChatGUI(c);
			EventQueue.invokeLater(new FrameShower(chatGUI));
		} catch (IOException e1) {
			JOptionPane.showMessageDialog(null, "Could not connect to server", "Error", JOptionPane.ERROR_MESSAGE);
			System.out.println("Could not connect to server");
		}
	}
	
	private static class FrameShower implements Runnable{
		private final ChatGUI gui;
		FrameShower(ChatGUI gui){
			this.gui = gui;
		}
		@Override
		public void run() {
			gui.setVisible(true);
			gui.read();
		}
	}

}
