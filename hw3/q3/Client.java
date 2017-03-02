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
    	  
        // TODO: set the mode of communication for sending commands to the server 
        // and display the name of the protocol that will be used in future  	  
    	if(tokens[1].equals("T")){
    		transferType = 'T';
    		//returnMessage = "communication set to TCP\n";
    	}else if (tokens[1].equals("U")){
    		transferType = 'U';
    		//returnMessage = "communication set to UDP\n";
    	}else{
    		System.out.println("Invalid mode! please use 'T' for TCP or 'U' for UDP.");
    	}
      }
      else if (tokens[0].equals("purchase")) {
        // TODO: send appropriate command to the server and display the
        // appropriate responses form the server
		boolean valid = true;
		try{
			int tmp = Integer.parseInt(tokens[3]);
			if(tmp < 1){
				valid = false;	
			}
		} catch (ArrayIndexOutOfBoundsException e){
			valid = false;
		}
		if(valid){
		  String message = "purchase " + tokens[1] +" "+ tokens[2]+" "+tokens[3];
		  if(transferType == 'T'){	  
			  returnMessage = tcpSendandGet(message);
		  }else{
			  returnMessage = udpSendandGet(message);
		  }
		}else{
			returnMessage = "please enter a quantity greater than zero!\n";
		}
      } else if (tokens[0].equals("cancel")) {
        // TODO: send appropriate command to the server and display the
        // appropriate responses form the server
    	  String message = "cancel " + tokens[1];
    	  if(transferType == 'T'){
    		  returnMessage = tcpSendandGet(message);
    	  }else{
    		  returnMessage = udpSendandGet(message);
    	  }
      } else if (tokens[0].equals("search")) {
        // TODO: send appropriate command to the server and display the
        // appropriate responses form the server
    	  String message = "search " + tokens[1];
    	  if(transferType == 'T'){
    		  returnMessage = tcpSendandGet(message);
    	  }else{
    		  returnMessage = udpSendandGet(message);
    	  }
      } else if (tokens[0].equals("list")) {
        // TODO: send appropriate command to the server and display the
        // appropriate responses form the server
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
System.out.print(returnMessage);
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
            inMessage += in+"\n";
       }
	} catch(IOException e){
		return inMessage;
	}
	return inMessage;
  }
}
