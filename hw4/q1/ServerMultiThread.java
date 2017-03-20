import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;

public class ServerMultiThread extends Thread {
    private Socket socket = null;
    public Inventory inventory;
    public ServerMultiThread(Socket s, Inventory inv) {
        socket = s; 
        inventory = inv;
    }    

    public void run() {
       try( 
               PrintWriter out = new PrintWriter(socket.getOutputStream(),true);
               BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));)
       {
           String input;
           while ((input = reader.readLine()) != null) {
             String output = parseInput(input);
             out.println(output);
             out.println("DONE");
           }
       
         socket.close();
       } catch(Exception e){
           e.printStackTrace();
       }  
    }

  public String parseInput(String inMessage){
	  //process client request
	  String[] request = inMessage.split("\\s+");
	  String outMessage = "";
	  if(request[0].equals("purchase")){
		  try{
			String username = request[1];
		  	String product = request[2];
		  	int quantity = Integer.parseInt(request[3]);
		  	outMessage = inventory.purchase(username, product, quantity);
		  } catch(NullPointerException | NumberFormatException f){
			  //purchase message didn't have all the fields defined or defined correctly
		  }
	  }else if(request[0].equals("cancel")){
		  try{
			  int order = Integer.parseInt(request[1]);
			  outMessage = inventory.cancel(order);
		  } catch(NullPointerException | NumberFormatException f){
			  //cancel message didn't specify an order or didn't give an integer
		  }
	  }else if(request[0].equals("search")){
		  try{
			  String username = request[1];
			  outMessage = inventory.search(username);
		  }catch(NullPointerException | NumberFormatException f){
			  //search message did not specify a username
		  }
	  }else if(request[0].equals("list")){
		  outMessage = inventory.list();
	  }
	return outMessage;
  }

}
