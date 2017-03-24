import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class Client {
	
	static Socket TCPclientSocket = null;
    static int tcpPort;
    public static int serverNum = 1;
    public static int numNeighbors;
    public static ArrayList<NameEntry> neighbors;
	
  public static void main (String[] args) {
    char transferType = 'T';

    Scanner sc = new Scanner(System.in);
    neighbors = new ArrayList<NameEntry>();
    try {
       numNeighbors = sc.nextInt();
       sc.nextLine();
        int i = 1;
        for (int j = 0; j < numNeighbors; j++) {
            String line = sc.nextLine();
            String[] s = line.split(":");
            NameEntry neighbor = new NameEntry(i, s[0], Integer.parseInt(s[1]));
            i++;
            neighbors.add (neighbor);
        }
    } catch(Exception e){
        e.printStackTrace();
        System.exit(1);
    }

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
    
    while (serverNum < numNeighbors + 1) {
    
	try{
        TCPclientSocket = new Socket(neighbors.get(serverNum - 1).getAddress().getHostName(), neighbors.get(serverNum - 1).getPort());
		DataOutputStream outToServer = new DataOutputStream(TCPclientSocket.getOutputStream());
		outToServer.writeBytes(message + "\n");
        TCPclientSocket.setSoTimeout(100);
		BufferedReader inFromServer = new BufferedReader(new InputStreamReader(TCPclientSocket.getInputStream()));
        String in;
       while ((in = inFromServer.readLine()) != null) {
           if (in.equals("DONE")) break;
            inMessage += in + "\n"; 
       }
        TCPclientSocket.setSoTimeout(0);
       break;
	} catch(IOException e){
        serverNum++;
	}
    }
	return inMessage+"\n";
  }
}
