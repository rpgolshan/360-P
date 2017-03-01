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
	  if(request[0].equals("Purchase")){
		  try{
			String username = request[1];		  
		  	String product = request[2];
		  	int quantity = Integer.parseInt(request[3]);
		  	//outMessage = purchase(username, product, quantity);
		  	outMessage = "purchase";
		  } catch(NullPointerException | NumberFormatException f){
			  //purchase message didn't have all the fields defined or defined correctly
		  }
	  }else if(request[0].equals("cancel")){
		  try{
			  int order = Integer.parseInt(request[1]);
			  //outMessage = cancel(order);
			  outMessage = "cancel";
		  } catch(NullPointerException | NumberFormatException f){
			  //cancel message didn't specify an order or didn't give an integer
		  }
	  }else if(request[0].equals("search")){
		  try{
			  String username = request[1];
			  //outMessage = search(username);
			  outMessage = "search";
		  }catch(NullPointerException | NumberFormatException f){
			  //search message did not specify a username
		  }
	  }else if(request[0].equals("list")){
		  //outMessage = list();
	  }	  
	  
	return outMessage;
	  
  }

}