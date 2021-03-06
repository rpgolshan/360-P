import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class Client {
	
	static Socket TCPclientSocket = null;
	static DatagramSocket UDPclientSocket = null;
	static String hostAddress;
    static int tcpPort;
    static int udpPort;
	
  public static void main (String[] args) {
    char transferType = 'T';
    if (args.length != 3) {
      System.out.println("ERROR: Provide 3 arguments");
      System.out.println("\t(1) <hostAddress>: the address of the server");
      System.out.println("\t(2) <tcpPort>: the port number for TCP connection");
      System.out.println("\t(3) <udpPort>: the port number for UDP connection");
      System.exit(-1);
    }

    hostAddress = args[0];
    tcpPort = Integer.parseInt(args[1]);
    udpPort = Integer.parseInt(args[2]);
    try{
    	TCPclientSocket = new Socket(hostAddress, tcpPort);
    	UDPclientSocket = new DatagramSocket();
    } catch(IOException e){
    	System.out.print("");
    }
    Scanner sc = new Scanner(System.in);
    while(sc.hasNextLine()) {
      String cmd = sc.nextLine();
      String[] tokens = cmd.split(" ");
      String returnMessage = "";
      
      if (tokens[0].equals("setmode")) {
    	if(tokens[1].equals("T")){
    		transferType = 'T';
    	}else if (tokens[1].equals("U")){
    		transferType = 'U';
    	}else{
    		//System.out.println("Invalid mode! please use 'T' for TCP or 'U' for UDP.\n");
    	}
      }
      else if (tokens[0].equals("purchase")) {
    	  boolean valid = true;
    	  try{
    		  if(Integer.parseInt(tokens[3]) < 1){
    			  valid = false;
    		  }
    	  }catch(ArrayIndexOutOfBoundsException e){
    		  valid = false;
    	  }
    	  if(valid == true){
	    	  if(transferType == 'T'){	  
	  		  	returnMessage = tcpSendandGet("purchase " + tokens[1] +" "+ tokens[2]+" "+tokens[3]);
	    	  }else{
	  		  	returnMessage = udpSendandGet("purchase " + tokens[1] +" "+ tokens[2]+" "+tokens[3]);
	    	  }	
    	  }
      } else if (tokens[0].equals("cancel")) {
    	  if(transferType == 'T'){
    		  returnMessage = tcpSendandGet("cancel " + tokens[1]);
    	  }else{
    		  returnMessage = udpSendandGet("cancel " + tokens[1]);
    	  }
      } else if (tokens[0].equals("search")) {
    	  if(transferType == 'T'){
    		  returnMessage = tcpSendandGet("search " + tokens[1]);
    	  }else{
    		  returnMessage = udpSendandGet("search " + tokens[1]);
    	  }
      } else if (tokens[0].equals("list")) {
    	  if(transferType == 'T'){
    		  returnMessage = tcpSendandGet("list");
    	  }else{
              returnMessage = udpSendandGet("list");
    	  }
      } else if (tokens[0].equals("exit")) {
          break;
      } else {
        System.out.println("ERROR: No such command");
    
      }
System.out.flush();
System.out.print(returnMessage );
    }
sc.close();
  }
  
  static String udpSendandGet(String message){
	  String inMessage = "";
	  try {
			byte[] sendbuf = new byte[4096];
			byte[] recbuf = new byte[4096];
			String outMessage = message + "\n";
			sendbuf = outMessage.getBytes();;
			InetAddress IPAddress = InetAddress.getByName(hostAddress);
			DatagramPacket outToServer = new DatagramPacket(sendbuf, sendbuf.length, IPAddress, udpPort);
			UDPclientSocket.send(outToServer);
			DatagramPacket inPacket = new DatagramPacket(recbuf, recbuf.length);
		    UDPclientSocket.receive(inPacket);
		    inMessage = new String(inPacket.getData(), inPacket.getOffset(), inPacket.getLength());	
		} catch (IOException e) {
			
		}
	return inMessage +"\n";
  }
  
static String tcpSendandGet(String message){
	String inMessage = "";
	try{
		DataOutputStream outToServer = new DataOutputStream(TCPclientSocket.getOutputStream());
		outToServer.writeBytes(message + "\n");
		BufferedReader inFromServer = new BufferedReader(new InputStreamReader(TCPclientSocket.getInputStream()));
        String in;
       while ((in = inFromServer.readLine()) != null) {
           if (in.equals("DONE")) break;
            inMessage += in + "\n"; 
       }
	} catch(IOException e){
		return inMessage;
	}
	return inMessage+"\n";
  }
}