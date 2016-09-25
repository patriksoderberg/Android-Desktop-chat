

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;

import javax.swing.*;

public class ChatGUI extends JFrame{
	
	private ChatClient client;
	private JTextArea	chatBox;
	private JTextField	messageBox;
	private String		username;
	
	LinkedList<String> sentMessages = new LinkedList<String>();
	
	public ChatGUI(ChatClient client){
		
		super("CONNECTED TO: " + client.getHost() + " - ON PORT: " + client.getPort());
		this.client = client;
		
		setLayout(new BorderLayout());
		
		chatBox = new JTextArea(10, 80);
		chatBox.setEditable(false);
		chatBox.setFont(new Font("Serif", Font.PLAIN, 15));
        chatBox.setLineWrap(true);
		JScrollPane chatScroll = new JScrollPane(chatBox);
		chatScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		
		messageBox = new JTextField("Enter message", 80);
		
		JPanel center = new JPanel();
		JPanel south = new JPanel();
		add(center, BorderLayout.CENTER);
		add(south, BorderLayout.SOUTH);
		
		south.add(messageBox);
		center.add(chatScroll);
		
		messageBox.addKeyListener(new ChatListener());
		messageBox.requestFocusInWindow();
		
		validate();
		pack();
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
	    
	    addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent ev) {
            	client.close();
            	dispose();
            }
        });
	   
	}
	
	/**
	 * Appends the chatbox with String
	 */
	public void append(String msg){
		chatBox.append(msg);
	}
	
	public void setUsername(String name){
		username = name;
	}
	
	/**
	 * Returns the last message entered in the message box. Returns null if no message.
	 */
	public String getMessage(){
		if(sentMessages.isEmpty()){
			return null;
		}
		else{
			System.out.println("getMessage != tom");
			return sentMessages.removeFirst();
		}
	}
	class ChatListener implements KeyListener{

		@Override
		public void keyPressed(KeyEvent e) {
			if(e.getKeyCode() == KeyEvent.VK_ENTER){
					client.sendMessage(messageBox.getText());
					messageBox.setText("");
			}		
		}

		@Override
		public void keyReleased(KeyEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void keyTyped(KeyEvent e) {
			// TODO Auto-generated method stub
			
		}	
	}

	public void read(){
		client.startRead(chatBox);
	}
	
	
}//class
