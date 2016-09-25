package com.example.patrik.androidchat;



import java.net.*;
import java.io.*;

public class ChatClient implements Runnable {

    public final static int DEFAULT_PORT = 2000;
    public final static String DEFAULT_HOST_NAME = "10.0.2.2";

    MessageListener messageListener;

    private Socket socket = new Socket();
    private int port = DEFAULT_PORT;
    private InetAddress host;

    private Writer out;
    private BufferedReader in;

    private boolean active = true;
    private Thread t = new Thread(this);

    private StringBuilder fromServer = new StringBuilder();

    public ChatClient(InetAddress host, int port, MessageListener messageListener) throws IOException{
        this.host = host;
        this.port = port;
        this.messageListener = messageListener;

        SocketAddress address = new InetSocketAddress(host, port);
        socket.connect(address);
        out = new OutputStreamWriter(socket.getOutputStream(), "ASCII");
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    public ChatClient(InetAddress host, MessageListener messageListener) throws IOException{
        this(host, DEFAULT_PORT, messageListener);
    }

    public ChatClient(String hostname, int port, MessageListener messageListener) throws UnknownHostException, IOException{
        this(InetAddress.getByName(hostname), port, messageListener);
    }

    public ChatClient(String hostname, MessageListener messageListener) throws UnknownHostException, IOException {
        this(InetAddress.getByName(hostname), DEFAULT_PORT, messageListener);
    }

    public ChatClient(MessageListener messageListener) throws UnknownHostException, IOException{
        this(DEFAULT_HOST_NAME, DEFAULT_PORT, messageListener);
    }

    //Getter methods
    public InetAddress getHost(){
        return host;
    }

    public int getPort(){
        return port;
    }

    //Sends a Message to the server
    public void sendMessage(Message msg){
        try{
            out.write(msg.toStringFormat());
            out.flush(); //needed?
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public void close(){
        try {
            out.close();
            in.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Read from Server
     */
    public void startRead(MessageAdapter messageAdapter){
        t.start();
    }
    @Override
    public void run(){

        String line;
        if( Thread.currentThread() == t){
            try{
                while(active){
                    Message msg = null;
                    msg = new Message(in.readLine());
                    messageListener.onMessageReceived(msg);
                }
            }catch(IOException e){ e.printStackTrace();}
        }
    }

    public interface MessageListener{
        public void onMessageReceived(Message message);
    }

} //class