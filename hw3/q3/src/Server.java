import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server{
  
  volatile static char transferType = 'T';
	
  ThreadLocal<String> clientRequest = new ThreadLocal<String>();
  String Crequest = new String();
  static int tcpPort;
  static int udpPort;
  static ServerSocket TCPSocket = null;
  static DatagramSocket UDPSocket = null;
  
  public Server(){
	  
  }
  
  public Server(String request){
	  clientRequest.set(request);
	  Crequest = request;
  }
  
  public static void main (String[] args) {
    
    if (args.length != 3) {
      System.out.println("ERROR: Provide 3 arguments");
      System.out.println("\t(1) <tcpPort>: the port number for TCP connection");
      System.out.println("\t(2) <udpPort>: the port number for UDP connection");
      System.out.println("\t(3) <file>: the file of inventory");

      System.exit(-1);
    }
    tcpPort = Integer.parseInt(args[0]);
    udpPort = Integer.parseInt(args[1]);
    String fileName = args[2];

    // parse the inventory file
    Map<String,Integer> inventory = new HashMap<String, Integer>();
    try {
    	String strLine = "";
    	BufferedReader input = new BufferedReader( new FileReader(fileName));
    	while ((strLine = input.readLine()) != null){
    		String[] line = strLine.split("\\s+");
    		inventory.put(line[0],Integer.parseInt(line[1]));
    	}
    } catch (IOException e) {
		e.printStackTrace();
	}
    // TODO: handle request from clients
    /*
     * whenever a new client connects, start up a new thread to take care of the cleint requests 
     * wait for client request based on udp/tcp --> send request to a new thread
     */
    //will have to make a list of transactions for each client?
    ExecutorService threadPool = Executors.newCachedThreadPool();
       
    
    byte[] buf = new byte[4096];
    
    try{
		TCPSocket = new ServerSocket(tcpPort);
		TCPServer tcpServer = new TCPServer(TCPSocket);
		UDPServer udpServer = new UDPServer(udpPort);
		threadPool.submit(new Thread(udpServer));
    	threadPool.submit(new Thread(tcpServer));
    	TCPSocket.close();
    } catch(IOException e){
    	
    }

    System.out.println("failure");
  }
  
  /******SYNCED MTHODS TO CHANGE INVENTORY*************/
  public String execute(String inMessage){
	  //process client request
	  String[] request = inMessage.split("\\s+");
	  String outMessage = "";
	  if(request[0].equals("setmode")){
		  /*
		   * IS SETMODE UNIQUE TO CLIENT OR DOES EVERY CLIENT SWITCH UDP/TCP IF ONE CLIENT CHANGES IT?
		   */
		  outMessage = "Set to communicate with server using ";
		  if(request[1].equals("T")){	
			  
			  outMessage += "TCP";
		  }else if(request[1].equals("U")){

			  outMessage += "UDP";
		  }
	  } else if(request[0].equals("Purchase")){
		  outMessage = "purchase";
	  }else if(request[0].equals("cancel")){
		  outMessage = "cancel";
	  }else if(request[0].equals("search")){
		  outMessage = "search";
	  }else if(request[0].equals("list")){
		  outMessage = "list";
	  }	  
	  
	return outMessage;
	  
  }

}