import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Scanner;

public class Client {
	
	static Socket TCPclientSocket = null;
	static String hostAddress;
    static int tcpPort;
	
  public static void main (String[] args) {
    char transferType = 'T';
    if (args.length != 2) {
      System.out.println("ERROR: Provide 2 arguments");
      System.out.println("\t(1) <hostAddress>: the address of the server");
      System.out.println("\t(2) <tcpPort>: the port number for TCP connection");
      System.exit(-1);
    }

    hostAddress = args[0];
    tcpPort = Integer.parseInt(args[1]);
    try{
    	TCPclientSocket = new Socket(hostAddress, tcpPort);
    } catch(IOException e){
    	System.out.print("");
    }
    Scanner sc = new Scanner(System.in);
    while(sc.hasNextLine()) {
      String cmd = sc.nextLine();
      String[] tokens = cmd.split(" ");
      String returnMessage = "";
      
      if (tokens[0].equals("purchase")) {
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
	    	  }
    	  }
      } else if (tokens[0].equals("cancel")) {
    	  if(transferType == 'T'){
    		  returnMessage = tcpSendandGet("cancel " + tokens[1]);
    	  }
      } else if (tokens[0].equals("search")) {
    	  if(transferType == 'T'){
    		  returnMessage = tcpSendandGet("search " + tokens[1]);
    	  }
      } else if (tokens[0].equals("list")) {
    	  if(transferType == 'T'){
    		  returnMessage = tcpSendandGet("list");
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
