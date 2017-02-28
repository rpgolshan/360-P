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
    /*
    try {
    	DataOutputStream outToServer = new DataOutputStream(TCPclientSocket.getOutputStream());
		outToServer.writeBytes("connect\n");
		TimeUnit.SECONDS.sleep(1);
		outToServer.close();
	} catch (IOException | InterruptedException e) {
		e.printStackTrace();
	}
    */
    while(sc.hasNextLine()) {
      String cmd = sc.nextLine();
      String[] tokens = cmd.split(" ");

      if (tokens[0].equals("setmode")) {
        // TODO: set the mode of communication for sending commands to the server 
        // and display the name of the protocol that will be used in future
    	  String returnMessage = "";
    	if(tokens[1].equals("T")){
    		
    		if(transferType=='T'){
    			returnMessage = tcpSendandGet("setmode T");
    		}else{
    			returnMessage = udpSendandGet("setmode T");
    		}
    		transferType = 'T';
    	}else if (tokens[1].equals("U")){
    		if(transferType=='T'){
    			returnMessage = tcpSendandGet("setmode U");
    		}else{
    			returnMessage = udpSendandGet("setmode U");
    		}	
    		transferType = 'U';
    	}else{
    		System.out.println("Invalid mode! please use 'T' for TCP or 'U' for UDP.");
    	}
    	System.out.println(returnMessage);
      }
      else if (tokens[0].equals("purchase")) {
        // TODO: send appropriate command to the server and display the
        // appropriate responses form the server
      } else if (tokens[0].equals("cancel")) {
        // TODO: send appropriate command to the server and display the
        // appropriate responses form the server
      } else if (tokens[0].equals("search")) {
        // TODO: send appropriate command to the server and display the
        // appropriate responses form the server
      } else if (tokens[0].equals("list")) {
        // TODO: send appropriate command to the server and display the
        // appropriate responses form the server
      } else {
        System.out.println("ERROR: No such command");
      }
    }
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
	return inMessage;
  }
  
static String tcpSendandGet(String message){
	String inMessage = "";
	try{
		DataOutputStream outToServer = new DataOutputStream(TCPclientSocket.getOutputStream());
		outToServer.writeBytes(message + "\n");
		BufferedReader inFromServer = new BufferedReader(new InputStreamReader(TCPclientSocket.getInputStream()));
		inMessage = inFromServer.readLine();
	} catch(IOException e){
		return inMessage;
	}
	return inMessage;
  }
}