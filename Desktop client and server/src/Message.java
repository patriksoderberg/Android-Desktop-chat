import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;


public class Message implements Serializable {
  
	private static final long serialVersionUID = 1L;
	public String text;
    public String ip;
    public String date;
    
    private int serverMessageCode;
    
    public Message(String text, String ip, String date){
        this.text = text;
        this.ip = ip;
        this.date = date;
    }
    
    public Message(String text, String ip, Date date){
    	this.text = text;
    	this.ip = ip;
    	
    	//Create a consistent date format
    	SimpleDateFormat printFormat = new SimpleDateFormat("HH:mm:ss");
    	this.date = printFormat.format(date);
    }
    
    public Message(String text, String ip, Date date, int code){
    	this(text, ip, date);
    	serverMessageCode = code;
    }
    
    public Message(String strMsg){
    	parseMessage(strMsg);
    }
    
    /**
     * Message fields are seperated with *
     * First(1) field is parsed as the date.
     * (2): ip
     * (3): text
     * @param strMsg
     */
    public void parseMessage(String strMsg){
    	String[] tokens = strMsg.split("\\*");
    	date = tokens[0];
    	ip = tokens[1];
    	text = tokens[2];
    }
    
    public String toStringFormat(){
    	String str = date + "*" + ip + "*" + text + "\n";
    	return str;
    }
    public int getServerCode(){
    	return serverMessageCode;
    }
    
}
