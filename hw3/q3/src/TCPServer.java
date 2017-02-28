import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class TCPServer extends Server implements Runnable {
	
	static ServerSocket TCPServerSocket;
	static Socket TCPSocket;
	
	public TCPServer(ServerSocket tcp_socket){
		this.TCPServerSocket = tcp_socket;
	}
	
	@Override
	public void run() {
		boolean connected = true;
		while(connected){
			try {	
				synchronized(this){//can only have one tcp connection at a time
					TCPSocket = TCPServerSocket.accept();
					String inMessage = ""; String outMessage = "";
					BufferedReader inFromClient = new BufferedReader(new InputStreamReader(TCPSocket.getInputStream()));				
		        	inMessage = inFromClient.readLine();//tcp blocking call
		        	
		        	outMessage = execute(inMessage);
		        	
		        	DataOutputStream outToClient = new DataOutputStream(TCPSocket.getOutputStream());
		            outToClient.writeBytes(outMessage + "\n");
					System.out.println(outMessage);
					TCPSocket.close();
				}
				
			} catch (IOException e) {
	            
			}		
		}
		
	}
	
}